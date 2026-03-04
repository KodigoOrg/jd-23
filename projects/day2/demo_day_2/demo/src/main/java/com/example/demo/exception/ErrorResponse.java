package com.example.demo.exception;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        int status,
        String code,
        String message,
        Instant timestamp,
        String path,
        List<String> details
) {
    public ErrorResponse(int status, String code, String message,
                         Instant timestamp, String path) {
        this(status, code, message, timestamp, path, List.of());
    }
}
