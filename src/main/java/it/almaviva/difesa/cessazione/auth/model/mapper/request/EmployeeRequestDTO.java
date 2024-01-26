package it.almaviva.difesa.cessazione.auth.model.mapper.request;

import it.almaviva.difesa.cessazione.auth.model.GenericUserInformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequestDTO extends GenericUserInformation {

    private static final long serialVersionUID = 4668992845099622071L;

    private Long loggedEmployeeId;

}
