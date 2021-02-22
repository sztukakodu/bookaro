package pl.sztukakodu.bookaro.order.application.port;

import lombok.*;
import pl.sztukakodu.bookaro.commons.Either;
import pl.sztukakodu.bookaro.order.domain.OrderItem;
import pl.sztukakodu.bookaro.order.domain.OrderStatus;
import pl.sztukakodu.bookaro.order.domain.Recipient;

import java.util.List;

public interface ManipulateOrderUseCase {
    PlaceOrderResponse placeOrder(PlaceOrderCommand command);

    void deleteOrderById(Long id);

    UpdateOrderStatusResponse updateOrderStatus(UpdateStatusCommand command);

    @Builder
    @Value
    @AllArgsConstructor
    class PlaceOrderCommand {
        @Singular
        List<OrderItemCommand> items;
        Recipient recipient;
    }

    @Value
    @Builder
    @AllArgsConstructor
    class UpdateStatusCommand {
        Long orderId;
        OrderStatus status;
        String principal;
    }

    @Value
    class OrderItemCommand {
        Long bookId;
        int quantity;
    }

    class PlaceOrderResponse extends Either<String, Long> {
        public PlaceOrderResponse(boolean success, String left, Long right) {
            super(success, left, right);
        }

        public static PlaceOrderResponse success(Long orderId) {
            return new PlaceOrderResponse(true, null, orderId);
        }

        public static PlaceOrderResponse failure(String error) {
            return new PlaceOrderResponse(false, error, null);
        }
    }

    class UpdateOrderStatusResponse extends Either<String, OrderStatus> {

        public UpdateOrderStatusResponse(boolean success, String left, OrderStatus right) {
            super(success, left, right);
        }

        public static UpdateOrderStatusResponse success(OrderStatus status) {
            return new UpdateOrderStatusResponse(true, null, status);
        }

        public static UpdateOrderStatusResponse failure(String error) {
            return new UpdateOrderStatusResponse(false, error, null);
        }
    }
}
