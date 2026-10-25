package com.farmconnect.product;

import com.farmconnect.farmer.Farmer;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;

@Controller
@RequestMapping("/farmer/crops")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String myCrops(@AuthenticationPrincipal Farmer farmer, Model model) {
        model.addAttribute("crops", productService.getCropsForFarmer(farmer));
        return "my-crops";
    }

    @GetMapping("/new")
    public String showAddCropPage() {
        return "add-crop";
    }

    @PostMapping
    public String addCrop(
            @RequestParam String name,
            @RequestParam String category,
            @RequestParam String description,
            @RequestParam BigDecimal price,
            @RequestParam String unit,
            @RequestParam int quantity,
            @RequestParam String quality,
            @RequestParam String location,
            @RequestParam("image") MultipartFile image,
            @AuthenticationPrincipal Farmer farmer,
            RedirectAttributes redirectAttributes) {
        try {
            productService.addCrop(name, category, description, price, unit,
                    quantity, quality, location, image, farmer);
            redirectAttributes.addFlashAttribute("successMessage", "Crop added successfully!");
            return "redirect:/farmer/dashboard";
        } catch (IllegalArgumentException | IOException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/farmer/crops/new";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id,
                               @AuthenticationPrincipal Farmer farmer,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("crop", productService.getCropForFarmer(id, farmer));
            return "edit-crop";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/farmer/crops";
        }
    }

    @PostMapping("/{id}/edit")
    public String updateCrop(@PathVariable Long id,
            @RequestParam String name,
            @RequestParam String category,
            @RequestParam String description,
            @RequestParam BigDecimal price,
            @RequestParam String unit,
            @RequestParam int quantity,
            @RequestParam String quality,
            @RequestParam String location,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal Farmer farmer,
            RedirectAttributes redirectAttributes) {
        try {
            productService.updateCrop(id, name, category, description, price, unit,
                    quantity, quality, location, image, farmer);
            redirectAttributes.addFlashAttribute("successMessage", "Crop updated successfully!");
        } catch (IllegalArgumentException | IOException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/farmer/crops/" + id + "/edit";
        }
        return "redirect:/farmer/crops";
    }

    @PostMapping("/{id}/delete")
    public String deleteCrop(@PathVariable Long id,
                             @AuthenticationPrincipal Farmer farmer,
                             RedirectAttributes redirectAttributes) {
        try {
            productService.deleteCrop(id, farmer);
            redirectAttributes.addFlashAttribute("successMessage", "Crop deleted successfully.");
        } catch (IllegalArgumentException | IOException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/farmer/crops";
    }
}
