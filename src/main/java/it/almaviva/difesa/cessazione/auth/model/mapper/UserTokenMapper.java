package it.almaviva.difesa.cessazione.auth.model.mapper;

import it.almaviva.difesa.cessazione.auth.data.entity.UserToken;
import it.almaviva.difesa.cessazione.auth.model.mapper.response.GenericResponseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserTokenMapper extends GenericResponseMapper<UserToken, UserTokenDTO> {
}
