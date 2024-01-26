package it.almaviva.difesa.cessazione.auth.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Code {

    R_SETUP_PROCEDURE_PARAM("R_SETUP_PROCEDURE_PARAM", "Impostazione Dei Parametri Iniziali Trasversali A Tutti I Procedimenti Cessazione"),
    R_RIASSIGN_PROCEDURE("R_RIASSIGN_PROCEDURE", "Riassegna La Lavorazione"),
    R_ASSIGN_ROLE_USER("R_ASSIGN_ROLE_USER", "Assegnazione Ruoli Agli Utenti"),
    R_LIST_PROCEDURE("R_LIST_PROCEDURE", "Visualizzazione Liste Procedimenti In Corso"),
    W_SEND_ADHOC("W_SEND_ADHOC", "Invia Lettere In Adhoc"),
    ;

    private final String code;
    private final String desc;

}
