package pizza_ordering.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pizza_ordering.exception.CategoryAlreadyExistsException;
import pizza_ordering.exception.CategoryInUseException;
import pizza_ordering.exception.CategoryNotFoundException;
import pizza_ordering.exception.InvalidStockException;
import pizza_ordering.repository.CategoryRepository;
import pizza_ordering.repository.ProductRepository;
import pizza_ordering.service.CategoryService;
import pizza_ordering.entity.Categories;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private Categories findCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }
    private void validateCategoryName(String name){

        if(name == null || name.trim().isEmpty()){
            throw new InvalidStockException("Category name cannot be empty");
        }

        if(categoryRepository.existsByCategoryNameIgnoreCase(name)){
            throw new CategoryAlreadyExistsException(name);
        }
    }

    @Override
    public Categories createCategory(Categories category){
        validateCategoryName(category.getCategoryName());
        return categoryRepository.save(category);
    }

    @Override
    public List<Categories> getAllCategories(){
        return categoryRepository.findAll();
    }

    @Override
    public void deleteCategory(Long categoryId){
        findCategoryOrThrow(categoryId);
        if(productRepository.existsByCategory_CategoryId(categoryId)){
            throw new CategoryInUseException(categoryId);
        }
        categoryRepository.deleteById(categoryId);
    }

}
