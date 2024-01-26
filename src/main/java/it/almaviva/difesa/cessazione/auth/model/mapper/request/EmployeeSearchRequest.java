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
public class EmployeeSearchRequest extends GenericUserInformation {

    private static final long serialVersionUID = -766048693193120288L;

    private String serialNumber;
    private String armedForceId;
    private String staffPositionId;
    private Short staffCategoryId;
    private String rankId;
    private String employeeCategory;
    private String roleCode;
    private Long employeeId;

}
