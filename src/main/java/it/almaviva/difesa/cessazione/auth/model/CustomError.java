package it.almaviva.difesa.cessazione.auth.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.Path;

@Data
public class CustomError {

    private String field;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String objectName;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Path propertyPath;
    private String messageError;

}