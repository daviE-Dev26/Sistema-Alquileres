package com.SistemaAlquiler.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ✅ Asegura la atomicidad de la operación

import com.SistemaAlquiler.dto.ActualizarInquilinoDTO;
import com.SistemaAlquiler.dto.ContratoFinalizadoDTO;
import com.SistemaAlquiler.dto.InquilinoListadoDTO;
import com.SistemaAlquiler.dto.usuario.CrearInquilinoDTO;
import com.SistemaAlquiler.entity.Cuarto;
import com.SistemaAlquiler.entity.Inquilino;
import com.SistemaAlquiler.entity.InquilinoCuarto;
import com.SistemaAlquiler.entity.InquilinoCuartoHistorico;
import com.SistemaAlquiler.entity.NotificacionMorosidad;
import com.SistemaAlquiler.entity.TipoDocumento;
import com.SistemaAlquiler.entity.Usuario;
import com.SistemaAlquiler.repository.CuartoRepository;
import com.SistemaAlquiler.repository.InquilinoCuartoRepository;
import com.SistemaAlquiler.repository.InquilinoRepository;
import com.SistemaAlquiler.repository.NotificacionMorosidadRepository;
import com.SistemaAlquiler.repository.TipoDocumentoRepository;
import com.SistemaAlquiler.repository.UsuarioRepository;
import com.SistemaAlquiler.entity.InquilinoHistorico;
import com.SistemaAlquiler.repository.InquilinoHistoricoRepository;
import com.SistemaAlquiler.repository.InquilinoCuartoHistoricoRepository;

@Service
public class InquilinoService {
    @Autowired
    private InquilinoRepository inquilinoRepository;
    @Autowired
    private InquilinoCuartoRepository inquilinoCuartoRepository;
    @Autowired
    private CuartoRepository cuartoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TipoDocumentoRepository tipoDocumentoRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private NotificacionMorosidadRepository notificacionRepository;
    @Autowired
    private PagoAlquilerService pagoAlquilerService;
    
    @Autowired
    private InquilinoHistoricoRepository inquilinoHistoricoRepository;
    
    @Autowired
    private InquilinoCuartoHistoricoRepository inquilinoCuartoHistoricoRepository;

    
    
