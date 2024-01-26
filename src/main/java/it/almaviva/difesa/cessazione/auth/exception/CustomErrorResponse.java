package it.almaviva.difesa.cessazione.auth.exception;

import lombok.Data;

@Data
public class CustomErrorResponse {

    private String timestamp;
    private String message;
    private int status;

}