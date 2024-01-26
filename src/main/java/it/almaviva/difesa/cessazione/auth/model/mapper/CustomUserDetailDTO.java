package it.almaviva.difesa.cessazione.auth.model.mapper;

import lombok.Data;

import java.util.Set;

@Data
public class CustomUserDetailDTO {

    private Long userId;
    private Long employeeId;
    private String username;
    private String firstName;
    private String lastName;
    private String rankDescription;
    private String uuid;
    private String token;
    private Set<String> authorities;

}