    @Transactional // ✅ Mantiene seguras las inserciones/actualizaciones simultáneas en la base de datos
    public Inquilino registrar(CrearInquilinoDTO dto) {

      // 1️⃣ Validar si la fecha de inicio es anterior a la fecha de hoy antes de crear entidades
      if (dto.getFechaInicio() != null && dto.getFechaInicio().isBefore(LocalDate.now())) {
          throw new RuntimeException("La fecha de inicio no puede ser anterior a hoy");
      }

      // 2️⃣ Validar existencia del Usuario (Propietario)
      Usuario usuario = usuarioRepository.findById(dto.getCodusu()).orElse(null);
      if (usuario == null) {
          throw new RuntimeException("Usuario no encontrado");
      }

      // 3️⃣ Validar existencia del Cuarto
      Cuarto cuarto = cuartoRepository.findById(dto.getCodcuar()).orElse(null);
      if (cuarto == null) {
          throw new RuntimeException("Cuarto no encontrado");
      }
      
      // Validar si el cuarto ya está ocupado por otra persona actualmente
      if ("Ocupado".equalsIgnoreCase(cuarto.getEstcuar())) {
          throw new RuntimeException("El cuarto seleccionado ya se encuentra ocupado.");
      }

      // 4️⃣ Buscar u optimizar la existencia del inquilino por su DNI para este propietario
      Optional<Inquilino> inquilinoExistente = inquilinoRepository.findByDocinqAndUsuarioCodusu(dto.getDocinq(), dto.getCodusu());
      Inquilino inquilino;

      if (inquilinoExistente.isPresent()) {

    	    inquilino = inquilinoExistente.get();

    	    boolean datosContactoCambiaron =
    	            !Objects.equals(inquilino.getCelinq(), dto.getCelinq())
    	            || !Objects.equals(inquilino.getCorinq(), dto.getCorinq());

    	    if (datosContactoCambiaron) {

    	        guardarHistoricoInquilino(
    	                inquilino,
    	                "ACTUALIZACION_REINGRESO"
    	        );

    	        inquilino.setCelinq(dto.getCelinq());
    	        inquilino.setCorinq(dto.getCorinq());

    	        inquilino = inquilinoRepository.save(inquilino);
    	    }

    	} else {

    	    TipoDocumento tipoDocumento =
    	            tipoDocumentoRepository.findById(1)
    	                    .orElseThrow(() ->
    	                            new RuntimeException(
    	                                    "Tipo de documento no encontrado"
    	                            )
    	                    );

    	    inquilino = Inquilino.builder()
    	            .nominq(dto.getNominq())
    	            .apepinq(dto.getApepinq())
    	            .apeminq(dto.getApeminq())
    	            .docinq(dto.getDocinq())
    	            .fecreg(LocalDate.now())
    	            .celinq(dto.getCelinq())
    	            .corinq(dto.getCorinq())
    	            .estinq(true)
    	            .tipoDocumento(tipoDocumento)
    	            .usuario(usuario)
    	            .build();

    	    inquilino = inquilinoRepository.save(inquilino);
    	}

      // 5️⃣ Asignar fecha de inicio por defecto si viene vacía
      LocalDate fechaInicio = dto.getFechaInicio();
      if (fechaInicio == null) {
          fechaInicio = LocalDate.now();
      }
      
      // 6️⃣ Construir y guardar el Contrato/Alquiler (InquilinoCuarto) usando el ID autoincremental
      InquilinoCuarto relacion = InquilinoCuarto.builder()
              .inquilino(inquilino)
              .cuarto(cuarto)
              .fechin(fechaInicio)
              .fechout(null)
              .montoTotal(cuarto.getPreccuar())
              .estado(true)
              .usuario(usuario)
              .build();
      
      relacion=inquilinoCuartoRepository.save(relacion);
      pagoAlquilerService.crearDeudaInicial(relacion);
      // 7️⃣ 🚀 CONTROL DE DISPONIBILIDAD TEMPORAL
      // Si ingresa el día de hoy, el cuarto cambia su estado a Ocupado inmediatamente.
      // Si el ingreso está agendado a futuro, el cuarto se queda disponible hasta la fecha indicada.
      if (fechaInicio.isEqual(LocalDate.now())) {
          cuarto.setEstcuar("Ocupado");
      } else {
          cuarto.setEstcuar("Disponible");
      }
      
      cuartoRepository.save(cuarto);

      return inquilino;
  }
  public List<Inquilino> findAll() {
      return inquilinoRepository.findAll();
  }

/*  public List<InquilinoListadoDTO> listarDashboard(int codusu) {
      List<InquilinoCuarto> alquileres = inquilinoCuartoRepository.findByEstadoTrueAndUsuarioCodusu(codusu);
      List<InquilinoListadoDTO> lista = new ArrayList<>();

      for (InquilinoCuarto ic : alquileres) {
          Inquilino i = ic.getInquilino();
          Long diasRestantes = 0L;

          if (ic.getFechout() != null) {
              diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), ic.getFechout());
              if (diasRestantes < 0) {
                  diasRestantes = 0L;
              }
          }

          lista.add(
              new InquilinoListadoDTO(
            	ic.getCodasig(),
            	i.getCodinq(),
                  i.getNominq() + " " + i.getApepinq() + " " + i.getApeminq(),
                  i.getDocinq(),
                  i.getCelinq(),
                  i.getCorinq(),
                  ic.getCuarto().getNumcuar(),
                  diasRestantes,
                  i.getEstinq() ? "Activo" : "Inactivo",
                  ic.getCuarto().getSede().getNombre(),
                  ic.getCuarto().getPiso().getNumero()
              )
          );
      }

      return lista;
  }*/
  public List<InquilinoListadoDTO> listarDashboard(int codusu) {
      List<InquilinoCuarto> alquileres = inquilinoCuartoRepository.findByEstadoTrueAndUsuarioCodusu(codusu);
      List<InquilinoListadoDTO> lista = new ArrayList<>();
      LocalDate hoy = LocalDate.now();

      for (InquilinoCuarto ic : alquileres) {
          // 🚀 FILTRO: Si la fecha de inicio es posterior a hoy, no va en el dashboard principal (es una Reserva)
          if (ic.getFechin() != null && ic.getFechin().isAfter(hoy)) {
              continue;
          }

          Inquilino i = ic.getInquilino();
          Long diasRestantes = 0L;
          boolean esMoroso = false;
          int diasMorosidad = 0;

          if (ic.getFechout() != null) {
              diasRestantes = ChronoUnit.DAYS.between(hoy, ic.getFechout());
              
              if (diasRestantes < 0) {
                  // 📧 ESCENARIO 1: INQUILINO EN MORA
                  diasMorosidad = Math.abs(diasRestantes.intValue());
                  esMoroso = true;
                  diasRestantes = 0L;
                  
                  // 🔔 Notificar al PROPIETARIO sobre la morosidad
                  Usuario propietario = ic.getUsuario();
                  if (propietario != null && propietario.getCorusu() != null) {
                      // ✅ CAMBIO: Usa existsBy en lugar de findBy(...).isPresent()
                      boolean yaEnviadoProp = notificacionRepository
                          .existsByCodinqAndDiasMoraAndTipoNotificacion(
                              i.getCodinq(), diasMorosidad, "MOROSIDAD"
                          );
                          
                      if (!yaEnviadoProp) {
                          emailService.enviarAlertaMorosidad(
                              propietario.getCorusu(),
                              i.getNominq() + " " + i.getApepinq() + " " + i.getApeminq(),
                              ic.getMontoTotal(),
                              diasMorosidad
                          );
                          
                          NotificacionMorosidad notifProp = NotificacionMorosidad.builder()
                              .codinq(i.getCodinq())
                              .diasMora(diasMorosidad)
                              .fechaEnvio(LocalDateTime.now())
                              .emailDestino(propietario.getCorusu())
                              .tipoNotificacion("MOROSIDAD")
                              .build();
                          notificacionRepository.save(notifProp);
                          System.out.println("📧 Correo de morosidad enviado al propietario: " + propietario.getCorusu());
                      } else {
                          System.out.println("⏭️ Alerta al propietario omitida (ya enviada para " + diasMorosidad + " días)");
                      }
                  }
                  
                  // 🔔 Notificar al INQUILINO sobre su propia morosidad
                  if (i.getCorinq() != null) {
                      // ✅ CAMBIO: Usa existsBy en lugar de findBy(...).isPresent()
                      boolean yaEnviadoInq = notificacionRepository
                          .existsByCodinqAndDiasMoraAndTipoNotificacion(
                              i.getCodinq(), diasMorosidad, "MOROSIDAD_INQUILINO"
                          );
                          
                      if (!yaEnviadoInq) {
                          emailService.enviarAlertaMorosidadInquilino(
                              i.getCorinq(),
                              i.getNominq() + " " + i.getApepinq(),
                              ic.getMontoTotal(),
                              diasMorosidad
                          );
                          
                          NotificacionMorosidad notifInq = NotificacionMorosidad.builder()
                              .codinq(i.getCodinq())
                              .diasMora(diasMorosidad)
                              .fechaEnvio(LocalDateTime.now())
                              .emailDestino(i.getCorinq())
                              .tipoNotificacion("MOROSIDAD_INQUILINO")
                              .build();
                          notificacionRepository.save(notifInq);
                          System.out.println("📧 Alerta de morosidad enviada al inquilino: " + i.getCorinq());
                      } else {
                          System.out.println("⏭️ Alerta al inquilino omitida (ya enviada para " + diasMorosidad + " días)");
                      }
                  }
                  
              } else if (diasRestantes <= 3 && diasRestantes > 0) {
                  // 📧 ESCENARIO 2: RECORDATORIO 3 DÍAS ANTES DEL VENCIMIENTO
                  int diasParaVencer = diasRestantes.intValue();
                  
                  if (i.getCorinq() != null) {
                      // ✅ CAMBIO: Usa existsBy en lugar de findBy(...).isPresent()
                      boolean yaEnviado = notificacionRepository
                          .existsByCodinqAndDiasMoraAndTipoNotificacion(
                              i.getCodinq(), diasParaVencer, "RECORDATORIO"
                          );
                          
                      if (!yaEnviado) {
                          emailService.enviarRecordatorioPago(
                              i.getCorinq(),
                              i.getNominq() + " " + i.getApepinq(),
                              ic.getMontoTotal(),
                              diasParaVencer
                          );
                          
                          NotificacionMorosidad notif = NotificacionMorosidad.builder()
                              .codinq(i.getCodinq())
                              .diasMora(diasParaVencer)
                              .fechaEnvio(LocalDateTime.now())
                              .emailDestino(i.getCorinq())
                              .tipoNotificacion("RECORDATORIO")
                              .build();
                          notificacionRepository.save(notif);
                          System.out.println("📧 Recordatorio enviado al inquilino: " + i.getCorinq() + " (faltan " + diasParaVencer + " días)");
                      } else {
                          System.out.println("⏭️ Recordatorio omitido (ya enviado para " + diasParaVencer + " días)");
                      }
                  }
              }
          }

          lista.add(
              new InquilinoListadoDTO(
                  ic.getCodasig(),
                  i.getCodinq(),
                  i.getNominq() + " " + i.getApepinq() + " " + i.getApeminq(),
                  i.getDocinq(),
                  i.getCelinq(),
                  i.getCorinq(),
                  ic.getCuarto().getNumcuar(),
                  diasRestantes,
                  esMoroso ? "MOROSO (" + diasMorosidad + " días)" : (i.getEstinq() ? "Activo" : "Inactivo"),
                  ic.getCuarto().getSede().getNombre(),
                  ic.getCuarto().getPiso().getNumero()
              )
          );
      }

      return lista;
  }
  
