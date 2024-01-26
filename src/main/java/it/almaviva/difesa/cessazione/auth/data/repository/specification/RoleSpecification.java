package it.almaviva.difesa.cessazione.auth.data.repository.specification;

import it.almaviva.difesa.cessazione.auth.data.common.GenericSpecification;
import it.almaviva.difesa.cessazione.auth.data.entity.Role;
import it.almaviva.difesa.cessazione.auth.model.mapper.RoleDTO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class RoleSpecification implements GenericSpecification<Role, RoleDTO> {

    @Override
    public Specification<Role> getSpecification(RoleDTO roleDTO) {
        return null;
    }

}
