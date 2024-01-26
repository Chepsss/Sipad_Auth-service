package it.almaviva.difesa.cessazione.auth.model.mapper;

import it.almaviva.difesa.cessazione.auth.data.common.GenericCriteriaModel;
import it.almaviva.difesa.cessazione.auth.model.mapper.response.GenericResponseDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class RoleDTO implements GenericResponseDTO, GenericCriteriaModel {

    private Long id;
    private String roleCode;
    private String name;
    private String description;
    private Set<PrivilegeDTO> privileges;

}
