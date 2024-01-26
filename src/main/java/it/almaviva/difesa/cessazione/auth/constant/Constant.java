package it.almaviva.difesa.cessazione.auth.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constant {

    //controller paths
    public static final String AUTH_URL = "/auth";
    public static final String EMPLOYEE_URL = "/employee";
    public static final String PARAMS_URL = "/params";
    public static final String ROLE_URL = "/role";
    public static final String USER_URL = "/user";
    public static final String PRIVILEGE_URL = "/privilege";

    //regexes
    public static final String FISCAL_CODE_REGEX = "^[a-zA-Z]{6}[0-9]{2}[abcdehlmprstABCDEHLMPRST]{1}[0-9]{2}([a-zA-Z]{1}[0-9]{3})[a-zA-Z]{1}$";

    //date formats
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String DD_MM_YYYY_HH_MM_SS = "dd-MM-yyyy hh:mm:ss";

    //headers
    public static final String AUTH_HEADER = "Authorization-Cess";
    public static final String AUTH = "auth";

    //cess roles
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String ADMIN_ROLE_ID = ROLE_PREFIX + "ADMIN";
    public static final String TEMPLATE_ADMIN_ROLE_ID = ROLE_PREFIX + "TEMPLATE_ADMIN";
    public static final String INSTRUCTOR_GRADUATED_ROLE_ID = ROLE_PREFIX + "INSTRUCTOR_GRADUATED";
    public static final String INSTRUCTOR_OFFICER_ROLE_ID = ROLE_PREFIX + "INSTRUCTOR_OFFICER";
    public static final String INSTRUCTOR_SUB_OFFICER_ROLE_ID = ROLE_PREFIX + "INSTRUCTOR_SUB_OFFICER";
    public static final String APPROVER_GRADUATED_ROLE_ID = ROLE_PREFIX + "APPROVER_GRADUATED";
    public static final String APPROVER_OFFICER_ROLE_ID = ROLE_PREFIX + "APPROVER_OFFICER";
    public static final String APPROVER_SUB_OFFICER_ROLE_ID = ROLE_PREFIX + "APPROVER_SUB_OFFICER";
    public static final String SIGNATURE_GRADUATED_ROLE_ID = ROLE_PREFIX + "SIGNATURE_GRADUATED";
    public static final String SIGNATURE_OFFICER_ROLE_ID = ROLE_PREFIX + "SIGNATURE_OFFICER";
    public static final String SIGNATURE_SUB_OFFICER_ROLE_ID = ROLE_PREFIX + "SIGNATURE_SUB_OFFICER";
    public static final String MANAGER_GRADUATED_ROLE_ID = ROLE_PREFIX + "MANAGER_GRADUATED";
    public static final String MANAGER_OFFICER_ROLE_ID = ROLE_PREFIX + "MANAGER_OFFICER";
    public static final String MANAGER_SUB_OFFICER_ROLE_ID = ROLE_PREFIX + "MANAGER_SUB_OFFICER";

    public static final String SERIAL_NUMBER = "serialNumber";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String FISCAL_CODE = "fiscalCode";
    public static final String BIRTH_DATE = "birthDate";
    public static final String ARMED_FORCE_DESCRIPTION = "armedForceDescription";
    public static final String STAFF_POSITION_DESCRIPTION = "staffPositionDescription";
    public static final String RANK_DESCRIPTION = "rankDescription";
    public static final String ROLES = "roles";

    public static final String USER_MUST_BE_LOGGED = "L'Utente deve essere loggato";
    public static final String WARNING_MSG = "La posizione di stato del dipendente non risulta compatibile con la cessazione";
    public static final String SAVE_DATA_NOT_SEND_EMAIL = "Dati salvati - Non è stato possibile inviare l'email di notifica della variazione all'utente.";
    public static final String SUCCESS = "Success";
    public static final String ROLE = "ruolo '%s'";
    public static final String SIPAD_USER_BY_EMPLOYEE_ID_NOT_FOUND = "SIPAD - Utente non trovato";
    public static final String SIPAD_USER_NOT_FOUND = SIPAD_USER_BY_EMPLOYEE_ID_NOT_FOUND + ": %s";
    public static final String USER_NOT_FOUND = "Utente %s non profilato";
    public static final String ONLY_ADMIN_CAN_MODIFY_ROLES = "Solo l'utente con ruolo amministratore può modificare i ruoli degli utenti";
    public static final String DELETE_USER_ADMIN_ERROR = "Non è consentito eliminare l'utente %s con il ruolo di amministratore locale";
    public static final String DELETE_USER_ERROR = "Non è consentito eliminare l'utente %s. L'utente ha un procedimento attivo assegnato in base al %s";
    public static final String DELETE_ADMIN_ROLE_ERROR = "Non è consentito eliminare il ruolo di amministratore locale";
    public static final String DELETE_ROLE_ERROR = "Non è consentito eliminare il %s. L'utente ha un procedimento attivo assegnato in base a quel ruolo";
    public static final String EMAIL_SUBJECT = "SIPAD - Notifica creazione/modifica abilitazioni";

    public static final String TEMPLATE_EMAIL_NOTIFICATION = "notificaAutorizzazione";
    public static final String SG_155_COGNOME = "sg155Cognome";
    public static final String SG_155_NOME = "sg155Nome";
    public static final String SG_155_CODICE_FISCALE = "sg155CodiceFiscale";
    public static final String SG_155_MATRICOLA = "sg155Matricola";
    public static final String SG_155_DATA_NASCITA = "sg155DataNascita";
    public static final String SG_155_DESCR_FFAA = "sg155DescrFfaa";
    public static final String SG_155_DESCR_POSSER = "sg155DescrPosser";
    public static final String SG_155_DESCR_GRADO = "sg155DescrGrado";

}
