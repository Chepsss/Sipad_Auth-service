package it.almaviva.difesa.cessazione.auth.model.mapper.request;

import it.almaviva.difesa.cessazione.auth.data.entity.UserToken;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserTokenRequestMapper extends GenericRequestMapper<UserToken, UserTokenRequestDTO> {
}
