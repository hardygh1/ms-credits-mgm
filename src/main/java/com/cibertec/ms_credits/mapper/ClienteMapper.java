package com.cibertec.ms_credits.mapper;

import com.cibertec.ms_credits.dto.request.ClienteRequest;
import com.cibertec.ms_credits.dto.response.ClienteResponse;
import com.cibertec.ms_credits.entity.Cliente;
import com.cibertec.ms_credits.entity.Parametro;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public Cliente toEntity(ClienteRequest request, Parametro tipoDocumento, Parametro estado) {
        return Cliente.builder()
                .tipoDocumento(tipoDocumento)
                .numeroDocumento(request.numeroDocumento())
                .nombres(request.nombres())
                .apellidos(request.apellidos())
                .telefono(request.telefono())
                .direccion(request.direccion())
                .correo(request.correo())
                .estado(estado)
                .build();
    }

    public void actualizar(Cliente cliente, ClienteRequest request, Parametro tipoDocumento) {
        cliente.setTipoDocumento(tipoDocumento);
        cliente.setNumeroDocumento(request.numeroDocumento());
        cliente.setNombres(request.nombres());
        cliente.setApellidos(request.apellidos());
        cliente.setTelefono(request.telefono());
        cliente.setDireccion(request.direccion());
        cliente.setCorreo(request.correo());
    }

    public ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getClienteId(),
                cliente.getTipoDocumento().getParametroId(),
                cliente.getTipoDocumento().getDescripcion(),
                cliente.getNumeroDocumento(),
                cliente.getNombres(),
                cliente.getApellidos(),
                cliente.getTelefono(),
                cliente.getDireccion(),
                cliente.getCorreo(),
                cliente.getEstado().getParametroId(),
                cliente.getEstado().getDescripcion(),
                cliente.getFechaRegistro()
        );
    }
}
