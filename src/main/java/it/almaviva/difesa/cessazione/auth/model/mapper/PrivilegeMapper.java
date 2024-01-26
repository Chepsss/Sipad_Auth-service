package it.almaviva.difesa.cessazione.auth.model.mapper;

import it.almaviva.difesa.cessazione.auth.data.entity.Privilege;
import it.almaviva.difesa.cessazione.auth.model.mapper.response.GenericResponseMapper;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface PrivilegeMapper extends GenericResponseMapper<Privilege, PrivilegeDTO> {
}