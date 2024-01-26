package it.almaviva.difesa.cessazione.auth.data.entity;

import it.almaviva.difesa.cessazione.auth.data.common.GenericEntity;
import it.almaviva.difesa.cessazione.auth.data.common.SortConstant;
import it.almaviva.difesa.cessazione.auth.data.common.Sortable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.domain.Sort;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "TP_TRPRI_PRIVILEGIO")
public class Privilege implements Serializable, GenericEntity, Sortable {

    private static final long serialVersionUID = -6121997012430669364L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "privilegeGenerator")
    @SequenceGenerator(name = "privilegeGenerator", sequenceName = "privilege_sequence", allocationSize = 1)
    @Column(name = "TRPRI_PRIVILEGIO_PK", nullable = false)
    private Long id;

    @NaturalId
    @Column(name = "TRPRI_COD_PRIVILEGIO", nullable = false)
    private String privilegeCode;

    @Column(name = "TRPRI_DESCRIZIONE", nullable = false)
    private String description;

    @ManyToMany
    @JoinTable(name = "TP_TRRUP_RUOLO_PRIVILEGIO",
            joinColumns = @JoinColumn(name = "TRRUP_TRPRI_PRIVILEGIO_PK"),
            inverseJoinColumns = @JoinColumn(name = "TRRUP_TRRUO_RUOLO_PK"))
    private Set<Role> roles = new LinkedHashSet<>();

    @Override
    public Sort getSort() {
        return SortConstant.SORT_BY_ID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Privilege privilege = (Privilege) o;
        return id != null && Objects.equals(id, privilege.id)
                && privilegeCode != null && Objects.equals(privilegeCode, privilege.privilegeCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(privilegeCode);
    }

}