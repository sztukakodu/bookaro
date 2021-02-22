package pl.sztukakodu.bookaro.order.domain;

import org.junit.jupiter.api.Test;
import pl.sztukakodu.bookaro.catalog.domain.Book;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderCostTest {


    @Test
    public void calculatesTotalPriceOfEmptyOrder() {
        // given
        OrderCost order = new OrderCost(
            Collections.emptySet(),
            Delivery.COURIER
        );

        // when
        BigDecimal price = order.finalPrice();

        // then
        assertEquals(BigDecimal.ZERO, price);
    }

    @Test
    public void calculatesTotalPrice() {
        // given
        Book book1 = new Book();
        book1.setPrice(new BigDecimal("12.50"));
        Book book2 = new Book();
        book2.setPrice(new BigDecimal("33.99"));
        Set<OrderItem> items = new HashSet<>(
            Arrays.asList(
                new OrderItem(book1, 2),
                new OrderItem(book2, 5)
            )
        );
        OrderCost order = new OrderCost(
            items,
            Delivery.COURIER
        );

        // when
        BigDecimal price = order.finalPrice();

        // then
        assertEquals(new BigDecimal("188.70"), price);
    }

}