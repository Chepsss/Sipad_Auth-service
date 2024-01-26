package it.almaviva.difesa.cessazione.auth.model.mapper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.almaviva.difesa.cessazione.auth.model.mapper.response.GenericResponseDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserTokenDTO implements GenericResponseDTO {

    @JsonIgnore
    private Long id;
    private String token;
    @JsonIgnore
    private Long userId;
    @JsonIgnore
    private Long employeeId;
    private String refreshToken;
    private Long expiresAt;

}
