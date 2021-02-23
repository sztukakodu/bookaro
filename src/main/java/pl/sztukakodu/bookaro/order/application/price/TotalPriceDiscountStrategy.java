package pl.sztukakodu.bookaro.order.application.price;

import pl.sztukakodu.bookaro.order.domain.Order;

import java.math.BigDecimal;

class TotalPriceDiscountStrategy implements DiscountStrategy {
    @Override
    public BigDecimal calculate(Order order) {
        return null;
    }
}
