package pl.sztukakodu.bookaro.order.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sztukakodu.bookaro.order.application.port.QueryOrderUseCase;
import pl.sztukakodu.bookaro.order.db.OrderJpaRepository;
import pl.sztukakodu.bookaro.order.domain.Order;
import pl.sztukakodu.bookaro.order.domain.OrderCost;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
class QueryOrderService implements QueryOrderUseCase {
    private final OrderJpaRepository repository;

    @Override
    @Transactional
    public List<RichOrder> findAll() {
        return repository.findAll()
                         .stream()
                         .map(this::toRichOrder)
                         .collect(Collectors.toList());
    }

    @Override
    public Optional<RichOrder> findById(Long id) {
        return repository.findById(id).map(this::toRichOrder);
    }

    private RichOrder toRichOrder(Order order) {
        OrderCost orderCost = new OrderCost(order.getItems(), order.getDelivery());
        return RichOrder
            .builder()
            .id(order.getId())
            .status(order.getStatus())
            .items(order.getItems())
            .recipient(order.getRecipient())
            .createdAt(order.getCreatedAt())
            .delivery(order.getDelivery())
            .itemsPrice(orderCost.itemsPrice())
            .deliveryPrice(orderCost.deliveryPrice())
            .discounts(orderCost.discounts())
            .finalPrice(orderCost.finalPrice())
            .build();
    }
}
