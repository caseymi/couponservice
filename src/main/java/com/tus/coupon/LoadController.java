package com.tus.coupon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class LoadController {

    private final ObjectMapper mapper = new ObjectMapper();
    private volatile String cachedJson; // simple cache for the file contents

    @GetMapping("/heavy")
    public Map<String, Object> heavy() throws IOException {
        // Load the data.json once per JVM (very simple cache)
        if (cachedJson == null) {
            synchronized (this) {
                if (cachedJson == null) {
                    var resource = new ClassPathResource("data.json");
                    byte[] bytes = resource.getInputStream().readAllBytes();
                    cachedJson = new String(bytes, StandardCharsets.UTF_8);
                }
            }
        }

        Instant start = Instant.now();

        JsonNode last = null;
        for (int i = 0; i < 5000; i++) {
            // CPU work: JSON parsing repeatedly
            last = mapper.readTree(cachedJson);
        }

        long ms = Duration.between(start, Instant.now()).toMillis();

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("message", "Success!");
        resp.put("iterations", 5000);
        resp.put("elapsed_ms", ms);
        resp.put("data_sample", last);
        return resp;
    }
}
