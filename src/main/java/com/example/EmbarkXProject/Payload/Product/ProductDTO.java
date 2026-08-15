package com.example.EmbarkXProject.Payload.Product;

import com.example.EmbarkXProject.Model.Category;
import lombok.Data;

@Data
public class ProductDTO {
    private Long productId;

    private String productName;
    private String description;
    private int quantity;
    private String image;
    private double price;
    private double discount;
    private double specialPrice;
    private Category category;

}
