package it.almaviva.difesa.cessazione.auth.service;

import it.almaviva.difesa.cessazione.auth.converter.CustomUserDetailConverter;
import it.almaviva.difesa.cessazione.auth.model.mapper.CustomUserDetailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final CustomUserDetailConverter userDetailConverter;

    public CustomUserDetailDTO getUserDetails() {
        return userDetailConverter.convert(SecurityContextHolder.getContext().getAuthentication());
    }

    public String getUserToken() {
        CustomUserDetailDTO customerUserDetail = getUserDetails();
        return customerUserDetail.getToken();
    }

    public Long getEmployeeIdOfUserLogged() {
        CustomUserDetailDTO customerUserDetail = getUserDetails();
        return customerUserDetail.getEmployeeId();
    }

    public Set<String> getUserRoles() {
        CustomUserDetailDTO user = getUserDetails();
        return user.getAuthorities();
    }

}
