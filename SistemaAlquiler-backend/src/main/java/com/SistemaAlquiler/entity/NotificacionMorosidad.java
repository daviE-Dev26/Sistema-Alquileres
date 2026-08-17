package com.SistemaAlquiler.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "notificacion_morosidad")
public class NotificacionMorosidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notif")
    private Integer idNotif;

    @Column(name = "codinq")
    private Integer codinq;

    @Column(name = "dias_mora")
    private Integer diasMora;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "email_destino")
    private String emailDestino;

    @Column(name = "tipo_notificacion")
    private String tipoNotificacion; // RECORDATORIO o MOROSIDAD
}