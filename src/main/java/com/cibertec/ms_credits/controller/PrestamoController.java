package com.cibertec.ms_credits.controller;

import com.cibertec.ms_credits.common.ApiResponse;
import com.cibertec.ms_credits.constants.ApiMessages;
import com.cibertec.ms_credits.dto.request.PagoRequest;
import com.cibertec.ms_credits.dto.request.PrestamoRequest;
import com.cibertec.ms_credits.dto.response.CuotaResponse;
import com.cibertec.ms_credits.dto.response.PagoResponse;
import com.cibertec.ms_credits.dto.response.PrestamoDetalleResponse;
import com.cibertec.ms_credits.dto.response.PrestamoResponse;
import com.cibertec.ms_credits.service.CuotaService;
import com.cibertec.ms_credits.service.PagoService;
import com.cibertec.ms_credits.service.PrestamoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;
    private final CuotaService cuotaService;
    private final PagoService pagoService;

    public PrestamoController(PrestamoService prestamoService, CuotaService cuotaService, PagoService pagoService) {
        this.prestamoService = prestamoService;
        this.cuotaService = cuotaService;
        this.pagoService = pagoService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PrestamoResponse>> registrar(@Valid @RequestBody PrestamoRequest request) {
        PrestamoResponse response = prestamoService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, ApiMessages.PRESTAMO_REGISTRADO));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PrestamoResponse>>> listar() {
        List<PrestamoResponse> response = prestamoService.listar();
        return ResponseEntity.ok(ApiResponse.success(response, ApiMessages.PRESTAMOS_LISTADOS));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PrestamoDetalleResponse>> obtenerDetalle(@PathVariable Integer id) {
        PrestamoDetalleResponse response = prestamoService.obtenerDetalle(id);
        return ResponseEntity.ok(ApiResponse.success(response, ApiMessages.PRESTAMO_DETALLE_OBTENIDO));
    }

    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<ApiResponse<PrestamoResponse>> aprobar(@PathVariable Integer id) {
        PrestamoResponse response = prestamoService.aprobar(id);
        return ResponseEntity.ok(ApiResponse.success(response, ApiMessages.PRESTAMO_APROBADO));
    }

    @GetMapping("/{id}/cuotas")
    public ResponseEntity<ApiResponse<List<CuotaResponse>>> listarCuotas(@PathVariable Integer id) {
        List<CuotaResponse> response = cuotaService.listarPorPrestamo(id);
        return ResponseEntity.ok(ApiResponse.success(response, ApiMessages.CUOTAS_LISTADAS));
    }

    @PostMapping("/{id}/pagos")
    public ResponseEntity<ApiResponse<PagoResponse>> registrarPago(@PathVariable Integer id,
                                                                     @Valid @RequestBody PagoRequest request) {
        PagoResponse response = pagoService.registrarPago(id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, ApiMessages.PAGO_REGISTRADO));
    }

    @GetMapping("/{id}/pagos")
    public ResponseEntity<ApiResponse<List<PagoResponse>>> listarPagos(@PathVariable Integer id) {
        List<PagoResponse> response = pagoService.listarPorPrestamo(id);
        return ResponseEntity.ok(ApiResponse.success(response, ApiMessages.PAGOS_LISTADOS));
    }
}
