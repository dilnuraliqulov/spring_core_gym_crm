package com.gymcrm.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API error response")
public class ErrorResponse {

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Error message", example = "Validation failed")
    private String message;

    @Schema(description = "Timestamp of the error")
    private LocalDateTime timestamp;

    @Schema(description = "Request path", example = "/api/trainee")
    private String path;

    @Schema(description = "Transaction ID for tracking", example = "abc-123-def")
    private String transactionId;

    @Schema(description = "Detailed validation errors (if applicable)")
    private List<String> errors;

    public ErrorResponse(int status, String message, String path, String transactionId) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.path = path;
        this.transactionId = transactionId;
    }

    public ErrorResponse(int status, String message, String path, String transactionId, List<String> errors) {
        this(status, message, path, transactionId);
        this.errors = errors;
    }
}

