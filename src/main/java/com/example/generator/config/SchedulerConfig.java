package com.example.generator.config;

import com.example.generator.service.ReportGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class SchedulerConfig {

    @Autowired
    private ReportGenerationService reportGenerationService;

    @Scheduled(cron = "${report.generation.schedule}")
    public void scheduleReportGeneration() {
        reportGenerationService.generateReport();
    }
}