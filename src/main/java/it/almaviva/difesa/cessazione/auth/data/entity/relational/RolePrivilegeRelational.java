package it.almaviva.difesa.cessazione.auth.data.entity.relational;

import it.almaviva.difesa.cessazione.auth.data.common.GenericEntity;
import it.almaviva.difesa.cessazione.auth.data.entity.Privilege;
import it.almaviva.difesa.cessazione.auth.data.entity.Role;
import it.almaviva.difesa.cessazione.auth.data.entity.composite.RolePrivilegeCompositeKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "TP_TRRUP_RUOLO_PRIVILEGIO")
public class RolePrivilegeRelational implements Serializable, GenericEntity {

    private static final long serialVersionUID = 923140868258104731L;

    @EmbeddedId
    @AttributeOverride(name = "roleId", column = @Column(name = "TRRUP_TRRUO_RUOLO_PK"))
    @AttributeOverride(name = "privilegeId", column = @Column(name = "TRRUP_TRPRI_PRIVILEGIO_PK"))
    private RolePrivilegeCompositeKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TRRUP_TRRUO_RUOLO_PK", nullable = false, insertable = false, updatable = false)
    @ToString.Exclude
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TRRUP_TRPRI_PRIVILEGIO_PK", nullable = false, insertable = false, updatable = false)
    @ToString.Exclude
    private Privilege privilege;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolePrivilegeRelational that = (RolePrivilegeRelational) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
