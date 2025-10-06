package com.example.cleanarch.infrastructure.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for ModelMapper bean.
 * ModelMapper is used for automatic object-to-object mapping.
 */
@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Configure matching strategy
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)  // Only map fields with exact name match
                .setSkipNullEnabled(true)                        // Skip null values during mapping
                .setAmbiguityIgnored(false);                     // Fail fast on ambiguous mappings

        return modelMapper;
    }
}
