package pl.sztukakodu.bookaro.order.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class Order {

    private Long id;

    @Builder.Default
    private OrderStatus status = OrderStatus.NEW;

    private List<OrderItem> items;

    private Recipient recipient;

    private LocalDateTime createdAt;

}
