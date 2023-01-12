using Grpc.Core;
using GrpcHello;

namespace GrpcHello.Services;

public class BookService : BookInfo.BookInfoBase
{
    private readonly ILogger<BookService> _logger;
    public BookService(ILogger<BookService> logger)
    {
        _logger = logger;
    }

    public override Task<SearchResponse> Search(SearchRequest request, ServerCallContext context)
    {
        return Task.FromResult(new SearchResponse
        {
            StatusCode = 0,
            Item = new BookItem
            {
                Id = 1,
                Title = request.Text,
                Author = "Yamauchi",
                Publisher = "Shuwa",
                Isbn = "7980",
                Price = 1000,
                Type = BookType.Mook
            }
        });
    }
}
