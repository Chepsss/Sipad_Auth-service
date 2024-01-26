package it.almaviva.difesa.cessazione.auth.data.entity;

import it.almaviva.difesa.cessazione.auth.data.common.GenericEntity;
import it.almaviva.difesa.cessazione.auth.data.common.SortConstant;
import it.almaviva.difesa.cessazione.auth.data.common.Sortable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "TP_CE_CATP_CATEG_PERSONALE")
public class CategPersonal implements Serializable, GenericEntity, Sortable {

    private static final long serialVersionUID = 3731748082513615360L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categGenerator")
    @SequenceGenerator(name = "categGenerator", sequenceName = "catpers_sequence", allocationSize = 1)
    @Column(name = "CECATP_PK", nullable = false)
    private Long id;

    @Column(name = "CECATP_COD", nullable = false, length = 25)
    private String categCod;

    @Column(name = "CECATP_DESC", nullable = false)
    private String categDesc;

    @OneToMany(mappedBy = "catPersAbilitato")
    private Set<Role> roles = new LinkedHashSet<>();

    @Override
    public Sort getSort() {
        return SortConstant.SORT_BY_ID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategPersonal that = (CategPersonal) o;
        return id.equals(that.id)
                && Objects.equals(categCod, that.categCod)
                && Objects.equals(categDesc, that.categDesc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, categCod, categDesc);
    }

}