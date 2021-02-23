package pl.sztukakodu.bookaro.catalog.application.port;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang.StringUtils;
import pl.sztukakodu.bookaro.catalog.domain.Book;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

public interface CatalogUseCase {
    List<Book> findAll();

    Optional<Book> findById(Long id);

    List<Book> findByTitle(String title);

    Optional<Book> findOneByTitle(String title);

    List<Book> findByAuthor(String author);

    List<Book> findByTitleAndAuthor(String title, String author);

    Optional<Book> findOneByTitleAndAuthor(String title, String author);

    Book addBook(CreateBookCommand command);

    UpdateBookResponse updateBook(UpdateBookCommand command);

    void removeById(Long id);

    @Value
    class CreateBookCommand {
        String title;
        String author;
        Integer year;
        BigDecimal price;

        public Book toBook() {
            return new Book(title, author, year, price);
        }
    }

    @Value
    @Builder
    @AllArgsConstructor
    class UpdateBookCommand {
        Long id;
        String title;
        String author;
        Integer year;
        BigDecimal price;

        public Book updateFields(Book book) {
            if (StringUtils.isNotBlank(title)) {
                book.setTitle(title);
            }
            if (StringUtils.isNotBlank(author)) {
                book.setAuthor(author);
            }
            if (year != null) {
                book.setYear(year);
            }
            if(price != null) {
                book.setPrice(price);
            }
            return book;
        }
    }

    @Value
    class UpdateBookResponse {
        public static UpdateBookResponse SUCCESS = new UpdateBookResponse(true, emptyList());

        boolean success;
        List<String> errors;
    }
}
