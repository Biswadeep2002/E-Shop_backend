package com.example.EmbarkXProject.Service.Category;

import com.example.EmbarkXProject.Exceptions.exceptions.APIException;
import com.example.EmbarkXProject.Exceptions.exceptions.NotUniqueExcaption;
import com.example.EmbarkXProject.Exceptions.exceptions.ResourceNotFoundException;
import com.example.EmbarkXProject.Model.Category;
import com.example.EmbarkXProject.Payload.Category.CategoryDTO;
import com.example.EmbarkXProject.Payload.Category.CategoryResponse;
import com.example.EmbarkXProject.Repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public ResponseEntity<String> addCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category newCategory = categoryRepository.findByCategoryName(category.getCategoryName());

        if(newCategory != null)
            throw new NotUniqueExcaption("Category with category name " + category.getCategoryName() + " already exists");

        categoryRepository.save(category);
        return new ResponseEntity<>("Success", HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<CategoryResponse> getCategoy(int pageNumber, int pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")?
                            Sort.by(sortBy).ascending():
                            Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);

        List<Category> categories = categoryPage.getContent();
        if(categories.isEmpty())
            throw new APIException("No Categories created till now");

        List<CategoryDTO> categoryDTOS = categories.stream().map(category -> modelMapper.map(category, CategoryDTO.class)).toList();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setLastPage(categoryPage.isLast());

        return new ResponseEntity<>(categoryResponse,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> updateCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);

        Category cc = categoryRepository.findById(category.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", category.getCategoryId()));


        cc.setCategoryName(category.getCategoryName());
        categoryRepository.save(cc);
        return new ResponseEntity<>("Updated", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> deleteCategory(long categoryId) {

        Category cc = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", categoryId));

        categoryRepository.deleteById(categoryId);
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }
}
