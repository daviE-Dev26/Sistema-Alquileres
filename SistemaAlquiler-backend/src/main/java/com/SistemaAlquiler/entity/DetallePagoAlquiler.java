package com.SistemaAlquiler.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "detalle_pago_alquiler")

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetallePagoAlquiler {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coddetalle")
    private Integer coddetalle;
    @ManyToOne
    @JoinColumn(name = "codpago")
    private PagoAlquiler pago;
    @ManyToOne
    @JoinColumn(name="codasig")
    private InquilinoCuarto contrato;
    @Column(nullable=false)
    private Integer anio;
    @Column(nullable=false)
    private Integer mes;
    @Column(name="fecha_pago")
    private LocalDateTime fechaPago;
    @Column(name = "monto")
    private Double monto;
    @Column(name = "estado")
    private String estado;

}