package com.example.generator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ReportGenerationService {

    private static final Logger logger = LoggerFactory.getLogger(ReportGenerationService.class);

    @Autowired
    private FileProcessorService fileProcessorService;

    @Value("${input.file.path}")
    private String inputFilePath;

    @Value("${reference.file.path}")
    private String referenceFilePath;

    @Value("${output.file.path}")
    private String outputFilePath;

    public void generateReport() {
        try {
            logger.info("Starting report generation");
            Path inputPath = Paths.get(inputFilePath);
            Path referencePath = Paths.get(referenceFilePath);
            Path outputPath = Paths.get(outputFilePath);

            fileProcessorService.processFiles(inputPath, referencePath, outputPath);
            logger.info("Report generation completed successfully");
        } catch (Exception e) {
            logger.error("Error during report generation", e);
        }
    }
}