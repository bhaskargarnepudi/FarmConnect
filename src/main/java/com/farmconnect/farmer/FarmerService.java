
package com.farmconnect.farmer;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class FarmerService {

    private final FarmerRepository farmerRepository;
    private final PasswordEncoder passwordEncoder;

    public FarmerService(
            FarmerRepository farmerRepository,
            PasswordEncoder passwordEncoder) {
        this.farmerRepository = farmerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(
            String name,
            String email,
            String phone,
            String password) {

        name = name == null ? "" : name.trim();
        email = email == null
                ? ""
                : email.trim().toLowerCase(Locale.ROOT);
        phone = phone == null ? "" : phone.trim();

        if (name.isBlank() || email.isBlank()
                || phone.isBlank() || password == null) {
            throw new IllegalArgumentException(
                    "Please complete all fields.");
        }

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException(
                    "Enter a valid email address.");
        }

        if (password.length() < 8 || password.length() > 72) {
            throw new IllegalArgumentException(
                    "Password must be 8 to 72 characters long.");
        }

        if (farmerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "This email is already registered. Please log in.");
        }

        Farmer farmer = new Farmer();
        farmer.setName(name);
        farmer.setEmail(email);
        farmer.setPhone(phone);
        farmer.setPasswordHash(passwordEncoder.encode(password));

        farmerRepository.save(farmer);
    }
}
