package it.almaviva.difesa.cessazione.auth.model.mapper.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class EmployeeDetailResponseDTO implements GenericResponseDTO {

    private String firstName;
    private String lastName;
    private String armedForceDescription;
    private String fiscalCode;
    private String staffPositionDescription;
    private String rankDescription;
    private String staffCategoryDescription;
    private String roleDescription;
    private LocalDate birthDate;
    private String abbrBirthProvince;
    private String birthCity;
    private String civilStatus;
    private String gender;

}
