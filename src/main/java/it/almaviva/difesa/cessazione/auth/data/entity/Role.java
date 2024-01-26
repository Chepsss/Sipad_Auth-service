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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
@Table(name = "TP_TRRUO_RUOLO")
public class Role implements Serializable, GenericEntity, Sortable {

    private static final long serialVersionUID = 34507880435860818L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roleGenerator")
    @SequenceGenerator(name = "roleGenerator", sequenceName = "role_sequence", allocationSize = 1)
    @Column(name = "TRRUO_RUOLO_PK", nullable = false)
    private Long id;

    @Column(name = "TRRUO_COD_RUOLO", nullable = false)
    private String roleCode;

    @NaturalId
    @Column(name = "TRRUO_NOME", nullable = false)
    private String name;

    @Column(name = "TRRUO_DESCRIZIONE", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRRUO_CATPER_ABILITATO")
    private CategPersonal catPersAbilitato;

    @ManyToMany
    @JoinTable(name = "TR_TRUTR_UTENTE_RUOLO",
            joinColumns = @JoinColumn(name = "TRUTR_TRRUO_RUOLO_PK"),
            inverseJoinColumns = @JoinColumn(name = "TRUTR_TRUTE_UTENTE_PK"))
    private Set<User> users = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "roles")
    private Set<Privilege> privileges = new LinkedHashSet<>();

    @Override
    public Sort getSort() {
        return SortConstant.SORT_BY_NAME;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return id != null && Objects.equals(id, role.id)
                && roleCode != null && Objects.equals(roleCode, role.roleCode)
                && name != null && Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, roleCode);
    }

}