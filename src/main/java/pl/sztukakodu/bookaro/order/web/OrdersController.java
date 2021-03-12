package pl.sztukakodu.bookaro.order.web;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.sztukakodu.bookaro.order.application.RichOrder;
import pl.sztukakodu.bookaro.order.application.port.ManipulateOrderUseCase;
import pl.sztukakodu.bookaro.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;
import pl.sztukakodu.bookaro.order.application.port.ManipulateOrderUseCase.UpdateStatusCommand;
import pl.sztukakodu.bookaro.order.application.port.QueryOrderUseCase;
import pl.sztukakodu.bookaro.order.domain.OrderStatus;
import pl.sztukakodu.bookaro.security.UserSecurity;
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
    private final UserSecurity userSecurity;

    @Secured("ROLE_ADMIN")
    @GetMapping
    public List<RichOrder> getOrders() {
        return queryOrder.findAll();
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @GetMapping("/{id}")
    public ResponseEntity<RichOrder> getOrderById(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        return queryOrder.findById(id)
                         .map(order -> authorize(order, user))
                         .orElse(ResponseEntity.notFound().build());
    }

    private ResponseEntity<RichOrder> authorize(RichOrder order, UserDetails user) {
        if (userSecurity.isOwnerOrAdmin(order.getRecipient().getEmail(), user)) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.status(FORBIDDEN).build();
    }

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

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @PatchMapping("/{id}/status")
    public ResponseEntity<Object> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> body, @AuthenticationPrincipal UserDetails user) {
        String status = body.get("status");
        OrderStatus orderStatus = OrderStatus
            .parseString(status)
            .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Unknown status: " + status));
        UpdateStatusCommand command = new UpdateStatusCommand(id, orderStatus, user);
        return manipulateOrder.updateOrderStatus(command)
                              .handle(
                                  newStatus -> ResponseEntity.accepted().build(),
                                  error -> ResponseEntity.status(error.getStatus()).build()
                              );
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        manipulateOrder.deleteOrderById(id);
    }
}
