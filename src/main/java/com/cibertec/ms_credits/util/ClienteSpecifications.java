package com.cibertec.ms_credits.util;

import com.cibertec.ms_credits.entity.Cliente;
import org.springframework.data.jpa.domain.Specification;

public final class ClienteSpecifications {

    private ClienteSpecifications() {
    }

    public static Specification<Cliente> conNumeroDocumento(String numeroDocumento) {
        if (numeroDocumento == null || numeroDocumento.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }
        String patron = "%" + numeroDocumento.toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("numeroDocumento")), patron);
    }

    public static Specification<Cliente> conNombreOApellido(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }
        String patron = "%" + nombre.toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("nombres")), patron),
                cb.like(cb.lower(root.get("apellidos")), patron));
    }

    public static Specification<Cliente> conEstadoId(Integer estadoId) {
        if (estadoId == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.equal(root.get("estado").get("parametroId"), estadoId);
    }

    public static Specification<Cliente> conTipoDocumentoId(Integer tipoDocumentoId) {
        if (tipoDocumentoId == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.equal(root.get("tipoDocumento").get("parametroId"), tipoDocumentoId);
    }
}
