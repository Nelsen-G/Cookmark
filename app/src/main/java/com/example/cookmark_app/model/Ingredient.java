package com.example.cookmark_app.model;

import java.io.Serializable;

public class Ingredient implements Serializable {
    private String name;

    public Ingredient(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
