package pizza_ordering.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pizza_ordering.dto.ProductRequest;
import pizza_ordering.dto.ProductResponse;
import pizza_ordering.exception.CategoryNotFoundException;
import pizza_ordering.exception.InvalidStockException;
import pizza_ordering.exception.OutOfStockException;
import pizza_ordering.exception.ProductNotFoundException;
import pizza_ordering.repository.CategoryRepository;
import pizza_ordering.repository.ProductRepository;
import pizza_ordering.service.ProductService;
import pizza_ordering.entity.Categories;
import pizza_ordering.entity.Product;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private Product findProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }
    private Categories findCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }
    private ProductResponse mapToResponse(Product product){

        return ProductResponse.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .isAvailable(product.getIsAvailable())
                .categoryId(product.getCategory().getCategoryId())
                .categoryName(product.getCategory().getCategoryName())
                .build();
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {

        if(request.getStockQuantity() < 0){
            throw new InvalidStockException("Stock cannot be negative");
        }
        if(request.getPrice() <= 0){
            throw new InvalidStockException("Price must be positive");
        }

        Categories category = findCategoryOrThrow(request.getCategoryId());
        Product product = Product.builder()
                .productName(request.getProductName())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .category(category)
                .isAvailable(request.getStockQuantity()>0)
                .build();

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();

    }

    @Override
    public ProductResponse getProductById(Long productId) {
        Product product = findProductOrThrow(productId);

        return mapToResponse(product);
    }

    @Override
    public ProductResponse updateProduct(Long productId, ProductRequest request) {

        Product product = findProductOrThrow(productId);
        Categories category = findCategoryOrThrow(request.getCategoryId());

        product.setProductName(request.getProductName());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setIsAvailable(request.getStockQuantity() > 0);
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long productId) {
        productRepository.delete(findProductOrThrow(productId));
    }

    @Override
    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        findCategoryOrThrow(categoryId);

        return productRepository.findByCategory_CategoryId(categoryId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void reduceStock(Long productId, Integer quantity) {
        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if(quantity <= 0){
            throw new InvalidStockException("Quantity must be positive");
        }

        if(product.getStockQuantity() < quantity){
            throw new OutOfStockException("Only " + product.getStockQuantity() + " items left");
        }

        int newStock = product.getStockQuantity() - quantity;
        product.setStockQuantity(newStock);

        if(newStock == 0){
            product.setIsAvailable(false);
        }

        productRepository.save(product);
    }


    @Override
    public ProductResponse addStock(Long productId, Integer quantity) {
        if(quantity <= 0){
            throw new InvalidStockException("Stock addition must be positive");
        }
        Product product = findProductOrThrow(productId);

        product.setStockQuantity(product.getStockQuantity() + quantity);
        product.setIsAvailable(true);

        Product updated = productRepository.save(product);

        return mapToResponse(productRepository.save(product));
    }

}
