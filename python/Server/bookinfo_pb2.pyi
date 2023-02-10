from google.protobuf.internal import enum_type_wrapper as _enum_type_wrapper
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from typing import ClassVar as _ClassVar, Mapping as _Mapping, Optional as _Optional, Union as _Union

BOOK: BookType
DESCRIPTOR: _descriptor.FileDescriptor
MOOK: BookType
OTHER: BookType
PAPERBACK: BookType
POCKET: BookType

class BookItem(_message.Message):
    __slots__ = ["author", "id", "isbn", "price", "publisher", "title", "type"]
    AUTHOR_FIELD_NUMBER: _ClassVar[int]
    ID_FIELD_NUMBER: _ClassVar[int]
    ISBN_FIELD_NUMBER: _ClassVar[int]
    PRICE_FIELD_NUMBER: _ClassVar[int]
    PUBLISHER_FIELD_NUMBER: _ClassVar[int]
    TITLE_FIELD_NUMBER: _ClassVar[int]
    TYPE_FIELD_NUMBER: _ClassVar[int]
    author: str
    id: int
    isbn: str
    price: int
    publisher: str
    title: str
    type: BookType
    def __init__(self, id: _Optional[int] = ..., title: _Optional[str] = ..., author: _Optional[str] = ..., publisher: _Optional[str] = ..., isbn: _Optional[str] = ..., price: _Optional[int] = ..., type: _Optional[_Union[BookType, str]] = ...) -> None: ...

class SearchRequest(_message.Message):
    __slots__ = ["text"]
    TEXT_FIELD_NUMBER: _ClassVar[int]
    text: str
    def __init__(self, text: _Optional[str] = ...) -> None: ...

class SearchResponse(_message.Message):
    __slots__ = ["item", "status_code"]
    ITEM_FIELD_NUMBER: _ClassVar[int]
    STATUS_CODE_FIELD_NUMBER: _ClassVar[int]
    item: BookItem
    status_code: int
    def __init__(self, status_code: _Optional[int] = ..., item: _Optional[_Union[BookItem, _Mapping]] = ...) -> None: ...

class BookType(int, metaclass=_enum_type_wrapper.EnumTypeWrapper):
    __slots__ = []
