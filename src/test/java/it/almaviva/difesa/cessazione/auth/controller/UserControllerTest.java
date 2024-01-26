package it.almaviva.difesa.cessazione.auth.controller;

import it.almaviva.difesa.cessazione.auth.config.MockCustomUserDetailUtils;
import it.almaviva.difesa.cessazione.auth.config.WithMockCustomUser;
import it.almaviva.difesa.cessazione.auth.constant.Constant;
import it.almaviva.difesa.cessazione.auth.converter.CustomUserDetailConverter;
import it.almaviva.difesa.cessazione.auth.data.entity.User;
import it.almaviva.difesa.cessazione.auth.data.repository.UserRepository;
import it.almaviva.difesa.cessazione.auth.model.CustomUserDetail;
import it.almaviva.difesa.cessazione.auth.model.mapper.CustomUserDetailDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.PrivilegeDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.RoleDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.UserDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.request.EmployeeSearchRequest;
import it.almaviva.difesa.cessazione.auth.model.mapper.request.UserRequestCreateDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.request.UserRequestCreateMapper;
import it.almaviva.difesa.cessazione.auth.service.UserService;
import it.almaviva.difesa.cessazione.auth.util.Code;
import it.almaviva.difesa.cessazione.auth.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class})
@ContextConfiguration
@WithMockCustomUser
@Slf4j
class UserControllerTest {

    private MockMvc mockMvc;
    private CustomUserDetail userDetail;

    @Mock
    private UserRequestCreateMapper userCreateMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private CustomUserDetailConverter userDetailConverter;
    @InjectMocks
    private UserController userController;
    @InjectMocks
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @BeforeEach
    void setUp() {
        userDetail = MockCustomUserDetailUtils
                .getMockCustomUserDetail(Utils.FISCAL_CODE, Utils.UUID_TO_TEST, Utils.RANKDESC_TO_TEST);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        when(userDetailConverter.convert(authentication)).thenReturn(getCustomUserDetailDTO(authentication));
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .build();
    }

    @Test
    @DisplayName("Test Create new User")
    void create_successCreated() throws Exception {

        UserRequestCreateDTO userReq = getUserRequestDTO(1L);
        User user = userCreateMapper.asEntity(userReq);
        when(userRepository.save(any(User.class))).thenReturn(user);

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post("/user")
                        .secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(Utils.convertObjectToJsonString(userReq))
        ).andExpect(status().isCreated()).andReturn();

        int responseStatus = result.getResponse().getStatus();
        log.debug("Create ==> " + responseStatus);
    }

    @Test
    @DisplayName("Test Update User")
    void update() throws Exception {
    }

    @Test
    @DisplayName("Test Cancel User by Id")
    void deleteById() throws Exception {
    }

    @Test
    @DisplayName("Test Search User")
    void searchUser() throws Exception {
        EmployeeSearchRequest request = new EmployeeSearchRequest();
        request.setFiscalCode(Utils.FISCAL_CODE);

        Pageable paging = PageRequest.of(0, 2, Sort.by("firstName,asc"));

        Page<UserDTO> userDTOS = new PageImpl<>(List.of(getUserDTO()), paging, 1);

//        when(userService.search(request, String.valueOf(userDetail.getAuthorities()), paging)).thenReturn(userDTOS);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/user/search")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .content(Utils.convertObjectToJsonString(request))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    @DisplayName("Test All Users paged")
    void getAllPaged() throws Exception {
    }

    @Test
    @DisplayName("Test Get User by Employee ID")
    void getUserByEmployeeId() throws Exception {
    }

    private UserRequestCreateDTO getUserRequestDTO(Long id) {
        UserRequestCreateDTO userReq = new UserRequestCreateDTO();
        userReq.setEmployeeId(id);

        List<RoleDTO> roles = List.of(getRoleDTO(1L, Constant.ADMIN_ROLE_ID, getPrivileges()));
        userReq.setRoles(roles);
        return userReq;
    }

    private UserDTO getUserDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(12L);
        userDTO.setEmployeeId(2L);
        userDTO.setFiscalCode(Utils.FISCAL_CODE);
        userDTO.setFirstName("Smith");
        userDTO.setRoles(new LinkedHashSet<>(List.of(getRoleDTO(1L, Constant.ADMIN_ROLE_ID, getPrivileges()))));

        return userDTO;
    }

    private RoleDTO getRoleDTO(Long id, String roleCode, Set<PrivilegeDTO> privileges) {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(id);
        roleDTO.setRoleCode(roleCode);
        roleDTO.setPrivileges(privileges);

        return roleDTO;
    }

    private PrivilegeDTO getPrivilegeDTO(Long id, Code code) {
        PrivilegeDTO privilegeDTO = new PrivilegeDTO();
        privilegeDTO.setId(id);
        privilegeDTO.setPrivilegeCode(code.getCode());
        privilegeDTO.setDescription(code.getDesc());
        return privilegeDTO;
    }

    private CustomUserDetailDTO getCustomUserDetailDTO(Authentication authentication) {
        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
        CustomUserDetailDTO dto = new CustomUserDetailDTO();
        dto.setUserId(customUserDetail.getUserId());
        dto.setEmployeeId(customUserDetail.getEmployeeId());
        dto.setUsername(customUserDetail.getUsername());
        dto.setFirstName(customUserDetail.getFirstName());
        dto.setLastName(customUserDetail.getLastName());
        dto.setRankDescription(customUserDetail.getRankDescription());
        dto.setUuid(customUserDetail.getUuid());
        dto.setAuthorities(customUserDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()));
        return dto;
    }

    private Set<PrivilegeDTO> getPrivileges() {
        return new LinkedHashSet<>(
                List.of(
                        getPrivilegeDTO(1L, Code.R_SETUP_PROCEDURE_PARAM),
                        getPrivilegeDTO(2L, Code.R_RIASSIGN_PROCEDURE),
                        getPrivilegeDTO(3L, Code.R_ASSIGN_ROLE_USER)
                ));
    }

}