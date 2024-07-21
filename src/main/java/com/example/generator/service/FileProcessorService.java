package com.example.generator.service;

import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.example.generator.model.DynamicRecord;
import com.example.generator.util.CsvUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FileProcessorService {

    @Autowired
    private CsvUtil csvUtil;

    @Autowired
    private TransformationService transformationService;

    public void processFiles(Path inputPath, Path referencePath, Path outputPath) throws IOException, CsvValidationException {
        List<DynamicRecord> inputRecords = csvUtil.readCsvFile(inputPath);
        List<DynamicRecord> referenceRecords = csvUtil.readCsvFile(referencePath);

        Map<String, DynamicRecord> referenceMap = referenceRecords.stream()
                .collect(Collectors.toMap(r -> r.getFields().get("refkey1") + "_" + r.getFields().get("refkey2"), r -> r));

        List<DynamicRecord> outputRecords = inputRecords.stream()
                .map(input -> transformationService.transform(input,
                        referenceMap.get(input.getFields().get("refkey1") + "_" + input.getFields().get("refkey2"))))
                .collect(Collectors.toList());

        csvUtil.writeCsvFile(outputPath, outputRecords);
    }
}