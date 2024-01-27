package com.example.autoamigos;

public class Car {
    private String brand_name;
    private String capacity;
    private int car_id;
    private String category_name;
    private String description;
    private String image_url;
    private String model;
    private int price;
    private String transmission;
    private int year;

    public Car() {
        // Default constructor required for Firebase
    }
    public Car(String brand_name, String capacity, int car_id, String category_name, String description, String image_url, String model, int price, String transmission, int year) {
        this.brand_name = brand_name;
        this.capacity = capacity;
        this.car_id = car_id;
        this.category_name = category_name;
        this.description = description;
        this.image_url = image_url;
        this.model = model;
        this.price = price;
        this.transmission = transmission;
        this.year = year;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public String getCapacity() {
        return capacity;
    }

    public int getCar_id() {
        return car_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getDescription() {
        return description;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getModel() {
        return model;
    }

    public int getPrice() {
        return price;
    }

    public String getTransmission() {
        return transmission;
    }

    public int getYear() {
        return year;
    }



}
