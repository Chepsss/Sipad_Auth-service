package it.almaviva.difesa.cessazione.auth.annotations;

import it.almaviva.difesa.cessazione.auth.validator.HasFiscalCodeOrFullNameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {HasFiscalCodeOrFullNameValidator.class})
public @interface HasFiscalCodeOrFullName {

    String message() default "{it.almaviva.difesa.cessazione.auth.model.GenericUserInformation.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
