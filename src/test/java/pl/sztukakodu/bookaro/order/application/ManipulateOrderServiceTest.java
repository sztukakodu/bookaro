package pl.sztukakodu.bookaro.order.application;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import pl.sztukakodu.bookaro.catalog.db.BookJpaRepository;
import pl.sztukakodu.bookaro.catalog.domain.Book;
import pl.sztukakodu.bookaro.order.application.port.ManipulateOrderUseCase.OrderItemCommand;
import pl.sztukakodu.bookaro.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;
import pl.sztukakodu.bookaro.order.application.port.ManipulateOrderUseCase.PlaceOrderResponse;
import pl.sztukakodu.bookaro.order.db.OrderJpaRepository;
import pl.sztukakodu.bookaro.order.domain.Recipient;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({ManipulateOrderService.class})
class ManipulateOrderServiceTest {

    @Autowired
    BookJpaRepository bookRepository;

    @Autowired
    ManipulateOrderService orderService;

    @Autowired
    OrderJpaRepository orderRepository;

    @Test
    public void userCanPlaceOrder() {
        // given
        Book effectiveJava = givenEffectiveJava(50L);
        Book jcip = givenJavaConcurrency(50L);
        PlaceOrderCommand command = PlaceOrderCommand
            .builder()
            .recipient(recipient())
            .item(new OrderItemCommand(effectiveJava.getId(), 10))
            .item(new OrderItemCommand(jcip.getId(), 10))
            .build();

        // when
        PlaceOrderResponse response = orderService.placeOrder(command);

        // then
        assertTrue(response.isSuccess());
        assertEquals(2, response.getRight().getItems().size());
        assertNotNull(response.getRight().getRecipient());
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

        // expect
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.placeOrder(command);
        });

        assertTrue(exception.getMessage().contains("Too many copies of book " + effectiveJava.getId() + " requested"));
    }

    @Test
    public void userCantPlaceEmptyOrder() {
        // given
        PlaceOrderCommand command = PlaceOrderCommand
            .builder()
            .recipient(recipient())
            .build();

        // when
        PlaceOrderResponse response = orderService.placeOrder(command);

        // then
        assertFalse(response.isSuccess());
        assertThat(response.getLeft(), containsStringIgnoringCase("Empty order"));
    }

    private Book givenJavaConcurrency(long available) {
        return bookRepository.save(new Book("Java Concurrency in Practice", 2006, new BigDecimal("99.90"), available));
    }

    private Book givenEffectiveJava(long available) {
        return bookRepository.save(new Book("Effective Java", 2005, new BigDecimal("199.90"), available));
    }

    private Recipient recipient() {
        return Recipient.builder().email("john@example.org").build();
    }
}