package it.almaviva.difesa.cessazione.auth.handler;

import it.almaviva.difesa.cessazione.auth.exception.InvalidTokenException;
import it.almaviva.difesa.cessazione.auth.exception.TokenRefreshException;
import it.almaviva.difesa.cessazione.auth.exception.UserUnauthorizedException;
import it.almaviva.difesa.cessazione.auth.model.ApiError;
import it.almaviva.difesa.cessazione.auth.model.CustomError;
import it.almaviva.difesa.cessazione.auth.model.RestApiError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
@Slf4j
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        log.error(">>>> ERROR in handleMethodArgumentNotValid: \n{}", ex);
        String message = "Method in Argument is not valid";
        List<CustomError> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            CustomError err = new CustomError();
            err.setField(error.getField());
            err.setMessageError(error.getDefaultMessage());

            errors.add(err);
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            CustomError err = new CustomError();
            err.setObjectName(error.getObjectName());
            err.setMessageError(error.getDefaultMessage());

            errors.add(err);

        }
        RestApiError apiError = new RestApiError(HttpStatus.BAD_REQUEST, message, errors);
        return handleExceptionInternal(ex, apiError, headers, apiError.getStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers,
                                                                          HttpStatus status,
                                                                          WebRequest request) {
        log.error(">>>> ERROR in handleMissingServletRequestParameter: \n{}", ex);
        CustomError error = new CustomError();
        error.setMessageError(ex.getParameterName() + " parameter is missing");

        RestApiError apiError = new RestApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), List.of(error));
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        log.error(">>>> ERROR in handleConstraintViolation: \n{}", ex);
        List<CustomError> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            CustomError error = new CustomError();
            error.setObjectName(violation.getRootBeanClass().getName());
            error.setPropertyPath(violation.getPropertyPath());
            error.setMessageError(violation.getMessage());
            errors.add(error);
        }

        RestApiError apiError = new RestApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        log.error(">>>> ERROR in handleMethodArgumentTypeMismatch: \n{}", ex);
        CustomError error = new CustomError();
        error.setMessageError(ex.getName() + " should be of type " + ex.getRequiredType().getName());

        RestApiError apiError = new RestApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), List.of(error));
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({InvalidTokenException.class})
    public ResponseEntity<Object> handleInvalidToken(InvalidTokenException ex, WebRequest request) {
        log.error(">>>> ERROR in handleInvalidToken: \n{}", ex);
        String error = "Authentication error";
        CustomError err = new CustomError();
        err.setMessageError(String.join(",", ex.getErrors()));
        RestApiError apiError = new RestApiError(HttpStatus.UNAUTHORIZED, error, err, ex);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<RestApiError> handleWebClientException(WebClientResponseException e) {
        log.error(">>>> ERROR in handleWebClientException: \n{}", e);
        CustomError err = new CustomError();
        var message = e.getResponseBodyAsString();
        int startIndex = StringUtils.ordinalIndexOf(message, "\"", 7) + 1;
        int endIndex = StringUtils.ordinalIndexOf(message, ",", 2) - 1;
        err.setMessageError(message.substring(startIndex, endIndex));
        RestApiError apiError = new RestApiError(e.getStatusCode(), message, err, e);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler(value = UserUnauthorizedException.class)
    public ResponseEntity<Object> UserUnauthorizedException(UserUnauthorizedException ex, WebRequest request) {
        log.error(">>>> ERROR in UserUnauthorizedException: \n{}", ex);
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler(value = UsernameNotFoundException.class)
    public ResponseEntity<Object> UsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        log.error(">>>> ERROR in UsernameNotFoundException: \n{}", ex);
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler(value = TokenRefreshException.class)
    public ResponseEntity<Object> TokenRefreshException(TokenRefreshException ex, WebRequest request) {
        log.error(">>>> ERROR in TokenRefreshException: \n{}", ex);
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

}
