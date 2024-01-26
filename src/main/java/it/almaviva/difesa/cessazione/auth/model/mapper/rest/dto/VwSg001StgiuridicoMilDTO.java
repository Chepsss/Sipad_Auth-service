package it.almaviva.difesa.cessazione.auth.model.mapper.rest.dto;

import it.almaviva.difesa.cessazione.auth.model.mapper.response.GenericResponseDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class VwSg001StgiuridicoMilDTO implements GenericResponseDTO {

    private String descrGradoSup;
    private Long idDip;
    private String idForzaArmata;
    private String descrForzaArmata;
    private String codGrado;
    private String descrGrado;
    private LocalDate dataDecGiuGrado;
    private String descrStatoGrado;
    private String descrCategoria;
    private String codRuolo;
    private String descrRuolo;
    private LocalDate dataDecGiuRuolo;
    private String descrStatoRuolo;
    private String codPosizione;
    private LocalDate dataDecGiuPosizione;
    private String descrPosizione;
    private String descrStatoPosizione;
    private String codTipArmaCorpo;
    private String descrTipoArmaCorpo;
    private LocalDate dataDecGiuImm;
    private LocalDate dataImm;
    private Long idCatPersonale;
    private Long idCatPosStato;
    private String descrCatoPosStato;
    private LocalDate dataIniTipPosStato;
    private LocalDate decorEconRuolo;
    private LocalDate decorEconGrado;
    private LocalDate dataDescrPosser;
    private String descrPosser;
    private Long idCtpCatPersonale;
    private String idPosser;

}
