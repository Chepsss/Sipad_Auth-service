package it.almaviva.difesa.cessazione.auth.model.mapper;

import it.almaviva.difesa.cessazione.auth.data.entity.Role;
import it.almaviva.difesa.cessazione.auth.model.mapper.response.GenericResponseMapper;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface RoleMapper extends GenericResponseMapper<Role, RoleDTO> {
}
