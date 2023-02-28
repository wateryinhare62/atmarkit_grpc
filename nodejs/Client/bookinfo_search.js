const async = require('async');
const parseArgs = require('minimist');
const _ = require('lodash');

const PROTO_PATH = __dirname + '/../Protos/bookinfo.proto';
const grpc = require('@grpc/grpc-js');
const protoLoader = require('@grpc/proto-loader');
const packageDefinition = protoLoader.loadSync(
    PROTO_PATH,
    {keepCase: true,
     longs: String,
     enums: String,
     defaults: true,
     oneofs: true
    });
const bookinfo = grpc.loadPackageDefinition(packageDefinition).bookinfo;

const client = new bookinfo.BookInfo('localhost:50051',
                                     grpc.credentials.createInsecure());

function runSearch(callback) {
  var argv = parseArgs(process.argv, {
    string: 'keyword'
  });
  const keyword = argv.keyword;
  if (keyword != undefined) {
    console.log(`Search by ${keyword}`);
    const request = {
      text: keyword
    };
    const call = client.search(request);
    call.on('data', (item) => {
        console.log(`Found item: ${item.item.title}`);
    });
    call.on('end', callback);
  }
}

if (require.main === module) {
  async.series([
    runSearch
  ]);
}
