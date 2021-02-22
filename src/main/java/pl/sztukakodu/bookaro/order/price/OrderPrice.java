package pl.sztukakodu.bookaro.order.price;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class OrderPrice {
    BigDecimal itemsPrice;
    BigDecimal deliveryPrice;
    BigDecimal discounts;

    public BigDecimal getFinalPrice() {
        return itemsPrice.add(deliveryPrice).subtract(discounts);
    }
}
