package pl.sztukakodu.bookaro.order.price;

import pl.sztukakodu.bookaro.order.domain.Order;
import pl.sztukakodu.bookaro.order.domain.OrderItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

class TotalPriceDiscountStrategy implements DiscountStrategy {
    @Override
    public BigDecimal calculate(Order order) {
        BigDecimal lowestBookPrice = lowestPrice(order.getItems());
        if (priceGreaterOrEqual(order, 400)) {
            return lowestBookPrice;
        } else if (priceGreaterOrEqual(order, 200)) {
            return lowestBookPrice.divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
        } else {
            return BigDecimal.ZERO;
        }
    }

    private boolean priceGreaterOrEqual(Order order, int threshold) {
        return order.getItemsPrice().compareTo(BigDecimal.valueOf(threshold)) >= 0;
    }

    private BigDecimal lowestPrice(Set<OrderItem> items) {
        return items.stream()
                    .map(x -> x.getBook().getPrice())
                    .sorted()
                    .findFirst()
                    .orElse(BigDecimal.ZERO);
    }
}
