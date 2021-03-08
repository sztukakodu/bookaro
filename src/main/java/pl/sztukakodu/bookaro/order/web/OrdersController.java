package pl.sztukakodu.bookaro.order.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.sztukakodu.bookaro.order.application.port.ManipulateOrderUseCase;
import pl.sztukakodu.bookaro.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;
import pl.sztukakodu.bookaro.order.application.port.ManipulateOrderUseCase.UpdateStatusCommand;
import pl.sztukakodu.bookaro.order.application.port.QueryOrderUseCase;
import pl.sztukakodu.bookaro.order.application.RichOrder;
import pl.sztukakodu.bookaro.order.domain.OrderStatus;
import pl.sztukakodu.bookaro.web.CreatedURI;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestController
@AllArgsConstructor
@RequestMapping("/orders")
class OrdersController {
    private final ManipulateOrderUseCase manipulateOrder;
    private final QueryOrderUseCase queryOrder;

    // admin
    @GetMapping
    public List<RichOrder> getOrders() {
        return queryOrder.findAll();
    }

    // konkretny uzytkownik - wlasciciel zamowienia
    // lub administrator
    @GetMapping("/{id}")
    public ResponseEntity<RichOrder> getOrderById(@PathVariable Long id) {
        return queryOrder.findById(id)
                         .map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    // kazdy
    @PostMapping
    @ResponseStatus(CREATED)
    public ResponseEntity<Object> createOrder(@RequestBody PlaceOrderCommand command) {
        return manipulateOrder
            .placeOrder(command)
            .handle(
                orderId -> ResponseEntity.created(orderUri(orderId)).build(),
                error -> ResponseEntity.badRequest().body(error)
            );
    }

    URI orderUri(Long orderId) {
        return new CreatedURI("/" + orderId).uri();
    }

    // administrator - dowolny status
    // wlasciciel zamowienia - anulowanie
    @PutMapping("/{id}/status")
    @ResponseStatus(ACCEPTED)
    public void updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        OrderStatus orderStatus = OrderStatus
            .parseString(status)
            .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Unknown status: " + status));
        // TODO-Darek: naprawic w module security
        UpdateStatusCommand command = new UpdateStatusCommand(id, orderStatus, "admin@example.org");
        manipulateOrder.updateOrderStatus(command);
    }

    // administrator
    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        manipulateOrder.deleteOrderById(id);
    }
}
