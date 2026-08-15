package com.example.EmbarkXProject.Controller;


import com.example.EmbarkXProject.Config.AppConstants;
import com.example.EmbarkXProject.Payload.Category.CategoryDTO;
import com.example.EmbarkXProject.Payload.Category.CategoryResponse;
import com.example.EmbarkXProject.Service.Category.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("add")
    public ResponseEntity<String> addCategory(@RequestBody @Valid CategoryDTO categoryDTO){
        return categoryService.addCategory(categoryDTO);
    }

    @GetMapping("/get")
    public ResponseEntity<CategoryResponse> getCategory(@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) int pageNumber,
                                                        @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) int pageSize,
                                                        @RequestParam(name = "categorySortBy", defaultValue = AppConstants.CATEGORY_SORT_BY, required = false) String sortBy,
                                                        @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder){
        return categoryService.getCategoy(pageNumber, pageSize,sortBy,sortOrder);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO){
        return categoryService.updateCategory(categoryDTO);
    }

    @DeleteMapping("delete/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable long categoryId){
        System.out.println("Deletion is in progress");
        return categoryService.deleteCategory(categoryId);
    }
}
