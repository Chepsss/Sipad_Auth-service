package it.almaviva.difesa.cessazione.auth.enums;

import it.almaviva.difesa.cessazione.auth.constant.Constant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum StatusEnum {

    CANNOT_DELETE_DATA("CANNOT_DELETE_DATA"),
    CANNOT_SAVE_DATA("CANNOT_SAVE_DATA"),
    CANNOT_UPDATE_DATA("CANNOT_UPDATE_DATA"),
    COMPILER_EQUALS_LOCAL_MANAGER("COMPILER_EQUALS_LOCAL_MANAGER"),
    COURSE_NOT_VALID("COURSE_NOT_VALID"),
    DIFFERENT_IDS_ATTACHMENT("DIFFERENT_IDS_ATTACHMENT"),
    DIFFERENT_PHASE_PROCEDURE("DIFFERENT_PHASE_PROCEDURE"),
    DIFFERENT_STATUS_OR_PHASE("DIFFERENT_STATUS_OR_PHASE"),
    DISSOCATED_TOKEN_ID_USER("DISSOCATED_TOKEN_ID_USER"),
    EMPLOYEE_ALREADY_BELONG_PROCEDURE("EMPLOYEE_ALREADY_BELONG_PROCEDURE"),
    EMPLOYEE_CURRENT_ASSIGNMENT_NOT_FINDABLE("EMPLOYEE_CURRENT_ASSIGNMENT_NOT_FINDABLE"),
    EMPLOYEE_DETAILS_NOT_FOUND("EMPLOYEE_DETAILS_NOT_FOUND"),
    USER_DETAILS_NOT_FOUND("USER_DETAILS_NOT_FOUND"),
    EMPLOYEE_HAS_ORDER_NUMBER("EMPLOYEE_HAS_ORDER_NUMBER"),
    EMPLOYEE_ID_ALREADY_USED("EMPLOYEE_ID_ALREADY_USED"),
    EMPLOYEE_NO_WITNESS("EMPLOYEE_NO_WITNESS"),
    EMPLOYEE_NOT_BELONG_GROUP("EMPLOYEE_NOT_BELONG_GROUP"),
    EMPLOYEE_NOT_BELONG_PROCEDURE("EMPLOYEE_NOT_BELONG_PROCEDURE"),
    EMPLOYEE_NOT_FINDABLE_ID("EMPLOYEE_NOT_FINDABLE_ID "),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR"),
    INVALID_ATTACHMENT_TYPE("INVALID_ATTACHMENT_TYPE"),
    INVALID_PERIOD("INVALID_PERIOD"),
    INVALID_ID_ROLE("INVALID_ID_ROLE"),
    INVALID_IDS_REQUEST("INVALID_IDS_REQUEST"),
    INVALID_PHASE("INVALID_PHASE"),
    INVALID_STATUS("INVALID_STATUS"),
    INVALID_TYPE_DESCRIPTOR("INVALID_TYPE_DESCRIPTOR"),
    INVALID_UPPER_SEQUENCE("INVALID_UPPER_SEQUENCE"),
    JSON_PROCESSING_ERROR("JSON_PROCESSING_ERROR"),
    JWT_OR_USERNAME_INVALID("JWT_OR_USERNAME_INVALID"),
    JWT_TOKEN_BEARER("JWT_TOKEN_BEARER"),
    JWT_TOKEN_EXPIRED_STATUS("JWT_TOKEN_EXPIRED_STATUS"),
    NO_PRIVILEGES_ROLE("NO_PRIVILEGES_ROLE"),
    NO_PROCEDURE_ONGOING("NO_PROCEDURE_ONGOING"),
    NO_RANK_VALUES_EMPLOYEE("NO_RANK_VALUES_EMPLOYEE"),
    NO_ROLES_CODE("NO_ROLES_CODE"),
    NOT_FOUND("NOT_FOUND"),
    NOT_EDITABLE("NOT_EDITABLE"),
    NOT_RELATED_ANY_INSTITUTION("NOT_RELATED_ANY_INSTITUTION"),
    NOT_SIGNABLE("NOT_SIGNABLE"),
    NOT_SUPPLIED_TOKEN("NOT_SUPPLIED_TOKEN"),
    NOT_VALID_FILE_EXTENSION("NOT_VALID_FILE_EXTENSION"),
    NOT_VALID_VALUE_CHECK("NOT_VALID_VALUE_CHECK"),
    NOT_VALID_VALUE_CHECK_REVISOR("NOT_VALID_VALUE_CHECK_REVISOR"),
    NOT_VALID_VALUE_QUESTION("NOT_VALID_VALUE_QUESTION"),
    NOT_VISIBLE_PROCEDURE("NOT_VISIBLE_PROCEDURE"),
    ROLE_NOT_FINDABLE_ID("ROLE_NOT_FINDABLE_ID"),
    USER_NOT_FOUND("USER_NOT_FOUND"),
    USER_NOT_CREATABLE_ROLE("USER_NOT_CREATABLE_ROLE"),
    USER_NOT_FINDABLE_ID("USER_NOT_FINDABLE_ID"),
    USER_ROLE_ALREADY_PRESENT("USER_ROLE_ALREADY_PRESENT"),
    USER_WITHOUT_HYERARCHY("USER_WITHOUT_HYERARCHY"),
    USER_NOT_FINDABLE_TOKEN("USER_NOT_FINDABLE_TOKEN"),
    XML_NOT_FOUND("XML_NOT_FOUND"),
    WRONG_ORDER_TASK("WRONG_ORDER_TASK");

    private final String nameMessage;

    @Getter
    @RequiredArgsConstructor
    public enum UserEnumField {
        LAST_NAME(Constant.LAST_NAME, Constant.SG_155_COGNOME),
        FIRST_NAME(Constant.FIRST_NAME, Constant.SG_155_NOME),
        FISCAL_CODE(Constant.FISCAL_CODE, Constant.SG_155_CODICE_FISCALE),
        SERIAL_NUMBER(Constant.SERIAL_NUMBER, Constant.SG_155_MATRICOLA),
        BIRTH_DATE(Constant.BIRTH_DATE, Constant.SG_155_DATA_NASCITA),
        ARMED_FORCE_DESCRIPTION(Constant.ARMED_FORCE_DESCRIPTION, Constant.SG_155_DESCR_FFAA),
        STAFF_POSITION_DESCRIPTION(Constant.STAFF_POSITION_DESCRIPTION, Constant.SG_155_DESCR_POSSER),
        RANK_DESCRIPTION(Constant.RANK_DESCRIPTION, Constant.SG_155_DESCR_GRADO),
        ;

        private final String labelDto;
        private final String entityColumn;

        public static UserEnumField fromLabelDto(String labelDto) {
            for (UserEnumField e : UserEnumField.values()) {
                if (e.getLabelDto().equalsIgnoreCase(labelDto)) {
                    return e;
                }
            }
            return null;
        }
    }

}
