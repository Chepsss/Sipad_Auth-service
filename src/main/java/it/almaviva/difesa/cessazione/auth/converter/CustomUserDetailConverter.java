package it.almaviva.difesa.cessazione.auth.converter;

import it.almaviva.difesa.cessazione.auth.model.CustomUserDetail;
import it.almaviva.difesa.cessazione.auth.model.mapper.CustomUserDetailDTO;
import it.almaviva.difesa.cessazione.auth.service.UserTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomUserDetailConverter implements Converter<Authentication, CustomUserDetailDTO> {

    private final UserTokenService userTokenService;

    @Override
    public CustomUserDetailDTO convert(Authentication authentication) {
        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
        CustomUserDetailDTO dto = new CustomUserDetailDTO();
        dto.setUserId(customUserDetail.getUserId());
        dto.setEmployeeId(customUserDetail.getEmployeeId());
        dto.setUsername(customUserDetail.getUsername());
        dto.setFirstName(customUserDetail.getFirstName());
        dto.setLastName(customUserDetail.getLastName());
        dto.setRankDescription(customUserDetail.getRankDescription());
        dto.setUuid(customUserDetail.getUuid());
        dto.setToken(userTokenService.getTokenByUserId(customUserDetail.getUserId()));
        dto.setAuthorities(customUserDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()));
        return dto;
    }

}
