package com.example.EmbarkXProject.Payload.Cart;

import com.example.EmbarkXProject.Payload.Product.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private long cartId;
    private double totalPrice = 0.0;
    private List<ProductDTO> products = new ArrayList<>();

//    public CartDTO(long cartId, double totalPrice, List<ProductDTO> productDTOS) {
//        this.cartId = cartId;
//        this.totalPrice = totalPrice;
//        this.productDTOS = productDTOS;
//    }
//
//    public CartDTO() {
//    }
//
//    public long getCartId() {
//        return cartId;
//    }
//
//    public void setCartId(long cartId) {
//        this.cartId = cartId;
//    }
//
//    public double getTotalPrice() {
//        return totalPrice;
//    }
//
//    public void setTotalPrice(double totalPrice) {
//        this.totalPrice = totalPrice;
//    }
//
//    public List<ProductDTO> getProductDTOS() {
//        return productDTOS;
//    }
//
//    public void setProductDTOS(List<ProductDTO> productDTOS) {
//        this.productDTOS = productDTOS;
//    }
}
