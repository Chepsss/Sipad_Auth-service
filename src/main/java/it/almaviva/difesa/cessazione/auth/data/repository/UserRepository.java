package it.almaviva.difesa.cessazione.auth.data.repository;

import it.almaviva.difesa.cessazione.auth.data.common.GenericRepository;
import it.almaviva.difesa.cessazione.auth.data.entity.Role;
import it.almaviva.difesa.cessazione.auth.data.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends GenericRepository<User, Long> {

    @Query("select case when count(u) = 1 then true else false end " +
            "from User u " +
            "inner join UserRoleRelational ur " +
            "on u = ur.user " +
            "inner join Role r " +
            "on r = ur.role " +
            "where u.employeeId = :employeeId " +
            "and r = :role")
    boolean existsByEmployeeIdAndRole(Long employeeId, Role role);

    @Query("select case when count(u) = 1 then true else false end " +
            "from User u " +
            "inner join UserRoleRelational ur " +
            "on u = ur.user " +
            "inner join Role r " +
            "on r = ur.role " +
            "where u.employeeId = :employeeId " +
            "and r in (:roles)")
    boolean existsByEmployeeIdAndRoles(Long employeeId, Set<Role> roles);

    Set<User> findAllByEmployeeIdIn(Set<Long> employeeIds);

    boolean existsByEmployeeId(Long employeeId);

    Page<User> findAllByRolesRoleCode(String roleCode, Pageable pageable);

    Page<User> findAll(Pageable pageable);

    Page<User> findAllByEmployeeIdNot(Long employeeId, Pageable pageable);

    List<User> findAllByEmployeeIdNot(Long employeeId);

    @Query("from User u where u.id in (:usersId)")
    Page<User> findPageOfUsersById(Set<Long> usersId, Pageable pageable);

    Optional<User> findByEmployeeId(Long employeeId);

    @Query("select u " +
            "from User u " +
            "inner join UserRoleRelational ur " +
            "on u = ur.user " +
            "inner join Role r " +
            "on r = ur.role " +
            "where r.roleCode in (:rolesCode)")
    List<User> findAllByRoleCodes(Set<String> rolesCode);

    @Query("select u " +
            "from User u " +
            "inner join UserRoleRelational ur " +
            "on u = ur.user " +
            "inner join Role r " +
            "on r = ur.role " +
            "where r.id in (:roleIds)")
    Page<User> findAllByRoleIds(List<Long> roleIds, Pageable pageable);

    Optional<User> findUserByEmployeeId(Long employeeId);

    Optional<User> findUserById(Long userId);

    @Query("select u " +
            "from User u " +
            "inner join UserRoleRelational ur " +
            "on u = ur.user " +
            "inner join Role r " +
            "on r = ur.role " +
            "where u.employeeId = :employeeId and r.roleCode = :roleCode ")
    Optional<User> findUsersByEmployeeIdsAndRoleCode(Long employeeId, String roleCode);

    @Query("select u " +
            "from User u " +
            "inner join UserRoleRelational ur " +
            "on u = ur.user " +
            "inner join Role r " +
            "on r = ur.role " +
            "where u.employeeId <> :employeeId and r.roleCode = :roleCode ")
    List<User> findUsersByEmployeeIdNotAndRoleCode(Long employeeId, String roleCode);

}
