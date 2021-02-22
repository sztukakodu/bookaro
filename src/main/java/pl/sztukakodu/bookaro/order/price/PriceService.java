package pl.sztukakodu.bookaro.order.price;

import org.springframework.stereotype.Service;
import pl.sztukakodu.bookaro.order.domain.Order;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PriceService {

    private final List<DiscountStrategy> strategies = List.of(
        new DeliveryDiscountStrategy(),
        new TotalPriceDiscountStrategy()
    );

    public OrderPrice calculatePrice(Order order) {
        return new OrderPrice(
            order.getItemsPrice(),
            order.getDeliveryPrice(),
            discounts(order)
        );
    }

    private BigDecimal discounts(Order order) {
        return strategies
            .stream()
            .map(strategy -> strategy.calculate(order))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
