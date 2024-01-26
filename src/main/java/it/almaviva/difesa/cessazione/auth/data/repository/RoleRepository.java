package it.almaviva.difesa.cessazione.auth.data.repository;

import it.almaviva.difesa.cessazione.auth.data.common.GenericRepository;
import it.almaviva.difesa.cessazione.auth.data.entity.Role;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional(readOnly = true)
public interface RoleRepository extends GenericRepository<Role, Long> {

    boolean existsByRoleCode(String roleCode);

    Optional<Role> findByRoleCode(String roleCode);

    List<Role> findAllByRoleCodeNotIn(Set<String> roleCode);

    List<Role> findAllByRoleCodeIn(List<String> roleCode);

    List<Role> findRolesByUsersId(Long userId);

    List<Role> findRolesByIdIn(List<Long> rolesId);

}
