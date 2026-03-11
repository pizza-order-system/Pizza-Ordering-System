package pizza_ordering.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pizza_ordering.dto.OrderItemResponse;
import pizza_ordering.dto.OrderResponse;
import pizza_ordering.entity.*;
import pizza_ordering.repository.*;
import pizza_ordering.service.ProductService;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemsRepository orderItemRepository;
    private final ProductService productService;

    @Transactional
    public OrderResponse placeOrder(User user){

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartItems> items = cartItemRepository.findByCart(cart);

        double total = 0;
        int totalQuantity = 0;

        for(CartItems item : items){
            total += item.getQuantity() * item.getPriceAtTime();
            totalQuantity += item.getQuantity();
        }

        Orders order = new Orders();

        order.setUser(user);
        order.setTotalAmount(total);
        order.setOrderQuantity(totalQuantity);
        order.setOrderStatus("PLACED");
        order.setOrderTime(LocalDateTime.now());

        orderRepository.save(order);

        List<OrderItemResponse> responseItems = new ArrayList<>();

        for(CartItems item : items){

            productService.reduceStock(item.getProduct().getProductId(), item.getQuantity());

            OrderItems orderItem = new OrderItems();
            orderItem.setOrder(order);
            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPriceAtPurchase((int) item.getPriceAtTime());

            orderItemRepository.save(orderItem);

            OrderItemResponse itemResponse = new OrderItemResponse();
            itemResponse.setProductId(item.getProduct().getProductId());
            itemResponse.setProductName(item.getProduct().getProductName());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setPrice(item.getPriceAtTime());

            responseItems.add(itemResponse);

        }

        cartItemRepository.deleteAll(items);

        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getOrderId());
        response.setTotalQuantity(totalQuantity);
        response.setTotalAmount(total);
        response.setStatus(order.getOrderStatus());
        response.setItems(responseItems);

        return response;
    }

    public List<OrderResponse> getUserOrders(User user){

        List<Orders> orders = orderRepository.findByUser(user);

        List<OrderResponse> responses = new ArrayList<>();

        for(Orders order : orders){

            List<OrderItems> orderItems = orderItemRepository.findByOrder(order);

            List<OrderItemResponse> items = new ArrayList<>();

            for(OrderItems item : orderItems){

                OrderItemResponse itemResponse = new OrderItemResponse();

                itemResponse.setProductId(item.getProduct().getProductId());
                itemResponse.setProductName(item.getProduct().getProductName());
                itemResponse.setQuantity(item.getQuantity());
                itemResponse.setPrice(item.getPriceAtPurchase());

                items.add(itemResponse);
            }

            OrderResponse response = new OrderResponse();

            response.setOrderId(order.getOrderId());
            response.setTotalQuantity(order.getOrderQuantity());
            response.setTotalAmount(order.getTotalAmount());
            response.setStatus(order.getOrderStatus());
            response.setItems(items);

            responses.add(response);
        }

        return responses;
    }

    public List<OrderResponse> getAllOrders(){

        List<Orders> orders = orderRepository.findAll();

        List<OrderResponse> responses = new ArrayList<>();

        for(Orders order : orders){

            List<OrderItems> orderItems = orderItemRepository.findByOrder(order);

            List<OrderItemResponse> items = new ArrayList<>();

            for(OrderItems item : orderItems){

                OrderItemResponse itemResponse = new OrderItemResponse();

                itemResponse.setProductId(item.getProduct().getProductId());
                itemResponse.setProductName(item.getProduct().getProductName());
                itemResponse.setQuantity(item.getQuantity());
                itemResponse.setPrice(item.getPriceAtPurchase());

                items.add(itemResponse);
            }

            OrderResponse response = new OrderResponse();

            response.setUserId(order.getUser().getUserId());
            response.setOrderId(order.getOrderId());
            response.setTotalQuantity(order.getOrderQuantity());
            response.setTotalAmount(order.getTotalAmount());
            response.setStatus(order.getOrderStatus());
            response.setItems(items);

            responses.add(response);
        }

        return responses;
    }

    public String cancelOrder(Long orderId, User user){

        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if(!order.getUser().getUserId().equals(user.getUserId())){
            throw new RuntimeException("You cannot cancel this order");
        }

        orderRepository.delete(order);

        return "Order cancelled";
    }

}