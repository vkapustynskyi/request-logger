package com.webclient.logger.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class TestController {

    private final WebClient webClient;

    @GetMapping("/test")
    public void test() {
        webClient.get()
                .uri("/bodia")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @GetMapping("/bodia")
    public String test2() {
        return "{\"value\": \"success\"}";
    }

}
