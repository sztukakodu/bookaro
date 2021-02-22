package pl.sztukakodu.bookaro.order.application;

import lombok.Builder;
import lombok.Getter;
import pl.sztukakodu.bookaro.order.domain.*;
import pl.sztukakodu.bookaro.order.price.OrderPrice;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Getter
public class RichOrder {
    Long id;
    OrderStatus status;
    Set<OrderItem> items;
    Recipient recipient;
    LocalDateTime createdAt;
    Delivery delivery;
    OrderPrice orderPrice;
    BigDecimal finalPrice;
}
