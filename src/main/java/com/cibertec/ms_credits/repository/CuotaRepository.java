package com.cibertec.ms_credits.repository;

import com.cibertec.ms_credits.entity.Cuota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CuotaRepository extends JpaRepository<Cuota, Integer> {

    List<Cuota> findByPrestamoPrestamoIdOrderByNumeroCuotaAsc(Integer prestamoId);

    Optional<Cuota> findFirstByPrestamoPrestamoIdAndEstadoCodigoOrderByNumeroCuotaAsc(Integer prestamoId, String estadoCodigo);

    boolean existsByPrestamoPrestamoIdAndEstadoCodigo(Integer prestamoId, String estadoCodigo);
}