  // 📅 NUEVO: Solo lista las Reservas a futuro (fechin > HOY)
  public List<InquilinoListadoDTO> listarReservas(int codusu) {
      List<InquilinoCuarto> alquileres = inquilinoCuartoRepository.findByEstadoTrueAndUsuarioCodusu(codusu);
      List<InquilinoListadoDTO> lista = new ArrayList<>();
      LocalDate hoy = LocalDate.now();

      for (InquilinoCuarto ic : alquileres) {
          // 🚀 FILTRO: Tomamos únicamente las asignaciones programadas a futuro
          if (ic.getFechin() != null && ic.getFechin().isAfter(hoy)) {
              Inquilino i = ic.getInquilino();
              
              // Calculamos cuántos días faltan para que llegue la fecha de inicio del contrato
              Long diasParaIniciar = ChronoUnit.DAYS.between(hoy, ic.getFechin());

              lista.add(
                  new InquilinoListadoDTO(
                      i.getCodinq(),
                      i.getCodinq(),
                      i.getNominq() + " " + i.getApepinq() + " " + i.getApeminq(),
                      i.getDocinq(),
                      i.getCelinq(),
                      i.getCorinq(),
                      ic.getCuarto().getNumcuar(),
                      diasParaIniciar,   // Pasamos los días faltantes en la misma columna numérica de la tabla
                      "Reservado",       // Estado estático visual para diferenciarlo en tu Frontend
                      ic.getCuarto().getSede().getNombre(),
                      ic.getCuarto().getPiso().getNumero()
                  )
              );
          }
      }

      return lista;
  }
    // =========================================================
    // 📝 Lógica interna para ACTUALIZAR al Inquilino y su Cuarto
    // =========================================================
  @Transactional
  public void actualizar(int id, CrearInquilinoDTO dto) {

      Inquilino inquilino = inquilinoRepository.findById(id)
              .orElseThrow(() ->
                      new RuntimeException("Inquilino no encontrado"));

      Optional<Inquilino> existente =
              inquilinoRepository.findByDocinqAndUsuarioCodusu(
                      dto.getDocinq(),
                      dto.getCodusu());

      if (existente.isPresent() &&
              !existente.get().getCodinq().equals(id)) {

          throw new RuntimeException(
                  "Ya tienes a otro inquilino registrado con este DNI.");

      }

      //nuevo codigo
	   // ===========================
	   // Guardar versión anterior
	   // ===========================
      boolean datosPersonalesCambiaron =
    	        !Objects.equals(inquilino.getNominq(), dto.getNominq())
    	        || !Objects.equals(inquilino.getApepinq(), dto.getApepinq())
    	        || !Objects.equals(inquilino.getApeminq(), dto.getApeminq())
    	        || !Objects.equals(inquilino.getDocinq(), dto.getDocinq())
    	        || !Objects.equals(inquilino.getCelinq(), dto.getCelinq())
    	        || !Objects.equals(inquilino.getCorinq(), dto.getCorinq());

    	if (datosPersonalesCambiaron) {

    	    guardarHistoricoInquilino(
    	            inquilino,
    	            "ACTUALIZACION_COMPLETA"
    	    );

    	    inquilino.setNominq(dto.getNominq());
    	    inquilino.setApepinq(dto.getApepinq());
    	    inquilino.setApeminq(dto.getApeminq());
    	    inquilino.setDocinq(dto.getDocinq());
    	    inquilino.setCelinq(dto.getCelinq());
    	    inquilino.setCorinq(dto.getCorinq());

    	    inquilinoRepository.save(inquilino);
    	}
      // ===========================
      // Actualizar datos personales
      // ===========================

      inquilino.setNominq(dto.getNominq());
      inquilino.setApepinq(dto.getApepinq());
      inquilino.setApeminq(dto.getApeminq());
      inquilino.setDocinq(dto.getDocinq());
      inquilino.setCelinq(dto.getCelinq());
      inquilino.setCorinq(dto.getCorinq());

      inquilinoRepository.save(inquilino);

      // ===========================
      // Buscar el contrato exacto
      // ===========================

      InquilinoCuarto relacionActual =
              inquilinoCuartoRepository
                      .findByCodasig(dto.getCodasig())
                      .orElseThrow(() ->
                              new RuntimeException("Contrato no encontrado"));

      // ===========================
      // Si no cambió de cuarto
      // ===========================

      if (relacionActual.getCuarto().getCodcuar()
              .equals(dto.getCodcuar())) {

          return;

      }

      // ===========================
      // Buscar nuevo cuarto
      // ===========================

      Cuarto nuevoCuarto =
              cuartoRepository.findById(dto.getCodcuar())
                      .orElseThrow(() ->
                              new RuntimeException("El nuevo cuarto no existe"));

      boolean ocupado =
              inquilinoCuartoRepository
                      .existeContratoActivo(
                              nuevoCuarto.getCodcuar());

      if (ocupado) {

          throw new RuntimeException(
                  "El nuevo cuarto seleccionado se encuentra ocupado.");

      }

      // ===========================
      // Liberar cuarto anterior
      // ===========================

      Cuarto cuartoAnterior =
              relacionActual.getCuarto();

      cuartoAnterior.setEstcuar("Disponible");

      cuartoRepository.save(cuartoAnterior);

      // ===========================
      // Ocupar nuevo cuarto
      // ===========================

      nuevoCuarto.setEstcuar("Ocupado");

      cuartoRepository.save(nuevoCuarto);

      // ===========================
      // Mover contrato
      // ===========================

      guardarHistoricoInquilinoCuarto(
    	        relacionActual,
    	        "TRASLADO"
    	);
      relacionActual.setCuarto(nuevoCuarto);

      relacionActual.setMontoTotal(
              nuevoCuarto.getPreccuar());

      // NO tocar fechas
      // NO tocar fechin
      // NO tocar fechout

      inquilinoCuartoRepository.save(relacionActual);

  }

