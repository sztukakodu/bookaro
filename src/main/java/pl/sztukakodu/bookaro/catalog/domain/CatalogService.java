package pl.sztukakodu.bookaro.catalog.domain;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatalogService {

    private final CatalogRepository repository;

    public CatalogService(@Qualifier("schoolCatalogRepository") CatalogRepository repository) {
        this.repository = repository;
    }

    public List<Book> findByTitle(String title) {
        return repository.findAll()
                         .stream()
                         .filter(book -> book.getTitle().startsWith(title))
                         .collect(Collectors.toList());
    }

}
