package it.almaviva.difesa.cessazione.auth.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.almaviva.difesa.cessazione.auth.constant.Constant;
import it.almaviva.difesa.cessazione.auth.converter.CustomUserDetailConverter;
import it.almaviva.difesa.cessazione.auth.data.entity.Privilege;
import it.almaviva.difesa.cessazione.auth.model.mapper.PrivilegeDTO;
import it.almaviva.difesa.cessazione.auth.service.PrivilegeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping(Constant.PRIVILEGE_URL)
@Validated
public class PrivilegeController extends GenericController<Privilege, Long, PrivilegeDTO, PrivilegeDTO> {

    private final PrivilegeService privilegeService;
    private final CustomUserDetailConverter userDetailConverter;

    protected PrivilegeController(PrivilegeService privilegeService, CustomUserDetailConverter userDetailConverter) {
        super(privilegeService, PrivilegeDTO.class);
        this.privilegeService = privilegeService;
        this.userDetailConverter = userDetailConverter;
    }

    @GetMapping(
            value = "/{roleCode}",
            produces = "application/json"
    )
    @SecurityRequirement(name = "auth-token")
    public ResponseEntity<List<PrivilegeDTO>> getAllByRoleCode(@PathVariable @NotBlank String roleCode, Authentication authentication) {
        var userLogged = userDetailConverter.convert(authentication);
        assert userLogged != null;
        return ResponseEntity.ok(privilegeService.findAllByRoleCode(roleCode, userLogged));
    }

}
