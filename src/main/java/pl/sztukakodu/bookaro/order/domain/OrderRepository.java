package pl.sztukakodu.bookaro.order.domain;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);

    List<Order> findAll();

    Optional<Order> findById(Long id);

    void deleteById(Long id);
}
