package pl.sztukakodu.bookaro.order.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sztukakodu.bookaro.catalog.db.BookJpaRepository;
import pl.sztukakodu.bookaro.catalog.domain.Book;
import pl.sztukakodu.bookaro.order.application.port.ManipulateOrderUseCase;
import pl.sztukakodu.bookaro.order.db.OrderJpaRepository;
import pl.sztukakodu.bookaro.order.db.RecipientJpaRepository;
import pl.sztukakodu.bookaro.order.domain.Order;
import pl.sztukakodu.bookaro.order.domain.OrderItem;
import pl.sztukakodu.bookaro.order.domain.Recipient;
import pl.sztukakodu.bookaro.order.domain.UpdateStatusResult;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
class ManipulateOrderService implements ManipulateOrderUseCase {
    private final OrderJpaRepository repository;
    private final BookJpaRepository bookJpaRepository;
    private final RecipientJpaRepository recipientJpaRepository;

    @Override
    public PlaceOrderResponse placeOrder(PlaceOrderCommand command) {
        Set<OrderItem> items = command
            .getItems()
            .stream()
            .map(this::toOrderItem)
            .collect(Collectors.toSet());
        Order order = Order
            .builder()
            .recipient(getOrCreateRecipient(command.getRecipient()))
            .delivery(command.getDelivery())
            .items(items)
            .build();
        Order savedOrder = repository.save(order);
        bookJpaRepository.saveAll(reduceBooks(items));
        return PlaceOrderResponse.success(savedOrder.getId());
    }

    private Recipient getOrCreateRecipient(Recipient recipient) {
        return recipientJpaRepository
            .findByEmailIgnoreCase(recipient.getEmail())
            .orElse(recipient);
    }

    private Set<Book> reduceBooks(Set<OrderItem> items) {
        return items
            .stream()
            .map(item -> {
                Book book = item.getBook();
                book.setAvailable(book.getAvailable() - item.getQuantity());
                return book;
            })
            .collect(Collectors.toSet());
    }

    private OrderItem toOrderItem(OrderItemCommand command) {
        Book book = bookJpaRepository.getOne(command.getBookId());
        int quantity = command.getQuantity();
        if (book.getAvailable() >= quantity) {
            return new OrderItem(book, quantity);
        }
        throw new IllegalArgumentException("Too many copies of book " + book.getId() + " requested: " + quantity + " of " + book.getAvailable() + " available ");
    }

    @Override
    public void deleteOrderById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public UpdateStatusResponse updateOrderStatus(UpdateStatusCommand command) {
        return repository
            .findById(command.getOrderId())
            .map(order -> {
                // todo: zmienic na ładną metodę "authorize"
                if (!hasAccess(command, order)) {
                    return UpdateStatusResponse.failure("Unauthorized");
                }
                UpdateStatusResult result = order.updateStatus(command.getStatus());
                if (result.isRevoked()) {
                    bookJpaRepository.saveAll(revokeBooks(order.getItems()));
                }
                repository.save(order);
                return UpdateStatusResponse.success(order.getStatus());
            })
            .orElse(UpdateStatusResponse.failure("Order not found"));
    }

    private boolean hasAccess(UpdateStatusCommand command, Order order) {
        // TODO-Darek: walidować z rolą ROLE_ADMIN a nie z adresem email
        // TODO-Darek: pozmieniać wszędzie gdzie wołam admin@example.org
        // TODO-Darek: użyć klasy UserSecurity
        String email = command.getEmail();
        return email.equalsIgnoreCase(order.getRecipient().getEmail()) ||
            email.equalsIgnoreCase("admin@example.org");
    }

    private Set<Book> revokeBooks(Set<OrderItem> items) {
        return items
            .stream()
            .map(item -> {
                Book book = item.getBook();
                book.setAvailable(book.getAvailable() + item.getQuantity());
                return book;
            })
            .collect(Collectors.toSet());
    }
}
