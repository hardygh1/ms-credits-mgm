package com.cibertec.ms_credits.repository;

import com.cibertec.ms_credits.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer>, JpaSpecificationExecutor<Cliente> {

    Optional<Cliente> findByTipoDocumentoParametroIdAndNumeroDocumento(Integer tipoDocumentoId, String numeroDocumento);

    boolean existsByTipoDocumentoParametroIdAndNumeroDocumento(Integer tipoDocumentoId, String numeroDocumento);

    boolean existsByTipoDocumentoParametroIdAndNumeroDocumentoAndClienteIdNot(
            Integer tipoDocumentoId, String numeroDocumento, Integer clienteId);
}
