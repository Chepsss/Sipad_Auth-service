package it.almaviva.difesa.cessazione.auth.service;

import it.almaviva.difesa.cessazione.auth.constant.Constant;
import it.almaviva.difesa.cessazione.auth.data.entity.Privilege;
import it.almaviva.difesa.cessazione.auth.data.entity.Role;
import it.almaviva.difesa.cessazione.auth.data.repository.PrivilegeRepository;
import it.almaviva.difesa.cessazione.auth.data.repository.RoleRepository;
import it.almaviva.difesa.cessazione.auth.data.repository.specification.PrivilegeSpecification;
import it.almaviva.difesa.cessazione.auth.enums.StatusEnum;
import it.almaviva.difesa.cessazione.auth.model.mapper.CustomUserDetailDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.PrivilegeDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.PrivilegeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service("privilegeService")
@Transactional(readOnly = true)
@Slf4j
public class PrivilegeService extends GenericService<Privilege, Long, PrivilegeDTO, PrivilegeDTO> {

    private final PrivilegeRepository privilegeRepository;
    private final RoleRepository roleRepository;
    private final PrivilegeMapper privilegeMapper;
    private final PrivilegeSpecification privilegeSpecification;

    public PrivilegeService(PrivilegeRepository privilegeRepository,
                            RoleRepository roleRepository,
                            PrivilegeMapper privilegeMapper,
                            PrivilegeSpecification privilegeSpecification) {
        super(privilegeRepository, privilegeMapper, privilegeSpecification);
        this.privilegeRepository = privilegeRepository;
        this.roleRepository = roleRepository;
        this.privilegeMapper = privilegeMapper;
        this.privilegeSpecification = privilegeSpecification;
    }

    public List<PrivilegeDTO> findAllByRoleCode(String roleCode, CustomUserDetailDTO userLogged) {
        var role = roleRepository.findByRoleCode(roleCode)
                .orElseThrow(() -> {
                    log.error("Role code {} is not found", roleCode);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, StatusEnum.NO_ROLES_CODE.getNameMessage());
                });
        checkIfRoleIdIsValid(role, userLogged.getAuthorities());
        List<Privilege> privileges = new ArrayList<>(role.getPrivileges());
        if (CollectionUtils.isEmpty(privileges)) {
            log.error("Role ID {} has not Privilege", role.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, StatusEnum.NO_PRIVILEGES_ROLE.getNameMessage());
        }
        return privilegeMapper.asDTOList(privileges);
    }

    private void checkIfRoleIdIsValid(Role role, Set<String> rolesOfUserLogged) {
        List<String> invalidRoleCodes = List.of(Constant.ADMIN_ROLE_ID);
        if (invalidRoleCodes.contains(role.getRoleCode()) && !rolesOfUserLogged.contains(Constant.ADMIN_ROLE_ID)) {
            log.error("Role code {} is invalid", role.getRoleCode());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, StatusEnum.INVALID_ID_ROLE.getNameMessage());
        }
    }

}
