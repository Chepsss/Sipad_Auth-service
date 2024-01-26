package it.almaviva.difesa.cessazione.auth.data.entity.composite;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Embeddable;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Embeddable
public class RolePrivilegeCompositeKey implements Serializable {

    private static final long serialVersionUID = 1430055231462045977L;

    private Long roleId;
    private Long privilegeId;

}