package pizza_ordering.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pizza_ordering.entity.Product;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory_CategoryId(Long categoryId);
    boolean existsByCategory_CategoryId(Long categoryId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.productId = :id")
    Optional<Product> findByIdForUpdate(Long id);

}
