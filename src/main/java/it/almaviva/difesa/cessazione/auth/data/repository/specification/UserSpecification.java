package it.almaviva.difesa.cessazione.auth.data.repository.specification;

import it.almaviva.difesa.cessazione.auth.data.common.GenericSpecification;
import it.almaviva.difesa.cessazione.auth.data.entity.User;
import it.almaviva.difesa.cessazione.auth.model.mapper.request.UserRequestDTO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UserSpecification implements GenericSpecification<User, UserRequestDTO> {

    @Override
    public Specification<User> getSpecification(UserRequestDTO userRequestDTO) {
        return null;
    }

}
