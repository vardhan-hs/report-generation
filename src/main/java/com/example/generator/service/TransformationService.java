package com.example.generator.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

import com.example.generator.model.DynamicRecord;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
@Slf4j
public class TransformationService {

    private final Properties transformationRules;

    @Autowired
    public TransformationService(Properties transformationRules) {
        this.transformationRules = transformationRules;
    }

    public DynamicRecord transform(DynamicRecord input, DynamicRecord reference) {
        DynamicRecord output = new DynamicRecord();
        Map<String, Object> outputFields = new HashMap<>();

        for (Map.Entry<Object, Object> entry : transformationRules.entrySet()) {
//            log.info("Transformation rules entry set is {}",transformationRules.entrySet());
            String outputField = (String) entry.getKey();
            String rule = (String) entry.getValue();
            outputFields.put(outputField, evaluateRule(rule, input, reference));
        }

        output.setFields(outputFields);
        return output;
    }

    private Object evaluateRule(String rule, DynamicRecord input, DynamicRecord reference) {
        Stack<String> operators = new Stack<>();
        Stack<Object> operands = new Stack<>();
        StringBuilder currentToken = new StringBuilder();

        for (int i = 0; i < rule.length(); i++) {
            char c = rule.charAt(i);
            if (c == ' ' && currentToken.length() > 0) {
                processToken(currentToken.toString(), operators, operands, input, reference);
                currentToken = new StringBuilder();
            } else if (c == '(') {
                if (currentToken.length() > 0) {
                    operators.push(currentToken.toString());
                    currentToken = new StringBuilder();
                }
                operators.push("(");
            } else if (c == ')') {
                if (currentToken.length() > 0) {
                    processToken(currentToken.toString(), operators, operands, input, reference);
                    currentToken = new StringBuilder();
                }
                while (!operators.peek().equals("(")) {
                    applyOperator(operators.pop(), operands);
                }
                operators.pop(); // Remove the "("
                if (!operators.empty() && !operators.peek().equals("(")) {
                    applyOperator(operators.pop(), operands);
                }
            } else if (c == ',') {
                if (currentToken.length() > 0) {
                    processToken(currentToken.toString(), operators, operands, input, reference);
                    currentToken = new StringBuilder();
                }
            } else {
                currentToken.append(c);
            }
        }

        if (currentToken.length() > 0) {
            processToken(currentToken.toString(), operators, operands, input, reference);
        }

        while (!operators.empty()) {
            applyOperator(operators.pop(), operands);
        }

        return operands.pop();
    }

    private void processToken(String token, Stack<String> operators, Stack<Object> operands,
                              DynamicRecord input, DynamicRecord reference) {
        if (isOperator(token)) {
            operators.push(token);
        } else {
            operands.push(getValue(token, input, reference));
        }
    }

    private boolean isOperator(String token) {
        return token.equals("concat") || token.equals("max") || token.equals("multiply") || token.equals("copy");
    }

    private void applyOperator(String operator, Stack<Object> operands) {
        switch (operator) {
            case "concat":
                String b = operands.pop().toString();
                String a = operands.pop().toString();
                operands.push(a + b);
                break;
            case "max":
                BigDecimal val2 = new BigDecimal(String.valueOf(operands.pop()));
                BigDecimal val1 = new BigDecimal(String.valueOf(operands.pop()));
                operands.push(val1.max(val2).toString());
                break;
            case "copy":
                break;
            case "multiply":
                BigDecimal factor2 = new BigDecimal(operands.pop().toString());
                BigDecimal factor1 = new BigDecimal(operands.pop().toString());
                operands.push(factor1.multiply(factor2).toString());
                break;
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    private Object getValue(String field, DynamicRecord input, DynamicRecord reference) {
        if (input.getFields().containsKey(field)) {
            return input.getFields().get(field);
        } else if (reference.getFields().containsKey(field)) {
            return reference.getFields().get(field);
        } else {
            throw new IllegalArgumentException("Field not found: " + field);
        }
    }
}