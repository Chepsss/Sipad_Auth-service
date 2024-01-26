package it.almaviva.difesa.cessazione.auth.controller;

import it.almaviva.difesa.cessazione.auth.constant.Constant;
import it.almaviva.difesa.cessazione.auth.converter.CustomUserDetailConverter;
import it.almaviva.difesa.cessazione.auth.model.EmployeeResponseDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.request.EmployeeSearchRequest;
import it.almaviva.difesa.cessazione.auth.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(Constant.EMPLOYEE_URL)
public class EmployeeController {

    private final EmployeeService employeeService;
    private final CustomUserDetailConverter userDetailConverter;

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeeResponseDTO> findById(@PathVariable @Min(1) Long id) {
        var userLogged = userDetailConverter.convert(SecurityContextHolder.getContext().getAuthentication());
        assert userLogged != null;
        return ResponseEntity.ok(employeeService.findEmployeeDetailById(id));
    }

    @PostMapping(value = "/searchEmployeeIds")
    public ResponseEntity<List<Long>> searchEmployeeIds(@RequestBody EmployeeSearchRequest searchDTO) {
        var userLogged = userDetailConverter.convert(SecurityContextHolder.getContext().getAuthentication());
        assert userLogged != null;
        return ResponseEntity.ok(employeeService.findByLastNameAndFirstNameOrCodiceFiscale(searchDTO));
    }

    @PostMapping(value = "/employeesMilitary", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<EmployeeResponseDTO>> getAllEmployeesMilitaryPaged(@RequestBody EmployeeSearchRequest searchDTO, Pageable pageable) {
        var userLogged = userDetailConverter.convert(SecurityContextHolder.getContext().getAuthentication());
        assert userLogged != null;
        return ResponseEntity.ok(employeeService.getAllEmployeesMilitaryPaged(searchDTO, userLogged, pageable));
    }

}