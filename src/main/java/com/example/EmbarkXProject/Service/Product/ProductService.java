//package com.example.EmbarkXProject.Service.Product;
//
//import com.example.EmbarkXProject.Exceptions.exceptions.APIException;
//import com.example.EmbarkXProject.Exceptions.exceptions.ResourceNotFoundException;
//import com.example.EmbarkXProject.Model.Cart;
//import com.example.EmbarkXProject.Model.Category;
//import com.example.EmbarkXProject.Model.Product;
//import com.example.EmbarkXProject.Model.Users;
//import com.example.EmbarkXProject.Payload.Cart.CartDTO;
//import com.example.EmbarkXProject.Payload.Product.ProductDTO;
//import com.example.EmbarkXProject.Payload.Product.ProductResponse;
//import com.example.EmbarkXProject.Repository.CartRepository;
//import com.example.EmbarkXProject.Repository.CategoryRepository;
//import com.example.EmbarkXProject.Repository.ProductRepository;
//import com.example.EmbarkXProject.Service.Cart.CartService;
//import com.example.EmbarkXProject.Service.File.FileService;
//import com.example.EmbarkXProject.Utill.AuthUtil;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Service
//public class ProductService implements ProductServiceInterface {
//
//    @Autowired
//    ProductRepository productRepository;
//    @Autowired
//    CategoryRepository categoryRepository;
//
//    @Autowired
//    ModelMapper modelMapper;
//
//    @Autowired
//    AuthUtil authUtil;
//
//    @Autowired
//    CartService cartService;
//
//    @Autowired
//    FileService fileService;
//
//    @Autowired
//    CartRepository cartRepository;
//
//    @Value("${project.image}")
//    private String path;
//
//    @Value("${image.base.url}")
//    private String imageBaseUrl;
//
//
//    @Override
//    public ResponseEntity<String> addProduct(ProductDTO productDTO, long categoryId) {
//        Product product = modelMapper.map(productDTO, Product.class);
//
//        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", categoryId));
//
//        product.setCategory(category);
//        product.setUser(authUtil.getLoggedInUser());
//        product.setSpecialPrice(product.getPrice() - (product.getPrice() * product.getDiscount()) / 100);
//
//        productRepository.save(product);
//        return new ResponseEntity<>("Success", HttpStatus.CREATED);
//    }
//
//    @Override
//    public ResponseEntity<ProductResponse> getAllProducts(int pageNumber, int pageSize, String sortBy, String sortOrder, String keyword, String category) {
//        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
//                Sort.by(sortBy).ascending() :
//                Sort.by(sortBy).descending();
//
//
//        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
//
//        Specification<Product> spec = Specification.where(null);
//        if (keyword != null && !keyword.isEmpty())
//            spec = spec.and(((root, query, criteriaBuilder) ->
//                    criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")), "%" + keyword.toLowerCase() + "%")));
//
//        if (category != null && !category.isEmpty())
//            spec = spec.and(((root, query, criteriaBuilder) ->
//                    criteriaBuilder.like(root.get("category").get("categoryName"), category)));
//
//        Page<Product> productsPage = productRepository.findAll(spec, pageDetails);
//        List<Product> products = productsPage.getContent();
//
//        if (products.isEmpty())
//            throw new APIException("No Products present");
//
//        List<ProductDTO> productDTOS = products.stream().map(product -> {
//            ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
//            productDTO.setImage(constructImageUrl(product.getImage()));
//            return productDTO;
//        }).toList();
//
//
//        ProductResponse productResponse = new ProductResponse();
//        productResponse.setContent(productDTOS);
//        productResponse.setPageNumber(productsPage.getNumber());
//        productResponse.setPageSize(productsPage.getSize());
//        productResponse.setTotalPages(productsPage.getTotalPages());
//        productResponse.setTotalElements(productsPage.getTotalElements());
//        productResponse.setLastPage(productsPage.isLast());
//
//        return new ResponseEntity<>(productResponse, HttpStatus.FOUND);
//    }
//
//    private String constructImageUrl(String imageName) {
//        return imageBaseUrl.endsWith("/") ? imageBaseUrl + imageName : imageBaseUrl + "/" + imageName;
//    }
//
//    @Override
//    public ResponseEntity<ProductResponse> getProductsByCategory(long categoryId, int pageNumber, int pageSize, String sortBy, String sortOrder) {
//        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));
//
//        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
//                Sort.by(sortBy).ascending() :
//                Sort.by(sortBy).descending();
//
//
//        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
//
//        Page<Product> productsPage = productRepository.findByCategory(category, pageDetails);
//        List<Product> products = productsPage.getContent();
////        List<Product> products = productRepository.
//
//        List<ProductDTO> productDTO = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();
//        ProductResponse productResponse = new ProductResponse();
//        productResponse.setContent(productDTO);
//
//        productResponse.setPageNumber(productsPage.getNumber());
//        productResponse.setPageSize(productsPage.getSize());
//        productResponse.setTotalPages(productsPage.getTotalPages());
//        productResponse.setTotalElements(productsPage.getTotalElements());
//        productResponse.setLastPage(productsPage.isLast());
//
//
//        return new ResponseEntity<>(productResponse, HttpStatus.FOUND);
//    }
//
//
//    @Override
//    public ResponseEntity<ProductResponse> getProductsByKeyword(String keyword, int pageNumber, int pageSize, String sortBy, String sortOrder) {
//
//
//        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
//                Sort.by(sortBy).ascending() :
//                Sort.by(sortBy).descending();
//
//
//        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
//        Page<Product> products = productRepository.findByProductNameLikeIgnoreCase("%" + keyword + "%", pageDetails);
//        if (products.isEmpty())
//            throw new APIException("No Product with the Keyword " + keyword);
//
////        Page<Product> productsPage = productRepository.findByCategory(category, pageDetails);
////        List<Product> products = page.getContent();
//
//        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();
//        ProductResponse productResponse = new ProductResponse();
//        productResponse.setContent(productDTOS);
//
//        productResponse.setPageNumber(products.getNumber());
//        productResponse.setPageSize(products.getSize());
//        productResponse.setTotalPages(products.getTotalPages());
//        productResponse.setTotalElements(products.getTotalElements());
//        productResponse.setLastPage(products.isLast());
//
//        return new ResponseEntity<>(productResponse, HttpStatus.FOUND);
//    }
//
//    @Override
//    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
//
//        //Get the Product from the DB
//        Product productFromDB = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "Product ID", productId));
//
//        //Upload image to the Server
//        //Get the file name of uploaded image
//        String fileName = fileService.uploadImage(path, image);
//
//        //Updating the new file name to the product
//        productFromDB.setImage(fileName);
//
//        //Save updated product
//        Product updatedProduct = productRepository.save(productFromDB);
//
//        //Return DTO after mapping product to the DTO
//        return modelMapper.map(updatedProduct, ProductDTO.class);
//
//    }
//
//    @Override
//    public ResponseEntity<ProductResponse> getAllProductsForAdmin(int pageNumber, int pageSize, String sortBy, String sortOrder) {
//        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
//                Sort.by(sortBy).ascending() :
//                Sort.by(sortBy).descending();
//
//
//        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
//
//        Page<Product> productsPage = productRepository.findAll(pageDetails);
//        List<Product> products = productsPage.getContent();
//
//        if (products.isEmpty())
//            throw new APIException("No Products present");
//
//        List<ProductDTO> productDTOS = products.stream().map(product -> {
//            ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
//            productDTO.setImage(constructImageUrl(product.getImage()));
//            return productDTO;
//        }).toList();
//
//
//        ProductResponse productResponse = new ProductResponse();
//        productResponse.setContent(productDTOS);
//        productResponse.setPageNumber(productsPage.getNumber());
//        productResponse.setPageSize(productsPage.getSize());
//        productResponse.setTotalPages(productsPage.getTotalPages());
//        productResponse.setTotalElements(productsPage.getTotalElements());
//        productResponse.setLastPage(productsPage.isLast());
//
//        return new ResponseEntity<>(productResponse, HttpStatus.FOUND);
//
//    }
//
//
//    @Override
//    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
//        Product productFromDb = productRepository.findById(productId)
//                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
//
//        Product product = modelMapper.map(productDTO, Product.class);
//
//        productFromDb.setProductName(product.getProductName());
//        productFromDb.setDescription(product.getDescription());
//        productFromDb.setQuantity(product.getQuantity());
//        productFromDb.setDiscount(product.getDiscount());
//        productFromDb.setPrice(product.getPrice());
//        productFromDb.setSpecialPrice(product.getSpecialPrice());
//
//        Product savedProduct = productRepository.save(productFromDb);
//
//        List<Cart> carts = cartRepository.findCartsByProductId(productId);
//
//        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
//            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
//
//            List<ProductDTO> products = cart.getCartItems().stream()
//                    .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());
//
//            cartDTO.setProducts(products);
//
//            return cartDTO;
//
//        }).collect(Collectors.toList());
//
//        cartDTOs.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));
//
//        return modelMapper.map(savedProduct, ProductDTO.class);
//    }
//
//    @Override
//    public ProductDTO deleteProduct(Long productId) {
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
//
//        // DELETE
//        List<Cart> carts = cartRepository.findCartsByProductId(productId);
//        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));
//
//        productRepository.delete(product);
//        return modelMapper.map(product, ProductDTO.class);
//    }
//
//    @Override
//    public ResponseEntity<ProductResponse> getAllProductsForSeller(int pageNumber, int pageSize, String sortBy, String sortOrder) {
//        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
//                Sort.by(sortBy).ascending() :
//                Sort.by(sortBy).descending();
//
//
//        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
//
//        Users user = authUtil.getLoggedInUser();
//        Page<Product> productsPage = productRepository.findByUser(user, pageDetails);
//        List<Product> products = productsPage.getContent();
//
//        if (products.isEmpty())
//            throw new APIException("No Products present");
//
//        List<ProductDTO> productDTOS = products.stream().map(product -> {
//            ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
//            productDTO.setImage(constructImageUrl(product.getImage()));
//            return productDTO;
//        }).toList();
//
//
//        ProductResponse productResponse = new ProductResponse();
//        productResponse.setContent(productDTOS);
//        productResponse.setPageNumber(productsPage.getNumber());
//        productResponse.setPageSize(productsPage.getSize());
//        productResponse.setTotalPages(productsPage.getTotalPages());
//        productResponse.setTotalElements(productsPage.getTotalElements());
//        productResponse.setLastPage(productsPage.isLast());
//
//        return new ResponseEntity<>(productResponse, HttpStatus.FOUND);
//
//    }
//
//}













