package it.almaviva.difesa.cessazione.auth.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.almaviva.difesa.cessazione.auth.constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@SuppressWarnings("all")
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiError {

    private HttpStatus httpStatus;
    private int status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.DD_MM_YYYY_HH_MM_SS)
    private LocalDateTime timestamp;
    private String message;
    private String error;

    private ApiError() {
        timestamp = LocalDateTime.now();
    }

    public ApiError(HttpStatus httpStatus) {
        this();
        this.status = httpStatus.value();
    }

    public ApiError(HttpStatus httpStatus, String message) {
        this();
        this.status = httpStatus.value();
        this.message = message;
        this.error = httpStatus.getReasonPhrase();
    }
}
