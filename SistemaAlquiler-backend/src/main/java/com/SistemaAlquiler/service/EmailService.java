package com.SistemaAlquiler.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // ✅ Se lee automáticamente del application.properties
    @Value("${spring.mail.username}")
    private String fromEmail;

    public void enviarAlertaMorosidad(String emailPropietario, String nombreInquilino, 
                                      double montoDeuda, int diasMorosidad) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);  // ✅ Usa la variable dinámica
            helper.setTo(emailPropietario);
            helper.setSubject("⚠️ Alerta de Morosidad - " + nombreInquilino);
            
            String htmlContent = """
                <html><body style='font-family: Arial, sans-serif;'>
                <h2 style='color: #dc3545;'>🔴 Notificación de Pago Pendiente</h2>
                <p>Estimado propietario,</p>
                <p>El inquilino <strong>%s</strong> presenta una deuda pendiente:</p>
                <ul>
                    <li><strong>Monto adeudado:</strong> S/ %.2f</li>
                    <li><strong>Días de mora:</strong> %d días</li>
                </ul>
                <hr><small>Sistema de Alquiler - Mensaje automático</small>
                </body></html>
                """.formatted(nombreInquilino, montoDeuda, diasMorosidad);

            helper.setText(htmlContent, true);
            mailSender.send(message);
            
            System.out.println("✅ Correo enviado a: " + emailPropietario);
        } catch (MessagingException e) {
            System.err.println("❌ Error enviando correo: " + e.getMessage());
        }
    }

    public void enviarAlertaMorosidadInquilino(String emailInquilino, String nombreInquilino, 
                                                double montoDeuda, int diasMorosidad) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);  // ✅ Usa la variable dinámica
            helper.setTo(emailInquilino);
            helper.setSubject("⚠️ Aviso de Morosidad - " + nombreInquilino);
            
            String htmlContent = """
                <html><body style='font-family: Arial, sans-serif;'>
                <h2 style='color: #dc3545;'>⚠️ Aviso de Pago Vencido</h2>
                <p>Estimado/a <strong>%s</strong>,</p>
                <p>Le informamos que su pago de alquiler se encuentra vencido:</p>
                <ul>
                    <li><strong>Monto adeudado:</strong> S/ %.2f</li>
                    <li><strong>Días de mora:</strong> %d días</li>
                </ul>
                <p>Por favor, regularice su situación lo antes posible.</p>
                <hr><small>Sistema de Alquiler - Mensaje automático</small>
                </body></html>
                """.formatted(nombreInquilino, montoDeuda, diasMorosidad);

            helper.setText(htmlContent, true);
            mailSender.send(message);
            
            System.out.println("✅ Alerta de morosidad enviada al inquilino: " + emailInquilino);
        } catch (MessagingException e) {
            System.err.println("❌ Error enviando alerta al inquilino: " + e.getMessage());
        }
    }

    public void enviarRecordatorioPago(String emailInquilino, String nombreInquilino, 
                                        double montoPago, int diasParaVencer) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);  // ✅ Usa la variable dinámica
            helper.setTo(emailInquilino);
            helper.setSubject("📅 Recordatorio de Pago - " + nombreInquilino);
            
            String htmlContent = """
                <html><body style='font-family: Arial, sans-serif;'>
                <h2 style='color: #28a745;'>📅 Recordatorio de Pago Próximo</h2>
                <p>Estimado/a <strong>%s</strong>,</p>
                <p>Le recordamos que su pago de alquiler está próximo a vencer:</p>
                <ul>
                    <li><strong>Monto a pagar:</strong> S/ %.2f</li>
                    <li><strong>Días restantes:</strong> %d días</li>
                </ul>
                <p>Por favor, realice el pago antes de la fecha de vencimiento.</p>
                <hr><small>Sistema de Alquiler - Mensaje automático</small>
                </body></html>
                """.formatted(nombreInquilino, montoPago, diasParaVencer);

            helper.setText(htmlContent, true);
            mailSender.send(message);
            
            System.out.println("✅ Recordatorio enviado a: " + emailInquilino);
        } catch (MessagingException e) {
            System.err.println("❌ Error enviando recordatorio: " + e.getMessage());
        }
    }
}