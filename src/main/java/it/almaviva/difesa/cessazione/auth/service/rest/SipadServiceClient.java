package it.almaviva.difesa.cessazione.auth.service.rest;

import it.almaviva.difesa.cessazione.auth.enums.StatusEnum;
import it.almaviva.difesa.cessazione.auth.model.mapper.request.EmployeeSearchRequest;
import it.almaviva.difesa.cessazione.auth.model.mapper.request.UserSearchRequest;
import it.almaviva.difesa.cessazione.auth.model.mapper.rest.dto.VwSg001StgiuridicoMilDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.rest.dto.VwSg155StgiurFastMiCiCriteria;
import it.almaviva.difesa.cessazione.auth.model.mapper.rest.dto.VwSg155StgiurFastMiCiDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.rest.dto.VwSg155StgiuridicoCriteria;
import it.almaviva.difesa.cessazione.auth.model.mapper.rest.dto.VwSg155StgiuridicoDTO;
import it.almaviva.difesa.cessazione.auth.util.UtilsMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class SipadServiceClient extends BaseRestClient {

    private static final String FIND_ALL_USERS_BY_CRITERIA = "%s/userFast/filter?size=%d&page=%d";
    private static final String FIND_ALL_EMPLOYEES_BY_CRITERIA = "%s/user/filter?size=%d&page=%d";
    private static final String SORT = "&sort=%s";
    private static final String FIND_USER_BY_ID = "%s/userFast/findById?employeeId={employeeId}";
    private static final String FIND_EMPLOYEE_BY_ID = "%s/user/findById?employeeId={employeeId}";
    private static final String FIND_USER_BY_CODE = "%s/userFast/find?fiscalCode={fiscalCode}";
    private static final String FIND_EMPLOYEE_BY_CODE = "%s/user/find?fiscalCode={fiscalCode}";
    private static final String CHECK_NOT_COMPATIBLE_WITH_CESSATION = "%s/user/checkNotCompatibleWithCessation?employeeId={employeeId}";
    private static final String FIND_MILITARY_STAFF = "%s/sg001StGiuridico/{idDip}";
    private static final String FIND_USERS_BY_IDS = "%s/userFast/findUsersById?usersIds={usersIds}";
    private static final String GET_USERS_BY_CRITERIA = "%s/user/usersId";

    @Value("${application.ms-sipad.baseurl}")
    private String sipadBaseUrl;

    public Page<VwSg155StgiuridicoDTO> findAllEmployeesByCriteria(EmployeeSearchRequest searchDTO,
                                                                  Pageable pageable,
                                                                  Set<String> categoryPersonal,
                                                                  boolean onlyMilitary) {
        String sort = getSortToString(pageable);
        String url = String.format(FIND_ALL_EMPLOYEES_BY_CRITERIA, sipadBaseUrl, pageable.getPageSize(), pageable.getPageNumber());
        if (sort != null) {
            url += String.format(SORT, sort);
        }
        VwSg155StgiuridicoCriteria criteria = new VwSg155StgiuridicoCriteria();
        criteria.setSg155Nome(searchDTO.getFirstName());
        criteria.setSg155Cognome(searchDTO.getLastName());
        criteria.setSg155CodiceFiscale(searchDTO.getFiscalCode());
        if (onlyMilitary) {
            criteria.setSg155Matricola(searchDTO.getSerialNumber());
            String codFfaa;
            if (Objects.nonNull(searchDTO.getArmedForceId()))
                codFfaa = searchDTO.getArmedForceId();
            else
                codFfaa = String.join(",", UtilsMethod.FORZA_ARMATA_LIST);
            criteria.setSg155CodFfaa(codFfaa);
        }
        criteria.setSg155IdPosser(searchDTO.getStaffPositionId());
        criteria.setSg155IdCatmil(searchDTO.getStaffCategoryId());
        criteria.setSg155CodGrado(searchDTO.getRankId());

        String descrCatmil = null;
        if (!CollectionUtils.isEmpty(categoryPersonal)) {
            if (Objects.nonNull(searchDTO.getEmployeeCategory())) {
                if (categoryPersonal.contains(searchDTO.getEmployeeCategory()))
                    descrCatmil = searchDTO.getEmployeeCategory();
                else
                    descrCatmil = ""; // Caso del filtro di una categoria personale non appartenente alle categorie personale dei ruoli dell'utente loggato
            } else {
                descrCatmil = String.join(",", categoryPersonal);
            }
        }
        criteria.setSg155DescrCatmil(descrCatmil);
        return callPostService(url, criteria, null, null, new ParameterizedTypeReference<RestPageImpl<VwSg155StgiuridicoDTO>>() {
        });
    }

    public List<Long> getUsersIdByCriteria(EmployeeSearchRequest searchDTO) {
        String url = String.format(GET_USERS_BY_CRITERIA, sipadBaseUrl);
        VwSg155StgiuridicoCriteria criteria = new VwSg155StgiuridicoCriteria();
        criteria.setSg155Nome(searchDTO.getFirstName());
        criteria.setSg155Cognome(searchDTO.getLastName());
        criteria.setSg155CodiceFiscale(searchDTO.getFiscalCode());
        return callPostService(url, criteria, null, null, new ParameterizedTypeReference<>() {
        });
    }

    public Page<VwSg155StgiurFastMiCiDTO> findAllUsersByCriteria(UserSearchRequest searchDTO,
                                                                 Pageable pageable) {
        String sort = getSortToString(pageable);
        String url = String.format(FIND_ALL_USERS_BY_CRITERIA, sipadBaseUrl, pageable.getPageSize(), pageable.getPageNumber());
        if (sort != null) {
            url += String.format(SORT, sort);
        }
        VwSg155StgiurFastMiCiCriteria criteria = new VwSg155StgiurFastMiCiCriteria();
        criteria.setSg155Nome(searchDTO.getFirstName());
        criteria.setSg155Cognome(searchDTO.getLastName());
        criteria.setSg155CodiceFiscale(searchDTO.getFiscalCode());
        return callPostService(url, criteria, null, null, new ParameterizedTypeReference<RestPageImpl<VwSg155StgiurFastMiCiDTO>>() {
        });
    }

    public Optional<VwSg155StgiuridicoDTO> findEmployeeById(Long employeeId) {
        UriTemplate uriTemplate = new UriTemplate(String.format(FIND_EMPLOYEE_BY_ID, sipadBaseUrl));
        URI uri = uriTemplate.expand(employeeId);
        return callGetService(uri.toString(), null, null, new ParameterizedTypeReference<>() {
        });
    }

    public VwSg155StgiurFastMiCiDTO findUserByEmployeeId(Long employeeId) {
        UriTemplate uriTemplate = new UriTemplate(String.format(FIND_USER_BY_ID, sipadBaseUrl));
        URI uri = uriTemplate.expand(employeeId);
        return callGetService(uri.toString(), null, VwSg155StgiurFastMiCiDTO.class, null);
    }

    public Optional<VwSg155StgiuridicoDTO> findEmployeeByCode(String fiscalCode) {
        UriTemplate uriTemplate = new UriTemplate(String.format(FIND_EMPLOYEE_BY_CODE, sipadBaseUrl));
        URI uri = uriTemplate.expand(fiscalCode);
        return callGetService(uri.toString(), null, null, new ParameterizedTypeReference<>() {
        });
    }

    public VwSg155StgiurFastMiCiDTO findUserByCode(String fiscalCode) {
        UriTemplate uriTemplate = new UriTemplate(String.format(FIND_USER_BY_CODE, sipadBaseUrl));
        URI uri = uriTemplate.expand(fiscalCode);
        return callGetService(uri.toString(), null, VwSg155StgiurFastMiCiDTO.class, null);
    }

    public boolean checkNotCompatibleWithCessation(Long employeeId) {
        UriTemplate uriTemplate = new UriTemplate(String.format(CHECK_NOT_COMPATIBLE_WITH_CESSATION, sipadBaseUrl));
        URI uri = uriTemplate.expand(employeeId);
        return callGetService(uri.toString(), null, Boolean.class, null);
    }

    public VwSg001StgiuridicoMilDTO findMilitaryStaffDetailByEmployeeId(Long idDip) {
        UriTemplate uriTemplate = new UriTemplate(String.format(FIND_MILITARY_STAFF, sipadBaseUrl));
        URI uri = uriTemplate.expand(idDip);
        return callGetService(uri.toString(), null, VwSg001StgiuridicoMilDTO.class, null);
    }

    public List<VwSg155StgiurFastMiCiDTO> findUsersByIds(List<String> usersIds) {
        UriTemplate uriTemplate = new UriTemplate(String.format(FIND_USERS_BY_IDS, sipadBaseUrl));
        URI uri = uriTemplate.expand(String.join(",", usersIds));
        return callGetService(uri.toString(), null, null, new ParameterizedTypeReference<>() {
        });
    }

    private String getSortToString(Pageable pageable) {
        Sort sorts = pageable.getSort();
        Optional<Sort.Order> order = sorts.stream().findFirst();
        if (order.isPresent()) {
            Sort.Direction direction = order.get().getDirection();
            StatusEnum.UserEnumField field = StatusEnum.UserEnumField.fromLabelDto(order.get().getProperty());
            if (field == null)
                return null;
            switch (field) {
                case LAST_NAME:
                    return StatusEnum.UserEnumField.LAST_NAME.getEntityColumn() + "," + direction.name();
                case FIRST_NAME:
                    return StatusEnum.UserEnumField.FIRST_NAME.getEntityColumn() + "," + direction.name();
                case FISCAL_CODE:
                    return StatusEnum.UserEnumField.FISCAL_CODE.getEntityColumn() + "," + direction.name();
                case SERIAL_NUMBER:
                    return StatusEnum.UserEnumField.SERIAL_NUMBER.getEntityColumn() + "," + direction.name();
                case BIRTH_DATE:
                    return StatusEnum.UserEnumField.BIRTH_DATE.getEntityColumn() + "," + direction.name();
                case ARMED_FORCE_DESCRIPTION:
                    return StatusEnum.UserEnumField.ARMED_FORCE_DESCRIPTION.getEntityColumn() + "," + direction.name();
                case STAFF_POSITION_DESCRIPTION:
                    return StatusEnum.UserEnumField.STAFF_POSITION_DESCRIPTION.getEntityColumn() + "," + direction.name();
                case RANK_DESCRIPTION:
                    return StatusEnum.UserEnumField.RANK_DESCRIPTION.getEntityColumn() + "," + direction.name();
                default:
                    return null;
            }
        }
        return null;
    }

}
