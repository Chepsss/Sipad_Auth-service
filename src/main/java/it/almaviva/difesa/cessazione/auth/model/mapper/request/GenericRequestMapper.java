package it.almaviva.difesa.cessazione.auth.model.mapper.request;

import it.almaviva.difesa.cessazione.auth.data.common.GenericEntity;

import java.util.List;
import java.util.Set;

public interface GenericRequestMapper<E extends GenericEntity, D extends GenericRequestDTO> {

    E asEntity(D dto);

    List<E> asEntityList(List<D> dto);

    Set<E> asEntitySet(Set<D> dto);

}
