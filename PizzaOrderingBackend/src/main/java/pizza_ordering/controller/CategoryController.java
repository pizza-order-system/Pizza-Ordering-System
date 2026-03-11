package pizza_ordering.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pizza_ordering.service.CategoryService;
import pizza_ordering.entity.Categories;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public Categories createCategory(@RequestBody Categories category) {
        return categoryService.createCategory(category);
    }

    @GetMapping
    public List<Categories> getAllCatgories() {
        return categoryService.getAllCategories();
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }



}
