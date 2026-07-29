package com.cibertec.ms_credits.repository;

import com.cibertec.ms_credits.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Integer> {

    List<Pago> findByCuotaPrestamoPrestamoIdOrderByFechaPagoAsc(Integer prestamoId);
}
