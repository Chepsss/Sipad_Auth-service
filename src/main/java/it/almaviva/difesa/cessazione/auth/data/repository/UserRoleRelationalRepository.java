package it.almaviva.difesa.cessazione.auth.data.repository;

import it.almaviva.difesa.cessazione.auth.data.common.GenericRepository;
import it.almaviva.difesa.cessazione.auth.data.entity.Role;
import it.almaviva.difesa.cessazione.auth.data.entity.User;
import it.almaviva.difesa.cessazione.auth.data.entity.composite.UserRoleCompositeKey;
import it.almaviva.difesa.cessazione.auth.data.entity.relational.UserRoleRelational;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserRoleRelationalRepository extends GenericRepository<UserRoleRelational, UserRoleCompositeKey> {

    @Query("select up.id.roleId from UserRoleRelational up where up.id.userId = :userId")
    Set<Long> findRolesIdByUserId(Long userId);

    Set<UserRoleRelational> findUserRoleRelationalById_UserId(Long userId);

    Set<UserRoleRelational> findUserRoleRelationalById_UserIdIn(Set<Long> usersId);

    @Modifying
    @Query("delete from UserRoleRelational up where up.user = :user and up.role in(:roles)")
    void deleteRolesIdRelatedToUserId(Set<Role> roles, User user);

    void deleteUserRoleRelationalByUser(User user);

}