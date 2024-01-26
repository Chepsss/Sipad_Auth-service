package it.almaviva.difesa.cessazione.auth.service;

import it.almaviva.difesa.cessazione.auth.constant.Constant;
import it.almaviva.difesa.cessazione.auth.data.entity.Role;
import it.almaviva.difesa.cessazione.auth.data.entity.User;
import it.almaviva.difesa.cessazione.auth.data.repository.RoleRepository;
import it.almaviva.difesa.cessazione.auth.data.repository.UserRepository;
import it.almaviva.difesa.cessazione.auth.data.repository.specification.RoleSpecification;
import it.almaviva.difesa.cessazione.auth.model.mapper.RoleDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.RoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service("roleService")
@Transactional(readOnly = true)
@Slf4j
public class RoleService extends GenericService<Role, Long, RoleDTO, RoleDTO> {

    private final RoleRepository roleRepository;
    private final RoleSpecification roleSpecification;
    private final RoleMapper roleMapper;
    private final UserRepository userRepository;

    public RoleService(RoleRepository roleRepository,
                       RoleMapper roleMapper,
                       RoleSpecification roleSpecification,
                       UserRepository userRepository) {
        super(roleRepository, roleMapper, roleSpecification);
        this.roleRepository = roleRepository;
        this.roleSpecification = roleSpecification;
        this.roleMapper = roleMapper;
        this.userRepository = userRepository;
    }

    public List<RoleDTO> findRolesByEmployeeId(Set<String> roleCodes, Long employeeId) {
        List<User> users = userRepository.findAllByRoleCodes(roleCodes);
        Set<String> roleCodesToExclude = new LinkedHashSet<>();
        users.stream()
                .filter(user -> user.getEmployeeId().compareTo(employeeId) != 0)
                .map(user -> user.getRoles().stream().map(Role::getRoleCode).collect(Collectors.toSet()))
                .forEach(roleCodesToExclude::addAll);

        var roles = roleRepository.findAllByRoleCodeNotIn(roleCodesToExclude);
        return roleMapper.asDTOList(roles);
    }

    public List<RoleDTO> getAll() {
        List<Role> roles = roleRepository.findAllByRoleCodeNotIn(Set.of(Constant.ADMIN_ROLE_ID, Constant.TEMPLATE_ADMIN_ROLE_ID));
        return roleMapper.asDTOList(roles);
    }

    public List<RoleDTO> getAllNotAdmin() {
        List<Role> roles = roleRepository.findAllByRoleCodeNotIn(Set.of(Constant.ADMIN_ROLE_ID));
        return roleMapper.asDTOList(roles);
    }

}
