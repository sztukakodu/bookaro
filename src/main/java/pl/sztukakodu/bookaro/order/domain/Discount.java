package pl.sztukakodu.bookaro.order.domain;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

@AllArgsConstructor
enum Discount {
    FREE_SHIPPING(new BigDecimal("100.0")) {
        @Override
        BigDecimal discount(BigDecimal deliveryPrice, Set<OrderItem> items) {
            return deliveryPrice;
        }
    },
    TAKE_ONE_HALF_PRICE(new BigDecimal("200.0")) {
        @Override
        BigDecimal discount(BigDecimal deliveryPrice, Set<OrderItem> items) {
            BigDecimal discount = lowestPrice(items).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
            return deliveryPrice.add(discount);
        }
    },
    TAKE_ONE_FREE(new BigDecimal("400.0")) {
        @Override
        BigDecimal discount(BigDecimal deliveryPrice, Set<OrderItem> items) {
            return deliveryPrice.add(lowestPrice(items));
        }
    };

    private static BigDecimal lowestPrice(Set<OrderItem> items) {
        return items.stream()
                    .map(x -> x.getBook().getPrice())
                    .sorted()
                    .findFirst()
                    .orElse(BigDecimal.ZERO);
    }

    private final BigDecimal threshold;

    abstract BigDecimal discount(BigDecimal deliveryPrice, Set<OrderItem> items);

    static BigDecimal calculateDiscount(OrderCost cost) {
        BigDecimal itemsPrice = cost.itemsPrice();
        if(itemsPrice.compareTo(TAKE_ONE_FREE.threshold) >= 0) {
            return TAKE_ONE_FREE.discount(cost.deliveryPrice(), cost.getItems());
        } else if (itemsPrice.compareTo(TAKE_ONE_HALF_PRICE.threshold) >= 0) {
            return TAKE_ONE_HALF_PRICE.discount(cost.deliveryPrice(), cost.getItems());
        } else if (itemsPrice.compareTo(FREE_SHIPPING.threshold) >= 0) {
            return TAKE_ONE_HALF_PRICE.discount(cost.deliveryPrice(), cost.getItems());
        } else {
            return BigDecimal.ZERO;
        }
    }
}
