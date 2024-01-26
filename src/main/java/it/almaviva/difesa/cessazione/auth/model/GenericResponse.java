package it.almaviva.difesa.cessazione.auth.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@RequiredArgsConstructor
public class GenericResponse implements Serializable {

    private static final long serialVersionUID = -4461403357371961042L;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final String warningMSG;

}
