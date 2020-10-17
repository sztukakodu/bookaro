package pl.sztukakodu.bookaro.order.application.port;

import pl.sztukakodu.bookaro.order.domain.Order;

import java.util.List;
import java.util.Optional;

public interface QueryOrderUseCase {
    List<Order> findAll();

    Optional<Order> findById(Long id);
}
