package com.farmconnect.support;

import com.farmconnect.farmer.Farmer;
import com.farmconnect.farmer.FarmerRepository;
import com.farmconnect.order.CropOrder;
import com.farmconnect.order.CropOrderRepository;
import com.farmconnect.product.Product;
import com.farmconnect.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Backfills contact emails only when a legacy crop name matches exactly one registered farmer. */
@Service
public class LegacyContactRepairService {
    private final FarmerRepository farmers;
    private final ProductRepository products;
    private final CropOrderRepository orders;

    public LegacyContactRepairService(FarmerRepository farmers, ProductRepository products, CropOrderRepository orders) {
        this.farmers = farmers;
        this.products = products;
        this.orders = orders;
    }

    @Transactional
    public void repairLegacyContacts() {
        List<Farmer> registeredFarmers = farmers.findAll();
        Map<String, List<Farmer>> farmersByName = registeredFarmers.stream()
                .collect(Collectors.groupingBy(f -> normalize(f.getName())));

        List<Product> allProducts = products.findAll();
        boolean productsChanged = false;
        for (Product product : allProducts) {
            if (product.getFarmerEmail() != null && !product.getFarmerEmail().isBlank()) continue;
            List<Farmer> matches = farmersByName.getOrDefault(normalize(product.getFarmerName()), List.of());
            if (matches.size() == 1) {
                product.setFarmerEmail(matches.get(0).getEmail());
                productsChanged = true;
            }
        }
        if (productsChanged) products.saveAll(allProducts);

        Map<Long, Product> productsById = allProducts.stream()
                .filter(p -> p.getId() != null)
                .collect(Collectors.toMap(Product::getId, Function.identity()));
        List<CropOrder> allOrders = orders.findAll();
        boolean ordersChanged = false;
        for (CropOrder order : allOrders) {
            if (order.getFarmerEmail() != null && !order.getFarmerEmail().isBlank()) continue;
            Product product = productsById.get(order.getProductId());
            if (product != null && product.getFarmerEmail() != null && !product.getFarmerEmail().isBlank()) {
                order.setFarmerEmail(product.getFarmerEmail());
                ordersChanged = true;
            }
        }
        if (ordersChanged) orders.saveAll(allOrders);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
