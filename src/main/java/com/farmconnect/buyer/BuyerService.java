package com.farmconnect.buyer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Locale;
@Service
public class BuyerService {
    private final BuyerRepository repository;
    private final PasswordEncoder passwordEncoder;
    public BuyerService(BuyerRepository repository, PasswordEncoder passwordEncoder) { this.repository = repository; this.passwordEncoder = passwordEncoder; }
    public void register(String name, String email, String phone, String password) {
        if (name == null || name.isBlank() || email == null || email.isBlank() || phone == null || phone.isBlank()) throw new IllegalArgumentException("Please fill in all fields.");
        if (password == null || password.length() < 8) throw new IllegalArgumentException("Password must contain at least 8 characters.");
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        if (repository.existsByEmail(normalizedEmail)) throw new IllegalArgumentException("An account with this email already exists.");
        Buyer buyer = new Buyer(); buyer.setName(name.trim()); buyer.setEmail(normalizedEmail); buyer.setPhone(phone.trim()); buyer.setPasswordHash(passwordEncoder.encode(password)); repository.save(buyer);
    }
}
