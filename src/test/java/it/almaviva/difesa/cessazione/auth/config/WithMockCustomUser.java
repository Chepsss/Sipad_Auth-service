package it.almaviva.difesa.cessazione.auth.config;

import it.almaviva.difesa.cessazione.auth.util.Utils;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {

    String username() default Utils.FISCAL_CODE;

    String uuid() default Utils.UUID_TO_TEST;

    String rankDescription() default Utils.RANKDESC_TO_TEST;

}
