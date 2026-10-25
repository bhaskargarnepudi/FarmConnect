package com.farmconnect.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrueOrderByIdAsc();

    List<Product> findByFarmerEmailIgnoreCaseOrderByIdDesc(String farmerEmail);

    @Query("select p from Product p where lower(p.farmerEmail) = lower(:email) " +
           "or (p.farmerEmail is null and lower(p.farmerName) = lower(:name)) " +
           "order by p.id desc")
    List<Product> findManageableByFarmer(@Param("email") String email, @Param("name") String name);
}
