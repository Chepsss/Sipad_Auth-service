package it.almaviva.difesa.cessazione.auth.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.almaviva.difesa.cessazione.auth.constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@SuppressWarnings("all")
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RestApiError {

    private HttpStatus status;
    private int statusCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.DD_MM_YYYY_HH_MM_SS)
    private LocalDateTime timestamp;
    private String message;
    private String debugMessage;
    private List<CustomError> errors;

    private RestApiError() {
        timestamp = LocalDateTime.now();
    }

    public RestApiError(HttpStatus status) {
        this();
        this.status = status;
    }

    public RestApiError(HttpStatus status, String message) {
        this();
        this.status = status;
        this.statusCode = status.value();
        this.message = message;
    }

    public RestApiError(HttpStatus status, Throwable ex) {
        this();
        this.status = status;
        this.statusCode = status.value();
        this.message = "Unexpected error";
        this.debugMessage = ex.getLocalizedMessage();
    }

    public RestApiError(HttpStatus status, String message, List<CustomError> errors, Throwable ex) {
        this();
        this.status = status;
        this.statusCode = status.value();
        this.message = message;
        this.debugMessage = ex.getLocalizedMessage();
        this.errors = errors;
    }

    public RestApiError(HttpStatus status, String message, List<CustomError> errors) {
        this();
        this.status = status;
        this.statusCode = status.value();
        this.message = message;
        this.errors = errors;
    }

    public RestApiError(HttpStatus status, String message, CustomError error, Throwable ex) {
        this();
        this.status = status;
        this.statusCode = status.value();
        this.message = message;
        this.debugMessage = ex.getLocalizedMessage();
        this.errors = List.of(error);
    }

}
