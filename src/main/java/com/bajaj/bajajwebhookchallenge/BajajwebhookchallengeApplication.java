package com.bajaj.bajajwebhookchallenge;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class BajajwebhookchallengeApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(BajajwebhookchallengeApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Bajaj Webhook Challenge Started ===");

        // Step 1: Send POST request to generate webhook
        String apiUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
        Map<String, String> userDetails = new HashMap<>();
        userDetails.put("name", "Gururaj Biradar");
        userDetails.put("regNo", "U25UV22T040019");
        userDetails.put("email", "gururajbiradar501@gmail.com");

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders postHeaders = new HttpHeaders();
        postHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> postEntity = new HttpEntity<>(userDetails, postHeaders);

        System.out.println("Generating webhook and token...");
        ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, postEntity, Map.class);
        System.out.println("API response: " + response.getBody());

        Map<String, Object> webhookData = response.getBody();
        String webhookUrl = (String) webhookData.get("webhook");
        String accessToken = (String) webhookData.get("accessToken");
        System.out.println("Webhook URL: " + webhookUrl);
        System.out.println("Access Token: " + accessToken);

        // Step 2: Solve SQL challenge assigned (Question 1 for odd regNo)
        String finalQuery = "SELECT p.AMOUNT AS SALARY, " +
            "CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, " +
            "TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, " +
            "d.DEPARTMENT_NAME " +
            "FROM PAYMENTS p " +
            "JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
            "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
            "WHERE DAY(p.PAYMENT_TIME) != 1 " +
            "ORDER BY p.AMOUNT DESC " +
            "LIMIT 1;";
        System.out.println("Final SQL Query: " + finalQuery);

        // Step 3: Submit answer to webhook with JWT token
        HttpHeaders answerHeaders = new HttpHeaders();
        answerHeaders.setContentType(MediaType.APPLICATION_JSON);
        answerHeaders.set("Authorization", accessToken);

        Map<String, String> answerBody = new HashMap<>();
        answerBody.put("finalQuery", finalQuery);

        HttpEntity<Map<String, String>> answerEntity = new HttpEntity<>(answerBody, answerHeaders);

        System.out.println("Submitting your solution...");
        ResponseEntity<String> submitResponse = restTemplate.postForEntity(webhookUrl, answerEntity, String.class);
        System.out.println("Submission Response: " + submitResponse.getBody());
        System.out.println("=== Challenge Completed Successfully ===");
    }
}
