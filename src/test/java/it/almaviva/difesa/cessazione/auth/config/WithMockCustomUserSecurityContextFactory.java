package it.almaviva.difesa.cessazione.auth.config;

import it.almaviva.difesa.cessazione.auth.model.CustomUserDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@Slf4j
final class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser mockCustomUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        CustomUserDetail userDetail = MockCustomUserDetailUtils
                .getMockCustomUserDetail(mockCustomUser.username(), mockCustomUser.uuid(), mockCustomUser.rankDescription());
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }

}
