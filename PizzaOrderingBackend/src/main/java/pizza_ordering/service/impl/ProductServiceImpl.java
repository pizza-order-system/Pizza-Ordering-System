package pizza_ordering.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pizza_ordering.dto.ProductRequest;
import pizza_ordering.dto.ProductResponse;
import pizza_ordering.repository.CategoryRepository;
import pizza_ordering.repository.ProductRepository;
import pizza_ordering.service.ProductService;
import pizza_ordering.entity.Categories;
import pizza_ordering.entity.Product;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Categories category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with id"));

        Product product = Product.builder()
                .productName(request.getProductName())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .category(category)
                .isAvailable(request.getStockQuantity()>0)
                .build();
        if(request.getStockQuantity() < 0){
            throw new RuntimeException("Stock cannot be negative");
        }
        if(request.getPrice() <= 0){
            throw new RuntimeException("Price must be positive");
        }

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
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return mapToResponse(product);
    }

    @Override
    public ProductResponse updateProduct(Long productId, ProductRequest request) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        product.setProductName(request.getProductName());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setIsAvailable(request.getStockQuantity() > 0);

        Product updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long productId) {

        if(!productRepository.existsById(productId)){
            throw new RuntimeException("Product not found with id: " + productId);
        }

        productRepository.deleteById(productId);
    }

    @Override
    public List<ProductResponse> getProductsByCategory(Long categoryId) {

        return productRepository.findByCategory_CategoryId(categoryId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void reduceStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        if(product.getStockQuantity() < quantity){
            throw new RuntimeException("Insufficient stock");
        }
        if(quantity <= 0){
            throw new RuntimeException("Quantity must be positive");
        }
        int newStock = product.getStockQuantity() - quantity;
        product.setStockQuantity(newStock);

        if(newStock == 0){
            product.setIsAvailable(false);
        }

        productRepository.save(product);
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
    public ProductResponse addStock(Long productId, Integer quantity) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if(quantity <= 0){
            throw new RuntimeException("Stock addition must be positive");
        }

        product.setStockQuantity(product.getStockQuantity() + quantity);

        if(product.getStockQuantity() > 0){
            product.setIsAvailable(true);
        }

        Product updated = productRepository.save(product);

        return mapToResponse(updated);
    }

}
