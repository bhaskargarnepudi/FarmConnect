package com.farmconnect.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String category;

    @NotBlank
    @Column(nullable = false, length = 1200)
    private String description;

    @NotNull
    @DecimalMin("0.01")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotBlank
    @Column(nullable = false)
    private String unit;

    @Min(0)
    @Column(nullable = false)
    private int quantityAvailable;

    @NotBlank
    @Column(nullable = false)
    private String quality;

    @NotBlank
    @Column(nullable = false)
    private String farmerName;

    @NotBlank
    @Column(nullable = false)
    private String location;

    @Column(length = 1000)
    private String imageUrl;

    @Column(name = "farmer_email")
    private String farmerEmail;

    @Column(nullable = false)
    private boolean active = true;

    protected Product() {}

    public Product(String name, String category, String description, BigDecimal price,
                   String unit, int quantityAvailable, String quality, String farmerName,
                   String location, String imageUrl) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.unit = unit;
        this.quantityAvailable = quantityAvailable;
        this.quality = quality;
        this.farmerName = farmerName;
        this.location = location;
        this.imageUrl = imageUrl;
        this.active = true;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public String getUnit() { return unit; }
    public int getQuantityAvailable() { return quantityAvailable; }
    public String getQuality() { return quality; }
    public String getFarmerName() { return farmerName; }
    public String getLocation() { return location; }
    public String getImageUrl() { return imageUrl; }
    public boolean isActive() { return active; }
    public String getFarmerEmail() { return farmerEmail; }

    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setQuantityAvailable(int quantityAvailable) { this.quantityAvailable = quantityAvailable; }
    public void setQuality(String quality) { this.quality = quality; }
    public void setLocation(String location) { this.location = location; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setFarmerEmail(String farmerEmail) { this.farmerEmail = farmerEmail; }
    public void setActive(boolean active) { this.active = active; }
}
