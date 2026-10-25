package com.farmconnect.order;
import com.farmconnect.buyer.Buyer;
import com.farmconnect.product.Product;
import com.farmconnect.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
@Service
public class OrderService {
    private final CropOrderRepository orders; private final ProductRepository products;
    public OrderService(CropOrderRepository orders, ProductRepository products) { this.orders=orders; this.products=products; }
    @Transactional
    public void placeOrder(Long productId, int quantity, Buyer buyer) {
        if (quantity < 1) throw new IllegalArgumentException("Quantity must be at least 1.");
        Product product = products.findById(productId).filter(Product::isActive).orElseThrow(() -> new IllegalArgumentException("Crop is no longer available."));
        if (quantity > product.getQuantityAvailable()) throw new IllegalArgumentException("Only " + product.getQuantityAvailable() + " units are available.");
        CropOrder order = new CropOrder(); order.setProductId(product.getId()); order.setProductName(product.getName()); order.setBuyerEmail(buyer.getEmail()); order.setBuyerName(buyer.getName()); order.setFarmerName(product.getFarmerName()); order.setFarmerEmail(product.getFarmerEmail()); order.setQuantity(quantity); order.setUnitPrice(product.getPrice()); order.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity))); order.setStatus("PLACED"); orders.save(order);
        product.setQuantityAvailable(product.getQuantityAvailable()-quantity); if (product.getQuantityAvailable()==0) product.setActive(false); products.save(product);
    }
    public List<CropOrder> buyerOrders(Buyer buyer) { return orders.findByBuyerEmailOrderByCreatedAtDesc(buyer.getEmail()); }
    public List<CropOrder> farmerOrders(String email) { return orders.findByFarmerEmailOrderByCreatedAtDesc(email); }
}
