package pl.sztukakodu.bookaro.catalog.domain;

import java.util.List;
import java.util.Optional;

public interface CatalogRepository {
    List<Book> findAll();

    Long save(Book book);

    Optional<Book> findById(Long bookId);

    void removeById(Long id);
}
