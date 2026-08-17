package com.SistemaAlquiler.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pago_alquiler")

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoAlquiler {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codpago")
    private Integer codpago;
    @ManyToOne
    @JoinColumn(name = "codasig")
    private InquilinoCuarto contrato;
    @ManyToOne
    @JoinColumn(name = "codusu")
    private Usuario usuario;
    @Column(name = "cantidad_meses")
    private Integer cantidadMeses;
    @Column(name = "monto")
    private Double monto;
    @Column(name = "fechaPago")
    private LocalDateTime fechaPago;
    @Column(name = "periodo_inicio")
    private LocalDate periodoInicio;
    @Column(name = "periodo_fin")
    private LocalDate periodoFin;
    @Column(name = "origen_pago")
    private String origenPago;
    @Enumerated(EnumType.STRING)
    @Column(name="metodo_pago")
    private MetodoPago metodoPago;
    @Enumerated(EnumType.STRING)
    @Column(name="estado_pago")
    private EstadoPago estadoPago;
    @Column(name = "culqi_charge_id")
    private String culqiChargeId;
    @Column(name = "numero_operacion")
    private String numeroOperacion;
    @Column(name="numero_comprobante")
    private String numeroComprobante;
    @Column(name = "observacion")
    private String observacion;
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
    @OneToMany(
            mappedBy = "pago",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DetallePagoAlquiler> detalles;
}