    // =========================================================
    // ❌ Lógica interna para ELIMINAR definitivamente el registro
    // =========================================================
    @Transactional
    public void finalizarContrato(int codasig){

    	InquilinoCuarto contrato =
    	        inquilinoCuartoRepository
    	        .findById(codasig)
    	        .orElseThrow(() ->
    	            new RuntimeException("Contrato no encontrado"));

        contrato.setEstado(false);

        contrato.setFechout(LocalDate.now());

        inquilinoCuartoRepository.save(contrato);

        Cuarto cuarto = contrato.getCuarto();

        cuarto.setEstcuar("Disponible");

        cuartoRepository.save(cuarto);

    }
    @Transactional
    public void reanudarContrato(int codasig){

    	InquilinoCuarto contrato =
    	        inquilinoCuartoRepository
    	        .findById(codasig)
    	        .orElseThrow(() ->
    	            new RuntimeException("Contrato no encontrado"));

        Cuarto cuarto = contrato.getCuarto();

        boolean ocupado =
                inquilinoCuartoRepository
                .existeContratoActivo(cuarto.getCodcuar());

        if(ocupado){

            throw new RuntimeException(
                "El cuarto donde vivía este inquilino ya fue asignado a otra persona."
            );

        }

        contrato.setEstado(true);

        contrato.setFechin(LocalDate.now());

        contrato.setFechout(
                LocalDate.now().plusMonths(0)
        );

        inquilinoCuartoRepository.save(contrato);

        cuarto.setEstcuar("Ocupado");

        cuartoRepository.save(cuarto);

    }
    public List<ContratoFinalizadoDTO> listarContratosFinalizados(int codusu){

        List<InquilinoCuarto> lista =
                inquilinoCuartoRepository
                .findByEstadoFalseAndUsuarioCodusu(codusu);

        List<ContratoFinalizadoDTO> dto =
                new ArrayList<>();

        for(InquilinoCuarto ic : lista){

            ContratoFinalizadoDTO d =
                    new ContratoFinalizadoDTO();

            d.setCodasig(ic.getCodasig());

            d.setCodcuar(ic.getCuarto().getCodcuar());

            d.setNombreCompleto(
                    ic.getInquilino().getNominq()
                    +" "+
                    ic.getInquilino().getApepinq());

            d.setNombreSede(
                    ic.getCuarto().getSede().getNombre());

            d.setNumeroPiso(
                    ic.getCuarto().getPiso().getNumero());

            d.setNumCuarto(
                    ic.getCuarto().getNumcuar());

            d.setFechaInicio(ic.getFechin());

            d.setFechaFin(ic.getFechout());

            dto.add(d);

        }

        return dto;

    }
    //TIPO DE LISTAS
/*    public List<InquilinoListadoDTO> listarPorCuarto(
            Integer codcuar){

        List<InquilinoCuarto> alquileres =
                inquilinoCuartoRepository
                .findByCuartoCodcuar(codcuar);

        List<InquilinoListadoDTO> lista =
                new ArrayList<>();

        for(InquilinoCuarto ic : alquileres){

            Inquilino i =
                    ic.getInquilino();

            Long diasRestantes = 0L;

            if(ic.getFechout() != null){

                diasRestantes =
                        ChronoUnit.DAYS.between(
                                LocalDate.now(),
                                ic.getFechout()
                        );

                if(diasRestantes < 0){
                    diasRestantes = 0L;
                }
            }

            lista.add(

                new InquilinoListadoDTO(

                    i.getCodinq(),

                    i.getNominq()
                    + " "
                    + i.getApepinq()
                    + " "
                    + i.getApeminq(),

                    i.getDocinq(),

                    i.getCelinq(),

                    i.getCorinq(),

                    ic.getCuarto()
                      .getNumcuar(),

                    diasRestantes,

                    i.getEstinq()
                        ? "Activo"
                        : "Inactivo"

                )

            );

        }

        return lista;
    }*/
    private InquilinoListadoDTO convertirDTO(
            InquilinoCuarto ic){

        Inquilino i = ic.getInquilino();

        Long diasRestantes = 0L;

        if(ic.getFechout() != null){

            diasRestantes =
                    ChronoUnit.DAYS.between(
                            LocalDate.now(),
                            ic.getFechout());

            if(diasRestantes < 0){
                diasRestantes = 0L;
            }
        }

        return new InquilinoListadoDTO(
        		ic.getCodasig(),
        		i.getCodinq(),
                i.getNominq() + " "
                        + i.getApepinq() + " "
                        + i.getApeminq(),
                i.getDocinq(),
                i.getCelinq(),
                i.getCorinq(),
                ic.getCuarto().getNumcuar(),
                diasRestantes,
                i.getEstinq()
                        ? "Activo"
                        : "Inactivo",
                ic.getCuarto().getSede().getNombre(),
                ic.getCuarto().getPiso().getNumero()
        );
    }
    public List<InquilinoListadoDTO> listarPorCuarto(
            Integer codcuar){

        List<InquilinoCuarto> alquileres =
                inquilinoCuartoRepository
                        .findByEstadoTrueAndCuartoCodcuar(
                                codcuar);

        return alquileres.stream()
                .map(this::convertirDTO)
                .toList();
    }
    
