package com.example.tp_bibliotheque.Objects;

public class HasWritten {
    private Book book;
    private Author author;
    private String role;

    public HasWritten(Book _book, Author _author, String _role) {
        book = _book;
        author = _author;
        role = _role;
    }

    public Book getBook() { return book; }
    public Author getAuthor() {
        return author;
    }
    public String getRole() {
        return role;
    }
}
