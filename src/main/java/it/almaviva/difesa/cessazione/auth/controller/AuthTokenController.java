package it.almaviva.difesa.cessazione.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.almaviva.difesa.cessazione.auth.constant.Constant;
import it.almaviva.difesa.cessazione.auth.exception.TokenRefreshException;
import it.almaviva.difesa.cessazione.auth.model.CustomUserDetail;
import it.almaviva.difesa.cessazione.auth.model.mapper.UserTokenDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.request.UserTokenRequestDTO;
import it.almaviva.difesa.cessazione.auth.service.UserTokenService;
import it.almaviva.difesa.cessazione.auth.service.rest.SecurityManagerClient;
import it.almaviva.difesa.cessazione.auth.service.rest.response.PostAuthResponse;
import it.almaviva.difesa.cessazione.auth.util.RestUtilsMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(Constant.AUTH_URL)
@Slf4j
@RequiredArgsConstructor
public class AuthTokenController {

    private final UserTokenService userTokenService;
    private final SecurityManagerClient securityManagerClient;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Operation(summary = "Get auth Token")
    @PostMapping("/authorize")
    public ResponseEntity<UserTokenDTO> authorize(@RequestBody @Valid UserTokenRequestDTO userTokenRequestDTO) {
        var userDetails = (CustomUserDetail) userTokenService.loadUserByUsername(userTokenRequestDTO.getFiscalCode());

        Authentication authenticate = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticate);

        PostAuthResponse authResponse = securityManagerClient.getAuthorization(userTokenRequestDTO.getFiscalCode());
        var token = authResponse.getToken();
        var refreshToken = authResponse.getRefreshToken();

        userTokenRequestDTO.setUserId(userDetails.getUserId());
        userTokenRequestDTO.setUuid(userDetails.getUuid());
        userTokenRequestDTO.setToken(token);
        userTokenRequestDTO.setRefreshToken(refreshToken);

        UserTokenDTO userTokenDTO;
        synchronized (this) {
            userTokenDTO = userTokenService.saveUserToken(userTokenRequestDTO);
        }
        userTokenDTO.setExpiresAt(expiration);

        return ResponseEntity.ok(userTokenDTO);
    }

    @Operation(summary = "Get for Refresh Token")
    @GetMapping("/refreshToken")
    public ResponseEntity<UserTokenDTO> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        try {
            var userToken = userTokenService.findByRefreshToken(refreshToken);

            String fiscalCode = RestUtilsMethod.getClaimValueFromTokenByKey(userToken.getToken(), Constant.FISCAL_CODE);

            var userDetails = (CustomUserDetail) userTokenService.loadUserByUsername(fiscalCode);
            Authentication authenticate = new UsernamePasswordAuthenticationToken(userDetails, null,
                    userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticate);

            PostAuthResponse authResponse = securityManagerClient.getAuthorization(fiscalCode);
            var token = authResponse.getToken();
            var newRefreshToken = authResponse.getRefreshToken();

            UserTokenRequestDTO userTokenRequestDTO = new UserTokenRequestDTO();
            userTokenRequestDTO.setFiscalCode(fiscalCode);
            userTokenRequestDTO.setUserId(userDetails.getUserId());
            userTokenRequestDTO.setUuid(userDetails.getUuid());
            userTokenRequestDTO.setToken(token);
            userTokenRequestDTO.setRefreshToken(newRefreshToken);

            UserTokenDTO userTokenDTO = userTokenService.saveUserToken(userTokenRequestDTO);
            userTokenDTO.setExpiresAt(expiration);

            return ResponseEntity.ok(userTokenDTO);

        } catch (Exception ex) {
            log.error(">>>>>>>>>> ERROR in refreshToken ", ex);
            throw new TokenRefreshException(refreshToken, ex.getMessage());
        }
    }

    @GetMapping("/getUserDetails")
    @SecurityRequirement(name = "auth-token")
    public CustomUserDetail getUserDetails() {
        return (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
