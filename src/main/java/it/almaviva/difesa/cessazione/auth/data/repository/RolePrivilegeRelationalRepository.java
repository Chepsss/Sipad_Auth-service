package it.almaviva.difesa.cessazione.auth.data.repository;

import it.almaviva.difesa.cessazione.auth.data.common.GenericRepository;
import it.almaviva.difesa.cessazione.auth.data.entity.composite.RolePrivilegeCompositeKey;
import it.almaviva.difesa.cessazione.auth.data.entity.relational.RolePrivilegeRelational;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
@Transactional(readOnly = true)
public interface RolePrivilegeRelationalRepository extends GenericRepository<RolePrivilegeRelational, RolePrivilegeCompositeKey> {

    @Query("select case when count(v) = :privilegesIdSize then true else false end " +
            "from RolePrivilegeRelational v " +
            "where v.role.id = :roleId and " +
            "v.privilege.id in (:privilegesId)")
    boolean existPrivilegesByRoleId(Long roleId, Set<Long> privilegesId, Long privilegesIdSize);

    @Query("select rp from RolePrivilegeRelational rp where rp.role.id = :roleId")
    List<RolePrivilegeRelational> findAllPrivilegesByRoleId(Long roleId);

}