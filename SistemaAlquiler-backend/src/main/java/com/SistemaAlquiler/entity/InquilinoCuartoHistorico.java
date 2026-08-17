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
@Table(name = "inquilino_cuarto_historico")
public class InquilinoCuartoHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codhistasig;


    private Integer codasig;

    @ManyToOne
    @JoinColumn(name = "codinq")
    private Inquilino inquilino;

    @ManyToOne
    @JoinColumn(name = "codcuar")
    private Cuarto cuarto;

    private LocalDate fechin;
    private LocalDate fechout;
    private Double montoTotal;
    private Boolean estado;

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