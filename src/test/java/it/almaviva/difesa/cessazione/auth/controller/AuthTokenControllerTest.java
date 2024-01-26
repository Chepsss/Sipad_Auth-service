package it.almaviva.difesa.cessazione.auth.controller;

import it.almaviva.difesa.cessazione.auth.config.MockCustomUserDetailUtils;
import it.almaviva.difesa.cessazione.auth.model.CustomUserDetail;
import it.almaviva.difesa.cessazione.auth.model.mapper.UserTokenDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.request.UserTokenRequestDTO;
import it.almaviva.difesa.cessazione.auth.service.UserTokenService;
import it.almaviva.difesa.cessazione.auth.util.JwtTokenUtil;
import it.almaviva.difesa.cessazione.auth.util.RestUtilsMethod;
import it.almaviva.difesa.cessazione.auth.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@Slf4j
class AuthTokenControllerTest {

    private String uuid;
    private CustomUserDetail userDetail;
    private UserTokenRequestDTO userReq;
    private String token;

    private MockMvc mockMvc;

    @Mock
    private UserTokenService userTokenService;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private AuthTokenController authTokenController;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID().toString();
        userDetail = MockCustomUserDetailUtils
                .getMockCustomUserDetail(Utils.FISCAL_CODE, uuid, Utils.RANKDESC_TO_TEST);
        token = Utils.getToken(userDetail);
        userReq = new UserTokenRequestDTO();
        userReq.setFiscalCode(Utils.FISCAL_CODE);
        userReq.setUuid(uuid);
        mockMvc = MockMvcBuilders.standaloneSetup(authTokenController).build();
    }

    @Test
    @DisplayName("Test API Authorize in AuthTokenController")
    void authorize_success() throws Exception {

        when(userTokenService.loadUserByUsername(userReq.getFiscalCode())).thenReturn(userDetail);
        when(jwtTokenUtil.generateToken(userDetail)).thenReturn(token);
        when(jwtTokenUtil.generateRefreshToken()).thenReturn(uuid);
        when(userTokenService.saveUserToken(any(UserTokenRequestDTO.class))).thenReturn(getUserTokenDTO(userReq, token, uuid));

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/auth/authorize")
                                .contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8)
                                .content(Utils.convertObjectToJsonString(userReq)))
                .andExpect(status().isOk()).andReturn();

        String response = result.getResponse().getContentAsString();
        log.debug("Authorize ==> " + response);
        assertNotNull(response);
    }

    @Test
    @DisplayName("Test API RefreshToken in AuthTokenController")
    void refreshToken() throws Exception {
        String fiscalCode = Utils.FISCAL_CODE;

        when(userTokenService.findByRefreshToken(uuid)).thenReturn(getUserTokenDTO(userReq, token, uuid));
        try (MockedStatic<RestUtilsMethod> restUtils = mockStatic(RestUtilsMethod.class)) {
            restUtils.when(() -> RestUtilsMethod.getClaimValueFromTokenByKey(token, "fiscalCode"))
                    .thenReturn(fiscalCode);
        }
        when(userTokenService.loadUserByUsername(fiscalCode)).thenReturn(userDetail);

        String newToken = Utils.getToken(userDetail);
        String newUUID = UUID.randomUUID().toString();

        when(jwtTokenUtil.generateToken(userDetail)).thenReturn(newToken);
        when(jwtTokenUtil.generateRefreshToken()).thenReturn(newUUID);
        when(userTokenService.saveUserToken(any(UserTokenRequestDTO.class))).thenReturn(getUserTokenDTO(userReq, newToken, newUUID));

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/auth/refreshToken")
                                .param("refreshToken", uuid)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk()).andReturn();

        String response = result.getResponse().getContentAsString();
        log.debug("RefreshToken ==> " + response);
        assertNotNull(response);
    }

    private UserTokenDTO getUserTokenDTO(UserTokenRequestDTO user, String token, String uuid) {
        UserTokenDTO dto = new UserTokenDTO();
        dto.setUserId(user.getUserId());
        dto.setToken(token);
        dto.setRefreshToken(uuid);

        return dto;
    }

}