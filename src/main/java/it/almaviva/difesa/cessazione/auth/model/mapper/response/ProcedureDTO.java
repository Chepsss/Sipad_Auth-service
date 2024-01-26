package it.almaviva.difesa.cessazione.auth.model.mapper.response;

import it.almaviva.difesa.cessazione.auth.model.mapper.RoleDTO;
import lombok.Data;

import java.util.List;

@Data
public class ProcedureDTO {

    private Long id;
    private String codeProcess;
    private String bpmnProcessId;
    private Long idAuthor;
    private String author;
    private Long idAssignedTo;
    private String codeState;
    private List<RoleDTO> roles;

}
