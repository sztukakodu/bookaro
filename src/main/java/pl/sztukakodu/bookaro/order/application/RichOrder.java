package pl.sztukakodu.bookaro.order.application;

import lombok.Builder;
import lombok.Getter;
import pl.sztukakodu.bookaro.order.domain.Delivery;
import pl.sztukakodu.bookaro.order.domain.OrderItem;
import pl.sztukakodu.bookaro.order.domain.OrderStatus;
import pl.sztukakodu.bookaro.order.domain.Recipient;

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
    BigDecimal itemsPrice;
    BigDecimal deliveryPrice;
    BigDecimal discounts;
    BigDecimal finalPrice;
}
