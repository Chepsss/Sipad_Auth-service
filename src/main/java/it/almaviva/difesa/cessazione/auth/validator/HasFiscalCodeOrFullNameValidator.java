package it.almaviva.difesa.cessazione.auth.validator;

import it.almaviva.difesa.cessazione.auth.annotations.HasFiscalCodeOrFullName;
import it.almaviva.difesa.cessazione.auth.model.GenericUserInformation;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HasFiscalCodeOrFullNameValidator implements ConstraintValidator<HasFiscalCodeOrFullName, GenericUserInformation> {

    @Override
    public void initialize(HasFiscalCodeOrFullName constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(GenericUserInformation value, ConstraintValidatorContext context) {
        if (value.getFiscalCode() == null && (value.getFirstName() != null || value.getLastName() != null)) {
            return true;
        } else {
            return hasEmployeeDetailCriteriaOnlyFiscalCode(value);
        }
    }

    private boolean hasEmployeeDetailCriteriaOnlyFiscalCode(GenericUserInformation genericEmployee) {
        if (genericEmployee.getFiscalCode() != null) {
            return hasOnlyFiscalCode(genericEmployee);
        }
        return false;
    }

    public boolean hasOnlyFiscalCode(GenericUserInformation GenericUserInformation) {
        try {
            List<Method> getterMethods = Arrays.stream(GenericUserInformation.getClass().getMethods())
                    .filter(method -> method.getName().startsWith("get") && !method.getName().equals("getClass") && !method.getName().equals("getFiscalCode"))
                    .collect(Collectors.toCollection(ArrayList::new));
            var hasOnlyFiscalCode = true;
            for (Method method : getterMethods) {
                if (!ObjectUtils.isEmpty(method.invoke(GenericUserInformation))) {
                    hasOnlyFiscalCode = false;
                    break;
                }
            }
            return hasOnlyFiscalCode;
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

}
