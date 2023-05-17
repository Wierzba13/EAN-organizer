package com.example.ean_code_scanner;

public class Product {
    private final String EANcode;
    private final String name;
    private final String price;
    private final String additionalComments;

    public Product(String EANcode, String name, String price, String additionalComments) {
        this.EANcode = EANcode;
        this.name = name;
        this.price = price;
        this.additionalComments = additionalComments;
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

    public String getAdditionalComments() { return additionalComments; }
}
