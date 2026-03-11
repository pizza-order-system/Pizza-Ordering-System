package pizza_ordering.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pizza_ordering.dto.OrderResponse;
import pizza_ordering.entity.Orders;
import pizza_ordering.entity.User;
import pizza_ordering.repository.UserRepository;
import pizza_ordering.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    @PostMapping
    public OrderResponse placeOrder(){

        User user = getCurrentUser();

        return orderService.placeOrder(user);
    }

    @GetMapping
    public List<OrderResponse> getMyOrders(){

        User user = getCurrentUser();

        return orderService.getUserOrders(user);
    }

    @GetMapping("/admin/orders")
    public List<OrderResponse> getAllOrders(){

        return orderService.getAllOrders();

    }

    @DeleteMapping("/{orderId}")
    public String cancelOrder(@PathVariable Long orderId){

        User user = getCurrentUser();

        return orderService.cancelOrder(orderId, user);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow();
    }



}