const async = require('async');
const fs = require('fs');
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

function runRegister(callback) {
  const argv = parseArgs(process.argv, {
    string: 'file'
  });
  const file = argv.file;
  if(file != undefined) {
    fs.readFile(file, (err, data) => {
      if (err) {
        callback(err);
        return;
      }
      const bookinfo_list = JSON.parse(data);
      const call = client.register((error, response) => {
        if (error) {
          callback(error);
          return;
        }
        callback();
        console.log(`Registered ${response.count} item(s).`);
      });
      function itemSender(item) {
        return (callback) => {
          console.log(`Register item: ${item.title}`);
          call.write({
            item: item
          });
          _.delay(callback, _.random(500, 1500));
        };
      }
      let item_senders = [];
      for (let i = 0; i < bookinfo_list.length; i++) {
        item_senders[i] = itemSender(bookinfo_list[i]);
      }
      async.series(item_senders, () => { call.end(); });
    });
  }
}

if (require.main === module) {
  async.series([
    runRegister
  ]);
}
