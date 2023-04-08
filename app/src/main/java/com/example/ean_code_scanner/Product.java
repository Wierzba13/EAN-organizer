package com.example.ean_code_scanner;

public class Product {
    private final String EANcode;
    private final String name;
    private final String price;

    public Product(String EANcode, String name, String price) {
        this.EANcode = EANcode;
        this.name = name;
        this.price = price;
    }

    public String getEANcode() {
        return EANcode;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }
}
