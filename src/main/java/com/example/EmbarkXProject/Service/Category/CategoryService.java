package com.example.EmbarkXProject.Service.Category;

import com.example.EmbarkXProject.Payload.Category.CategoryDTO;
import com.example.EmbarkXProject.Payload.Category.CategoryResponse;
import org.springframework.http.ResponseEntity;


public interface CategoryService {
    ResponseEntity<String> addCategory(CategoryDTO categoryDTO);

    ResponseEntity<CategoryResponse> getCategoy(int pageNumber, int pageSize, String sortBy, String sortOrder);

    ResponseEntity<String> updateCategory(CategoryDTO categoryDTO);

    ResponseEntity<String> deleteCategory(long categoryId);
}
