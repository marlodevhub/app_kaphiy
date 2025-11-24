package com.marlodev.app_android.domain.model;

public class ProductImage {
    private int id;
    private String role;
    private String url;
    public ProductImage() {
    }
    public ProductImage(int id, String role, String url) {
        this.id = id;
        this.role = role;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
