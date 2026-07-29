package com.cibertec.ms_credits.repository;

import com.cibertec.ms_credits.entity.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrestamoRepository extends JpaRepository<Prestamo, Integer> {
}
