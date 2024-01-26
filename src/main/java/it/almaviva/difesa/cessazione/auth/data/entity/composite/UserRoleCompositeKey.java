package it.almaviva.difesa.cessazione.auth.data.entity.composite;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class UserRoleCompositeKey implements Serializable {

    private static final long serialVersionUID = 7849127422203597524L;

    private Long userId;
    private Long roleId;

}