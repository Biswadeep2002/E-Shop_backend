package com.example.EmbarkXProject.Controller;

import com.example.EmbarkXProject.Model.Cart;
import com.example.EmbarkXProject.Model.Users;
import com.example.EmbarkXProject.Payload.Cart.CartDTO;
import com.example.EmbarkXProject.Payload.Cart.CartItemDTO;
import com.example.EmbarkXProject.Repository.CartRepository;
import com.example.EmbarkXProject.Service.Cart.CartService;
import com.example.EmbarkXProject.Utill.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("auth/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    CartRepository cartRepository;

    @PostMapping("create")
    public ResponseEntity<String> createOrUpdateCart(@RequestBody List<CartItemDTO> cartItemDTOS){
        String response = cartService.createOrUpdateCartWithItems(cartItemDTOS);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("users/getCartById")
    public ResponseEntity<CartDTO> getCartById(){
        String emailId = authUtil.getLoggedInUserEmail();
        Cart cart = cartRepository.findCartByEmail(emailId);

        //For users cart not yet created
        if (cart == null) {
            cart = new Cart();
            cart.setUser(authUtil.getLoggedInUser());
            cart.setTotalPrice(0.0);

            cart = cartRepository.save(cart);
        }

        Long cartId = cart.getCartId();
        CartDTO cartDTO = cartService.getCart(emailId, cartId);
        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);
    }

    @PostMapping("addToCart/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable long productId, @PathVariable int quantity){
        return cartService.addToCart(productId, quantity);
    }

    @GetMapping("allCarts")
    public ResponseEntity<List<CartDTO>> getAllCarts(){
        return cartService.getAllCarts();
    }

    @GetMapping("userCart")
    public ResponseEntity<CartDTO> getCartOfLoggedInUser(){
        return cartService.getCartOfLoggedInUser();
    }

    @GetMapping("user")
    public ResponseEntity<Users> getCurrentUser(){
        Users user = authUtil.getLoggedInUser();
        return new ResponseEntity<>(user, HttpStatus.FOUND);
    }

    @PutMapping("product/{productId}/operation/{operation}")
    public ResponseEntity<CartDTO> updateCart(@PathVariable long productId, @PathVariable String operation){
        CartDTO cartDTO = cartService.updateCart(productId, operation.equalsIgnoreCase("delete") ? -1 : 1);

        return new ResponseEntity<>(cartDTO, HttpStatus.FOUND);
    }

    @DeleteMapping("{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable long cartId,
                                                        @PathVariable long productId){
        String status = cartService.deleteProductFromCart(cartId, productId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
