package pl.sztukakodu.bookaro.order.domain;

import lombok.Value;
import pl.sztukakodu.bookaro.catalog.domain.Book;

@Value
public class OrderItem {
    Book book;
    int quantity;
}
