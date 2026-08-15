package com.example.EmbarkXProject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.yaml.snakeyaml.events.Event;

@Entity
@Data
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long cartItemId;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;
    private double discount;
    private double productPrize;

    public CartItem() {
    }
}

