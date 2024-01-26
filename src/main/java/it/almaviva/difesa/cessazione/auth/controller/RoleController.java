package it.almaviva.difesa.cessazione.auth.controller;

import it.almaviva.difesa.cessazione.auth.constant.Constant;
import it.almaviva.difesa.cessazione.auth.converter.CustomUserDetailConverter;
import it.almaviva.difesa.cessazione.auth.model.mapper.RoleDTO;
import it.almaviva.difesa.cessazione.auth.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(Constant.ROLE_URL)
@Validated
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final CustomUserDetailConverter userDetailConverter;

    @GetMapping("/{employeeId}")
    public ResponseEntity<List<RoleDTO>> getRolesByEmployeeId(@PathVariable @Min(1) Long employeeId) {
        var userLogged = userDetailConverter.convert(SecurityContextHolder.getContext().getAuthentication());
        assert userLogged != null : Constant.USER_MUST_BE_LOGGED;
        var authorities = userLogged.getAuthorities();
        return ResponseEntity.ok(roleService.findRolesByEmployeeId(authorities, employeeId));
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<RoleDTO>> getAll() {
        var userLogged = userDetailConverter.convert(SecurityContextHolder.getContext().getAuthentication());
        assert userLogged != null : Constant.USER_MUST_BE_LOGGED;
        return ResponseEntity.ok(roleService.getAll());
    }

    @GetMapping("/allNotAdmin")
    public ResponseEntity<List<RoleDTO>> getAllNotAdmin() {
        var userLogged = userDetailConverter.convert(SecurityContextHolder.getContext().getAuthentication());
        assert userLogged != null : Constant.USER_MUST_BE_LOGGED;
        return ResponseEntity.ok(roleService.getAllNotAdmin());
    }

}
