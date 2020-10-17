package pl.sztukakodu.bookaro.order.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sztukakodu.bookaro.order.application.port.MaipulateOrderUseCase;
import pl.sztukakodu.bookaro.order.domain.Order;
import pl.sztukakodu.bookaro.order.domain.OrderRepository;

@Service
@RequiredArgsConstructor
class MaipulateOrderService implements MaipulateOrderUseCase {
    private final OrderRepository repository;

    @Override
    public PlaceOrderResponse placeOrder(PlaceOrderCommand command) {
        Order order = Order
            .builder()
            .recipient(command.getRecipient())
            .items(command.getItems())
            .build();
        Order save = repository.save(order);
        return PlaceOrderResponse.success(save.getId());
    }
}
