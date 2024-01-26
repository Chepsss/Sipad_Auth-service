package it.almaviva.difesa.cessazione.auth.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.almaviva.difesa.cessazione.auth.model.mapper.response.GenericResponseDTO;
import it.almaviva.difesa.cessazione.auth.model.mapper.rest.dto.VwSg155StgiuridicoDTO;
import it.almaviva.difesa.cessazione.auth.util.CommonUtilsMethod;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeResponseDTO implements GenericResponseDTO {

    private Long employeeId;
    private String lastName;
    private String firstName;
    private String fiscalCode;
    private String serialNumber;
    private boolean alreadyPresent;
    private LocalDate birthDate;
    private String abbrBirthProvince;
    private String birthCity;
    private String civilStatus;
    private String gender;
    private String email;
    private String armedForceId;
    private String armedForceDescription;
    private String staffPositionId;
    private String staffPositionDescription;
    private Long staffCategoryId;
    private String staffCategoryDescription;
    private String rankId;
    private String rankDescription;
    private String roleId;
    private String roleDescription;
    private LocalDate roleLegalDate;
    private String legalStatusCategoryDesc;
    private boolean canStartCessation;

    private LocalDate dataDecGiuGrado;
    private String descrCatpers;

    // Military Staff Detail
    private String catPersDescription;
    private String armsTypeDescription;
    private LocalDate startServiceLegalDate;
    private String warningMSG;

    private String uidPosSer;
    private String codCatpers;
    private String codUidCatpers;
    private LocalDate dataDecGiuCatpers;
    private String acrPosSer;
    private String codSpecCat;
    private String codUidSpecCat;
    private String descrSpecCat;
    private String descrSpecInc;
    private LocalDate dataAnzSp;

    public static EmployeeResponseDTO copyProperties(VwSg155StgiuridicoDTO sg155StgiuridicoDTO) {
        EmployeeResponseDTO employeeResponseDTO = new EmployeeResponseDTO();

        employeeResponseDTO.setEmployeeId(sg155StgiuridicoDTO.getSg155IdDip());
        employeeResponseDTO.setLastName(sg155StgiuridicoDTO.getSg155Cognome());
        employeeResponseDTO.setFirstName(sg155StgiuridicoDTO.getSg155Nome());
        employeeResponseDTO.setFiscalCode(sg155StgiuridicoDTO.getSg155CodiceFiscale());
        employeeResponseDTO.setBirthDate(CommonUtilsMethod.dateToLocalDate(sg155StgiuridicoDTO.getSg155DataNascita()));
        employeeResponseDTO.setAbbrBirthProvince(sg155StgiuridicoDTO.getSg155SiglaProvNasc());
        employeeResponseDTO.setBirthCity(sg155StgiuridicoDTO.getSg155ComuneNascita());
        employeeResponseDTO.setCivilStatus(sg155StgiuridicoDTO.getSg155StatoCivile());
        employeeResponseDTO.setGender(sg155StgiuridicoDTO.getSg155Sesso());
        employeeResponseDTO.setEmail(sg155StgiuridicoDTO.getSg155MailUfficio());
        employeeResponseDTO.setSerialNumber(sg155StgiuridicoDTO.getSg155Matricola());
        employeeResponseDTO.setArmedForceId(sg155StgiuridicoDTO.getSg155CodFfaa());
        employeeResponseDTO.setArmedForceDescription(sg155StgiuridicoDTO.getSg155DescrFfaa());
        employeeResponseDTO.setStaffPositionId(sg155StgiuridicoDTO.getSg155IdPosser());
        employeeResponseDTO.setStaffPositionDescription(sg155StgiuridicoDTO.getSg155DescrPosser());
        if (sg155StgiuridicoDTO.getSg155IdCatmil() != null)
            employeeResponseDTO.setStaffCategoryId(Long.valueOf(sg155StgiuridicoDTO.getSg155IdCatmil()));
        employeeResponseDTO.setStaffCategoryDescription(sg155StgiuridicoDTO.getSg155DescrCatmil());
        employeeResponseDTO.setRankId(sg155StgiuridicoDTO.getSg155CodGrado());
        employeeResponseDTO.setRankDescription(sg155StgiuridicoDTO.getSg155DescrGrado());
        employeeResponseDTO.setRoleDescription(sg155StgiuridicoDTO.getSg155DescrRuolo());
        employeeResponseDTO.setRoleLegalDate(CommonUtilsMethod.dateToLocalDate(sg155StgiuridicoDTO.getSg155DataDecGiuRuolo()));
        employeeResponseDTO.setLegalStatusCategoryDesc(sg155StgiuridicoDTO.getSg155DescrCatpos());
        employeeResponseDTO.setDescrCatpers(sg155StgiuridicoDTO.getSg155DescrCatpers());
        employeeResponseDTO.setDataDecGiuGrado(CommonUtilsMethod.dateToLocalDate(sg155StgiuridicoDTO.getSg155DataDecGiuGrado()));
        employeeResponseDTO.setUidPosSer(sg155StgiuridicoDTO.getSg155UidPosSer());
        employeeResponseDTO.setCodCatpers(sg155StgiuridicoDTO.getSg155CodCatpers());
        employeeResponseDTO.setCodUidCatpers(sg155StgiuridicoDTO.getSg155CodUidCatpers());
        employeeResponseDTO.setDataDecGiuCatpers(CommonUtilsMethod.dateToLocalDate(sg155StgiuridicoDTO.getSg155DataDecGiuCatpers()));
        employeeResponseDTO.setAcrPosSer(sg155StgiuridicoDTO.getSg155AcrPosSer());
        employeeResponseDTO.setCodSpecCat(sg155StgiuridicoDTO.getSg155CodSpecCat());
        employeeResponseDTO.setCodUidSpecCat(sg155StgiuridicoDTO.getSg155CodUidSpecCat());
        employeeResponseDTO.setDescrSpecCat(sg155StgiuridicoDTO.getSg155DescrSpecCat());
        employeeResponseDTO.setDescrSpecInc(sg155StgiuridicoDTO.getSg155DescrSpecInc());
        employeeResponseDTO.setDataAnzSp(CommonUtilsMethod.dateToLocalDate(sg155StgiuridicoDTO.getSg155DataAnzSp()));

        return employeeResponseDTO;
    }

}
