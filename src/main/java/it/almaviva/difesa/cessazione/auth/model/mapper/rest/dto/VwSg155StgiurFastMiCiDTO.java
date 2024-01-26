package it.almaviva.difesa.cessazione.auth.model.mapper.rest.dto;

import it.almaviva.difesa.cessazione.auth.model.mapper.response.GenericResponseDTO;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class VwSg155StgiurFastMiCiDTO implements Serializable, GenericResponseDTO {

    private static final long serialVersionUID = -7915103169953003490L;

    private Long sg155IdDip;
    private String sg155Cognome;
    private String sg155Nome;
    private String sg155CodiceFiscale;
    private LocalDate sg155DataNascita;
    private String sg155Sesso;
    private String sg155Mail;
    private String sg155CodGrado;
    private String sg155DescrGrado;
    private String sg155CodFfaa;
    private String sg155DescrFfaa;

}