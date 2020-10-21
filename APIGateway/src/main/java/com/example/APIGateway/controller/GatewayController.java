package com.example.APIGateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@RestController
public class GatewayController {

//    @Autowired
//    RestTemplate restTemplate;

    @Autowired
    private WebClient webClient;

//    @GetMapping("/transactions/{username}")
//    public ResponseEntity<String> getTransactions(@PathVariable String username, @RequestHeader MultiValueMap<String, String> headers) {
//        HttpEntity<String> request = new HttpEntity<>(username, headers);
//        return restTemplate.getForEntity("http://localhost:8082/transactions/" + username, String.class, request);
//    }
    @GetMapping("/transactions/{username}")
    public Mono<String> getTransactions(@PathVariable String username, @RequestHeader MultiValueMap<String, String> headers) {
        try {
            return webClient.get()
                    .uri("http://localhost:8082/transactions/" + username)
                    .headers(httpHeaders -> {
                        httpHeaders.addAll(headers);
                    })
                    .retrieve()
                    .bodyToMono(String.class);
        } catch (WebClientResponseException ex) {
            System.out.println("Error response code: " + ex.getRawStatusCode() + " and the message is: " + ex.getResponseBodyAsString());
            throw ex;
        } catch (Exception ex) {
            System.out.println("Exception occured in login: " + ex);
            throw ex;
        }
    }

//    @PostMapping("/users")
//    public ResponseEntity<UserResponseModel> getUsers(@RequestBody UserRequestModel registerData, @RequestHeader MultiValueMap<String, String> headers) {
//        HttpEntity<UserRequestModel> request = new HttpEntity<>(registerData, headers);
//        return restTemplate.postForEntity("http://localhost:8081/users", request, UserResponseModel.class);
//    }
    @PostMapping("/users")
    public Mono<UserResponseModel> getUsers(@RequestBody UserRequestModel registerData, @RequestHeader MultiValueMap<String, String> headers) {
        try {
            return webClient.post()
                    .uri("http://localhost:8081/users")
//                .body(BodyInserters.fromValue(registerData))
                    .bodyValue(registerData)
                    .headers(httpHeaders -> {
                        httpHeaders.addAll(headers);
                    })
                    .retrieve()
                    .bodyToMono(UserResponseModel.class);
        } catch (WebClientResponseException ex) {
            System.out.println("Error response code: " + ex.getRawStatusCode() + " and the message is: " + ex.getResponseBodyAsString());
            throw ex;
        } catch (Exception ex) {
            System.out.println("Exception occured in login: " + ex);
            throw ex;
        }
    }

//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestBody UserRequestModel usernamePassword, @RequestHeader MultiValueMap<String, String> headers) {
//        HttpEntity<UserRequestModel> request = new HttpEntity<>(usernamePassword, headers);
//        return restTemplate.postForEntity("http://localhost:8081/login", request, String.class);
//    }
    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@RequestBody UserRequestModel usernamePassword, @RequestHeader MultiValueMap<String, String> headers) {
        try {
            return webClient.post()
                    .uri("http://localhost:8081/login")
                    .bodyValue(usernamePassword)
                    .headers(httpHeaders -> {
                        httpHeaders.addAll(headers);
                    })
                    .exchange()
                    .flatMap(res -> {
                        return res.toEntity(String.class);
                    });
        } catch (WebClientResponseException ex) {
            System.out.println("Error response code: " + ex.getRawStatusCode() + " and the message is: " + ex.getResponseBodyAsString());
            throw ex;
        } catch (Exception ex) {
            System.out.println("Exception occured in login: " + ex);
            throw ex;
        }
    }
}