    public List<InquilinoListadoDTO> listarPorPiso(
            Integer codpiso){

    	System.out.println("CODPISO RECIBIDO: " + codpiso);

    	List<InquilinoCuarto> alquileres =
    	        inquilinoCuartoRepository
    	                .findByEstadoTrueAndCuartoPisoCodpiso(
    	                        codpiso);

    	System.out.println("ALQUILERES ENCONTRADOS: " + alquileres.size());

        return alquileres.stream()
                .map(this::convertirDTO)
                .toList();
    }
    public List<InquilinoListadoDTO> listarPorSede(
            Integer codsede){

    	System.out.println("CODSEDE RECIBIDO: " + codsede);

    	List<InquilinoCuarto> alquileres =
    	        inquilinoCuartoRepository
    	                .findByEstadoTrueAndCuartoSedeCodsede(
    	                        codsede);

    	System.out.println("ALQUILERES ENCONTRADOS: " + alquileres.size());

        return alquileres.stream()
                .map(this::convertirDTO)
                .toList();
    }
    public CrearInquilinoDTO obtener(Integer codasig){

        InquilinoCuarto contrato =
                inquilinoCuartoRepository
                .findById(codasig)
                .orElseThrow(() ->
                        new RuntimeException("Contrato no encontrado"));

        Inquilino i = contrato.getInquilino();

        CrearInquilinoDTO dto =
                new CrearInquilinoDTO();
        dto.setCodinq(i.getCodinq());
        dto.setCodasig(contrato.getCodasig());
        dto.setNominq(i.getNominq());
        dto.setApepinq(i.getApepinq());
        dto.setApeminq(i.getApeminq());
        dto.setDocinq(i.getDocinq());
        dto.setCelinq(i.getCelinq());
        dto.setCorinq(i.getCorinq());

        dto.setCodcuar(
                contrato.getCuarto().getCodcuar());

        dto.setCodusu(
                contrato.getUsuario().getCodusu());

        dto.setFechaInicio(
                contrato.getFechin());
        
        dto.setCodsede(
        	    contrato.getCuarto()
        	            .getSede()
        	            .getCodsede());

        	dto.setNombreSede(
        	    contrato.getCuarto()
        	            .getSede()
        	            .getNombre());

        	dto.setCodpiso(
        	    contrato.getCuarto()
        	            .getPiso()
        	            .getCodpiso());
        	dto.setNumeroPiso(
        		    contrato.getCuarto().getPiso().getNumero());
        	dto.setNumeroCuarto(
        	    contrato.getCuarto()
        	            .getNumcuar());
        	dto.setEstado(
        		    contrato.getCuarto().getEstcuar()
        		);
        return dto;
    }
    
