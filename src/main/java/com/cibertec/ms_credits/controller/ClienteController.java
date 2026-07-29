package com.cibertec.ms_credits.controller;

import com.cibertec.ms_credits.common.ApiResponse;
import com.cibertec.ms_credits.constants.ApiMessages;
import com.cibertec.ms_credits.dto.request.ClienteEstadoRequest;
import com.cibertec.ms_credits.dto.request.ClienteRequest;
import com.cibertec.ms_credits.dto.response.ClienteResponse;
import com.cibertec.ms_credits.dto.response.PageResponse;
import com.cibertec.ms_credits.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private static final Set<String> SORT_FIELDS_PERMITIDOS =
            Set.of("clienteId", "nombres", "apellidos", "numeroDocumento", "fechaRegistro");
    private static final String SORT_FIELD_DEFAULT = "clienteId";
    private static final int SIZE_DEFAULT = 10;
    private static final int SIZE_MAXIMO = 100;

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClienteResponse>> registrar(@Valid @RequestBody ClienteRequest request) {
        ClienteResponse response = clienteService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, ApiMessages.CLIENTE_REGISTRADO));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ClienteResponse>>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer estadoId,
            @RequestParam(required = false) Integer tipoDocumentoId) {
        Pageable pageable = resolverPageable(page, size, sort);
        PageResponse<ClienteResponse> response =
                clienteService.listar(documento, nombre, estadoId, tipoDocumentoId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response, ApiMessages.CLIENTES_LISTADOS));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponse>> obtenerPorId(@PathVariable Integer id) {
        ClienteResponse response = clienteService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.success(response, ApiMessages.CLIENTE_OBTENIDO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponse>> actualizar(@PathVariable Integer id,
                                                                     @Valid @RequestBody ClienteRequest request) {
        ClienteResponse response = clienteService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, ApiMessages.CLIENTE_ACTUALIZADO));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<ClienteResponse>> cambiarEstado(@PathVariable Integer id,
                                                                        @Valid @RequestBody ClienteEstadoRequest request) {
        ClienteResponse response = clienteService.cambiarEstado(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, ApiMessages.CLIENTE_ESTADO_ACTUALIZADO));
    }

    private Pageable resolverPageable(int page, int size, String sort) {
        int paginaSegura = Math.max(page, 0);
        int tamanoSeguro = size < 1 ? SIZE_DEFAULT : Math.min(size, SIZE_MAXIMO);
        return PageRequest.of(paginaSegura, tamanoSeguro, resolverSort(sort));
    }

    private Sort resolverSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.ASC, SORT_FIELD_DEFAULT);
        }

        String[] partes = sort.split(",");
        String campo = partes[0].trim();
        if (!SORT_FIELDS_PERMITIDOS.contains(campo)) {
            return Sort.by(Sort.Direction.ASC, SORT_FIELD_DEFAULT);
        }

        Sort.Direction direccion = (partes.length > 1 && "desc".equalsIgnoreCase(partes[1].trim()))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return Sort.by(direccion, campo);
    }
}
