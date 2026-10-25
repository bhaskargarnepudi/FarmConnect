package com.farmconnect.config;

import com.farmconnect.buyer.BuyerRepository;
import com.farmconnect.farmer.FarmerRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import java.io.IOException;
import java.util.Locale;

@Configuration
public class SecurityConfig {
    @Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean public UserDetailsService userDetailsService(FarmerRepository farmers, BuyerRepository buyers) {
        return email -> {
            String normalized = email.trim().toLowerCase(Locale.ROOT);
            return farmers.findByEmail(normalized).<org.springframework.security.core.userdetails.UserDetails>map(f -> f)
                .or(() -> buyers.findByEmail(normalized).map(b -> (org.springframework.security.core.userdetails.UserDetails)b))
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
        };
    }
    @Bean public AuthenticationSuccessHandler roleBasedSuccessHandler() {
        return (request, response, authentication) -> {
            boolean buyer = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_BUYER"));
            response.sendRedirect(buyer ? "/buyer/dashboard" : "/farmer/dashboard");
        };
    }
    @Bean public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationSuccessHandler roleBasedSuccessHandler) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/register", "/login", "/buyer/register", "/css/**", "/js/**", "/images/**", "/uploads/crops/**", "/error").permitAll()
            .requestMatchers("/farmer/**").hasRole("FARMER")
            .requestMatchers("/buyer/**").hasRole("BUYER")
            .anyRequest().permitAll())
            .formLogin(form -> form.loginPage("/login").loginProcessingUrl("/login").usernameParameter("email").passwordParameter("password").successHandler(roleBasedSuccessHandler).failureUrl("/login?error").permitAll())
            .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/").permitAll());
        return http.build();
    }
}
