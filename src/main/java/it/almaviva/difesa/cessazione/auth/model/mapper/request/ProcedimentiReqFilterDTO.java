package it.almaviva.difesa.cessazione.auth.model.mapper.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class ProcedimentiReqFilterDTO implements GenericRequestDTO {

    @NotNull
    private String codiceFiscale;
    private String filtro;

}
