package com.farmconnect.order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CropOrderRepository extends JpaRepository<CropOrder, Long> {
    List<CropOrder> findByBuyerEmailOrderByCreatedAtDesc(String buyerEmail);
    List<CropOrder> findByFarmerEmailOrderByCreatedAtDesc(String farmerEmail);
}
