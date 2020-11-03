package pl.sztukakodu.bookaro.order.domain;

import lombok.Value;

@Value
public class UpdateStatusResult {
    OrderStatus newStatus;
    boolean revoked;

    static UpdateStatusResult ok(OrderStatus newStatus) {
        return new UpdateStatusResult(newStatus, false);
    }

    static UpdateStatusResult revoked(OrderStatus newStatus) {
        return new UpdateStatusResult(newStatus, true);
    }
}
