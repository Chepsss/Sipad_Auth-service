package it.almaviva.difesa.cessazione.auth.data.common;


import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public interface GenericSpecification<E extends GenericEntity, C extends GenericCriteriaModel> {

    Specification<E> getSpecification(C c);
}