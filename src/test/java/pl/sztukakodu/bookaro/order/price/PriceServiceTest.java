package pl.sztukakodu.bookaro.order.price;

import org.junit.jupiter.api.Test;
import pl.sztukakodu.bookaro.catalog.domain.Book;
import pl.sztukakodu.bookaro.order.domain.Order;
import pl.sztukakodu.bookaro.order.domain.OrderItem;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PriceServiceTest {

    PriceService service = new PriceService();

    @Test
    public void calculatesTotalPriceOfEmptyOrder() {
        // given
        Order order = Order.builder().build();

        // when
        OrderPrice price = service.calculatePrice(order);

        // then
        assertEquals(BigDecimal.ZERO, price.getFinalPrice());
        assertEquals(BigDecimal.ZERO, price.getItemsPrice());
    }

    @Test
    public void calculatesTotalPrice() {
        // given
        Book book1 = new Book();
        book1.setPrice(new BigDecimal("12.50"));
        Book book2 = new Book();
        book2.setPrice(new BigDecimal("33.99"));
        Order order = Order.builder()
                           .item(new OrderItem(book1, 2))
                           .item(new OrderItem(book2, 2))
                           .build();

        // when
        OrderPrice price = service.calculatePrice(order);

        // then
        assertEquals(new BigDecimal("92.98"), price.getItemsPrice());
        assertEquals(new BigDecimal("102.97"), price.getFinalPrice());
    }

}