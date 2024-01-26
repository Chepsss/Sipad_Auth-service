package it.almaviva.difesa.cessazione.auth.data.common;

import it.almaviva.difesa.cessazione.auth.enums.StatusEnum;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

public class PreventAnyDataManipulation {

    @PrePersist
    public void onPrePersist(Object o) {
        var objectClassName = o.getClass().getEnclosingClass() == null ? "null" : o.getClass().getEnclosingClass().getName();
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(StatusEnum.CANNOT_SAVE_DATA.getNameMessage(), objectClassName));
    }

    @PreUpdate
    public void onPreUpdate(Object o) {
        var objectClassName = o == null ? "null" : o.getClass();
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(StatusEnum.CANNOT_UPDATE_DATA.getNameMessage(), objectClassName));
    }

    @PreRemove
    public void onPreRemove(Object o) {
        var objectClassName = o == null ? "null" : o.getClass();
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(StatusEnum.CANNOT_DELETE_DATA.getNameMessage(), objectClassName));
    }
}
