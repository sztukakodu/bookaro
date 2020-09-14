package pl.sztukakodu.bookaro.catalog.infrastructure;

import org.springframework.stereotype.Repository;
import pl.sztukakodu.bookaro.catalog.domain.Book;
import pl.sztukakodu.bookaro.catalog.domain.CatalogRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
class MemoryCatalogRepository implements CatalogRepository {
    private static final AtomicLong ID_GENERATOR = new AtomicLong();
    private final Map<Long, Book> storage = new ConcurrentHashMap<>();

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Long save(Book book) {
        long id = nextId();
        book.setId(id);
        storage.put(id, book);
        return id;
    }

    @Override
    public Optional<Book> findById(Long bookId) {
        return Optional.ofNullable(storage.get(bookId));
    }

    @Override
    public void removeById(Long id) {
        storage.remove(id);
    }

    private long nextId() {
        return ID_GENERATOR.incrementAndGet();
    }
}
