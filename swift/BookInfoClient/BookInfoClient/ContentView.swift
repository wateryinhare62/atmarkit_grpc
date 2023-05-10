//
//  ContentView.swift
//  BookInfoClient
//
//  Created by Takashi Yamauchi on 2023/04/06.
//

import SwiftUI
import GRPC

struct ContentView: View {
    @State var inputText = ""
    @State var str = "検索は実行されていません"
    @State var items: [String] = []
    var client: Bookinfo_MultiBookInfoAsyncClient? = nil

    init() {
        do {
            let group = PlatformSupport.makeEventLoopGroup(loopCount: 1)
            let channel = try GRPCChannelPool.with(
                target: .host("localhost", port: 50051),
                transportSecurity: .plaintext,
                eventLoopGroup: group
            )
            self.client = Bookinfo_MultiBookInfoAsyncClient(
                channel: channel
            )
        } catch {
            dump(error)
        }
    }
    var body: some View {
        VStack {
            Text("キーワード")
            TextField("キーワードを入力してボタンをタップ", text: $inputText)
                .padding()
                .border(.gray)
            Button {
                Task {
                    do {
                        var request = Bookinfo_SearchRequest()
                        request.text = inputText
                        items = []
                        for try await response in self.client!.search(request) {
                            self.items.append(response.item.title)
                            try await Task.sleep(nanoseconds: UInt64(1e8))
                        }
                        str = String(format: "%d件、見つかりました", self.items.count)
                    } catch {
                        dump(error)
                    }
                }
            } label: {
                Text("検索開始")
            }
            .padding()
            Text(str)
            List(items, id:\.self) { item in
                Text(item)
            }
        }
        .padding()
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
