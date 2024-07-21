# CSV Report Generation Service

## Overview
This Spring Boot application processes CSV files, applies configurable transformation rules, and generates output CSV files. It's designed to handle large datasets efficiently and supports various transformation operations.

## Features
- Configurable transformation rules
- Support for input, reference, and output CSV files
- RESTful API to trigger report generation
- Scheduled report generation
- Dynamic handling of up to 250 input/output fields
- Extensible transformation operations (concat, copy, max, multiply)

## Requirements
- Java 11 or higher
- Maven 3.6 or higher
- Spring Boot 2.6.x

## Configuration
Transformation rules are defined in application.properties:

```properties
transformation.rules={'outfield1':'concat field1 field2', \
                      'outfield2':'copy refdata1', \
                      'outfield3':'concat refdata2 refdata3', \
                      'outfield4':'multiply field3 max(field5,refdata4)', \
                      'outfield5':'max field5 refdata4'}