package it.almaviva.difesa.cessazione.auth.model.mapper.request;

import it.almaviva.difesa.cessazione.auth.model.mapper.UserTokenDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class UserTokenRequestDTO extends UserTokenDTO implements GenericRequestDTO {

    @NotNull
    private String fiscalCode;
    private String uuid;

    public String getFiscalCode() {
        return fiscalCode.toUpperCase();
    }

}
