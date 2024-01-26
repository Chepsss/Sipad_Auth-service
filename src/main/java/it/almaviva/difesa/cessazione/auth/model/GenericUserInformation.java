package it.almaviva.difesa.cessazione.auth.model;

import it.almaviva.difesa.cessazione.auth.constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericUserInformation extends GenericRequest implements Serializable {

    private static final long serialVersionUID = 923677350231923849L;

    @Pattern(regexp = Constant.FISCAL_CODE_REGEX)
    private String fiscalCode;
    private String firstName;
    private String lastName;

}
