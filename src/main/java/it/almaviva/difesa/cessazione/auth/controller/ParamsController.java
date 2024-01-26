package it.almaviva.difesa.cessazione.auth.controller;

import it.almaviva.difesa.cessazione.auth.constant.Constant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constant.PARAMS_URL)

public class ParamsController {

    @GetMapping("/fiscal-code")
    public ResponseEntity<String> getFiscalCodeRegex() {
        return ResponseEntity.ok(Constant.FISCAL_CODE_REGEX);
    }

}