    //nuevo codigo
    @Transactional
    public Inquilino actualizarInquilinoContacto(
            Integer codinq,
            ActualizarInquilinoDTO dto) {

        Inquilino inquilino = inquilinoRepository.findById(codinq)
                .orElseThrow(() ->
                        new RuntimeException("Inquilino no encontrado"));

        String celular = dto.getCelinq() == null
                ? ""
                : dto.getCelinq().trim();

        String correo = dto.getCorinq() == null
                ? ""
                : dto.getCorinq().trim();

        if (!celular.matches("^9\\d{8}$")) {
            throw new RuntimeException(
                    "El celular debe iniciar con 9 y tener 9 dígitos");
        }

        if (!correo.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new RuntimeException("Correo inválido");
        }

        boolean datosContactoCambiaron =
                !Objects.equals(inquilino.getCelinq(), celular)
                || !Objects.equals(inquilino.getCorinq(), correo);

        if (!datosContactoCambiaron) {
            return inquilino;
        }

        guardarHistoricoInquilino(
                inquilino,
                "ACTUALIZACION_CONTACTO"
        );


        inquilino.setCelinq(celular);
        inquilino.setCorinq(correo);

        return inquilinoRepository.save(inquilino);
    }
    
    //nuevo
    public List<InquilinoHistorico> listarHistoricoDatos(
            String dni,
            Integer codusu) {

        return inquilinoHistoricoRepository
                .findByDocinqAndUsuarioCodusuOrderByFechaMovimientoDesc(
                        dni,
                        codusu
                );
    }
    
