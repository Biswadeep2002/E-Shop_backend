package com.example.EmbarkXProject.Service.Product;

import com.example.EmbarkXProject.Payload.Product.ProductDTO;
import com.example.EmbarkXProject.Payload.Product.ProductResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductServiceInterface {
    ResponseEntity<String> addProduct(ProductDTO productDTO, long categoryId);

    ResponseEntity<ProductResponse> getAllProducts(int pageNumber, int pageSize, String sortBy, String sortOrder, String keyword, String category);

    ResponseEntity<ProductResponse> getProductsByCategory(long categoryId, int pageNumber, int pageSize, String sortBy, String sortOrder);

//    ResponseEntity<ProductResponse> getProductsByKeyword(String keyword);

    ResponseEntity<ProductResponse> getProductsByKeyword(String keyword, int pageNumber, int pageSize, String sortBy, String sortOrder);

    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;

    ResponseEntity<ProductResponse> getAllProductsForAdmin(int pageNumber, int pageSize, String sortBy, String sortOrder);

    ProductDTO updateProduct(Long productId, ProductDTO productDTO);

    ProductDTO deleteProduct(Long productId);

    ResponseEntity<ProductResponse> getAllProductsForSeller(int pageNumber, int pageSize, String sortBy, String sortOrder);
}
