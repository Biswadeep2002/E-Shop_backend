package com.example.EmbarkXProject.Service.Cart;

import com.example.EmbarkXProject.Payload.Cart.CartDTO;
import com.example.EmbarkXProject.Payload.Cart.CartItemDTO;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CartService {
    ResponseEntity<CartDTO> addToCart(long productId, int quantity);

    ResponseEntity<List<CartDTO>> getAllCarts();

    ResponseEntity<CartDTO> getCartOfLoggedInUser();

    @Transactional
    CartDTO updateCart(long productId, int quantity);

    String deleteProductFromCart(long cartId, long productId);

    String createOrUpdateCartWithItems(List<CartItemDTO> cartItemDTOS);

    void updateProductInCarts(Long cartId, Long productId);

    CartDTO getCart(String emailId, Long cartId);
}
