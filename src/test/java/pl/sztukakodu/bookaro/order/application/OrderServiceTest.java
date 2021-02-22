package pl.sztukakodu.bookaro.order.application;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogUseCase;
import pl.sztukakodu.bookaro.catalog.db.BookJpaRepository;
import pl.sztukakodu.bookaro.catalog.domain.Book;
import pl.sztukakodu.bookaro.order.application.port.ManipulateOrderUseCase.*;
import pl.sztukakodu.bookaro.order.application.port.QueryOrderUseCase;
import pl.sztukakodu.bookaro.order.domain.OrderStatus;
import pl.sztukakodu.bookaro.order.domain.Recipient;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class OrderServiceTest {

    @Autowired
    BookJpaRepository bookRepository;

    @Autowired
    ManipulateOrderService service;

    @Autowired
    QueryOrderUseCase queryOrderService;

    @Autowired
    CatalogUseCase catalogUseCase;

    @Test
    public void userCanPlaceOrder() {
        // given
        Book effectiveJava = givenEffectiveJava(50L);
        Book jcip = givenJavaConcurrency(50L);
        PlaceOrderCommand command = PlaceOrderCommand
            .builder()
            .recipient(recipient())
            .item(new OrderItemCommand(effectiveJava.getId(), 15))
            .item(new OrderItemCommand(jcip.getId(), 10))
            .build();

        // when
        PlaceOrderResponse response = service.placeOrder(command);

        // then
        assertTrue(response.isSuccess());
        assertEquals(35L, availableCopiesOf(effectiveJava));
        assertEquals(40L, availableCopiesOf(jcip));
    }

    @Test
    public void userCanRevokeOrder() {
        // given
        Book effectiveJava = givenEffectiveJava(50L);
        Long orderId = placedOrder(effectiveJava.getId(), 15);
        assertEquals(35L, availableCopiesOf(effectiveJava));

        // when
        updateOrderStatus(orderId, OrderStatus.CANCELLED);

        // then
        assertEquals(50L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.CANCELLED, queryOrderService.findById(orderId)
                                                             .get()
                                                             .getStatus());
    }

    private UpdateOrderStatusResponse updateOrderStatus(Long orderId, OrderStatus status) {
        return service.updateOrderStatus(new UpdateStatusCommand(orderId, status, "admin@example.org"));
    }

    @Disabled("homework")
    public void userCannotRevokePaidOrder() {
        // user nie moze wycofac juz oplaconego zamowienia
    }

    @Disabled("homework")
    public void userCannotRevokeShippedOrder() {
        // user nie moze wycofac juz wyslanego zamowienia
    }

    @Disabled("homework")
    public void userCannotOrderNoExistingBooks() {
        // user nie moze zamowic nieistniejacych ksiazek
    }

    @Disabled("homework")
    public void userCannotOrderNegativeNumberOfBooks() {
        // user nie moze zamowic ujemnej liczby ksiazek
    }

    @Test
    @Disabled("requires security module")
    public void userCannotRevokeOtherUserOrder() {
        // given
        Book effectiveJava = givenEffectiveJava(50L);
        String recipient = "john@example.org";
        Long orderId = placedOrder(effectiveJava.getId(), 15, recipient);
        assertEquals(35L, availableCopiesOf(effectiveJava));

        // when
        UpdateStatusCommand command = UpdateStatusCommand
            .builder()
            .orderId(orderId)
            .status(OrderStatus.CANCELLED)
            .principal("adam@example.org")
            .build();
        UpdateOrderStatusResponse response = service.updateOrderStatus(command);

        // then
        assertEquals(35L, availableCopiesOf(effectiveJava));
        assertFalse(response.isSuccess());
        assertEquals(OrderStatus.NEW, queryOrderService.findById(orderId).get().getStatus());

    }

    private Long placedOrder(Long bookId, int copies, String recipient) {
        PlaceOrderCommand command = PlaceOrderCommand
            .builder()
            .recipient(recipient(recipient))
            .item(new OrderItemCommand(bookId, copies))
            .build();
        return service.placeOrder(command)
                      .getRight();
    }

    private Long placedOrder(Long bookId, int copies) {
        return placedOrder(bookId, copies, "john@example.org");
    }

    @Test
    public void userCantOrderMoreBooksThanAvailable() {
        // given
        Book effectiveJava = givenEffectiveJava(5L);
        PlaceOrderCommand command = PlaceOrderCommand
            .builder()
            .recipient(recipient())
            .item(new OrderItemCommand(effectiveJava.getId(), 10))
            .build();

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.placeOrder(command);
        });

        // then
        assertTrue(exception.getMessage()
                            .contains("Too many copies of book " + effectiveJava.getId() + " requested"));
    }

    private Book givenJavaConcurrency(long available) {
        return bookRepository.save(new Book("Java Concurrency in Practice", 2006, new BigDecimal("99.90"), available));
    }

    private Book givenEffectiveJava(long available) {
        return bookRepository.save(new Book("Effective Java", 2005, new BigDecimal("199.90"), available));
    }

    private Recipient recipient() {
        return recipient("john@example.org");
    }

    private Recipient recipient(String email) {
        return Recipient.builder()
                        .email(email)
                        .build();
    }

    private Long availableCopiesOf(Book effectiveJava) {
        return catalogUseCase.findById(effectiveJava.getId())
                             .get()
                             .getAvailable();
    }
}