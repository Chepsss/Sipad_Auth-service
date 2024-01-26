package it.almaviva.difesa.cessazione.auth.data.entity.relational;

import it.almaviva.difesa.cessazione.auth.data.common.GenericEntity;
import it.almaviva.difesa.cessazione.auth.data.entity.Role;
import it.almaviva.difesa.cessazione.auth.data.entity.User;
import it.almaviva.difesa.cessazione.auth.data.entity.composite.UserRoleCompositeKey;
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
@Table(name = "TR_TRUTR_UTENTE_RUOLO")
public class UserRoleRelational implements Serializable, GenericEntity {

    private static final long serialVersionUID = 1846194384486885067L;

    @EmbeddedId
    @AttributeOverride(name = "userId", column = @Column(name = "TRUTR_TRUTE_UTENTE_PK"))
    @AttributeOverride(name = "roleId", column = @Column(name = "TRUTR_TRRUO_RUOLO_PK"))
    private UserRoleCompositeKey id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TRUTR_TRUTE_UTENTE_PK", nullable = false, insertable = false, updatable = false)
    @ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TRUTR_TRRUO_RUOLO_PK", nullable = false, insertable = false, updatable = false)
    @ToString.Exclude
    private Role role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRoleRelational that = (UserRoleRelational) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
