package it.almaviva.difesa.cessazione.auth.data.entity;

import it.almaviva.difesa.cessazione.auth.data.common.Editable;
import it.almaviva.difesa.cessazione.auth.data.common.GenericEntity;
import it.almaviva.difesa.cessazione.auth.data.common.SortConstant;
import it.almaviva.difesa.cessazione.auth.data.common.Sortable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "TB_TRUTT_UTENTE_TOKEN")
public class UserToken extends AbstractAuditingEntity implements Serializable, GenericEntity, Editable<Long>, Sortable {

    private static final long serialVersionUID = 1670372118384144598L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tokenGenerator")
    @SequenceGenerator(name = "tokenGenerator", sequenceName = "token_sequence", allocationSize = 1)
    @Column(name = "TRUTT_UTENTE_TOKEN_PK", nullable = false)
    private Long id;

    @Column(name = "TRUTT_TOKEN", nullable = false)
    private String token;

    @Column(name = "TRUTT_REFRESH_TOKEN", nullable = false)
    private String refreshToken;

    @Column(name = "TRUTT_UUID", nullable = false)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRUTE_UTENTE_PK")
    private User user;

    @Override
    public Sort getSort() {
        return SortConstant.SORT_BY_LAST_UPDATED_DATE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserToken userToken = (UserToken) o;
        return id != null && Objects.equals(id, userToken.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}