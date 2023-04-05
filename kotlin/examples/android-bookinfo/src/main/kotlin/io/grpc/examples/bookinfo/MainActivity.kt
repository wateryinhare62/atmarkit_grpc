package io.grpc.examples.bookinfo

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer    // new
import androidx.compose.foundation.layout.height    // new
import androidx.compose.foundation.lazy.LazyColumn  // new
import androidx.compose.foundation.lazy.items  // new
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.launch
import java.io.Closeable

//import bookinfo.*

class MainActivity : AppCompatActivity() {

    private val uri by lazy { Uri.parse(resources.getString(R.string.server_url)) }
    private val bookinfoService by lazy { MultiBookInfoRCP(uri) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface(color = MaterialTheme.colors.background) {
                MultiBookInfo(bookinfoService)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bookinfoService.close()
    }
}

class MultiBookInfoRCP(uri: Uri) : Closeable {
    val responseState = mutableStateOf("")
    val list = arrayListOf("")

    private val channel = let {
        println("Connecting to ${uri.host}:${uri.port}")

        val builder = ManagedChannelBuilder.forAddress(uri.host, uri.port)
        if (uri.scheme == "https") {
            builder.useTransportSecurity()
        } else {
            builder.usePlaintext()
        }

        builder.executor(Dispatchers.IO.asExecutor()).build()
    }

    private val bookinfo = MultiBookInfoGrpcKt.MultiBookInfoCoroutineStub(channel)

    suspend fun search(text: String) {
        try {
            var count = 0
            list.clear()
            val request = searchRequest { this.text = text }
            bookinfo.search(request).collect { searchResponse -> 
                list.add(searchResponse.item.title)
                count += 1
            }
            responseState.value = "${count} 個が見つかりました。"
        } catch (e: Exception) {
            responseState.value = e.message ?: "Unknown Error"
            e.printStackTrace()
        }
    }

    override fun close() {
        channel.shutdownNow()
    }
}

@Composable
fun MultiBookInfo(multiBookInfoRCP: MultiBookInfoRCP) {

    val scope = rememberCoroutineScope()

    val nameState = remember { mutableStateOf(TextFieldValue()) }

    Column(Modifier.fillMaxWidth().fillMaxHeight(), Arrangement.Top, Alignment.CenterHorizontally) {
        Text(stringResource(R.string.name_hint), modifier = Modifier.padding(top = 10.dp))
        OutlinedTextField(nameState.value, { nameState.value = it })

        Button({ scope.launch { multiBookInfoRCP.search(nameState.value.text) } }, Modifier.padding(10.dp)) {
            Text(stringResource(R.string.send_request))
        }

        if (multiBookInfoRCP.responseState.value.isNotEmpty()) {
            Text(multiBookInfoRCP.responseState.value)
        }

        LazyColumn(modifier = Modifier.padding(all = 8.dp)) {
            items(multiBookInfoRCP.list) { item ->
                Surface(shape = MaterialTheme.shapes.medium, elevation = 1.dp) {
                    Text(item, modifier = Modifier.padding(all = 4.dp))
                }
                Spacer(modifier = Modifier.height(4.dp))
            } 
        }
    }
}
