package com.SistemaAlquiler.entity;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "inquilino_cuarto")
public class InquilinoCuarto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codasig")
    private Integer codasig; // 🚀 Nueva clave primaria autoincremental única

    @ManyToOne
    @JoinColumn(name = "codinq", nullable = false)
    private Inquilino inquilino; // Ahora es una relación simple, ya no es @Id

    @ManyToOne
    @JoinColumn(name = "codcuar", nullable = false)
    private Cuarto cuarto; // Ahora es una relación simple, ya no es @Id

    @Column(name = "fechin", nullable = false)
    private LocalDate fechin;

    @Column(name = "fechout")
    private LocalDate fechout;

    @Column(name = "montoTotal", nullable = false)
    private Double montoTotal;

    @Column(name = "estado", nullable = false)
    private Boolean estado;

    @ManyToOne
    @JoinColumn(name = "codusu", nullable = false)
    private Usuario usuario;

}