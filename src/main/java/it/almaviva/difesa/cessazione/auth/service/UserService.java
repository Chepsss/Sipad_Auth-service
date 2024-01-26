package it.almaviva.difesa.cessazione.auth.service;

import it.almaviva.difesa.cessazione.auth.constant.Constant;
import it.almaviva.difesa.cessazione.auth.data.entity.Role;
import it.almaviva.difesa.cessazione.auth.data.entity.User;
import it.almaviva.difesa.cessazione.auth.data.entity.composite.UserRoleCompositeKey;
import it.almaviva.difesa.cessazione.auth.data.entity.relational.UserRoleRelational;
import it.almaviva.difesa.cessazione.auth.data.repository.RoleRepository;
import it.almaviva.difesa.cessazione.auth.data.repository.UserRepository;
import it.almaviva.difesa.cessazione.auth.data.repository.UserRoleRelationalRepository;
import it.almaviva.difesa.cessazione.auth.enums.StatusEnum;
import it.almaviva.difesa.cessazione.auth.exception.ServiceException;
import it.almaviva.difesa.cessazione.auth.model.mapper.CustomUserDetailDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.RoleDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.RoleMapper;
import it.almaviva.difesa.cessazione.auth.model.mapper.UserDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.UserMapper;
import it.almaviva.difesa.cessazione.auth.model.mapper.UserTokenMapper;
import it.almaviva.difesa.cessazione.auth.model.mapper.request.UserRequestCreateDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.request.UserRequestCreateMapper;
import it.almaviva.difesa.cessazione.auth.model.mapper.request.UserRequestDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.request.UserRequestMapper;
import it.almaviva.difesa.cessazione.auth.model.mapper.request.UserSearchRequest;
import it.almaviva.difesa.cessazione.auth.model.mapper.response.ProcedureDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.response.UserDetailResponseDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.rest.dto.VwSg155StgiurFastMiCiDTO;
import it.almaviva.difesa.cessazione.auth.service.rest.ProcedureServiceClient;
import it.almaviva.difesa.cessazione.auth.service.rest.SipadServiceClient;
import it.almaviva.difesa.cessazione.auth.util.UtilsMethod;
import it.almaviva.difesa.queuehelper.report.QueueReportService;
import it.almaviva.difesa.queuehelper.report.UtenteReportDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static it.almaviva.difesa.cessazione.auth.util.StreamClass.createDeltaPredicateBetweenTwoCollections;
import static it.almaviva.difesa.cessazione.auth.util.StreamClass.getDeltaSetBetweenTwoCollections;

