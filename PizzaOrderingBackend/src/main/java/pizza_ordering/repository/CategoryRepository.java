package pizza_ordering.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pizza_ordering.entity.Categories;

public interface CategoryRepository extends JpaRepository<Categories, Long> {
    boolean existsByCategoryNameIgnoreCase(String categoryName);


}
