package it.almaviva.difesa.cessazione.auth.controller;

import it.almaviva.difesa.cessazione.auth.constant.Constant;
import it.almaviva.difesa.cessazione.auth.converter.CustomUserDetailConverter;
import it.almaviva.difesa.cessazione.auth.data.entity.User;
import it.almaviva.difesa.cessazione.auth.model.GenericResponse;
import it.almaviva.difesa.cessazione.auth.model.mapper.UserDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.request.UserRequestCreateDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.request.UserRequestDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.request.UserSearchRequest;
import it.almaviva.difesa.cessazione.auth.model.mapper.response.UserDetailResponseDTO;
import it.almaviva.difesa.cessazione.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(Constant.USER_URL)
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final CustomUserDetailConverter userDetailConverter;

    @PostMapping
    @Secured({Constant.ADMIN_ROLE_ID})
    public ResponseEntity<Page<UserDTO>> getAllUsersPaged(@RequestBody UserSearchRequest searchDTO, Pageable pageable) {
        var userLogged = userDetailConverter.convert(SecurityContextHolder.getContext().getAuthentication());
        assert userLogged != null;
        return ResponseEntity.ok(userService.findAllUsersPaged(searchDTO, userLogged.getEmployeeId(), pageable));
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<GenericResponse> create(@RequestBody @Valid UserRequestCreateDTO dto) {
        var userLogged = userDetailConverter
                .convert(SecurityContextHolder.getContext().getAuthentication());
        assert userLogged != null;
        User userCreated = userService.create(dto, false);
        return sendEmailNotification(userCreated);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GenericResponse> update(@RequestBody @Valid UserRequestDTO dto, @PathVariable Long id) {
        var userLogged = userDetailConverter.convert(SecurityContextHolder.getContext().getAuthentication());
        assert userLogged != null;
        User userUpdated = this.userService.update(dto, id, userLogged);
        return sendEmailNotification(userUpdated);
    }

    private ResponseEntity<GenericResponse> sendEmailNotification(User userUpdated) {
        try {
            this.userService.sendEmailNotification(userUpdated);
            log.debug(">>>>>>>>>>>>>>>> Sending email notification completed");
        } catch (MailException | MessagingException e) {
            log.error("Error sending email notification >>>>>>>>> {}", e.getMessage());
            return ResponseEntity.ok(new GenericResponse(null, Constant.SAVE_DATA_NOT_SEND_EMAIL));
        }
        return ResponseEntity.ok(new GenericResponse(Constant.SUCCESS, null));
    }

    @DeleteMapping({"/{id}"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        var userLogged = userDetailConverter.convert(SecurityContextHolder.getContext().getAuthentication());
        assert userLogged != null;
        this.userService.deleteById(id, userLogged.getToken(), false);
    }

    @PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<UserDTO>> searchUser(@RequestBody @Valid UserSearchRequest request, Pageable pageable) {
        var userLogged = userDetailConverter.convert(SecurityContextHolder.getContext().getAuthentication());
        assert userLogged != null;
        log.debug(String.format("userLogged => %s", userLogged));
        return ResponseEntity.ok(userService.search(request, String.valueOf(userLogged.getAuthorities()), pageable));
    }

    @PostMapping(value = "/all", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<UserDTO>> getAllPaged(@RequestBody UserSearchRequest searchDTO, Pageable pageable) {
        var userLogged = userDetailConverter.convert(SecurityContextHolder.getContext().getAuthentication());
        assert userLogged != null;
        return ResponseEntity.ok(userService.getAllPaged(searchDTO, pageable, userLogged.getEmployeeId()));
    }

    @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDetailResponseDTO> getUserById(@PathVariable Long userId) {
        var userLogged = userDetailConverter.convert(SecurityContextHolder.getContext().getAuthentication());
        assert userLogged != null;
        return ResponseEntity.ok(userService.getUserDetailById(userId, true));
    }

    @GetMapping(value = "/userByEmployeeId/{employeeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> getUserByEmployeeId(@PathVariable Long employeeId) {
        var userLogged = userDetailConverter.convert(SecurityContextHolder.getContext().getAuthentication());
        assert userLogged != null;
        return ResponseEntity.ok(userService.findUserByEmployeeId(employeeId));
    }

    @GetMapping(value = "/userDetailByEmployeeId/{employeeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDetailResponseDTO> getUserDetailByEmployeeId(@PathVariable Long employeeId) {
        var userLogged = userDetailConverter.convert(SecurityContextHolder.getContext().getAuthentication());
        assert userLogged != null;
        return ResponseEntity.ok(userService.getUserDetailById(employeeId, false));
    }

    @PostMapping(value = "/listByRoles", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDTO> listByRoles(@RequestBody List<Long> ids, Pageable pageable) {
        var userLogged = userDetailConverter.convert(SecurityContextHolder.getContext().getAuthentication());
        assert userLogged != null;
        return userService.listByRoles(pageable, ids).getContent();
    }

    @PostMapping(value = "/searchUserRoleIds", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> searchUserRoleIds(@RequestBody @Valid UserSearchRequest request) {
        var userLogged = userDetailConverter.convert(SecurityContextHolder.getContext().getAuthentication());
        assert userLogged != null;
        log.debug(String.format("userLogged => %s", userLogged));
        return ResponseEntity.ok(userService.filterUserOnCodeRule(request));
    }

    @PostMapping(value = "/searchUserFromRole", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDTO>> searchUserFromRole(@RequestBody UserSearchRequest searchDTO) {

        var userLogged = userDetailConverter.convert(SecurityContextHolder.getContext().getAuthentication());
        assert userLogged != null;
        List<UserDTO> result = userService.findUsersByEmployeeIdNotAndRoleCode(searchDTO);
        return ResponseEntity.ok(result);
    }

}
