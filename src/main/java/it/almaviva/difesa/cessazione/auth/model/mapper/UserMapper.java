package it.almaviva.difesa.cessazione.auth.model.mapper;

import it.almaviva.difesa.cessazione.auth.data.entity.User;
import it.almaviva.difesa.cessazione.auth.model.mapper.response.GenericResponseMapper;
import it.almaviva.difesa.cessazione.auth.model.mapper.rest.dto.VwSg155StgiurFastMiCiDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface UserMapper extends GenericResponseMapper<User, UserDTO> {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "employeeId", source = "sg155IdDip")
    @Mapping(target = "fiscalCode", source = "sg155CodiceFiscale")
    @Mapping(target = "firstName", source = "sg155Nome")
    @Mapping(target = "lastName", source = "sg155Cognome")
    @Mapping(target = "birthDate", source = "sg155DataNascita")
    @Mapping(target = "sex", source = "sg155Sesso")
    @Mapping(target = "email", source = "sg155Mail")
    @Mapping(target = "rankId", source = "sg155CodGrado")
    @Mapping(target = "rankDescription", source = "sg155DescrGrado")
    @Mapping(target = "armedForceId", source = "sg155CodFfaa")
    @Mapping(target = "armedForceDescription", source = "sg155DescrFfaa")
    UserDTO copyProperties(VwSg155StgiurFastMiCiDTO source);
}
