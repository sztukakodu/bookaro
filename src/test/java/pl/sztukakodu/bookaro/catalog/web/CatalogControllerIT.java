package pl.sztukakodu.bookaro.catalog.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase.CreateBookCommand;
import pl.sztukakodu.bookaro.catalog.db.AuthorJpaRepository;
import pl.sztukakodu.bookaro.catalog.domain.Author;
import pl.sztukakodu.bookaro.catalog.domain.Book;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
class CatalogControllerIT {

    @Autowired
    CatalogUseCase catalog;

    @Autowired
    CatalogController controller;

    @Autowired
    AuthorJpaRepository authorJpaRepository;

    @Test
    public void getListOfBooks() {
        // given
        Author goetz = authorJpaRepository.save(new Author("Brian Goetz"));
        Author bloch = authorJpaRepository.save(new Author("Joshua Bloch"));
        catalog.addBook(
            new CreateBookCommand(
                "Java Concurrency in Practice",
                Collections.singleton(goetz.getId()),
                2004,
                new BigDecimal("99.00"),
                50L
            )
        );
        catalog.addBook(
            new CreateBookCommand(
                "Effective Java",
                Collections.singleton(bloch.getId()),
                2007,
                new BigDecimal("79.00"),
                50L
            )
        );

        // when
        List<Book> books = controller.getAll(Optional.empty(), Optional.empty());

        // then
        assertEquals(2, books.size());
    }

}