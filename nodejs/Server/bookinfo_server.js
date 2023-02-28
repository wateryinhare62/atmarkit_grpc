const fs = require('fs');
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

let bookinfo_list = [];

function search(call) {
  const keyword = call.request.text;
  _.each(bookinfo_list, (bookinfo) => {
    const title = bookinfo.title;
    const author = bookinfo.author;
    const publisher = bookinfo.publisher;
    if (title.includes(keyword) || author.includes(keyword) || publisher.includes(keyword)) {
      const item = {
        title: title, author: author, publisher: publisher,
        isbn: bookinfo.isbn, price: bookinfo.price, type: bookinfo.type
      };
      const res = {
        status_code: 1,
        item: item
      }
      call.write(res);
    }
  });
  call.end();
}

function register(call, callback) {
  let count = 0;
  call.on('data', (request) => {
    count += 1;
    bookinfo_list.push(request.item);
  });
  call.on('end', () => {
    callback(null, {
      count: count
    });
  });
}

function getServer() {
  const server = new grpc.Server();
  server.addService(bookinfo.BookInfo.service, {
    search: search,
    register: register
  });
  return server;
}

if (require.main === module) {
  const server = getServer();
  server.bindAsync('0.0.0.0:50051', grpc.ServerCredentials.createInsecure(), () => {
    const DB_PATH = __dirname + '/../Data/bookinfo.json';
    fs.readFile(DB_PATH, (err, data) => {
      if (err) throw err;
      bookinfo_list = JSON.parse(data);
      server.start();
    });
  });
}

exports.getServer = getServer;
