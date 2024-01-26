package it.almaviva.difesa.cessazione.auth.config;

import it.almaviva.difesa.cessazione.auth.constant.Constant;
import it.almaviva.difesa.cessazione.auth.model.CustomUserDetail;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@NoArgsConstructor
public class MockCustomUserDetailUtils {

    public static CustomUserDetail getMockCustomUserDetail(String username, String uuid, String rankDescription) {
        CustomUserDetail userDetail = new CustomUserDetail();
        userDetail.setUserId(1L);
        userDetail.setEmployeeId(1L);
        userDetail.setUsername(username);
        userDetail.setFirstName("Mario");
        userDetail.setLastName("Rossi");
        userDetail.setRankDescription(rankDescription);
        userDetail.setUuid(uuid);
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(Constant.ADMIN_ROLE_ID),
                new SimpleGrantedAuthority(Constant.APPROVER_GRADUATED_ROLE_ID),
                new SimpleGrantedAuthority(Constant.INSTRUCTOR_OFFICER_ROLE_ID)
        );
        userDetail.setAuthorities(authorities);
        return userDetail;
    }

}
