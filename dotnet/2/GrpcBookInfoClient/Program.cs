// See https://aka.ms/new-console-template for more information
//Console.WriteLine("Hello, World!");

using System.Threading.Tasks;
using Grpc.Net.Client;
using GrpcBookInfoClient;

if (args.Length == 0) {
    Console.WriteLine("引数が必要です");
    return;
}
using var channel = GrpcChannel.ForAddress("http://localhost:5299");
var client = new BookInfo.BookInfoClient(channel);
var reply = await client.SearchAsync(
    new SearchRequest { Text = args[0] });
if (reply.StatusCode == 0) {
    Console.WriteLine($"見つかりません: {args[0]}");
} else {
    Console.WriteLine($"検索結果: {reply.Item.Title} by {reply.Item.Author}");
}
