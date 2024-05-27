package com.cloudapp.cloud_application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.cloudapp.cloud_application.service.DynamoDBService;
import com.cloudapp.cloud_application.service.LambdaService;
import com.cloudapp.cloud_application.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FileController {

    private final S3Service s3Service;
    private final DynamoDBService dynamoDBService;
    private final LambdaService lambdaService;

    @Autowired
    public FileController(S3Service s3Service, DynamoDBService dynamoDBService, LambdaService lambdaService) {
        this.s3Service = s3Service;
        this.dynamoDBService = dynamoDBService;
        this.lambdaService = lambdaService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("uploaderId") String uploaderId,
                                             @RequestParam("description") String description) throws IOException {
        String bucketName = "springboot-project-1"; // Replace with your bucket name
        String key = file.getOriginalFilename();
        String fileId = UUID.randomUUID().toString();

        // Convert MultipartFile to File
        File tempFile = File.createTempFile("temp", file.getOriginalFilename());
        file.transferTo(tempFile);

        s3Service.uploadFile(bucketName, key, tempFile);

        // Save metadata to DynamoDB
        dynamoDBService.saveFileMetadata(fileId, key, file.getSize(), file.getContentType(), uploaderId, description);

        // Prepare payload for Lambda
        Map<String, Object> payload = new HashMap<>();
        payload.put("fileId", fileId);
        payload.put("bucketName", bucketName);
        payload.put("fileName", key);
        payload.put("fileSize", file.getSize());
        payload.put("fileType", file.getContentType());
        payload.put("uploaderId", uploaderId);
        payload.put("description", description);

        // Invoke Lambda function
        String response = lambdaService.invokeLambda("EnhancedProcessFileLambda", new ObjectMapper().writeValueAsString(payload));

        // Delete temp file
        tempFile.delete();

        return ResponseEntity.ok("File uploaded successfully. Lambda response: " + response);
    }

    @GetMapping("/metadata")
    public ResponseEntity<List<Map<String, Object>>> getAllFileMetadata() {
        List<Map<String, Object>> metadata = dynamoDBService.getAllFileMetadata();
        return ResponseEntity.ok(metadata);
    }

    @GetMapping("/presigned-url")
    public ResponseEntity<String> getPresignedUrl(@RequestParam("fileName") String fileName) {
        String bucketName = "springboot-project-1"; // Replace with your bucket name
        String url = s3Service.generatePreSignedUrl(bucketName, fileName);
        return ResponseEntity.ok(url);
    }
}
