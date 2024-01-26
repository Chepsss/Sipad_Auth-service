package it.almaviva.difesa.cessazione.auth.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class CancelRolesException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final HttpStatus status;

    public CancelRolesException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
