package com.something.restaurantpos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class RestaurantPosApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantPosApplication.class, args);
    }

}
