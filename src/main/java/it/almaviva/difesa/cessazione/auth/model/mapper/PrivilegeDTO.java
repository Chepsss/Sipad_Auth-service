package it.almaviva.difesa.cessazione.auth.model.mapper;

import it.almaviva.difesa.cessazione.auth.data.common.GenericCriteriaModel;
import it.almaviva.difesa.cessazione.auth.model.mapper.response.GenericResponseDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PrivilegeDTO implements GenericResponseDTO, GenericCriteriaModel {

    private Long id;
    private String privilegeCode;
    private String description;

}
