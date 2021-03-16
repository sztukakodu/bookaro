package pl.sztukakodu.bookaro.order.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

public enum OrderStatus {
    NEW {
        @Override
        public UpdateStatusResult updateStatus(OrderStatus status) {
            switch(status) {
                case PAID:
                    return UpdateStatusResult.ok(PAID);
                case CANCELLED:
                    return UpdateStatusResult.revoked(CANCELLED);
                case ABANDONED:
                    return  UpdateStatusResult.revoked(ABANDONED);
                default:
                    return super.updateStatus(status);
            }
        }
    },
    PAID {
        @Override
        public UpdateStatusResult updateStatus(OrderStatus status) {
            if (status == SHIPPED) {
                return UpdateStatusResult.ok(SHIPPED);
            }
            return super.updateStatus(status);
        }
    },
    CANCELLED,
    ABANDONED,
    SHIPPED;

    public static Optional<OrderStatus> parseString(String value) {
        return Arrays.stream(values())
                     .filter(it -> StringUtils.equalsIgnoreCase(it.name(), value))
                     .findFirst();
    }

    public UpdateStatusResult updateStatus(OrderStatus status) {
        throw new IllegalArgumentException("Unable to mark " + this.name() + " order as " + status.name());
    }
}