@Service("userService")
@Transactional(rollbackFor = Exception.class)
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRelationalRepository userRoleRelationalRepository;
    private final UserRequestCreateMapper userCreateMapper;
    private final UserRequestMapper userRequestMapper;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final ProcedureServiceClient procedureServiceClient;
    private final EmailService emailService;
    private final SipadServiceClient sipadServiceClient;
    private final QueueReportService queueReportService;
    private final UserTokenMapper userTokenMapper;

    @Value("${queue:#{true}}")
    private Boolean queueEnable;

    public Page<UserDTO> findAllUsersPaged(UserSearchRequest searchDTO, Long loggedEmployeeId, Pageable pageable) {
        Page<VwSg155StgiurFastMiCiDTO> sg155StgiurFastMiCiDTOS = sipadServiceClient.findAllUsersByCriteria(searchDTO, pageable);
        List<VwSg155StgiurFastMiCiDTO> dtos = sg155StgiurFastMiCiDTOS.getContent().parallelStream()
                .filter(dto -> Objects.nonNull(loggedEmployeeId) && !dto.getSg155IdDip().equals(loggedEmployeeId))
                .collect(Collectors.toList());
        List<UserDTO> userDTOS = dtos.parallelStream()
                .map(userMapper::copyProperties)
                .collect(Collectors.toList());

        var usersId = userDTOS.parallelStream()
                .map(UserDTO::getEmployeeId)
                .collect(Collectors.toSet());

        Set<Long> userIds = userRepository.findAllByEmployeeIdIn(usersId).stream()
                .map(User::getEmployeeId)
                .collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(userIds)) {
            userDTOS.parallelStream()
                    .forEach(e -> {
                        if (userIds.contains(e.getEmployeeId())) {
                            e.setAlreadyPresent(true);
                        }
                    });
        }
        return new PageImpl<>(userDTOS, sg155StgiurFastMiCiDTOS.getPageable(), sg155StgiurFastMiCiDTOS.getTotalElements());
    }

    public User create(UserRequestCreateDTO dto, boolean isFromQueue) {
        return createUser(dto, isFromQueue);
    }

    public User update(UserRequestDTO dto, Long userId, CustomUserDetailDTO userLogged) {
        return updateUserIfExists(dto, userId, userLogged);
    }

    public void deleteById(Long userId, String token, boolean isFromQueue) {
        deleteUserAndRelatedRolesByUserId(userId, token, isFromQueue);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> search(UserSearchRequest request, String roleCode, Pageable pageable) {
        List<UserDTO> userDTOList = new ArrayList<>();
        Page<VwSg155StgiurFastMiCiDTO> usersPage = sipadServiceClient.findAllUsersByCriteria(request, pageable);
        usersPage.forEach(dto -> {
            var user = userRepository.findByEmployeeId(dto.getSg155IdDip()).orElse(null);
            if (user == null) {
                return;
            }
            if (Constant.ADMIN_ROLE_ID.equals(roleCode)) {
                return;
            }
            this.addUserDTOToList(userDTOList,
                    user,
                    dto.getSg155CodiceFiscale(),
                    dto.getSg155Nome(),
                    dto.getSg155Cognome());
        });
        return new PageImpl<>(userDTOList, pageable, usersPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllPaged(UserSearchRequest searchDTO, Pageable pageable, Long employeeId) {
        List<String> usersIds;
        List<VwSg155StgiurFastMiCiDTO> usersDetail;
        List<UserDTO> userSearchResponseDTOList = new ArrayList<>();
        List<User> userList = userRepository.findAllByEmployeeIdNot(employeeId);
        if (isFilter(searchDTO)) {
            usersIds = userList.parallelStream()
                    .map(User::getEmployeeId)
                    .map(String::valueOf)
                    .collect(Collectors.toList());
            usersDetail = sipadServiceClient.findUsersByIds(usersIds);
            usersDetail = usersDetail.parallelStream()
                    .filter(user -> filterByFiscalCode(searchDTO, user) && filterByFirstNameLastName(searchDTO, user))
                    .collect(Collectors.toList());
            usersDetail.forEach(user ->
                    this.addUserDTOToList(
                            userSearchResponseDTOList,
                            getUserByEmployeeId(userList, user.getSg155IdDip()),
                            user.getSg155CodiceFiscale(),
                            user.getSg155Nome(),
                            user.getSg155Cognome())
            );
        } else {
            usersIds = userList.parallelStream()
                    .map(User::getEmployeeId)
                    .map(String::valueOf)
                    .collect(Collectors.toList());
            usersDetail = sipadServiceClient.findUsersByIds(usersIds);
            usersDetail.forEach(user ->
                    this.addUserDTOToList(
                            userSearchResponseDTOList,
                            getUserByEmployeeId(userList, user.getSg155IdDip()),
                            user.getSg155CodiceFiscale(),
                            user.getSg155Nome(),
                            user.getSg155Cognome())
            );
        }
        List<UserDTO> newUserListSorted = sortUserList(userSearchResponseDTOList, pageable);
        return UtilsMethod.getPageFromList(newUserListSorted, pageable);
    }

    public void addUserDTOToList(List<UserDTO> userDTOList, User user, String fiscalCode, String firstName, String lastName) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getId());
        dto.setEmployeeId(user.getEmployeeId());
        dto.setFiscalCode(fiscalCode);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        var rolesUser = user.getRoles().stream()
                .map(roleMapper::asDTO)
                .sorted(Comparator.comparing(RoleDTO::getDescription))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        dto.setRoles(rolesUser);
        var userToken = userTokenMapper.asDTOSet(user.getUserTokens());
        dto.setUserTokens(userToken);
        boolean isAdmin = rolesUser.stream().anyMatch(role -> role.getRoleCode().equalsIgnoreCase(Constant.ADMIN_ROLE_ID));
        dto.setCanDelete(!isAdmin);
        userDTOList.add(dto);
    }

    @Transactional(readOnly = true)
    public UserDetailResponseDTO getUserDetailById(Long id, boolean isGetByUserId) {
        User user;
        if (isGetByUserId) {
            user = userRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("User with ID {} is not found", id);
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, StatusEnum.USER_NOT_FINDABLE_ID.getNameMessage());
                    });
        } else {
            user = userRepository.findUserByEmployeeId(id)
                    .orElseThrow(() -> {
                        log.error("User with ID {} is not found", id);
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, StatusEnum.USER_NOT_FINDABLE_ID.getNameMessage());
                    });
        }
        var roles = new ArrayList<>(user.getRoles());
        UserDetailResponseDTO respDTO = new UserDetailResponseDTO();
        respDTO.setEmployeeId(user.getEmployeeId());
        respDTO.setRoles(roleMapper.asDTOList(roles));
        var userDTO = this.findUserByEmployeeId(user.getEmployeeId());
        respDTO.setEmployeeDetail(userDTO);
        return respDTO;
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> listByRoles(Pageable pageable, List<Long> ids) {
        Page<User> users = userRepository.findAllByRoleIds(ids, pageable);
        List<UserDTO> userDtoList = getUserDTOSCompleteData(users.getContent());
        return new PageImpl<>(userDtoList, pageable, users.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Long filterUserOnCodeRule(UserSearchRequest searchDto) {
        Optional<Long> employeeId = this.findByUsernameAndFirstNameOrCodiceFiscale(searchDto, Pageable.ofSize(1))
                .stream().findFirst();
        return employeeId.flatMap(id -> userRepository.findUsersByEmployeeIdsAndRoleCode(id, searchDto.getRoleCode())
                .map(User::getEmployeeId)).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> findUsersByEmployeeIdNotAndRoleCode(UserSearchRequest searchDto) {
        return userRepository.findUsersByEmployeeIdNotAndRoleCode(searchDto.getEmployeeId(), searchDto.getRoleCode())
                .parallelStream()
                .map(user -> {
                    try {
                        return this.findUserByEmployeeId(user.getEmployeeId());
                    } catch (Exception e) {
                        log.error("Error in findUsersByEmployeeIdNotAndRoleCode => ", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public UserDTO findUserDetailByFiscalCode(String fiscalCode) {
        try {
            VwSg155StgiurFastMiCiDTO sg155StgiurFastMiCiDTO = sipadServiceClient.findUserByCode(fiscalCode);
            return userMapper.copyProperties(sg155StgiurFastMiCiDTO);
        } catch (ServiceException e) {
            throw new UsernameNotFoundException(String.format(Constant.SIPAD_USER_NOT_FOUND, fiscalCode));
        }

    }

    public UserDTO findUserByEmployeeId(Long employeeId) {
        try {
            VwSg155StgiurFastMiCiDTO sg155StgiurFastMiCiDTO = sipadServiceClient.findUserByEmployeeId(employeeId);
            return userMapper.copyProperties(sg155StgiurFastMiCiDTO);
        } catch (ServiceException e) {
            throw new UsernameNotFoundException(Constant.SIPAD_USER_BY_EMPLOYEE_ID_NOT_FOUND);
        }
    }

    public List<Long> findByUsernameAndFirstNameOrCodiceFiscale(UserSearchRequest searchDTO, Pageable pageable) {
        Page<VwSg155StgiurFastMiCiDTO> users = sipadServiceClient.findAllUsersByCriteria(searchDTO, pageable);
        return users.getContent().parallelStream().map(VwSg155StgiurFastMiCiDTO::getSg155IdDip).collect(Collectors.toList());
    }

    private boolean filterByFiscalCode(UserSearchRequest searchDTO, VwSg155StgiurFastMiCiDTO dto) {
        return StringUtils.isBlank(searchDTO.getFiscalCode()) ||
                (StringUtils.isNotBlank(searchDTO.getFiscalCode()) && searchDTO.getFiscalCode().equalsIgnoreCase(dto.getSg155CodiceFiscale()));
    }

    private boolean filterByFirstNameLastName(UserSearchRequest searchDTO, VwSg155StgiurFastMiCiDTO dto) {
        if (StringUtils.isNotBlank(searchDTO.getFirstName()) && StringUtils.isNotBlank(searchDTO.getLastName())) {
            return searchDTO.getFirstName().equalsIgnoreCase(dto.getSg155Nome())
                    && searchDTO.getLastName().equalsIgnoreCase(dto.getSg155Cognome());
        }
        return true;
    }

    private List<UserDTO> sortUserList(List<UserDTO> userDTOList, Pageable pageable) {
        Optional<Sort.Order> optionalOrder = pageable.getSort().stream().findAny();
        if (optionalOrder.isPresent()) {
            String property = optionalOrder.get().getProperty();
            Sort.Direction direction = optionalOrder.get().getDirection();
            if (property.equalsIgnoreCase(Constant.FIRST_NAME)) {
                return UtilsMethod.sortList(userDTOList, direction, Comparator.comparing(UserDTO::getFirstName));
            } else if (property.equalsIgnoreCase(Constant.LAST_NAME)) {
                return UtilsMethod.sortList(userDTOList, direction, Comparator.comparing(UserDTO::getLastName));
            } else if (property.equalsIgnoreCase(Constant.FISCAL_CODE)) {
                return UtilsMethod.sortList(userDTOList, direction, Comparator.comparing(UserDTO::getFiscalCode));
            }
        }
        return userDTOList;
    }

    private User createUser(UserRequestCreateDTO userRequestDTO, boolean isFromQueue) {
        log.debug("INIT createUser");
        checkIfUserRequestDTOisValid(userRequestDTO);
        List<String> requestRoles = userRequestDTO.getRoles().stream().map(RoleDTO::getRoleCode).collect(Collectors.toList());
        List<Role> roles = roleRepository.findAllByRoleCodeIn(requestRoles);

        var user = userCreateMapper.asEntity(userRequestDTO);
        var userDetail = this.findUserByEmployeeId(user.getEmployeeId());
        user.setRoles(new LinkedHashSet<>(roles));
        user.setTruteCodFfaa(userDetail.getArmedForceId());
        user.setTruteInattivo(false);
        if (isFromQueue) {
            user.setInsertCode("CENTRAL");
            user.setLastUpdatedCode("CENTRAL");
        }

        var savedUser = userRepository.save(user);
        log.debug(String.format("User %s saved", savedUser));
        userRoleRelationalRepository.saveAll(getUserRoleRelational(savedUser.getId(), roles.stream().map(Role::getId).collect(Collectors.toSet())));
        UtenteReportDTO utenteReportDTO = setUserReportDTO(savedUser, true);
        if (Boolean.TRUE.equals(queueEnable)) {
            queueReportService.sendRegistrazioneUtente(utenteReportDTO);
        }
        log.debug(">>>>>>>>>>>>>>>> Roles of the user are saved correctly");
        return savedUser;
    }

    private UtenteReportDTO setUserReportDTO(User user, boolean added) {
        UtenteReportDTO utenteReportDTO = new UtenteReportDTO();
        UserDetailResponseDTO userDetailResponseDTO = getUserDetailById(user.getId(), true);
        utenteReportDTO.setId(user.getEmployeeId());
        utenteReportDTO.setCodiceFiscale(userDetailResponseDTO.getEmployeeDetail().getFiscalCode());
        utenteReportDTO.setAggiunto(added);
        return utenteReportDTO;
    }

    private User updateUserIfExists(UserRequestDTO userRequestDTO, Long userId, CustomUserDetailDTO userLogged) {
        log.debug("INIT updateUserIfExists");
        List<String> requestRoles = userRequestDTO.getRoles().stream().map(RoleDTO::getRoleCode).collect(Collectors.toList());
        AtomicReference<User> userUpdated = new AtomicReference<>();
        userRepository.findById(userId).ifPresentOrElse(user -> {
            log.debug("User with id {} found", userId);
            if (!hasRoleAdmin(userLogged)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Constant.ONLY_ADMIN_CAN_MODIFY_ROLES);
            }
            if (hasRolesChanged(requestRoles, user.getRoles())) {
                checkIfRoleCodeIsValid(requestRoles);
            }
            List<Role> reqRoles = roleRepository.findAllByRoleCodeIn(requestRoles);
            List<Role> userRoles = user.getRoles().stream()
                    .filter(role -> !role.getRoleCode().equals(Constant.ADMIN_ROLE_ID))
                    .collect(Collectors.toList());
            updateRolesRelatedToAnUser(user, reqRoles, userRoles, userLogged.getToken());
            userUpdated.set(updateUser(user, userRequestDTO));
        }, () -> {
            String error = String.format("Cannot find any user with the given id: %d", userId);
            log.error(error);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, StatusEnum.USER_NOT_FINDABLE_ID.getNameMessage());
        });
        return userUpdated.get();
    }

    private void checkIfUserRequestDTOisValid(UserRequestCreateDTO userRequestDTO) {
        log.debug("INIT checkIfUserRequestDTOisValid");
        checkIfEmployeeIdAlreadyExists(userRequestDTO.getEmployeeId());
    }

    private void checkIfEmployeeIdAlreadyExists(Long employeeId) {
        log.debug("INIT checkIfEmployeeIdAlreadyExists");
        if (isEmployeeIdAlreadyPresent(employeeId)) {
            var error = String.format(StatusEnum.EMPLOYEE_ID_ALREADY_USED.getNameMessage(), employeeId);
            log.error(error);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
        }
    }

    private void checkIfRoleCodeIsValid(List<String> roles) {
        log.debug("INIT checkIfRoleCodeIsValid");
        String error;
        if (isRoleCodeInvalid(roles)) {
            error = String.format(StatusEnum.ROLE_NOT_FINDABLE_ID.getNameMessage(), String.join(",", roles));
            log.error(error);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, error);
        } else if (roles.stream().anyMatch(Constant.ADMIN_ROLE_ID::equals)) {
            error = String.format(StatusEnum.USER_NOT_CREATABLE_ROLE.getNameMessage(), String.join(",", roles));
            log.error(error);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
        }
    }

    private boolean isRoleCodeInvalid(List<String> roles) {
        java.util.function.Predicate<String> existPredicate = roleRepository::existsByRoleCode;
        return roles.stream().noneMatch(existPredicate);
    }

    private boolean isEmployeeIdAlreadyPresent(Long employeeId) {
        return userRepository.existsByEmployeeId(employeeId);
    }

    private boolean hasRolesChanged(List<String> requestRoles, Set<Role> userFoundRoles) {
        java.util.function.Predicate<Role> rolePredicate = role -> requestRoles.contains(role.getRoleCode());
        return userFoundRoles.stream().noneMatch(rolePredicate);
    }

    private User updateUser(User user, UserRequestDTO userRequestDTO) {
        log.debug("INIT updateUser");
        var userToUpdate = userRequestMapper.asEntity(userRequestDTO);
        userToUpdate.setId(user.getId());
        userToUpdate.setEmployeeId(user.getEmployeeId());
        userToUpdate.setTruteCodFfaa(user.getTruteCodFfaa());
        userToUpdate.setTruteInattivo(user.getTruteInattivo());
        userToUpdate.setInsertCode(user.getInsertCode());
        userToUpdate.setInsertDate(user.getInsertDate());
        return userRepository.save(userToUpdate);
    }

    private void updateRolesRelatedToAnUser(User user, List<Role> newRoles, List<Role> oldRoles, String token) {
        log.debug("INIT updatePrivilegesRelatedToAnUser");

        List<Long> newRolesId = newRoles.stream().map(Role::getId).collect(Collectors.toList());
        List<Long> oldRolesId = oldRoles.stream().map(Role::getId).collect(Collectors.toList());

        Set<Long> rolesIdToInsert = getDeltaSetBetweenTwoCollections(newRolesId, createDeltaPredicateBetweenTwoCollections(oldRolesId));

        if (!CollectionUtils.isEmpty(rolesIdToInsert))
            populateRelationalTable(user, rolesIdToInsert);

        Set<Long> rolesIdToDelete = getDeltaSetBetweenTwoCollections(oldRolesId, createDeltaPredicateBetweenTwoCollections(newRolesId));

        if (!CollectionUtils.isEmpty(rolesIdToDelete)) {
            assertCancellationRoles(user, rolesIdToDelete, false, token);
            deleteFromRelationalTable(user, rolesIdToDelete);
        }
    }

    private void deleteFromRelationalTable(User user, Set<Long> rolesIdToDelete) {
        userRoleRelationalRepository.deleteAll(getUserRoleRelational(user.getId(), rolesIdToDelete));
    }

    private void populateRelationalTable(User user, Set<Long> rolesId) {
        log.debug("INIT populateRelationalTable");
        userRoleRelationalRepository.saveAll(getUserRoleRelational(user.getId(), rolesId));
        log.debug(">>>>>>>>>>>>>>>> Relational table populated");
    }

    private Set<UserRoleRelational> getUserRoleRelational(Long userId, Set<Long> rolesId) {
        Set<UserRoleRelational> userRoleRelationalSet = new HashSet<>();
        rolesId.forEach(roleId -> {
            UserRoleRelational userRoleRelational = new UserRoleRelational();
            UserRoleCompositeKey relationalId = new UserRoleCompositeKey();
            relationalId.setUserId(userId);
            relationalId.setRoleId(roleId);
            userRoleRelational.setId(relationalId);
            userRoleRelationalSet.add(userRoleRelational);
        });
        return userRoleRelationalSet;
    }

    private void deleteUserAndRelatedRolesByUserId(Long userId, String token, boolean isFromQueue) {
        log.debug("INIT deleteUserAndRelatedRolesByUserId");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(StatusEnum.USER_NOT_FINDABLE_ID.getNameMessage(), userId)));

        if (!isFromQueue) {
            boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getRoleCode().equalsIgnoreCase(Constant.ADMIN_ROLE_ID));
            UserDTO userDetail = this.findUserByEmployeeId(user.getEmployeeId());
            String nomeCognome = StringUtils.capitalize(userDetail.getFirstName().toLowerCase()) + " " +
                    StringUtils.capitalize(userDetail.getLastName().toLowerCase());
            if (isAdmin) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(Constant.DELETE_USER_ADMIN_ERROR, nomeCognome));
            }
            Set<Long> rolesId = user.getRoles().stream().map(Role::getId).collect(Collectors.toSet());
            try {
                assertCancellationRoles(user, rolesId, true, token);
            } catch (ResponseStatusException e) {
                throw new ResponseStatusException(e.getStatus(), String.format(Constant.DELETE_USER_ERROR, nomeCognome, e.getReason()));
            }
        }
        UtenteReportDTO utenteReportDTO = setUserReportDTO(user, false);
        deleteAllRolesRelatedToUser(user);
        deleteUser(user);
        if (Boolean.TRUE.equals(queueEnable)) {
            queueReportService.sendRegistrazioneUtente(utenteReportDTO);
        }
    }

    private void deleteAllRolesRelatedToUser(User user) {
        log.debug("INIT deleteAllRolesRelatedToUser");
        Set<Role> userRolesToDelete = user.getRoles();
        if (!CollectionUtils.isEmpty(userRolesToDelete)) {
            log.debug("Found roles related to the user with id: {}", user.getId());
            userRoleRelationalRepository.deleteRolesIdRelatedToUserId(userRolesToDelete, user);
            log.debug("Roles related to the given user deleted");
        }
    }

    private void deleteUser(User user) {
        userRepository.delete(user);
    }

    private User getUserByEmployeeId(List<User> users, Long employeeId) {
        return users.stream()
                .filter(u -> u.getEmployeeId().equals(employeeId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, StatusEnum.INTERNAL_SERVER_ERROR.getNameMessage()));
    }

    private List<UserDTO> getUserDTOSCompleteData(List<User> users) {
        List<String> usersIds = users.stream().map(User::getEmployeeId).map(String::valueOf).collect(Collectors.toList());
        List<VwSg155StgiurFastMiCiDTO> userList = sipadServiceClient.findUsersByIds(usersIds);
        return users.stream().map(user -> {
            UserDTO userDto = userMapper.asDTO(user);
            Optional<VwSg155StgiurFastMiCiDTO> userOpt = userList.stream()
                    .filter(e -> e.getSg155IdDip().equals(user.getEmployeeId())
                            && Objects.nonNull(e.getSg155Cognome())
                            && Objects.nonNull(e.getSg155Nome()))
                    .findFirst();
            if (userOpt.isPresent()) {
                VwSg155StgiurFastMiCiDTO dto = userOpt.get();
                userDto.setUserId(user.getId());
                userDto.setFirstName(dto.getSg155Nome());
                userDto.setLastName(dto.getSg155Cognome());
                userDto.setFiscalCode(dto.getSg155CodiceFiscale());
            }
            return userDto;
        }).collect(Collectors.toList());
    }

    private boolean isFilter(UserSearchRequest searchDTO) {
        return Objects.nonNull(searchDTO.getFiscalCode())
                || Objects.nonNull(searchDTO.getLastName())
                || Objects.nonNull(searchDTO.getFirstName());
    }

    private void assertCancellationRoles(User user, Set<Long> rolesIdToDelete, boolean cancelUser, String token) {
        Set<UserRoleRelational> userRolesToDelete = getUserRoleRelational(user.getId(), rolesIdToDelete);
        if (!userRolesToDelete.isEmpty()) {
            List<Long> idToDelete = userRolesToDelete.stream().map(role -> role.getId().getRoleId()).collect(Collectors.toList());
            List<String> rolesToDelete = roleRepository.findAllById(idToDelete).stream().map(Role::getRoleCode).collect(Collectors.toList());
            AtomicReference<String> error = new AtomicReference<>();
            if (rolesToDelete.contains(Constant.ADMIN_ROLE_ID)) {
                error.set(Constant.DELETE_ADMIN_ROLE_ERROR);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error.get());
            } else {
                List<ProcedureDTO> procedureDTOS = procedureServiceClient.getProceduresNotClosed(user.getEmployeeId(), token);
                procedureDTOS.parallelStream().forEach(proc ->
                        proc.getRoles().parallelStream()
                                .filter(role -> rolesToDelete.contains(role.getRoleCode()))
                                .findAny()
                                .ifPresent(dto -> {
                                    String err = Constant.ROLE;
                                    if (Boolean.FALSE.equals(cancelUser)) {
                                        err = String.format(Constant.DELETE_ROLE_ERROR, err);
                                    }
                                    error.set(err);
                                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(error.get(), dto.getDescription()));
                                })
                );
            }
        }
    }

    private boolean hasRoleAdmin(CustomUserDetailDTO userLogged) {
        return userLogged.getAuthorities().contains(Constant.ADMIN_ROLE_ID);
    }

    @Transactional(readOnly = true)
    public void sendEmailNotification(User user) throws MailException, MessagingException {
        Set<UserRoleRelational> userRoleRelational = userRoleRelationalRepository.findUserRoleRelationalById_UserId(user.getId());
        List<Long> newRolesId = userRoleRelational.stream().map(rel -> rel.getId().getRoleId()).collect(Collectors.toList());
        List<String> newRoles = roleRepository.findRolesByIdIn(newRolesId).stream().map(Role::getDescription).collect(Collectors.toList());

        if (!newRoles.isEmpty()) {
            UserDTO userDetail = this.findUserByEmployeeId(user.getEmployeeId());
            String userMail = userDetail.getEmail();
            Map<String, Object> variables = new HashMap<>();
            variables.put(Constant.ROLES, newRoles);
            emailService.sendHTMLMessage(userMail, Constant.EMAIL_SUBJECT, Constant.TEMPLATE_EMAIL_NOTIFICATION, variables);
        }
    }

}
