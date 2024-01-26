package it.almaviva.difesa.cessazione.auth.model.mapper.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class InstitutionRequestDTO implements GenericRequestDTO {

    @NotNull
    @Size(min = 3)
    private String description;

}
