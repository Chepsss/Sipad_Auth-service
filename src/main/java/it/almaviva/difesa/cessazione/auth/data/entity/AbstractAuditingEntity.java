package it.almaviva.difesa.cessazione.auth.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Base abstract class for entities which will hold definitions for created, last modified, created by,
 * last modified by attributes.
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @CreatedBy
    @Column(name = "COD_INS", nullable = false, length = 20, updatable = false)
    @JsonIgnore
    private String insertCode;

    @CreatedDate
    @Column(name = "DATA_INS", updatable = false)
    @JsonIgnore
    private LocalDateTime insertDate = LocalDateTime.now();

    @LastModifiedBy
    @Column(name = "COD_ULT_AGG", length = 20)
    @JsonIgnore
    private String lastUpdateCode;

    @LastModifiedDate
    @Column(name = "DATA_ULT_AGG")
    @JsonIgnore
    private LocalDateTime lastUpdatedDate = LocalDateTime.now();

}
