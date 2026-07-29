package com.cibertec.ms_credits.repository;

import com.cibertec.ms_credits.entity.Parametro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParametroRepository extends JpaRepository<Parametro, Integer> {

    Optional<Parametro> findByTipoAndCodigo(String tipo, String codigo);

    List<Parametro> findByTipoOrderByCodigoAsc(String tipo);
}
