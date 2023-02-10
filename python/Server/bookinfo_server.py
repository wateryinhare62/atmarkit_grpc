import asyncio
import logging
import json

import grpc
from bookinfo_pb2 import BookItem
from bookinfo_pb2 import SearchResponse
from bookinfo_pb2 import SearchRequest
from bookinfo_pb2_grpc import MultiBookInfoServicer
from bookinfo_pb2_grpc import add_MultiBookInfoServicer_to_server

#NUMBER_OF_REPLY = 10

USER_DB = "../Data/bookinfo.json"
with open(USER_DB, mode="r") as fp:
    books = json.load(fp)

class BookInfo(MultiBookInfoServicer):

    async def Search(self, request: SearchRequest,
                       context: grpc.aio.ServicerContext) -> SearchResponse:
        logging.info("Serving Search request %s", request)
        text = request.text
        for i in books:
            title = i["title"]
            author = i["author"]
            publisher = i["publisher"]
            if text in title or text in author or text in publisher:
                bookitem = BookItem(title=i["title"], author=i["author"],
                    publisher=i["publisher"], isbn=i["isbn"], price=i["price"],
                    type=i["type"])
                yield SearchResponse(status_code = 1, item = bookitem)

async def serve() -> None:
    server = grpc.aio.server()
    add_MultiBookInfoServicer_to_server(BookInfo(), server)
    listen_addr = "[::]:50051"
    server.add_insecure_port(listen_addr)
    logging.info("Starting server on %s", listen_addr)
    await server.start()
    await server.wait_for_termination()

if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    asyncio.run(serve())