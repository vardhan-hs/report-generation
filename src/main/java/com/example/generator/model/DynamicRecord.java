package com.example.generator.model;


import lombok.Data;
import java.util.Map;

@Data
public class DynamicRecord {
    private Map<String, Object> fields;
}