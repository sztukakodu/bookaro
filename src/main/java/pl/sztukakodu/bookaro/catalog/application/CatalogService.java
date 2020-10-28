package pl.sztukakodu.bookaro.catalog.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase;
import pl.sztukakodu.bookaro.catalog.db.AuthorJpaRepository;
import pl.sztukakodu.bookaro.catalog.db.BookJpaRepository;
import pl.sztukakodu.bookaro.catalog.domain.Author;
import pl.sztukakodu.bookaro.catalog.domain.Book;
import pl.sztukakodu.bookaro.uploads.application.ports.UploadUseCase;
import pl.sztukakodu.bookaro.uploads.domain.Upload;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static pl.sztukakodu.bookaro.uploads.application.ports.UploadUseCase.SaveUploadCommand;

@Service
@AllArgsConstructor
class CatalogService implements CatalogUseCase {

    private final BookJpaRepository repository;
    private final AuthorJpaRepository authorRepository;
    private final UploadUseCase upload;

    @Override
    public List<Book> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Book> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Book> findByTitle(String title) {
        return repository.findAll()
                         .stream()
                         .filter(book -> book.getTitle().toLowerCase().startsWith(title.toLowerCase()))
                         .collect(Collectors.toList());
    }

    @Override
    public Optional<Book> findOneByTitle(String title) {
        return repository.findAll()
                         .stream()
                         .filter(book -> book.getTitle().startsWith(title))
                         .findFirst();
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return repository.findAll()
                         .stream()
                         // TODO-Darek: fix
//                         .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                         .collect(Collectors.toList());
    }

    @Override
    public List<Book> findByTitleAndAuthor(String title, String author) {
        return repository.findAll()
                         .stream()
                         // TODO-Darek: fix
//                         .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                         .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                         .collect(Collectors.toList());
    }

    @Override
    public Optional<Book> findOneByTitleAndAuthor(String title, String author) {
        return repository.findAll()
                         .stream()
                         .filter(book -> book.getTitle().startsWith(title))
//                         .filter(book -> book.getAuthor().startsWith(author))
                         .findFirst();
    }

    @Override
    public Book addBook(CreateBookCommand command) {
        Book book = toBook(command);
        return repository.save(book);
    }

    private Book toBook(CreateBookCommand command) {
        Book book = new Book(command.getTitle(), command.getYear(), command.getPrice());
        Set<Author> authors = fetchAuthorsByIds(command.getAuthors());
        book.setAuthors(authors);
        return book;
    }

    private Set<Author> fetchAuthorsByIds(Set<Long> authors) {
        return authors.stream()
                      .map(id -> authorRepository
                          .findById(id)
                          .orElseThrow(() -> new IllegalArgumentException("No author with id: " + id))
                      )
                      .collect(Collectors.toSet());
    }

    @Override
    public UpdateBookResponse updateBook(UpdateBookCommand command) {
        return repository
            .findById(command.getId())
            .map(book -> {
                Book updatedBook = toBook(command, book);
                repository.save(updatedBook);
                return UpdateBookResponse.SUCCESS;
            })
            .orElseGet(() -> new UpdateBookResponse(false, Collections.singletonList("Book not found with id: " + command.getId())));
    }

    private Book toBook(UpdateBookCommand command, Book book) {
        if (command.getTitle() != null) {
            book.setTitle(command.getTitle());
        }
        if (command.getAuthors() != null && !command.getAuthors().isEmpty()) {
            Set<Author> authors = fetchAuthorsByIds(command.getAuthors());
            book.setAuthors(authors);
        }
        if (command.getYear() != null) {
            book.setYear(command.getYear());
        }
        if (command.getPrice() != null) {
            book.setPrice(command.getPrice());
        }
        return book;
    }

    @Override
    public void removeById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void updateBookCover(UpdateBookCoverCommand command) {
        repository.findById(command.getId())
                  .ifPresent(book -> {
                      Upload savedUpload = upload.save(new SaveUploadCommand(command.getFilename(), command.getFile(), command.getContentType()));
                      book.setCoverId(savedUpload.getId());
                      repository.save(book);
                  });
    }

    @Override
    public void removeBookCover(Long id) {
        repository.findById(id)
                  .ifPresent(book -> {
                      if (book.getCoverId() != null) {
                          upload.removeById(book.getCoverId());
                          book.setCoverId(null);
                          repository.save(book);
                      }
                  });
    }

}
