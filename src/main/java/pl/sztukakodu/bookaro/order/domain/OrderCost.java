package pl.sztukakodu.bookaro.order.domain;

import lombok.Value;

import java.math.BigDecimal;
import java.util.Set;

@Value
public class OrderCost {
    Set<OrderItem> items;
    Delivery delivery;

    public BigDecimal itemsPrice() {
        return items.stream()
                    .map(item -> item.getBook().getPrice().multiply(new BigDecimal(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal deliveryPrice() {
        if(items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return delivery.getPrice();
    }

    public BigDecimal discounts() {
        return Discount.calculateDiscount(this);
    }

    public BigDecimal finalPrice() {
        return itemsPrice()
            .add(deliveryPrice())
            .subtract(discounts());
    }
}