    public List<InquilinoCuartoHistorico> listarHistoricoCuartos(
            String dni,
            Integer codusu) {

        return inquilinoCuartoHistoricoRepository
                .findByInquilinoDocinqAndUsuarioCodusuOrderByFechaMovimientoDesc(
                        dni,
                        codusu
                );
    }
    
    public List<InquilinoHistorico> listarTodosHistoricos(Integer codusu){
    	return inquilinoHistoricoRepository.findByUsuarioCodusu(codusu);
    }
    
    public List<InquilinoCuartoHistorico> listarTodosHistoricosCuartos(Integer codusu){
    	return inquilinoCuartoHistoricoRepository.findByUsuarioCodusu(codusu);
    }
    
    
    private void guardarHistoricoInquilino(
            Inquilino inquilino,
            String tipoMovimiento) {

        InquilinoHistorico historico = InquilinoHistorico.builder()
                .codinq(inquilino.getCodinq())
                .nominq(inquilino.getNominq())
                .apepinq(inquilino.getApepinq())
                .apeminq(inquilino.getApeminq())
                .docinq(inquilino.getDocinq())
                .fecreg(inquilino.getFecreg())
                .celinq(inquilino.getCelinq())
                .corinq(inquilino.getCorinq())
                .estinq(inquilino.getEstinq())
                .tipoDocumento(inquilino.getTipoDocumento())
                .usuario(inquilino.getUsuario())
                .tipoMovimiento(tipoMovimiento)
                .fechaMovimiento(LocalDateTime.now())
                .build();

        inquilinoHistoricoRepository.save(historico);
    }
    
        //nuevo codigo
    private void guardarHistoricoInquilinoCuarto(
        InquilinoCuarto contrato,
        String tipoMovimiento) {

    	InquilinoCuartoHistorico historico =
            InquilinoCuartoHistorico.builder()
                    .codasig(contrato.getCodasig())
                    .inquilino(contrato.getInquilino())
                    .cuarto(contrato.getCuarto())
                    .fechin(contrato.getFechin())
                    .fechout(contrato.getFechout())
                    .montoTotal(contrato.getMontoTotal())
                    .estado(contrato.getEstado())
                    .usuario(contrato.getUsuario())
                    .tipoMovimiento(tipoMovimiento)
                    .fechaMovimiento(LocalDateTime.now())
                    .build();

    inquilinoCuartoHistoricoRepository.save(historico);
    }
}

