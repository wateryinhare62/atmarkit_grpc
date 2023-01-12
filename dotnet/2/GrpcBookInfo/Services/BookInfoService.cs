using Grpc.Core;
using GrpcBookInfo;

namespace GrpcBookInfo.Services;

public class BookInfoService : BookInfo.BookInfoBase
{
    private readonly ILogger<BookInfoService> _logger;
    private readonly List<BookItem> _items;
    public BookInfoService(ILogger<BookInfoService> logger)
    {
        _logger = logger;
        _items = new List<BookItem> {
            new BookItem {
                Title = "Rust", Author = "Yamauchi"
            },
            new BookItem {
                Title = "gRPC", Author = "Nao"
            },
            new BookItem {
                Title = "WebAssembly", Author = "TamaDigi"
            }
        };
    }

    public override Task<SearchResponse> Search(SearchRequest request, ServerCallContext context)
    {
        SearchResponse response = new SearchResponse{
            StatusCode = 0
        };
        _items.ForEach(item => {
            if (response.StatusCode == 0) {
                if (item.Title.Contains(request.Text) ||
                    item.Author.Contains(request.Text)) {
                    response.StatusCode = 1;
                    response.Item = item;
                }
            }
        });
        return Task.FromResult(response);
        /* 既定の内容
        return Task.FromResult(new SearchResponse
        {
            StatusCode = 0,
            Item = new BookItem
            {
                Title = request.Text
            }
        });
        */
    }
}
