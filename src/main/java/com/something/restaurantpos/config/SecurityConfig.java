package com.something.restaurantpos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(config -> config
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/img/**","/home/**", "/uploads/feedbacks/**").permitAll()
                        .requestMatchers("/login", "/register", "/forgot-password", "/reset-password").permitAll()
                        .requestMatchers("/feedback/verify", "/feedback/submit", "/feedback/success").permitAll()
                        .requestMatchers("/feedback/**").permitAll()
                        .requestMatchers("/profile/**").authenticated()
                        // PHÂN QUYỀN
                        .requestMatchers("/admin/**").hasRole("QUẢN_TRỊ")
                        .requestMatchers("/manager/**").hasAnyRole("QUẢN_LÝ", "QUẢN_TRỊ")
                        .requestMatchers("/cashier/**").hasAnyRole("THU_NGÂN", "QUẢN_LÝ", "QUẢN_TRỊ")
                        .requestMatchers("/waiter/**").hasAnyRole("PHỤC_VỤ", "QUẢN_TRỊ")
                        .requestMatchers("/kitchen/**").hasAnyRole("BẾP", "QUẢN_TRỊ")
                        .requestMatchers("/host/**").hasAnyRole("LỄ_TÂN", "QUẢN_LÝ")

                        .anyRequest().authenticated()
                )
                // CẤU HÌNH FORM LOGIN
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                // CẤU HÌNH LOGOUT
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
} 