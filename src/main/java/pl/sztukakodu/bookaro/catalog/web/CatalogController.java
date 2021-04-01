package pl.sztukakodu.bookaro.catalog.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase.CreateBookCommand;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase.UpdateBookCommand;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase.UpdateBookCoverCommand;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase.UpdateBookResponse;
import pl.sztukakodu.bookaro.catalog.domain.Author;
import pl.sztukakodu.bookaro.catalog.domain.Book;
import pl.sztukakodu.bookaro.web.CreatedURI;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RequestMapping("/catalog")
@RestController
@AllArgsConstructor
class CatalogController {
    private final CatalogUseCase catalog;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RestBook> getAll(
        HttpServletRequest request,
        @RequestParam Optional<String> title,
        @RequestParam Optional<String> author) {
        List<Book> books;
        if (title.isPresent() && author.isPresent()) {
            books = catalog.findByTitleAndAuthor(title.get(), author.get());
        } else if (title.isPresent()) {
            books = catalog.findByTitle(title.get());
        } else if (author.isPresent()) {
            books = catalog.findByAuthor(author.get());
        } else {
            books = catalog.findAll();
        }
        return books.stream()
                    .map(book -> toRestBook(request, book))
                    .collect(toList());
    }

    private RestBook toRestBook(HttpServletRequest request, Book book) {
        String coverUrl = ServletUriComponentsBuilder
            .fromRequest(request)
            .path("/uploads/{id}/file")
            .build(book.getCoverId())
            .toASCIIString();
        return new RestBook(
            book.getId(),
            book.getTitle(),
            book.getYear(),
            book.getPrice(),
            coverUrl,
            book.getAvailable() > 0,
            toRestAuthors(book.getAuthors())
        );
    }

    private Set<RestAuthor> toRestAuthors(Set<Author> authors) {
        return authors.stream().map(a -> new RestAuthor(a.getName())).collect(Collectors.toSet());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return catalog
            .findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Secured("ROLE_ADMIN")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateBook(@PathVariable Long id, @RequestBody RestBookCommand command) {
        UpdateBookResponse response = catalog.updateBook(command.toUpdateCommand(id));
        if (!response.isSuccess()) {
            String message = String.join(", ", response.getErrors());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    @Secured("ROLE_ADMIN")
    @PutMapping(value = "/{id}/cover", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addBookCover(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        catalog.updateBookCover(new UpdateBookCoverCommand(
            id,
            file.getBytes(),
            file.getContentType(),
            file.getOriginalFilename()
        ));
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}/cover")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBookCover(@PathVariable Long id) {
        catalog.removeBookCover(id);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> addBook(@Valid @RequestBody RestBookCommand command) {
        Book book = catalog.addBook(command.toCreateCommand());
        return ResponseEntity.created(createdBookUri(book)).build();
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        catalog.removeById(id);
    }

    private URI createdBookUri(Book book) {
        return new CreatedURI("/" + book.getId().toString()).uri();
    }

    @Data
    private static class RestBookCommand {
        @NotBlank(message = "Please provide a title")
        private String title;

        @NotEmpty
        private Set<Long> authors;

        @NotNull
        private Integer year;

        @NotNull
        @PositiveOrZero
        private Long available;

        @NotNull
        @DecimalMin("0.00")
        private BigDecimal price;

        CreateBookCommand toCreateCommand() {
            return new CreateBookCommand(title, authors, year, price, available);
        }

        UpdateBookCommand toUpdateCommand(Long id) {
            return new UpdateBookCommand(id, title, authors, year, price);
        }
    }
}
