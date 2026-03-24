package com.example.COFFEEHOUSE.Config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve files from src/Assets/
        registry.addResourceHandler("/Assets/**")
                .addResourceLocations("file:src/Assets/");
    }
}