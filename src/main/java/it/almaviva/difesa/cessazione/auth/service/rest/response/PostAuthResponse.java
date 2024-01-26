package it.almaviva.difesa.cessazione.auth.service.rest.response;

import lombok.Data;

@Data
public class PostAuthResponse {

    private String token;
    private String refreshToken;

}
