package it.almaviva.difesa.cessazione.auth.service;

import it.almaviva.difesa.cessazione.auth.constant.Constant;
import it.almaviva.difesa.cessazione.auth.data.entity.CategPersonal;
import it.almaviva.difesa.cessazione.auth.data.entity.Role;
import it.almaviva.difesa.cessazione.auth.data.repository.RoleRepository;
import it.almaviva.difesa.cessazione.auth.enums.StatusEnum;
import it.almaviva.difesa.cessazione.auth.model.EmployeeResponseDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.CustomUserDetailDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.request.EmployeeSearchRequest;
import it.almaviva.difesa.cessazione.auth.model.mapper.rest.dto.VwSg001StgiuridicoMilDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.rest.dto.VwSg155StgiuridicoDTO;
import it.almaviva.difesa.cessazione.auth.service.rest.SipadServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service("employeeService")
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final RoleRepository roleRepository;
    private final SipadServiceClient sipadServiceClient;

    public EmployeeResponseDTO findEmployeeDetailById(Long employeeId) {
        VwSg155StgiuridicoDTO vwSg155StgiuridicoDTO = sipadServiceClient.findEmployeeById(employeeId).orElseThrow(() -> {
            log.error("Employee with ID {} is not found", employeeId);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, StatusEnum.EMPLOYEE_DETAILS_NOT_FOUND.getNameMessage());
        });
        return EmployeeResponseDTO.copyProperties(vwSg155StgiuridicoDTO);
    }

    public EmployeeResponseDTO findEmployeeDetailByFiscalCode(String fiscalCode) {
        VwSg155StgiuridicoDTO vwSg155StgiuridicoDTO = sipadServiceClient.findEmployeeByCode(fiscalCode).orElseThrow(() -> {
            log.error("Employee with CF {} is not found", fiscalCode);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, StatusEnum.EMPLOYEE_DETAILS_NOT_FOUND.getNameMessage());
        });
        return EmployeeResponseDTO.copyProperties(vwSg155StgiuridicoDTO);
    }

    public Page<EmployeeResponseDTO> getAllEmployeesMilitaryPaged(EmployeeSearchRequest searchDTO, CustomUserDetailDTO userLogged, Pageable pageable) {
        Set<String> userRoles = userLogged.getAuthorities();
        if (userRoles.isEmpty()) {
            return Page.empty(pageable);
        }
        Set<String> categPersonals = roleRepository.findAllByRoleCodeIn(new ArrayList<>(userRoles))
                .stream()
                .filter(Objects::nonNull)
                .map(Role::getCatPersAbilitato)
                .filter(Objects::nonNull)
                .map(CategPersonal::getCategDesc)
                .collect(Collectors.toSet());
        var employeePage = sipadServiceClient.findAllEmployeesByCriteria(searchDTO, pageable, categPersonals, true);
        Page<EmployeeResponseDTO> employeeResponseDTOPage = filterEmployee(employeePage, userLogged.getEmployeeId());
        employeeResponseDTOPage.get().forEach(emp -> {
            if (!categPersonals.isEmpty() && categPersonals.contains(emp.getStaffCategoryDescription())) {
                emp.setCanStartCessation(true);
            }
            addMilitaryDetail(emp);
            boolean isNotCompatible = false;
            try {
                isNotCompatible = sipadServiceClient.checkNotCompatibleWithCessation(emp.getEmployeeId());
            } catch (Exception e) {
                log.error(">>>>>>>>>>>>>>>> Error in checkNotCompatibleWithCessation {}", e.getMessage());
            }
            if (isNotCompatible) {
                emp.setWarningMSG(Constant.WARNING_MSG);
            }
        });
        return employeeResponseDTOPage;
    }

    public List<Long> findByLastNameAndFirstNameOrCodiceFiscale(EmployeeSearchRequest searchDTO) {
        return sipadServiceClient.getUsersIdByCriteria(searchDTO);
    }

    private Page<EmployeeResponseDTO> filterEmployee(Page<VwSg155StgiuridicoDTO> employeeDetailPage,
                                                     Long loggedEmployeeId) {
        List<VwSg155StgiuridicoDTO> sg155StgiuridicoDTOS = employeeDetailPage.getContent().parallelStream()
                .filter(vwSg155StgiuridicoDTO -> vwSg155StgiuridicoDTO != null && Objects.nonNull(loggedEmployeeId) && !loggedEmployeeId.equals(vwSg155StgiuridicoDTO.getSg155IdDip()))
                .collect(Collectors.toList());
        List<EmployeeResponseDTO> employeeResponseDTOS = sg155StgiuridicoDTOS.parallelStream()
                .map(EmployeeResponseDTO::copyProperties)
                .collect(Collectors.toList());
        return new PageImpl<>(employeeResponseDTOS, employeeDetailPage.getPageable(), employeeDetailPage.getTotalElements());
    }

    private void addMilitaryDetail(EmployeeResponseDTO responseDTO) {
        VwSg001StgiuridicoMilDTO sg001StgiuridicoMilDTO = sipadServiceClient.findMilitaryStaffDetailByEmployeeId(responseDTO.getEmployeeId());
        if (sg001StgiuridicoMilDTO != null) {
            responseDTO.setCatPersDescription(sg001StgiuridicoMilDTO.getDescrPosizione());
            responseDTO.setArmsTypeDescription(sg001StgiuridicoMilDTO.getDescrTipoArmaCorpo());
            responseDTO.setStartServiceLegalDate(sg001StgiuridicoMilDTO.getDataDecGiuImm());
            responseDTO.setRankId(sg001StgiuridicoMilDTO.getCodGrado());
            responseDTO.setRoleId(sg001StgiuridicoMilDTO.getCodRuolo());
            responseDTO.setArmedForceId(sg001StgiuridicoMilDTO.getIdForzaArmata());
        }
    }

}
