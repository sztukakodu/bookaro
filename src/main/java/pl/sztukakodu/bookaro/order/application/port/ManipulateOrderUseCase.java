package pl.sztukakodu.bookaro.order.application.port;

import lombok.*;
import pl.sztukakodu.bookaro.commons.Either;
import pl.sztukakodu.bookaro.order.domain.Order;
import pl.sztukakodu.bookaro.order.domain.OrderItem;
import pl.sztukakodu.bookaro.order.domain.OrderStatus;
import pl.sztukakodu.bookaro.order.domain.Recipient;

import java.util.List;

public interface ManipulateOrderUseCase {
    PlaceOrderResponse placeOrder(PlaceOrderCommand command);

    void deleteOrderById(Long id);

    void updateOrderStatus(Long id, OrderStatus status);

    @Builder
    @Value
    @AllArgsConstructor
    class PlaceOrderCommand {
        @Singular
        List<OrderItemCommand> items;
        Recipient recipient;
    }

    @Value
    class OrderItemCommand {
        Long bookId;
        int quantity;
    }

    class PlaceOrderResponse extends Either<String, Order> {
        public PlaceOrderResponse(boolean success, String error, Order order) {
            super(success, error, order);
        }

        public static PlaceOrderResponse success(Order order) {
            return new PlaceOrderResponse(true, null, order);
        }

        public static PlaceOrderResponse failure(String error) {
            return new PlaceOrderResponse(false, error, null);
        }
    }
}
