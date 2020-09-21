package pl.sztukakodu.bookaro.order.application.port;

import pl.sztukakodu.bookaro.order.domain.Order;

import java.util.List;

public interface QueryOrderUseCase {
    List<Order> findAll();
}
