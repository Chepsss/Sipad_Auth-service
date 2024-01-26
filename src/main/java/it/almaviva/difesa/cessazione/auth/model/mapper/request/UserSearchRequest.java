package it.almaviva.difesa.cessazione.auth.model.mapper.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchRequest implements GenericRequestDTO {

    private String fiscalCode;
    private String lastName;
    private String firstName;
    private String roleCode;
    private Long employeeId;

}
