package kz.arannati.arannati.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Standard error response object for REST API errors.
 */
@Data
public class ErrorResponse {
    private HttpStatus status;
    private int statusCode;
    private String message;
    private String path;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    private List<ValidationError> errors = new ArrayList<>();

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(HttpStatus status, String message) {
        this();
        this.status = status;
        this.statusCode = status.value();
        this.message = message;
    }

    public ErrorResponse(HttpStatus status, String message, String path) {
        this(status, message);
        this.path = path;
    }

    /**
     * Represents a validation error for a specific field.
     */
    @Data
    public static class ValidationError {
        private String field;
        private String message;

        public ValidationError(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }

    public void addValidationError(String field, String message) {
        errors.add(new ValidationError(field, message));
    }
}