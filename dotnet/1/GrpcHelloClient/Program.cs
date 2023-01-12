// See https://aka.ms/new-console-template for more information
// Console.WriteLine("Hello, World!");

using System.Threading.Tasks;
using Grpc.Net.Client;
using GrpcHelloClient;

//using var channel = GrpcChannel.ForAddress("https://localhost:7231");
using var channel = GrpcChannel.ForAddress("http://localhost:5289");
var client = new Greeter.GreeterClient(channel);
var reply = await client.SayHelloAsync(
    new HelloRequest { Name = "やまうちなお" });
Console.WriteLine("gRPC: " + reply.Message);
