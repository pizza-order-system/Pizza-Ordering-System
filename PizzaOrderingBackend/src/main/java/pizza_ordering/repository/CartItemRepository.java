package pizza_ordering.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pizza_ordering.entity.Cart;
import pizza_ordering.entity.CartItems;
import pizza_ordering.entity.Product;

import java.util.List;
import java.util.Optional;


public interface CartItemRepository extends JpaRepository<CartItems, Integer> {
    List<CartItems> findByCart(Cart cart);

    CartItems findByCartAndProduct(Cart cart, Product product);

}
