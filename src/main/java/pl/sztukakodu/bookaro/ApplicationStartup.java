package pl.sztukakodu.bookaro;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase.CreateBookCommand;
import pl.sztukakodu.bookaro.catalog.db.AuthorJpaRepository;
import pl.sztukakodu.bookaro.catalog.domain.Author;
import pl.sztukakodu.bookaro.catalog.domain.Book;
import pl.sztukakodu.bookaro.order.application.port.ManipulateOrderUseCase;
import pl.sztukakodu.bookaro.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;
import pl.sztukakodu.bookaro.order.application.port.ManipulateOrderUseCase.PlaceOrderResponse;
import pl.sztukakodu.bookaro.order.application.port.QueryOrderUseCase;
import pl.sztukakodu.bookaro.order.domain.OrderItem;
import pl.sztukakodu.bookaro.order.domain.Recipient;

import java.math.BigDecimal;
import java.util.Set;

@Component
@AllArgsConstructor
class ApplicationStartup implements CommandLineRunner {

    private final CatalogUseCase catalog;
    private final ManipulateOrderUseCase placeOrder;
    private final QueryOrderUseCase queryOrder;
    private final AuthorJpaRepository authorRepository;

    @Override
    public void run(String... args) {
        initData();
        placeOrder();
    }

    private void placeOrder() {
        Book panTadeusz = catalog.findOneByTitle("Java Puzzlers")
                                 .orElseThrow(() -> new IllegalStateException("Cannot find a book"));
        Book chlopi = catalog.findOneByTitle("Effective Java")
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
            .item(new OrderItem(panTadeusz.getId(), 16))
            .item(new OrderItem(chlopi.getId(), 7))
            .build();

        PlaceOrderResponse response = placeOrder.placeOrder(command);
        String result = response.handle(
            orderId -> "Created ORDER with id: " + orderId,
            error -> "Failed to created order: " + error
        );
        System.out.println(result);

        // list all orders
        queryOrder.findAll()
                  .forEach(order -> System.out.println("GOT ORDER WITH TOTAL PRICE: " + order.totalPrice() + " DETAILS: " + order));
    }

    private void initData() {
        Author bloch = new Author("Joshua", "Bloch");
        Author gafter = new Author("Neal", "Gafter");
        authorRepository.save(bloch);
        authorRepository.save(gafter);
        CreateBookCommand puzzlers = new CreateBookCommand(
            "Java Puzzlers",
            Set.of(bloch.getId(), gafter.getId()),
            2005,
            new BigDecimal("99.00")
        );
        CreateBookCommand effective = new CreateBookCommand(
            "Effective Java",
            Set.of(bloch.getId()),
            2018,
            new BigDecimal("79.00")
        );
        catalog.addBook(puzzlers);
        catalog.addBook(effective);
    }
}
