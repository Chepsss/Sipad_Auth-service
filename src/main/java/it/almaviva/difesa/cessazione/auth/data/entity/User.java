package it.almaviva.difesa.cessazione.auth.data.entity;

import it.almaviva.difesa.cessazione.auth.data.common.Editable;
import it.almaviva.difesa.cessazione.auth.data.common.GenericEntity;
import it.almaviva.difesa.cessazione.auth.data.common.SortConstant;
import it.almaviva.difesa.cessazione.auth.data.common.Sortable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "TB_TRUTE_UTENTE")
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable, GenericEntity, Editable<Long>, Sortable {

    private static final long serialVersionUID = 7715275489105522586L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userGenerator")
    @SequenceGenerator(name = "userGenerator", sequenceName = "user_sequence", allocationSize = 1)
    @Column(name = "TRUTE_UTENTE_PK", nullable = false)
    private Long id;

    @Column(name = "TRUTE_ANDIP_ID_PK", unique = true, nullable = false)
    private Long employeeId;

    @Size(max = 60)
    @NotNull
    @Column(name = "TRUTE_COD_FFAA", nullable = false, length = 60)
    private String truteCodFfaa;

    @NotNull
    @Column(name = "TRUTE_INATTIVO", nullable = false)
    private Boolean truteInattivo = false;

    @CreatedBy
    @Size(max = 20)
    @NotNull
    @Column(name = "TRUTE_COD_INS", nullable = false, length = 20)
    private String insertCode;

    @CreatedDate
    @NotNull
    @Column(name = "TRUTE_DATA_INS", nullable = false)
    private LocalDateTime insertDate;

    @LastModifiedBy
    @Size(max = 20)
    @NotNull
    @Column(name = "TRUTE_COD_ULT_AGG", nullable = false, length = 20)
    private String lastUpdatedCode;

    @LastModifiedDate
    @NotNull
    @Column(name = "TRUTE_DATA_ULT_AGG", nullable = false)
    private LocalDateTime lastUpdatedDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserToken> userTokens = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
    private Set<Role> roles = new LinkedHashSet<>();

    @Override
    public Sort getSort() {
        return SortConstant.SORT_BY_LAST_UPDATED_DATE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id)
                && employeeId != null && Objects.equals(employeeId, user.employeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId);
    }

}