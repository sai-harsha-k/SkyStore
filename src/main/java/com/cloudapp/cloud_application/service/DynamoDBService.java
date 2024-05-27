package com.cloudapp.cloud_application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DynamoDBService {

    private final DynamoDbClient dynamoDbClient;

    @Autowired
    public DynamoDBService(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public void saveFileMetadata(String fileId, String fileName, long fileSize, String fileType, String uploaderId, String description) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("fileId", AttributeValue.builder().s(fileId).build());
        item.put("fileName", AttributeValue.builder().s(fileName).build());
        item.put("fileSize", AttributeValue.builder().n(Long.toString(fileSize)).build());
        item.put("fileType", AttributeValue.builder().s(fileType).build());
        item.put("uploadDate", AttributeValue.builder().s(Long.toString(System.currentTimeMillis())).build());
        item.put("uploaderId", AttributeValue.builder().s(uploaderId).build());
        item.put("description", AttributeValue.builder().s(description).build());

        // Add new attributes as needed
        item.put("newAttribute1", AttributeValue.builder().s("newValue1").build());
        item.put("newAttribute2", AttributeValue.builder().n("123").build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName("FileMetadata")
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
    }

    public List<Map<String, Object>> getAllFileMetadata() {
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName("FileMetadata")
                .build();

        ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
        return scanResponse.items().stream()
                .map(this::convertAttributeValueMap)
                .collect(Collectors.toList());
    }

    private Map<String, Object> convertAttributeValueMap(Map<String, AttributeValue> attributeValueMap) {
        Map<String, Object> result = new HashMap<>();
        attributeValueMap.forEach((key, value) -> {
            if (value.s() != null) {
                result.put(key, value.s());
            } else if (value.n() != null) {
                result.put(key, Long.parseLong(value.n()));
            } else if (value.bool() != null) {
                result.put(key, value.bool());
            } else if (value.hasL()) {
                result.put(key, value.l().stream().map(this::convertAttributeValue).collect(Collectors.toList()));
            } else if (value.hasM()) {
                result.put(key, convertAttributeValueMap(value.m()));
            } else if (value.hasSs()) {
                result.put(key, value.ss());
            } else if (value.hasNs()) {
                result.put(key, value.ns().stream().map(Long::parseLong).collect(Collectors.toList()));
            }
        });
        return result;
    }

    private Object convertAttributeValue(AttributeValue value) {
        if (value.s() != null) {
            return value.s();
        } else if (value.n() != null) {
            return Long.parseLong(value.n());
        } else if (value.bool() != null) {
            return value.bool();
        } else if (value.hasL()) {
            return value.l().stream().map(this::convertAttributeValue).collect(Collectors.toList());
        } else if (value.hasM()) {
            return convertAttributeValueMap(value.m());
        } else if (value.hasSs()) {
            return value.ss();
        } else if (value.hasNs()) {
            return value.ns().stream().map(Long::parseLong).collect(Collectors.toList());
        }
        return null;
    }
}
