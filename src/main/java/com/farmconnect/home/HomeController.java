package com.farmconnect.home;

import com.farmconnect.product.Product;
import com.farmconnect.product.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    private final ProductService productService;

    public HomeController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Product> products = productService.getActiveProducts();
        model.addAttribute("products", products);
        model.addAttribute("productCount", products.size());
        return "index";
    }
}
