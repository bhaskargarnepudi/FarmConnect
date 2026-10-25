package com.farmconnect.order;
import com.farmconnect.buyer.Buyer;
import com.farmconnect.farmer.Farmer;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
@RequestMapping("/buyer/orders")
public class OrderController {
    private final OrderService service; private final com.farmconnect.product.ProductRepository products;
    public OrderController(OrderService service, com.farmconnect.product.ProductRepository products){this.service=service;this.products=products;}
    @GetMapping("/new") public String newOrder(@RequestParam Long productId, Model model){ model.addAttribute("product",products.findById(productId).filter(com.farmconnect.product.Product::isActive).orElseThrow(() -> new IllegalArgumentException("Crop unavailable"))); return "place-order"; }
    @PostMapping public String placeOrder(@RequestParam Long productId,@RequestParam int quantity,@AuthenticationPrincipal Buyer buyer,RedirectAttributes ra){ try{service.placeOrder(productId,quantity,buyer);ra.addFlashAttribute("successMessage","Order placed successfully!");}catch(IllegalArgumentException ex){ra.addFlashAttribute("errorMessage",ex.getMessage());} return "redirect:/buyer/dashboard"; }
}
