package it.almaviva.difesa.cessazione.auth.model.mapper.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserRequestCreateDTO extends UserRequestDTO {

    private Long employeeId;

}
