package com.cibertec.ms_credits.repository;

import com.cibertec.ms_credits.entity.DetalleCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleCotizacionRepository extends JpaRepository<DetalleCotizacion, Integer> {

    List<DetalleCotizacion> findByCotizacionCotizacionIdOrderByNumeroCuotaAsc(Integer cotizacionId);
}
