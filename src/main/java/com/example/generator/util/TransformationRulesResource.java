package com.example.generator.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class TransformationRulesResource  {

    @Value("${transformation.rules.path}")
    private String transformationRulesPath;

    @Bean
    public Properties transformationRules() throws IOException {
//        return PropertiesLoaderUtils.loadProperties(transformationRulesResource);
        Properties properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream(transformationRulesPath)) {
            properties.load(inputStream);
        }
        return properties;
    }
}