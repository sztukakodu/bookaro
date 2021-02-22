package pl.sztukakodu.bookaro.order.price;

import pl.sztukakodu.bookaro.order.domain.Order;

import java.math.BigDecimal;

class DeliveryDiscountStrategy implements DiscountStrategy {

    @Override
    public BigDecimal calculate(Order order) {
        if (order.getItemsPrice().compareTo(BigDecimal.valueOf(100)) >= 0) {
            return order.getDeliveryPrice();
        }
        return BigDecimal.ZERO;
    }
}
