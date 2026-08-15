package com.example.EmbarkXProject.Service.Cart;

import com.example.EmbarkXProject.Model.Users;
import com.example.EmbarkXProject.Payload.Cart.CartItemDTO;
import com.example.EmbarkXProject.Utill.AuthUtil;
import com.example.EmbarkXProject.Exceptions.exceptions.APIException;
import com.example.EmbarkXProject.Exceptions.exceptions.ResourceNotFoundException;
import com.example.EmbarkXProject.Model.Cart;
import com.example.EmbarkXProject.Model.CartItem;
import com.example.EmbarkXProject.Model.Product;
import com.example.EmbarkXProject.Payload.Cart.CartDTO;
import com.example.EmbarkXProject.Payload.Product.ProductDTO;
import com.example.EmbarkXProject.Repository.CartItemRepository;
import com.example.EmbarkXProject.Repository.CartRepository;
import com.example.EmbarkXProject.Repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public ResponseEntity<CartDTO> addToCart(long productId, int quantity) {
        Cart cart = createCart();

        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "Product Id", productId));

        CartItem newCartItem = cartItemRepository.findByCartIdAndProductId(cart.getCartId(),productId);
        if(newCartItem != null)
            throw new APIException("Product with Product Id " + productId + " already exists");

        if(product.getQuantity() <= 0)
            throw new APIException("Product not available");

        if(product.getQuantity() < quantity)
            throw new APIException("Please order the amount greater or equals to " + product.getQuantity());

        CartItem cartItem = new CartItem();

        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setDiscount(product.getDiscount());
        cartItem.setProductPrize(product.getSpecialPrice());

        cartItemRepository.save(cartItem);

        cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrize() * quantity));

        cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productDTOStream = cartItems.stream().map(item -> {
            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            map.setQuantity(item.getQuantity());
            return map;
        });

        cartDTO.setProducts(productDTOStream.toList());

        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<CartDTO>> getAllCarts() {

        List<Cart> carts = cartRepository.findAll();
        if(carts.isEmpty())
            throw new APIException("No cart exists");

        List<CartDTO> cartDTOS = carts.stream()
                .map(cart -> {
                    CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
                    cart.getCartItems().forEach(c -> c.getProduct().setQuantity(c.getQuantity()));
                    List<ProductDTO> productDTOS = cart.getCartItems().stream()
                            .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class))
                            .toList();
                    cartDTO.setProducts(productDTOS);
                    return cartDTO;
                }).toList();


        return new ResponseEntity<>(cartDTOS, HttpStatus.FOUND);
    }

    @Override
    public ResponseEntity<CartDTO> getCartOfLoggedInUser() {
        String emailId = authUtil.getLoggedInUserEmail();

        Cart cart = cartRepository.findCartByEmail(emailId);
        if(cart == null)
            throw new APIException("No Cart found");

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        cart.getCartItems().forEach(c -> c.getProduct().setQuantity(c.getQuantity()));
        List<ProductDTO> productDTOS = cart.getCartItems().stream()
                .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).toList();

        cartDTO.setProducts(productDTOS);
        return new ResponseEntity<>(cartDTO, HttpStatus.FOUND);
    }

    @Transactional
    @Override
    public CartDTO updateCart(long productId, int quantity) {
        String email = authUtil.getLoggedInUserEmail();
        Cart UserCart = cartRepository.findCartByEmail(email);
        long cartId = UserCart.getCartId();

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "Cart Id", cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new APIException("Product Not Available"));

        if(product.getQuantity() <= 0)
            throw new APIException("Product not available");

        if(product.getQuantity() < quantity)
            throw new APIException("Please order the amount greater or equals to " + product.getQuantity());

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cartId, productId);
        if(cartItem == null)
            throw new APIException("Product" + product.getProductName() + " Does not exist");

        cartItem.setProductPrize(product.getSpecialPrice());
        cartItem.setQuantity(cartItem.getQuantity() * quantity);
        cartItem.setDiscount(product.getDiscount());
        cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrize() * quantity));

        cartRepository.save(cart);
        CartItem updatedItem = cartItemRepository.save(cartItem);
        if(updatedItem.getQuantity() == 0)
            cartItemRepository.deleteById(updatedItem.getCartItemId());

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productDTOStream = cartItems.stream().map(item -> {
            ProductDTO prd = modelMapper.map(item.getProduct(), ProductDTO.class);
            prd.setQuantity(item.getQuantity());
            return prd;
        });

        cartDTO.setProducts(productDTOStream.toList());
        return null;
    }

    @Transactional
    @Override
    public String deleteProductFromCart(long cartId, long productId) {

        Cart cart = cartRepository.findById(cartId).
                orElseThrow(() -> new ResourceNotFoundException("Cart", "Cart Id", cartId));

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cartId, productId);
        if(cartItem == null)
            throw new ResourceNotFoundException("Product", "ProductId", productId);

        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrize() * cartItem.getQuantity()));

        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);

        return "Product" + cartItem.getProduct().getProductName() + " is removed from the cart";
    }

    @Transactional
    @Override
    public String createOrUpdateCartWithItems(List<CartItemDTO> cartItemDTOS) {
        String emailId = authUtil.getLoggedInUserEmail();
        Cart existingCart = cartRepository.findCartByEmail(emailId);

        if(existingCart == null){
            existingCart = new Cart();
            existingCart.setTotalPrice(0.0);
            existingCart.setUser(authUtil.getLoggedInUser());
            existingCart = cartRepository.save(existingCart);
        }else{
            cartItemRepository.deleteAllByCartId(existingCart.getCartId());
        }

        double totalPrice = 0.00;

        for(CartItemDTO cartItemDTO : cartItemDTOS){
            Long productId = cartItemDTO.getProductId();
            Integer quantity = cartItemDTO.getQuantity();
            Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "Product Id", productId));
            product.setQuantity(product.getQuantity() - quantity);
            totalPrice += product.getSpecialPrice() * quantity;

            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCart(existingCart);
            cartItem.setQuantity(quantity);
            cartItem.setProductPrize(product.getSpecialPrice());
            cartItem.setDiscount(product.getDiscount());
            cartItemRepository.save(cartItem);
        }
        existingCart.setTotalPrice(totalPrice);
        cartRepository.save(existingCart);
        return "Cart created/updated with the new items successfully";

    }


    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cartId, productId);

        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " not available in the cart!!!");
        }

        double cartPrice = cart.getTotalPrice()
                - (cartItem.getProductPrize() * cartItem.getQuantity());

        cartItem.setProductPrize(product.getSpecialPrice());

        cart.setTotalPrice(cartPrice
                + (cartItem.getProductPrize() * cartItem.getQuantity()));

        cartItem = cartItemRepository.save(cartItem);
    }


    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);
        if (cart == null){
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        }
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        cart.getCartItems().forEach(c ->
                c.getProduct().setQuantity(c.getQuantity()));
        List<ProductDTO> products = cart.getCartItems().stream()
                .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class))
                .toList();
        cartDTO.setProducts(products);
        return cartDTO;
    }

    private Cart createCart(){
        Cart userCart = cartRepository.findCartByEmail(authUtil.getLoggedInUserEmail());

        if(userCart != null)
            return userCart;

        Cart cart = new Cart();
        cart.setTotalPrice(0.0);
        cart.setUser(authUtil.getLoggedInUser());
        return cartRepository.save(cart);
    }
}
