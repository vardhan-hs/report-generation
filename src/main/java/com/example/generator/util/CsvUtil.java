package com.example.generator.util;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.example.generator.model.DynamicRecord;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class CsvUtil {

    public List<DynamicRecord> readCsvFile(Path path) throws IOException, CsvValidationException {
        try (Reader reader = Files.newBufferedReader(path);
             CSVReader csvReader = new CSVReader(reader)) {

            String[] headers = csvReader.readNext();
            List<DynamicRecord> records = new ArrayList<>();

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                DynamicRecord record = new DynamicRecord();
                Map<String, Object> fields = new LinkedHashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    fields.put(headers[i], line[i]);
                }
                record.setFields(fields);
                records.add(record);
            }

            return records;
        }
    }

    public void writeCsvFile(Path path, List<DynamicRecord> data) throws IOException {
        if (data.isEmpty()) {
            return;
        }

        try (Writer writer = Files.newBufferedWriter(path);
             CSVWriter csvWriter = new CSVWriter(writer)) {

            // Write headers
            String[] headers = data.get(0).getFields().keySet().toArray(new String[0]);
            csvWriter.writeNext(headers);

            // Write data
            for (DynamicRecord record : data) {
                String[] line = new String[headers.length];
                for (int i = 0; i < headers.length; i++) {
                    Object value = record.getFields().get(headers[i]);
                    line[i] = value != null ? value.toString() : "";
                }
                csvWriter.writeNext(line);
            }
        }
    }
}
