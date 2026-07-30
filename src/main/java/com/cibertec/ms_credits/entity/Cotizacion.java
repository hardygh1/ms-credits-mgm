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
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cotizaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cotizacion_id")
    private Integer cotizacionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "monto", precision = 12, scale = 2, nullable = false)
    private BigDecimal monto;

    @Column(name = "tasa_interes", precision = 5, scale = 2, nullable = false)
    private BigDecimal tasaInteres;

    @Column(name = "plazo_meses", nullable = false)
    private Integer plazoMeses;

    @Column(name = "cuota_mensual", precision = 12, scale = 2, nullable = false)
    private BigDecimal cuotaMensual;

    @Column(name = "total_interes", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalInteres;

    @Column(name = "total_pagar", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalPagar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id", nullable = false)
    private Parametro estado;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;
}
