package com.cibertec.ms_credits.controller;

import com.cibertec.ms_credits.common.ApiResponse;
import com.cibertec.ms_credits.constants.ApiMessages;
import com.cibertec.ms_credits.dto.response.ParametroResponse;
import com.cibertec.ms_credits.service.ParametroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/parametros")
public class ParametroController {

    private final ParametroService parametroService;

    public ParametroController(ParametroService parametroService) {
        this.parametroService = parametroService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ParametroResponse>>> listar(
            @RequestParam(required = false) String tipo) {
        List<ParametroResponse> response = parametroService.listar(tipo);
        return ResponseEntity.ok(ApiResponse.success(response, ApiMessages.PARAMETROS_LISTADOS));
    }
}
