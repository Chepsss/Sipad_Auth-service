package it.almaviva.difesa.cessazione.auth.model.mapper;

import it.almaviva.difesa.cessazione.auth.model.mapper.response.GenericResponseDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
@Data
@NoArgsConstructor
public class UserDTO implements GenericResponseDTO {

    private Long userId;
    private Long employeeId;
    private String fiscalCode;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String sex;
    private String email;
    private String rankId;
    private String rankDescription;
    private String armedForceId;
    private String armedForceDescription;
    private Set<RoleDTO> roles;
    private Set<UserTokenDTO> userTokens;
    private boolean alreadyPresent;
    private boolean canDelete = true;

}
