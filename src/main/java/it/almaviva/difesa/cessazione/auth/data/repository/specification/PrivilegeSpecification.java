package it.almaviva.difesa.cessazione.auth.data.repository.specification;

import it.almaviva.difesa.cessazione.auth.data.common.GenericSpecification;
import it.almaviva.difesa.cessazione.auth.data.entity.Privilege;
import it.almaviva.difesa.cessazione.auth.model.mapper.PrivilegeDTO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PrivilegeSpecification implements GenericSpecification<Privilege, PrivilegeDTO> {

    @Override
    public Specification<Privilege> getSpecification(PrivilegeDTO privilegeDTO) {
        return null;
    }

}
