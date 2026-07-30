package com.cibertec.ms_credits.controller;

import com.cibertec.ms_credits.common.ApiResponse;
import com.cibertec.ms_credits.constants.ApiMessages;
import com.cibertec.ms_credits.dto.request.CotizacionRequest;
import com.cibertec.ms_credits.dto.response.CotizacionDetalleResponse;
import com.cibertec.ms_credits.dto.response.CotizacionResponse;
import com.cibertec.ms_credits.dto.response.PrestamoResponse;
import com.cibertec.ms_credits.service.CotizacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cotizaciones")
public class CotizacionController {

    private final CotizacionService cotizacionService;

    public CotizacionController(CotizacionService cotizacionService) {
        this.cotizacionService = cotizacionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CotizacionResponse>> registrar(@Valid @RequestBody CotizacionRequest request) {
        CotizacionResponse response = cotizacionService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, ApiMessages.COTIZACION_REGISTRADA));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CotizacionResponse>>> listar() {
        List<CotizacionResponse> response = cotizacionService.listar();
        return ResponseEntity.ok(ApiResponse.success(response, ApiMessages.COTIZACIONES_LISTADAS));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CotizacionDetalleResponse>> obtenerDetalle(@PathVariable Integer id) {
        CotizacionDetalleResponse response = cotizacionService.obtenerDetalle(id);
        return ResponseEntity.ok(ApiResponse.success(response, ApiMessages.COTIZACION_DETALLE_OBTENIDO));
    }

    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<ApiResponse<PrestamoResponse>> aprobar(@PathVariable Integer id) {
        PrestamoResponse response = cotizacionService.aprobar(id);
        return ResponseEntity.ok(ApiResponse.success(response, ApiMessages.COTIZACION_APROBADA));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CotizacionResponse>> eliminar(@PathVariable Integer id) {
        CotizacionResponse response = cotizacionService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success(response, ApiMessages.COTIZACION_ELIMINADA));
    }
}
