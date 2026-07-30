package com.cibertec.ms_credits.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cuotas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cuota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cuota_id")
    private Integer cuotaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestamo_id", nullable = false)
    private Prestamo prestamo;

    @Column(name = "numero_cuota", nullable = false)
    private Integer numeroCuota;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "capital", precision = 12, scale = 2, nullable = false)
    private BigDecimal capital;

    @Column(name = "interes", precision = 12, scale = 2, nullable = false)
    private BigDecimal interes;

    @Column(name = "monto", precision = 12, scale = 2, nullable = false)
    private BigDecimal monto;

    @Column(name = "saldo", precision = 12, scale = 2, nullable = false)
    private BigDecimal saldo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id", nullable = false)
    private Parametro estado;
}
