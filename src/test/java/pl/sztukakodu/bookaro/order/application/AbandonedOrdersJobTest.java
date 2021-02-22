package pl.sztukakodu.bookaro.order.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import pl.sztukakodu.bookaro.catalog.db.BookJpaRepository;
import pl.sztukakodu.bookaro.catalog.domain.Book;
import pl.sztukakodu.bookaro.clock.Clock;
import pl.sztukakodu.bookaro.order.application.port.ManipulateOrderUseCase;
import pl.sztukakodu.bookaro.order.application.port.QueryOrderUseCase;
import pl.sztukakodu.bookaro.order.domain.OrderStatus;
import pl.sztukakodu.bookaro.order.domain.Recipient;

import java.math.BigDecimal;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
    properties = "app.orders.payment-period=PT1H"
)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AbandonedOrdersJobTest {

    @TestConfiguration
    static class TestConfig {

        @Bean
        Clock.Fake clock() {
           return new Clock.Fake();
        }
    }

    @Autowired
    BookJpaRepository bookRepository;

    @Autowired
    ManipulateOrderService manipulateOrderService;

    @Autowired
    QueryOrderUseCase queryOrderService;

    @Autowired
    Clock.Fake clock;

    @Autowired
    AbandonedOrdersJob ordersJob;

    @Test
    public void shouldAbandonOrders() {
        // given - orders
        Book book = givenEffectiveJava(50L);
        Long orderId = placedOrder(book.getId(), 30);

        // when - run
        clock.tick(Duration.ofHours(2));
        ordersJob.run();

        // then - status changed
        assertEquals(OrderStatus.ABANDONED, queryOrderService.findById(orderId).get().getStatus());
    }

    private Long placedOrder(Long bookId, int copies) {
        ManipulateOrderUseCase.PlaceOrderCommand command = ManipulateOrderUseCase.PlaceOrderCommand
            .builder()
            .recipient(recipient())
            .item(new ManipulateOrderUseCase.OrderItemCommand(bookId, copies))
            .build();
        return manipulateOrderService.placeOrder(command).getRight();
    }

    private Recipient recipient() {
        return Recipient.builder().email("john@example.org").build();
    }

    private Book givenEffectiveJava(long available) {
        return bookRepository.save(new Book("Effective Java", 2005, new BigDecimal("199.90"), available));
    }
}