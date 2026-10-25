package com.farmconnect.order;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "crop_orders")
public class CropOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private Long productId;
    @Column(nullable=false) private String productName;
    @Column(nullable=false) private String buyerEmail;
    @Column(nullable=false) private String buyerName;
    @Column(nullable=false) private String farmerName;
    private String farmerEmail;
    @Column(nullable=false) private int quantity;
    @Column(nullable=false, precision=12, scale=2) private BigDecimal unitPrice;
    @Column(nullable=false, precision=12, scale=2) private BigDecimal totalPrice;
    @Column(nullable=false) private String status = "PLACED";
    @Column(nullable=false, updatable=false) private LocalDateTime createdAt = LocalDateTime.now();
    public CropOrder() {}
    public Long getId(){return id;} public Long getProductId(){return productId;} public String getProductName(){return productName;} public String getBuyerEmail(){return buyerEmail;} public String getBuyerName(){return buyerName;} public String getFarmerName(){return farmerName;} public String getFarmerEmail(){return farmerEmail;} public int getQuantity(){return quantity;} public BigDecimal getUnitPrice(){return unitPrice;} public BigDecimal getTotalPrice(){return totalPrice;} public String getStatus(){return status;} public LocalDateTime getCreatedAt(){return createdAt;}
    public void setProductId(Long v){productId=v;} public void setProductName(String v){productName=v;} public void setBuyerEmail(String v){buyerEmail=v;} public void setBuyerName(String v){buyerName=v;} public void setFarmerName(String v){farmerName=v;} public void setFarmerEmail(String v){farmerEmail=v;} public void setQuantity(int v){quantity=v;} public void setUnitPrice(BigDecimal v){unitPrice=v;} public void setTotalPrice(BigDecimal v){totalPrice=v;} public void setStatus(String v){status=v;}
}
