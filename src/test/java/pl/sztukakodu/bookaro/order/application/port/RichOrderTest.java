package pl.sztukakodu.bookaro.order.application.port;

import org.junit.jupiter.api.Test;
import pl.sztukakodu.bookaro.order.domain.OrderItem;
import pl.sztukakodu.bookaro.order.domain.OrderStatus;
import pl.sztukakodu.bookaro.order.domain.Recipient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RichOrderTest {

    @Test
    public void calculatesTotalPriceOfEmptyOrder() {
        // given
        Set<OrderItem> items = Collections.emptySet();
        RichOrder order = new RichOrder(
            1L,
            OrderStatus.PAID,
            items,
            Recipient.builder().build(),
            LocalDateTime.now()
        );

        // when
        BigDecimal price = order.totalPrice();

        // then
        assertEquals(price, new BigDecimal("0"));
    }

}