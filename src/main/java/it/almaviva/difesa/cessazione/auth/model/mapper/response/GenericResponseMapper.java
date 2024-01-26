package it.almaviva.difesa.cessazione.auth.model.mapper.response;

import it.almaviva.difesa.cessazione.auth.data.common.GenericEntity;

import java.util.List;
import java.util.Set;

public interface GenericResponseMapper<E extends GenericEntity, D extends GenericResponseDTO> {

    D asDTO(E entity);

    List<D> asDTOList(List<E> entity);

    Set<D> asDTOSet(Set<E> entity);

}
