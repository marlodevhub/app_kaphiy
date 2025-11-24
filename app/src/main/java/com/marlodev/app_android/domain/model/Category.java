package com.marlodev.app_android.domain.model;

public class Category {
    private int id;
    private String title;

    public Category( ) {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
