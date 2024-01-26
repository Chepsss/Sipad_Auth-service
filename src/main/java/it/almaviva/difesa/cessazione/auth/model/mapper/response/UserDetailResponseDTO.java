package it.almaviva.difesa.cessazione.auth.model.mapper.response;

import it.almaviva.difesa.cessazione.auth.model.mapper.RoleDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.UserDTO;
import lombok.Data;

import java.util.List;

@Data
public class UserDetailResponseDTO implements GenericResponseDTO {

    private Long employeeId;
    private List<RoleDTO> roles;
    private UserDTO employeeDetail;

}
