package com.farmconnect.auth;

import com.farmconnect.buyer.Buyer;
import com.farmconnect.buyer.BuyerService;
import com.farmconnect.farmer.Farmer;
import com.farmconnect.farmer.FarmerService;
import com.farmconnect.order.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    private final FarmerService farmerService; private final BuyerService buyerService; private final OrderService orderService;
    public AuthController(FarmerService farmerService, BuyerService buyerService, OrderService orderService) { this.farmerService=farmerService; this.buyerService=buyerService; this.orderService=orderService; }
    @GetMapping("/register") public String showRegistrationPage() { return "register"; }
    @PostMapping("/register") public String registerFarmer(@RequestParam String name, @RequestParam String email, @RequestParam String phone, @RequestParam String password, RedirectAttributes ra) {
        try { farmerService.register(name,email,phone,password); ra.addFlashAttribute("successMessage","Registration successful! Please log in."); return "redirect:/login?registered"; }
        catch (IllegalArgumentException ex) { ra.addFlashAttribute("errorMessage",ex.getMessage()); return "redirect:/register"; }
    }
    @GetMapping("/login") public String showLoginPage() { return "login"; }
    @GetMapping("/farmer/dashboard") public String farmerDashboard(@AuthenticationPrincipal Farmer farmer, Model model) {
        model.addAttribute("farmerName",farmer.getName()); model.addAttribute("farmerEmail",farmer.getEmail());
        model.addAttribute("farmerOrders",orderService.farmerOrders(farmer.getEmail())); return "farmer-dashboard";
    }
    @GetMapping("/buyer/register") public String showBuyerRegistration() { return "buyer-register"; }
    @PostMapping("/buyer/register") public String registerBuyer(@RequestParam String name,@RequestParam String email,@RequestParam String phone,@RequestParam String password,RedirectAttributes ra) {
        try { buyerService.register(name,email,phone,password); ra.addFlashAttribute("successMessage","Buyer account created. Please log in."); return "redirect:/login?registered"; }
        catch (IllegalArgumentException ex) { ra.addFlashAttribute("errorMessage",ex.getMessage()); return "redirect:/buyer/register"; }
    }
    @GetMapping("/buyer/dashboard") public String buyerDashboard(@AuthenticationPrincipal Buyer buyer, Model model) {
        model.addAttribute("buyerName",buyer.getName()); model.addAttribute("buyerEmail",buyer.getEmail()); model.addAttribute("orders",orderService.buyerOrders(buyer)); return "buyer-dashboard";
    }
}
