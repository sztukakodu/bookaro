package pl.sztukakodu.bookaro.catalog.web;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase;
import pl.sztukakodu.bookaro.catalog.db.AuthorJpaRepository;
import pl.sztukakodu.bookaro.catalog.domain.Author;
import pl.sztukakodu.bookaro.catalog.domain.Book;
import pl.sztukakodu.bookaro.order.application.port.ManipulateOrderUseCase;
import pl.sztukakodu.bookaro.order.application.port.QueryOrderUseCase;
import pl.sztukakodu.bookaro.order.domain.OrderItem;
import pl.sztukakodu.bookaro.order.domain.Recipient;

import java.math.BigDecimal;
import java.util.Set;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
class AdminController {

    private final CatalogUseCase catalog;
    private final ManipulateOrderUseCase placeOrder;
    private final QueryOrderUseCase queryOrder;
    private final AuthorJpaRepository authorJpaRepository;

    @PostMapping("/data")
    @Transactional
    public void initialize() {
        initData();
        placeOrder();
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

        ManipulateOrderUseCase.PlaceOrderCommand command = ManipulateOrderUseCase.PlaceOrderCommand
            .builder()
            .recipient(recipient)
            .item(new OrderItem(effectiveJava.getId(), 16))
            .item(new OrderItem(puzzlers.getId(), 7))
            .build();

        ManipulateOrderUseCase.PlaceOrderResponse response = placeOrder.placeOrder(command);
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
        Author joshua = new Author("Joshua", "Bloch");
        Author neal = new Author("Neal", "Gafter");
        authorJpaRepository.save(joshua);
        authorJpaRepository.save(neal);

        CatalogUseCase.CreateBookCommand effectiveJava = new CatalogUseCase.CreateBookCommand(
            "Effective Java",
            Set.of(joshua.getId()),
            2005,
            new BigDecimal("79.00")
        );
        CatalogUseCase.CreateBookCommand javaPuzzlers = new CatalogUseCase.CreateBookCommand(
            "Java Puzzlers",
            Set.of(joshua.getId(), neal.getId()),
            2018,
            new BigDecimal("99.00")
        );
        catalog.addBook(javaPuzzlers);
        catalog.addBook(effectiveJava);
    }
}
