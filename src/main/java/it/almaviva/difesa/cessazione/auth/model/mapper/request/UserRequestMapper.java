package it.almaviva.difesa.cessazione.auth.model.mapper.request;

import it.almaviva.difesa.cessazione.auth.data.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRequestMapper extends GenericRequestMapper<User, UserRequestDTO> {
}