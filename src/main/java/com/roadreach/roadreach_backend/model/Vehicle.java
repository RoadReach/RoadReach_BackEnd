package com.roadreach.roadreach_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    private String id;

    private String city; // <-- Added column

    private String type;
    private String company;
    private int price;
    private int passengers;
    private int bags;
    private String transmission;
    private boolean eco;
    private boolean luxury;
    private String imageUrl;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public int getPassengers() { return passengers; }
    public void setPassengers(int passengers) { this.passengers = passengers; }

    public int getBags() { return bags; }
    public void setBags(int bags) { this.bags = bags; }

    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }

    public boolean isEco() { return eco; }
    public void setEco(boolean eco) { this.eco = eco; }

    public boolean isLuxury() { return luxury; }
    public void setLuxury(boolean luxury) { this.luxury = luxury; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
