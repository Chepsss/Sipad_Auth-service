package it.almaviva.difesa.cessazione.auth.listener;

import it.almaviva.difesa.cessazione.auth.constant.Constant;
import it.almaviva.difesa.cessazione.auth.converter.CustomUserDetailConverter;
import it.almaviva.difesa.cessazione.auth.data.entity.Role;
import it.almaviva.difesa.cessazione.auth.data.entity.User;
import it.almaviva.difesa.cessazione.auth.data.entity.composite.UserRoleCompositeKey;
import it.almaviva.difesa.cessazione.auth.data.entity.relational.UserRoleRelational;
import it.almaviva.difesa.cessazione.auth.data.repository.RoleRepository;
import it.almaviva.difesa.cessazione.auth.data.repository.UserRepository;
import it.almaviva.difesa.cessazione.auth.data.repository.UserRoleRelationalRepository;
import it.almaviva.difesa.cessazione.auth.model.mapper.RoleDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.request.UserRequestCreateDTO;
import it.almaviva.difesa.cessazione.auth.service.UserService;
import it.almaviva.difesa.queuehelper.admin_change.AdminChangeDTO;
import it.almaviva.difesa.queuehelper.admin_change.QueueAdminChangeListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QueueAdminChangeListenerImpl implements QueueAdminChangeListener {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CustomUserDetailConverter customUserDetailConverter;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRoleRelationalRepository userRoleRelationalRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onAdminChange(AdminChangeDTO adminChangeDTO) {
        try {
            log.debug(">>>>>>>>>>>>> onAdminChange>>>>>>>>>>>>>>>>");
            User user;
            user = userRepository.findByEmployeeId(adminChangeDTO.getId()).orElse(null);
            if (user == null && adminChangeDTO.isAdmin()) {
                user = createAdminUser(adminChangeDTO);
            }
            if (user != null && !adminChangeDTO.isAdmin()) {
                List<String> userRoles = user.getRoles().stream().map(Role::getRoleCode).collect(Collectors.toList());
                UserRoleCompositeKey userRoleCompositeKey = getUserCompositeKey(user);
                deleteRoleAdminOrUser(user, userRoles, userRoleCompositeKey);
            }
        } catch (Exception e) {
            log.error(">>>>>>>>>> ERROR onAdminChange", e);
        }
    }

    private void deleteRoleAdminOrUser(User user, List<String> userRoles, UserRoleCompositeKey roleAdmin) {
        if (userRoles.size() > 1) { // list NOT contains ONLY ROLE_ADMIN
            UserRoleRelational userRoleRelational = new UserRoleRelational();
            userRoleRelational.setId(roleAdmin);
            userRoleRelationalRepository.delete(userRoleRelational);
        } else {
            userService.deleteById(user.getId(), null, true);
        }
    }

    private UserRoleCompositeKey getUserCompositeKey(User user) {
        UserRoleCompositeKey userRoleCompositeKey = new UserRoleCompositeKey();
        roleRepository.findByRoleCode(Constant.ADMIN_ROLE_ID).ifPresent(role -> {
            userRoleCompositeKey.setUserId(user.getId());
            userRoleCompositeKey.setRoleId(role.getId());
        });
        return userRoleCompositeKey;
    }

    private User createAdminUser(AdminChangeDTO adminChangeDTO) {
        UserRequestCreateDTO userRequestCreateDTO = new UserRequestCreateDTO();
        RoleDTO roleAdminDTO = new RoleDTO();
        roleAdminDTO.setRoleCode(Constant.ADMIN_ROLE_ID);
        List<RoleDTO> roleDTOList = new ArrayList<>();
        roleDTOList.add(roleAdminDTO);
        userRequestCreateDTO.setEmployeeId(adminChangeDTO.getId());
        userRequestCreateDTO.setRoles(roleDTOList);
        return userService.create(userRequestCreateDTO, true);
    }
}
