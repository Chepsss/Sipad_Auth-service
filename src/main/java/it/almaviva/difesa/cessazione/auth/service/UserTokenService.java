package it.almaviva.difesa.cessazione.auth.service;

import com.fasterxml.uuid.Generators;
import it.almaviva.difesa.cessazione.auth.constant.Constant;
import it.almaviva.difesa.cessazione.auth.data.entity.Privilege;
import it.almaviva.difesa.cessazione.auth.data.entity.Role;
import it.almaviva.difesa.cessazione.auth.data.entity.User;
import it.almaviva.difesa.cessazione.auth.data.entity.UserToken;
import it.almaviva.difesa.cessazione.auth.data.repository.PrivilegeRepository;
import it.almaviva.difesa.cessazione.auth.data.repository.UserRepository;
import it.almaviva.difesa.cessazione.auth.data.repository.UserTokenRepository;
import it.almaviva.difesa.cessazione.auth.enums.StatusEnum;
import it.almaviva.difesa.cessazione.auth.exception.UserUnauthorizedException;
import it.almaviva.difesa.cessazione.auth.model.CustomUserDetail;
import it.almaviva.difesa.cessazione.auth.model.mapper.UserDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.UserTokenDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.UserTokenMapper;
import it.almaviva.difesa.cessazione.auth.model.mapper.request.UserTokenRequestDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.request.UserTokenRequestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserTokenService implements UserDetailsService {

    private final UserTokenRepository userTokenRepository;
    private final UserTokenRequestMapper userTokenRequestMapper;
    private final UserTokenMapper userTokenMapper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PrivilegeRepository privilegeRepository;

    @Transactional
    public UserTokenDTO saveUserToken(UserTokenRequestDTO userTokenRequestDTO) {
        log.debug("INIT createUserToken");

        User user = userRepository.findUserById(userTokenRequestDTO.getUserId()).orElse(new User());
        log.debug("User found is EmployeeId>>>>>>>>>> {}, UserId>>>>>>>>>> {}", user.getEmployeeId(), userTokenRequestDTO.getUserId());

        UserToken userToken = userTokenRepository.findByInsertCode(userTokenRequestDTO.getFiscalCode()).orElse(null);

        if (userToken == null) {
            log.debug("<============== User Token is not found =================>");
            userToken = userTokenRequestMapper.asEntity(userTokenRequestDTO);
            if (user.getEmployeeId() == null) {
                user.setEmployeeId(userTokenRequestDTO.getEmployeeId());
                userToken.setUser(userRepository.save(user));
            }
        } else {
            log.debug("<============== User Token is found UserId: {} =================>", userToken.getId());
            userToken.setToken(userTokenRequestDTO.getToken());
            userToken.setRefreshToken(userTokenRequestDTO.getRefreshToken());
            userToken.setUser(user);
            userToken.setUuid(userTokenRequestDTO.getUuid());
        }

        userTokenRepository.save(userToken);
        return userTokenMapper.asDTO(userToken);
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String fiscalCode) throws UsernameNotFoundException {
        return loadUser(fiscalCode);
    }

    private CustomUserDetail loadUser(String fiscalCode) {

        AtomicReference<CustomUserDetail> customUserDetail = new AtomicReference<>(new CustomUserDetail());

        UserDTO userDTO = userService.findUserDetailByFiscalCode(fiscalCode);
        if (userDTO != null) {

            userRepository.findByEmployeeId(userDTO.getEmployeeId())
                    .ifPresentOrElse(user ->
                                    customUserDetail.set(new CustomUserDetail(
                                            user.getId(),
                                            userDTO.getEmployeeId(),
                                            userDTO.getFiscalCode(),
                                            userDTO.getFirstName(),
                                            userDTO.getLastName(),
                                            userDTO.getRankDescription(),
                                            Generators.timeBasedGenerator().generate().toString(),
                                            getAuthorities(privilegeRepository.findPrivilegesByRoles(user.getRoles()), user.getRoles())
                                    )),
                            () -> {
                                String error = String.format(Constant.USER_NOT_FOUND, fiscalCode);
                                log.error(">>>>>> ERROR: {}", error);
                                throw new UserUnauthorizedException(error);
                            }
                    );

        } else {
            log.error("SIPAD User not found with fiscal code: {}", fiscalCode);
            throw new UsernameNotFoundException(String.format(Constant.SIPAD_USER_NOT_FOUND, fiscalCode));
        }
        return customUserDetail.get();
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Collection<Privilege> privileges, Set<Role> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        privileges.stream().map(p -> new SimpleGrantedAuthority(p.getPrivilegeCode())).forEach(authorities::add);

        if (!roles.isEmpty()) {
            roles.forEach(roleCode -> authorities.add(new SimpleGrantedAuthority(roleCode.getRoleCode())));
        }

        return authorities;
    }

    @Transactional(readOnly = true)
    public UserTokenDTO findTokenByUuid(String uuid) {

        Optional<UserToken> userToken = userTokenRepository.findByUuid(uuid);

        if (userToken.isPresent()) {
            return userTokenMapper.asDTO(userToken.get());
        } else {
            log.error("Token with uuid {} is not found", uuid);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(StatusEnum.USER_NOT_FINDABLE_TOKEN.getNameMessage(), uuid));
        }
    }

    @Transactional(readOnly = true)
    public UserTokenDTO findByRefreshToken(String refreshToken) {
        log.debug("INIT refreshToken");

        return userTokenRepository.findByRefreshToken(refreshToken)
                .map(userTokenMapper::asDTO)
                .orElseThrow(() -> {
                    log.error("RefreshToken {} is not found", refreshToken);
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, StatusEnum.NOT_FOUND.getNameMessage());
                });
    }

    @Transactional(readOnly = true)
    public String getTokenByUserId(Long userId) {
        log.debug("INIT refreshToken");
        UserToken userToken = userTokenRepository.findByUserId(userId).orElse(null);
        return (userToken != null) ? userToken.getToken() : null;
    }

}
