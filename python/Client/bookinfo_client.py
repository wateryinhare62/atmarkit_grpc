import asyncio
import logging
import sys

import grpc
import bookinfo_pb2
import bookinfo_pb2_grpc

async def run(keyword) -> None:
    async with grpc.aio.insecure_channel("localhost:50051") as channel:
        stub = bookinfo_pb2_grpc.MultiBookInfoStub(channel)

        async for response in stub.Search(
            bookinfo_pb2.SearchRequest(text=keyword)):
            print("Client received from async generator: " +
                  response.item.title)

        stream = stub.Search(
            bookinfo_pb2.SearchRequest(text=keyword))
        while True:
            response = await stream.read()
            if response == grpc.aio.EOF:
                break
            print("Client received from direct read: " +
                  response.item.title)

if __name__ == "__main__":
    logging.basicConfig()
    if len(sys.argv) >= 2:
        asyncio.run(run(sys.argv[1]))