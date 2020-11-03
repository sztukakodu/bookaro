package pl.sztukakodu.bookaro.order.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sztukakodu.bookaro.catalog.db.BookJpaRepository;
import pl.sztukakodu.bookaro.catalog.domain.Book;
import pl.sztukakodu.bookaro.order.application.port.ManipulateOrderUseCase;
import pl.sztukakodu.bookaro.order.db.OrderJpaRepository;
import pl.sztukakodu.bookaro.order.db.RecipientJpaRepository;
import pl.sztukakodu.bookaro.order.domain.*;

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
            .items(items)
            .build();
        Order save = repository.save(order);
        bookJpaRepository.saveAll(reduceBooks(items));
        return PlaceOrderResponse.success(save.getId());
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
    public void updateOrderStatus(Long id, OrderStatus status) {
        repository.findById(id)
                  .ifPresent(order -> {
                      UpdateStatusResult result = order.updateStatus(status);
                      if(result.isRevoked()) {
                          bookJpaRepository.saveAll(revokeBooks(order.getItems()));
                      }
                      repository.save(order);
                  });
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
