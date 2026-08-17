package com.SistemaAlquiler.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "inquilino_historico")
public class InquilinoHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codhistinq;


    private Integer codinq;

    private String nominq;
    private String apepinq;
    private String apeminq;
    private String docinq;
    private LocalDate fecreg;
    private String celinq;
    private String corinq;
    private Boolean estinq;

    @ManyToOne
    @JoinColumn(name = "codtipdoc")
    private TipoDocumento tipoDocumento;

    @ManyToOne
    @JoinColumn(name = "codusu")
    private Usuario usuario;

    @Column(name = "fecha_movimiento")
    private LocalDateTime fechaMovimiento;

    @Column(name = "tipo_movimiento")
    private String tipoMovimiento;

    @PrePersist
    public void registrarFechaMovimiento() {
        if (fechaMovimiento == null) {
            fechaMovimiento = LocalDateTime.now();
        }
    }
}