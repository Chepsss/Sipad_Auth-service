package it.almaviva.difesa.cessazione.auth.data.repository;

import it.almaviva.difesa.cessazione.auth.data.common.GenericRepository;
import it.almaviva.difesa.cessazione.auth.data.common.GenericSearchRepository;
import it.almaviva.difesa.cessazione.auth.data.entity.Privilege;
import it.almaviva.difesa.cessazione.auth.data.entity.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PrivilegeRepository extends GenericRepository<Privilege, Long>, GenericSearchRepository<Privilege> {

    @Query("select case when count(p) = :privilegesIdSize then true else false end from Privilege p where p.id in (:privilegesId)")
    boolean existPrivilegesById(Set<Long> privilegesId, Long privilegesIdSize);

    @Query(value = "select p " +
            "from Privilege p " +
            "inner join RolePrivilegeRelational rp " +
            "on p = rp.privilege " +
            "inner join Role r " +
            "on r = rp.role " +
            "where r in (:roles)")
    List<Privilege> findPrivilegesByRoles(Set<Role> roles);

}