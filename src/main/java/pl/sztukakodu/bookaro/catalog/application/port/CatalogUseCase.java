package pl.sztukakodu.bookaro.catalog.application.port;

import lombok.Builder;
import lombok.Value;
import pl.sztukakodu.bookaro.catalog.domain.Book;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

public interface CatalogUseCase {
    List<Book> findAllByTitle(String title);

    void addBook(CreateBookCommand command);

    List<Book> findAll();

    void removeById(Long id);

    UpdateBookResponse updateBook(UpdateBookCommand command);

    Optional<Book> findOneByTitleAndAuthor(String title, String author);

    @Value
    class CreateBookCommand {
        String title;
        String author;
        Integer year;
    }

    @Value
    class CreateBookResponse {
        boolean success;
        Long bookId;
        List<String> errors;
    }

    @Value
    @Builder
    class UpdateBookCommand {
        Long bookId;
        String title;
        String author;
        Integer year;

        public Book updateFields(Book book) {
            if (title != null) {
                book.setTitle(title);
            }
            if (author != null) {
                book.setAuthor(author);
            }
            if (year != null) {
                book.setYear(year);
            }
            return book;
        }
    }

    @Value
    class UpdateBookResponse {
        public static final UpdateBookResponse SUCCESS = new UpdateBookResponse(true, emptyList());

        boolean success;
        List<String> errors;

        public static UpdateBookResponse failed(String... errors) {
            return new UpdateBookResponse(false, Arrays.asList(errors));
        }
    }
}
