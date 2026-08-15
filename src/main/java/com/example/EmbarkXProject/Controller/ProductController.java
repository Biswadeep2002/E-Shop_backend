package com.example.EmbarkXProject.Controller;

import com.example.EmbarkXProject.Config.AppConstants;
import com.example.EmbarkXProject.Model.Category;
import com.example.EmbarkXProject.Model.Product;
import com.example.EmbarkXProject.Payload.Product.ProductDTO;
import com.example.EmbarkXProject.Payload.Product.ProductResponse;
import com.example.EmbarkXProject.Service.Product.ProductService;
import com.sun.net.httpserver.HttpsServer;
import jakarta.servlet.ServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("auth/product")
public class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping("add/{categoryId}")
    public ResponseEntity<String> addProduct(@RequestBody ProductDTO productDTO, @PathVariable long categoryId){
        return productService.addProduct(productDTO, categoryId);
    }

    @GetMapping("get")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortByProductId", defaultValue = AppConstants.PRODUCT_SORT_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder){
        return productService.getAllProducts(pageNumber,pageSize,sortBy,sortOrder, keyword, category);
    }

    @GetMapping("get/{categoryId}")
    public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable long categoryId,
                                                                 @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) int pageNumber,
                                                                 @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) int pageSize,
                                                                 @RequestParam(name = "sortByProductId", defaultValue = AppConstants.PRODUCT_SORT_BY, required = false) String sortBy,
                                                                 @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder){
        return productService.getProductsByCategory(categoryId,pageNumber,pageSize,sortBy,sortOrder);
    }

    @GetMapping("get/keyword/{keyword}")
    public ResponseEntity<ProductResponse> yup(@PathVariable String keyword,
                                               @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) int pageNumber,
                                               @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) int pageSize,
                                               @RequestParam(name = "sortByProductId", defaultValue = AppConstants.PRODUCT_SORT_BY, required = false) String sortBy,
                                               @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder) {
        return productService.getProductsByKeyword(keyword, pageNumber, pageSize, sortBy, sortOrder);
    }

    @PutMapping("/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId, @RequestParam("image")MultipartFile image) throws IOException {

        ProductDTO updatedProduct = productService.updateProductImage(productId, image);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }


    @GetMapping("/admin/get")
    public ResponseEntity<ProductResponse> getAllProductsForAdmin(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortByProductId", defaultValue = AppConstants.PRODUCT_SORT_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder){
        return productService.getAllProductsForAdmin (pageNumber,pageSize,sortBy,sortOrder);
    }


    @GetMapping("/seller/get")
    public ResponseEntity<ProductResponse> getAllProductsForSeller(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortByProductId", defaultValue = AppConstants.PRODUCT_SORT_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder){
        return productService.getAllProductsForSeller(pageNumber,pageSize,sortBy,sortOrder);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody ProductDTO productDTO,
                                                    @PathVariable Long productId){
        ProductDTO updatedProductDTO = productService.updateProduct(productId, productDTO);
        return new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId){
        ProductDTO deletedProduct = productService.deleteProduct(productId);
        return new ResponseEntity<>(deletedProduct, HttpStatus.OK);
    }

}
