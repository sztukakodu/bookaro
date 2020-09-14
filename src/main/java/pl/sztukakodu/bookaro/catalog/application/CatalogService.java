package pl.sztukakodu.bookaro.catalog.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase;
import pl.sztukakodu.bookaro.catalog.domain.Book;
import pl.sztukakodu.bookaro.catalog.domain.CatalogRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CatalogService implements CatalogUseCase {

    private final CatalogRepository repository;

    @Override
    public List<Book> findAllByTitle(String title) {
        return repository.findAll()
                         .stream()
                         .filter(book -> book.getTitle().startsWith(title))
                         .collect(Collectors.toList());
    }

    @Override
    public void addBook(CreateBookCommand command) {
        Book newBook = new Book(command.getTitle(), command.getAuthor(), command.getYear());
        repository.save(newBook);
    }

    @Override
    public List<Book> findAll() {
        return repository.findAll();
    }

    @Override
    public void removeById(Long id) {
        repository.removeById(id);
    }

    @Override
    public UpdateBookResponse updateBook(UpdateBookCommand command) {
        return repository.findById(command.getBookId())
                         .map(book -> {
                             Book updatedBook = command.updateFields(book);
                             repository.save(updatedBook);
                             return UpdateBookResponse.SUCCESS;
                         })
                         .orElseGet(() -> UpdateBookResponse.failed("Book not found"));
    }

    @Override
    public Optional<Book> findOneByTitleAndAuthor(String title, String author) {
        return repository.findAll()
                         .stream()
                         .filter(book -> book.getTitle().equalsIgnoreCase(title))
                         .filter(book -> book.getAuthor().equalsIgnoreCase(author))
                         .findAny();
    }
}
