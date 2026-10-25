package com.farmconnect.product;

import com.farmconnect.farmer.Farmer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository repository;
    private final Path uploadDirectory;

    public ProductService(
            ProductRepository repository,
            @Value("${farmconnect.upload-dir:uploads/crops}") String uploadDir) {
        this.repository = repository;
        this.uploadDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    public List<Product> getActiveProducts() {
        return repository.findByActiveTrueOrderByIdAsc();
    }

    public List<Product> getCropsForFarmer(Farmer farmer) {
        return repository.findManageableByFarmer(farmer.getEmail(), farmer.getName());
    }

    public Product getCropForFarmer(Long id, Farmer farmer) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Crop not found."));
        ensureOwner(product, farmer);
        return product;
    }

    public void seedDemoProductsIfEmpty() {
        if (repository.count() > 0) return;

        repository.saveAll(List.of(
            new Product("Sona Masoori Rice", "Rice",
                "Medium-grain rice suitable for everyday meals. Ask the farmer about bag size and harvest details.",
                new BigDecimal("1250.00"), "25 kg bag", 40, "Premium grade",
                "Ramesh Kumar", "Guntur, Andhra Pradesh",
                "https://images.unsplash.com/photo-1586201375761-83865001e31c?auto=format&fit=crop&w=900&q=80"),
            new Product("Basmati Rice", "Rice",
                "Long-grain aromatic rice. Contact the farmer to confirm the variety and available quantity.",
                new BigDecimal("1850.00"), "25 kg bag", 24, "Aromatic long grain",
                "Lakshmi Farms", "Palnadu, Andhra Pradesh",
                "https://images.unsplash.com/photo-1516684732162-798a0062be99?auto=format&fit=crop&w=900&q=80"),
            new Product("Fresh Tomatoes", "Vegetables",
                "Freshly harvested tomatoes. Price and quantity may vary with market conditions.",
                new BigDecimal("28.00"), "1 kg", 200, "Fresh harvest",
                "Srinivas Reddy", "Narasaraopet, Andhra Pradesh",
                "https://images.unsplash.com/photo-1546094096-0df4bcaaa337?auto=format&fit=crop&w=900&q=80"),
            new Product("Onions", "Vegetables",
                "Farm-fresh onions available in bulk or small quantities. Confirm the final price with the farmer.",
                new BigDecimal("32.00"), "1 kg", 150, "Sorted produce",
                "Green Field Farm", "Vinukonda, Andhra Pradesh",
                "https://images.unsplash.com/photo-1508747703725-719777637510?auto=format&fit=crop&w=900&q=80"),
            new Product("Fresh Bananas", "Fruits",
                "Fresh bananas sold by weight. Contact the farmer for bulk orders.",
                new BigDecimal("45.00"), "1 dozen", 80, "Fresh and ripe",
                "Anjali Farms", "Bapatla, Andhra Pradesh",
                "https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?auto=format&fit=crop&w=900&q=80"),
            new Product("Green Chilli", "Vegetables",
                "Green chillies for household and wholesale buyers. Ask about the available harvest quantity.",
                new BigDecimal("60.00"), "1 kg", 75, "Fresh harvest",
                "Venkatesh Naidu", "Macherla, Andhra Pradesh",
                "https://images.unsplash.com/photo-1588252303782-cb80119abd6d?auto=format&fit=crop&w=900&q=80")
        ));
    }

    public void addCrop(String name, String category, String description,
                        BigDecimal price, String unit, int quantity, String quality,
                        String location, MultipartFile image, Farmer farmer) throws IOException {
        validateFields(name, category, description, price, unit, quantity, quality, location);
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Please select a crop photo.");
        }
        String imageUrl = saveImage(image);
        Product product = new Product(name.trim(), category.trim(), description.trim(), price,
                unit.trim(), quantity, quality.trim(), farmer.getName(), location.trim(), imageUrl);
        product.setFarmerEmail(farmer.getEmail());
        try {
            repository.save(product);
        } catch (RuntimeException exception) {
            deleteLocalImage(imageUrl);
            throw exception;
        }
    }

    public void updateCrop(Long id, String name, String category, String description,
                           BigDecimal price, String unit, int quantity, String quality,
                           String location, MultipartFile image, Farmer farmer) throws IOException {
        Product product = getCropForFarmer(id, farmer);
        validateFields(name, category, description, price, unit, quantity, quality, location);

        String oldImageUrl = product.getImageUrl();
        String newImageUrl = oldImageUrl;
        if (image != null && !image.isEmpty()) {
            newImageUrl = saveImage(image);
        }

        product.setName(name.trim());
        product.setCategory(category.trim());
        product.setDescription(description.trim());
        product.setPrice(price);
        product.setUnit(unit.trim());
        product.setQuantityAvailable(quantity);
        product.setQuality(quality.trim());
        product.setLocation(location.trim());
        product.setImageUrl(newImageUrl);

        try {
            repository.save(product);
        } catch (RuntimeException exception) {
            if (!newImageUrl.equals(oldImageUrl)) deleteLocalImage(newImageUrl);
            throw exception;
        }
        if (!newImageUrl.equals(oldImageUrl)) deleteLocalImage(oldImageUrl);
    }

    public void deleteCrop(Long id, Farmer farmer) throws IOException {
        Product product = getCropForFarmer(id, farmer);
        String imageUrl = product.getImageUrl();
        repository.delete(product);
        deleteLocalImage(imageUrl);
    }

    private void ensureOwner(Product product, Farmer farmer) {
        // This starter project currently stores the farmer's name on each product.
        // The comparison prevents other accounts with a different name from editing it.
        if (farmer == null) {
            throw new IllegalArgumentException("Please log in to manage crops.");
        }
        String ownerEmail = product.getFarmerEmail();
        boolean ownerMatches = ownerEmail != null && ownerEmail.equalsIgnoreCase(farmer.getEmail());
        // Backward compatibility for crops uploaded before farmer_email was added.
        // New crop listings are always checked by the authenticated farmer's email.
        if (ownerEmail == null && product.getFarmerName() != null) {
            ownerMatches = product.getFarmerName().equalsIgnoreCase(farmer.getName());
            if (ownerMatches) product.setFarmerEmail(farmer.getEmail());
        }
        if (!ownerMatches) {
            throw new IllegalArgumentException("You can manage only your own crops.");
        }
    }

    private void validateFields(String name, String category, String description,
                                BigDecimal price, String unit, int quantity,
                                String quality, String location) {
        if (name == null || name.isBlank() || category == null || category.isBlank()
                || description == null || description.isBlank() || unit == null || unit.isBlank()
                || quality == null || quality.isBlank() || location == null || location.isBlank()) {
            throw new IllegalArgumentException("Please fill in all fields.");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
    }

    private String saveImage(MultipartFile image) throws IOException {
        if (image.getSize() > 5L * 1024 * 1024) {
            throw new IllegalArgumentException("Photo must be 5 MB or less.");
        }
        String originalName = image.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.lastIndexOf('.') >= 0) {
            extension = originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase();
        }
        Set<String> allowedExtensions = Set.of("jpg", "jpeg", "png", "webp");
        Set<String> allowedTypes = Set.of("image/jpeg", "image/png", "image/webp");
        if (!allowedExtensions.contains(extension) || image.getContentType() == null
                || !allowedTypes.contains(image.getContentType())) {
            throw new IllegalArgumentException("Upload a JPG, PNG or WEBP photo.");
        }
        Files.createDirectories(uploadDirectory);
        String fileName = UUID.randomUUID() + "." + extension;
        Path destination = uploadDirectory.resolve(fileName).normalize();
        if (!destination.startsWith(uploadDirectory)) {
            throw new IllegalArgumentException("Invalid image path.");
        }
        image.transferTo(destination);
        return "/uploads/crops/" + fileName;
    }

    private void deleteLocalImage(String imageUrl) throws IOException {
        if (imageUrl == null || !imageUrl.startsWith("/uploads/crops/")) return;
        String fileName = imageUrl.substring("/uploads/crops/".length());
        if (fileName.contains("/") || fileName.contains("\\") || fileName.isBlank()) return;
        Path file = uploadDirectory.resolve(fileName).normalize();
        if (file.startsWith(uploadDirectory)) Files.deleteIfExists(file);
    }
}
