package com.farmconnect.config;

import com.farmconnect.product.ProductService;
import com.farmconnect.support.LegacyContactRepairService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoDataConfig {
    @Bean
    CommandLineRunner seedProducts(ProductService productService, LegacyContactRepairService repairService) {
        return args -> {
            productService.seedDemoProductsIfEmpty();
            repairService.repairLegacyContacts();
        };
    }
}
