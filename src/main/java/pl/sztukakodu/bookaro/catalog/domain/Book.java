package pl.sztukakodu.bookaro.catalog.domain;

import java.util.StringJoiner;

public class Book {
    Long id;
    String title;
    String author;
    Integer year;

    public Book(Long id, String title, String author, Integer year) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Book.class.getSimpleName() + "[", "]")
            .add("id=" + id)
            .add("title='" + title + "'")
            .add("author='" + author + "'")
            .add("year=" + year)
            .toString();
    }
}