package com.example.EmbarkXProject.Service.Product;

import com.example.EmbarkXProject.Exceptions.exceptions.APIException;
import com.example.EmbarkXProject.Exceptions.exceptions.ResourceNotFoundException;
import com.example.EmbarkXProject.Model.Cart;
import com.example.EmbarkXProject.Model.Category;
import com.example.EmbarkXProject.Model.Product;
import com.example.EmbarkXProject.Model.Users;
import com.example.EmbarkXProject.Payload.Cart.CartDTO;
import com.example.EmbarkXProject.Payload.Product.ProductDTO;
import com.example.EmbarkXProject.Payload.Product.ProductResponse;
import com.example.EmbarkXProject.Repository.CartRepository;
import com.example.EmbarkXProject.Repository.CategoryRepository;
import com.example.EmbarkXProject.Repository.ProductRepository;
import com.example.EmbarkXProject.Service.Cart.CartService;
import com.example.EmbarkXProject.Service.File.FileService;
import com.example.EmbarkXProject.Utill.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService implements ProductServiceInterface {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    CartService cartService;

    @Autowired
    FileService fileService;

    @Autowired
    CartRepository cartRepository;

//    @Value("${project.image}")
//    private String path;
//
//    @Value("${image.base.url}")
//    private String imageBaseUrl;


    @Override
    public ResponseEntity<String> addProduct(ProductDTO productDTO, long categoryId) {
        Product product = modelMapper.map(productDTO, Product.class);

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", categoryId));

        product.setCategory(category);
        product.setUser(authUtil.getLoggedInUser());
        product.setSpecialPrice(product.getPrice() - (product.getPrice() * product.getDiscount()) / 100);

        productRepository.save(product);
        return new ResponseEntity<>("Success", HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ProductResponse> getAllProducts(int pageNumber, int pageSize, String sortBy, String sortOrder, String keyword, String category) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();


        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Specification<Product> spec = Specification.where(null);
        if (keyword != null && !keyword.isEmpty())
            spec = spec.and(((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")), "%" + keyword.toLowerCase() + "%")));

        if (category != null && !category.isEmpty())
            spec = spec.and(((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("category").get("categoryName"), category)));

        Page<Product> productsPage = productRepository.findAll(spec, pageDetails);
        List<Product> products = productsPage.getContent();

        if (products.isEmpty())
            throw new APIException("No Products present");

        List<ProductDTO> productDTOS = products.stream().map(product -> {
            ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
//            productDTO.setImage(constructImageUrl(product.getImage()));
            productDTO.setImage(product.getImage());
            return productDTO;
        }).toList();


        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productsPage.getNumber());
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setLastPage(productsPage.isLast());

        return new ResponseEntity<>(productResponse, HttpStatus.FOUND);
    }

//    private String constructImageUrl(String imageName) {
//        return imageBaseUrl.endsWith("/") ? imageBaseUrl + imageName : imageBaseUrl + "/" + imageName;
//    }

    @Override
    public ResponseEntity<ProductResponse> getProductsByCategory(long categoryId, int pageNumber, int pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();


        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Product> productsPage = productRepository.findByCategory(category, pageDetails);
        List<Product> products = productsPage.getContent();
//        List<Product> products = productRepository.

        List<ProductDTO> productDTO = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTO);

        productResponse.setPageNumber(productsPage.getNumber());
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setLastPage(productsPage.isLast());


        return new ResponseEntity<>(productResponse, HttpStatus.FOUND);
    }


    @Override
    public ResponseEntity<ProductResponse> getProductsByKeyword(String keyword, int pageNumber, int pageSize, String sortBy, String sortOrder) {


        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();


        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> products = productRepository.findByProductNameLikeIgnoreCase("%" + keyword + "%", pageDetails);
        if (products.isEmpty())
            throw new APIException("No Product with the Keyword " + keyword);

//        Page<Product> productsPage = productRepository.findByCategory(category, pageDetails);
//        List<Product> products = page.getContent();

        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);

        productResponse.setPageNumber(products.getNumber());
        productResponse.setPageSize(products.getSize());
        productResponse.setTotalPages(products.getTotalPages());
        productResponse.setTotalElements(products.getTotalElements());
        productResponse.setLastPage(products.isLast());

        return new ResponseEntity<>(productResponse, HttpStatus.FOUND);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {

        // Validate uploaded file
        if (image == null || image.isEmpty()) {
            throw new APIException("Please select an image to upload.");
        }

        if (image.getContentType() == null ||
                !image.getContentType().startsWith("image/")) {
            throw new APIException("Only image files are allowed.");
        }

        if (image.getSize() > 5 * 1024 * 1024) { // 5 MB
            throw new APIException("Image size must not exceed 5 MB.");
        }

        String contentType = image.getContentType();

        if (contentType == null ||
                !(contentType.equals("image/jpeg")
                        || contentType.equals("image/png")
                        || contentType.equals("image/jpg"))) {

            throw new APIException("Only JPG and PNG images are allowed.");
        }

        //Get the Product from the DB
        Product productFromDB = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "Product ID", productId));

        //Upload image to the Server
        //Get the file name of uploaded image
        String imageUrl = fileService.uploadImage(image);

        //Updating the new file name to the product
        productFromDB.setImage(imageUrl);

        //Save updated product
        Product updatedProduct = productRepository.save(productFromDB);

        //Return DTO after mapping product to the DTO
        return modelMapper.map(updatedProduct, ProductDTO.class);

    }

    @Override
    public ResponseEntity<ProductResponse> getAllProductsForAdmin(int pageNumber, int pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();


        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Product> productsPage = productRepository.findAll(pageDetails);
        List<Product> products = productsPage.getContent();

        if (products.isEmpty())
            throw new APIException("No Products present");

        List<ProductDTO> productDTOS = products.stream().map(product -> {
            ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
//            productDTO.setImage(constructImageUrl(product.getImage()));
            return productDTO;
        }).toList();


        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productsPage.getNumber());
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setLastPage(productsPage.isLast());

        return new ResponseEntity<>(productResponse, HttpStatus.FOUND);

    }


    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        Product product = modelMapper.map(productDTO, Product.class);

        productFromDb.setProductName(product.getProductName());
        productFromDb.setDescription(product.getDescription());
        productFromDb.setQuantity(product.getQuantity());
        productFromDb.setDiscount(product.getDiscount());
        productFromDb.setPrice(product.getPrice());
        productFromDb.setSpecialPrice(product.getSpecialPrice());

        Product savedProduct = productRepository.save(productFromDb);

        List<Cart> carts = cartRepository.findCartsByProductId(productId);

        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

            List<ProductDTO> products = cart.getCartItems().stream()
                    .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());

            cartDTO.setProducts(products);

            return cartDTO;

        }).collect(Collectors.toList());

        cartDTOs.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));

        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // DELETE
        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));

        productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public ResponseEntity<ProductResponse> getAllProductsForSeller(int pageNumber, int pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();


        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Users user = authUtil.getLoggedInUser();
        Page<Product> productsPage = productRepository.findByUser(user, pageDetails);
        List<Product> products = productsPage.getContent();

        if (products.isEmpty())
            throw new APIException("No Products present");

        List<ProductDTO> productDTOS = products.stream().map(product -> {
            ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
            productDTO.setImage(product.getImage());
//            productDTO.setImage(constructImageUrl(product.getImage()));
            return productDTO;
        }).toList();


        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productsPage.getNumber());
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setLastPage(productsPage.isLast());

        return new ResponseEntity<>(productResponse, HttpStatus.FOUND);

    }

}

