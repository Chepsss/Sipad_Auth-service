package it.almaviva.difesa.cessazione.auth.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class InvalidTokenException extends AuthenticationException {

    private static final long serialVersionUID = -102763894119904386L;

    private final String message;
    private final HttpStatus status;
    private final List<String> errors;

    public InvalidTokenException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.message = message;
        this.errors = new ArrayList<>();
    }

    public InvalidTokenException(String message, HttpStatus status, List<String> errors) {
        super(message);
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public InvalidTokenException(String message, HttpStatus status, List<String> errors, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.status = status;
        this.errors = errors;
    }

}
