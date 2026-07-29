package com.cibertec.ms_credits.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "parametros",
        indexes = @Index(name = "idx_parametro_tipo", columnList = "tipo"),
        uniqueConstraints = @UniqueConstraint(name = "uk_parametro_tipo_codigo", columnNames = {"tipo", "codigo"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parametro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parametro_id")
    private Integer parametroId;

    @Column(name = "tipo", length = 30, nullable = false)
    private String tipo;

    @Column(name = "codigo", length = 30, nullable = false)
    private String codigo;

    @Column(name = "descripcion", length = 100, nullable = false)
    private String descripcion;

    @Column(name = "estado", length = 20, nullable = false)
    @Builder.Default
    private String estado = "ACTIVO";

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}
