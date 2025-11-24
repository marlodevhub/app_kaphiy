package com.marlodev.app_android.data.network.model.order;

import com.marlodev.app_android.data.network.model.product.ProductResponse;

public class CartItem {

    private Long id;
    private ProductResponse product;
    private int quantity;


   public CartItem(Long id, ProductResponse product, int quantity) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
    }

    // getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ProductResponse getProduct() { return product; }
    public void setProduct(ProductResponse product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }


}