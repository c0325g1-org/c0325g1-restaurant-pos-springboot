package com.something.restaurantpos.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dbirumluw",
                "api_key", "798873336284925",
                "api_secret", "QQuUotKBPLPwXZbm54Pea6TiXsg",
                "secure", true
        ));
    }
}
