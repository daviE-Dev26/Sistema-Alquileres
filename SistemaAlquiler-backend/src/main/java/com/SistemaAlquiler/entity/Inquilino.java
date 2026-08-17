package com.SistemaAlquiler.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "inquilino")
public class Inquilino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

}