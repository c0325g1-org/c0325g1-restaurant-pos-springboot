package com.something.restaurantpos.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/tts")
public class TTSController {

    private static final String GOOGLE_TTS_API = "https://texttospeech.googleapis.com/v1/text:synthesize";

    @Value("${google.tts.api.key}")
    private String apiKey;

    @PostMapping
    public ResponseEntity<?> proxyGoogleTTS(@RequestBody Map<String, Object> body) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            String url = GOOGLE_TTS_API + "?key=" + apiKey;
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lỗi gọi Google TTS: " + e.getMessage()));
        }
    }
}
