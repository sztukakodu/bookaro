package pl.sztukakodu.bookaro.catalog.application;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogInitializerUseCase;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase.CreateBookCommand;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase.UpdateBookCoverCommand;
import pl.sztukakodu.bookaro.catalog.db.AuthorJpaRepository;
import pl.sztukakodu.bookaro.catalog.domain.Author;
import pl.sztukakodu.bookaro.catalog.domain.Book;
import pl.sztukakodu.bookaro.jpa.BaseEntity;
import pl.sztukakodu.bookaro.order.application.port.ManipulateOrderUseCase;
import pl.sztukakodu.bookaro.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;
import pl.sztukakodu.bookaro.order.application.port.QueryOrderUseCase;
import pl.sztukakodu.bookaro.order.domain.Delivery;
import pl.sztukakodu.bookaro.order.domain.Recipient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
class CatalogInitializerService implements CatalogInitializerUseCase {

    private final AuthorJpaRepository authorJpaRepository;
    private final ManipulateOrderUseCase placeOrder;
    private final QueryOrderUseCase queryOrder;
    private final CatalogUseCase catalog;
    private final RestTemplate restTemplate;

    @Override
    @Transactional
    public void initialize() {
        initData();
        placeOrder();
    }

    private void initData() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource("books.csv").getInputStream()))) {
            CsvToBean<CsvBook> build = new CsvToBeanBuilder<CsvBook>(reader)
                .withType(CsvBook.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

            build.stream().forEach(this::initBook);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to parse CSV file", e);
        }
    }

    private void initBook(CsvBook csvBook) {
        Set<Long> authors = Arrays
            .stream(csvBook.authors.split(","))
            .filter(StringUtils::isNotBlank)
            .map(String::trim)
            .map(this::getOrCreateAuthor)
            .map(BaseEntity::getId)
            .collect(Collectors.toSet());
        CreateBookCommand command = new CreateBookCommand(
            csvBook.title,
            authors,
            csvBook.year,
            csvBook.amount,
            50L
        );
        Book book = catalog.addBook(command);
        catalog.updateBookCover(updateBookCoverCommand(book.getId(), csvBook.thumbnail));
    }

    private UpdateBookCoverCommand updateBookCoverCommand(Long bookId, String thumbnailUrl) {
        ResponseEntity<byte[]> response = restTemplate.exchange(thumbnailUrl, HttpMethod.GET, null, byte[].class);
        String contentType = response.getHeaders()
                                     .getContentType()
                                     .toString();
        return new UpdateBookCoverCommand(bookId, response.getBody(), contentType, "cover");
    }

    private Author getOrCreateAuthor(String name) {
        return authorJpaRepository
            .findByNameIgnoreCase(name)
            .orElseGet(() -> authorJpaRepository.save(new Author(name)));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CsvBook {
        @CsvBindByName
        private String title;
        @CsvBindByName
        private String authors;
        @CsvBindByName
        private Integer year;
        @CsvBindByName
        private BigDecimal amount;
        @CsvBindByName
        private String thumbnail;
    }

    private void placeOrder() {
        Book effectiveJava = catalog.findOneByTitle("Effective Java")
                                    .orElseThrow(() -> new IllegalStateException("Cannot find a book"));
        Book puzzlers = catalog.findOneByTitle("Java Puzzlers")
                               .orElseThrow(() -> new IllegalStateException("Cannot find a book"));

        // create recipient
        Recipient recipient = Recipient
            .builder()
            .name("Jan Kowalski")
            .phone("123-456-789")
            .street("Armii Krajowej 31")
            .city("Krakow")
            .zipCode("30-150")
            .email("jan@example.org")
            .build();

        PlaceOrderCommand command = PlaceOrderCommand
            .builder()
            .recipient(recipient)
            .item(new ManipulateOrderUseCase.OrderItemCommand(effectiveJava.getId(), 16))
            .item(new ManipulateOrderUseCase.OrderItemCommand(puzzlers.getId(), 7))
            .delivery(Delivery.COURIER)
            .build();

        ManipulateOrderUseCase.PlaceOrderResponse response = placeOrder.placeOrder(command);
        String result = response.handle(
            orderId -> "Created ORDER with id: " + orderId,
            error -> "Failed to created order: " + error
        );
        log.info(result);

        // list all orders
        queryOrder.findAll()
                  .forEach(order -> log.info("GOT ORDER WITH TOTAL PRICE: " + order.getFinalPrice() + " DETAILS: " + order));
    }
}
