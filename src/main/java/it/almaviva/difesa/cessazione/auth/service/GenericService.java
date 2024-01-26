package it.almaviva.difesa.cessazione.auth.service;

import it.almaviva.difesa.cessazione.auth.data.common.GenericCriteriaModel;
import it.almaviva.difesa.cessazione.auth.data.common.GenericEntity;
import it.almaviva.difesa.cessazione.auth.data.common.GenericRepository;
import it.almaviva.difesa.cessazione.auth.data.common.GenericSpecification;
import it.almaviva.difesa.cessazione.auth.data.common.Sortable;
import it.almaviva.difesa.cessazione.auth.enums.StatusEnum;
import it.almaviva.difesa.cessazione.auth.model.mapper.response.GenericResponseDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.response.GenericResponseMapper;
import it.almaviva.difesa.cessazione.auth.util.UtilsMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public abstract class GenericService<E extends GenericEntity & Sortable, I extends Serializable, D extends GenericResponseDTO, C extends GenericCriteriaModel> {

    private final GenericRepository<E, I> repository;
    private final GenericResponseMapper<E, D> mapper;
    private final GenericSpecification<E, C> specification;

    @Transactional(readOnly = true)
    public List<D> findAll() {
        List<E> finds = repository.findAll();
        return mapper.asDTOList(finds);
    }

    @Transactional(readOnly = true)
    public Page<D> findAll(Pageable pageable) {
        pageable = UtilsMethod.setSortToPageableIfNecessary(pageable, getClass());
        Page<E> page = repository.findAll(pageable);
        return page.map(mapper::asDTO);
    }

    @Transactional(readOnly = true)
    public D findById(I id) {
        Optional<E> entity = repository.findById(id);
        if (entity.isEmpty()) {
            String className = UtilsMethod.getClassName(getClass());
            log.error("Class name {} is not found", className);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(StatusEnum.NOT_FOUND.getNameMessage(), className));
        }
        return mapper.asDTO(entity.get());
    }

    @Transactional(readOnly = true)
    public Page<D> search(C searchModel, Pageable pageable) {
        pageable = UtilsMethod.setSortToPageableIfNecessary(pageable, getClass());
        Specification<E> searchSpecification = specification.getSpecification(searchModel);
        Page<E> searchResultPage = repository.findAll(searchSpecification, pageable);
        return searchResultPage.map(mapper::asDTO);
    }

}
