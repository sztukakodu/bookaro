package pl.sztukakodu.bookaro.order.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.sztukakodu.bookaro.order.domain.Order;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
}
