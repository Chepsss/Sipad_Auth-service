package it.almaviva.difesa.cessazione.auth.exception;

public class UserUnauthorizedException extends RuntimeException {
    private static final long serialVersionUID = -1588412390968886285L;

    public UserUnauthorizedException(String message) {
        super(message);
    }
}
