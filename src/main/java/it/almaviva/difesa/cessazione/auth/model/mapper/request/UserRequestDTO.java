package it.almaviva.difesa.cessazione.auth.model.mapper.request;

import it.almaviva.difesa.cessazione.auth.model.mapper.RoleDTO;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UserRequestDTO implements GenericRequestDTO {

    @NotNull
    @NotEmpty(message = "List of roles are empty")
    private List<RoleDTO> roles;

}
