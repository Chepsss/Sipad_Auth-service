package it.almaviva.difesa.cessazione.auth.controller;

import it.almaviva.difesa.cessazione.auth.data.common.GenericCriteriaModel;
import it.almaviva.difesa.cessazione.auth.data.common.GenericEntity;
import it.almaviva.difesa.cessazione.auth.data.common.Sortable;
import it.almaviva.difesa.cessazione.auth.model.mapper.response.GenericResponseDTO;
import it.almaviva.difesa.cessazione.auth.service.GenericService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public abstract class GenericController<E extends GenericEntity & Sortable, I extends Serializable, D extends GenericResponseDTO, C extends GenericCriteriaModel> {

    private final GenericService<E, I, D, C> service;

    private final Class<D> typeD;

    @GetMapping("/all-paged")
    public ResponseEntity<Page<D>> getAllPaged(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/all")
    public ResponseEntity<List<D>> getAll() {
        List<D> listDTO = service.findAll();
        return ResponseEntity.ok(listDTO);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<D>> search(@RequestBody C searchModel, Pageable pageable) {
        return ResponseEntity.ok(service.search(searchModel, pageable));
    }

}
