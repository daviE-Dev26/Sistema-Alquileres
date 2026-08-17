package com.SistemaAlquiler.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.SistemaAlquiler.dto.auth.LoginRequest;
import com.SistemaAlquiler.dto.auth.LoginResponse;
import com.SistemaAlquiler.dto.usuario.ActualizarPassResponse;
import com.SistemaAlquiler.dto.usuario.ActualizarPerfilDTO;
import com.SistemaAlquiler.dto.usuario.CambiarPasswordDTO;
import com.SistemaAlquiler.entity.TipoDocumento;
import com.SistemaAlquiler.entity.Usuario;
import com.SistemaAlquiler.repository.RolRepository;
import com.SistemaAlquiler.repository.TipoDocumentoRepository;
import com.SistemaAlquiler.repository.UsuarioRepository;

@Service
public class UsuarioService {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private RolRepository rolRepository;
	
	@Autowired
	private TipoDocumentoRepository tipoDocumentoRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public List<Usuario> findAll(){
		return usuarioRepository.findAll();
	}
	
	public Usuario findById(int id) {
		return usuarioRepository.findById(id).orElse(null);
	}
	public List<Usuario> listarPendientes(){

	    return usuarioRepository.findByEstusu("Pendiente");

	}
/*
	public Usuario aprobarUsuario(int id){

	    Usuario usuario = usuarioRepository.findById(id).orElse(null);

	    if(usuario == null) return null;

	    usuario.setEstusu("Aprobado");
	    usuario.setFecrechazo(null);

	    usuarioRepository.save(usuario);

	    return usuarioRepository.save(usuario);

	}

	public Usuario rechazarUsuario(int id){

	    Usuario usuario = usuarioRepository.findById(id).orElse(null);

	    if(usuario == null) return null;

	    usuario.setEstusu("Rechazado");
	    usuario.setFecrechazo(LocalDate.now());

	    usuarioRepository.save(usuario);

	    return usuarioRepository.save(usuario);

	}
	public Usuario create(CrearUsuarioDTO dto) {
		
		Rol rol = rolRepository.findById(dto.getCodrol()).orElse(null);
		TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(dto.getCodtipdoc()).orElse(null);
		
		Usuario usuario = Usuario.builder()
				.nomusu(dto.getNomusu())
				.apepusu(dto.getApepusu())
				.apemusu(dto.getApemusu())
				.docusu(dto.getDocusu())
				.dirusu(dto.getDirusu())
				.fecusu(java.time.LocalDate.now())
				.fecsolicitud(LocalDate.now())
				.celusu(dto.getCelusu())
				.corusu(dto.getCorusu())
				.passusu(passwordEncoder.encode(dto.getPassword()))
				.estusu("Pendiente")
				.rol(rol)
				.tipoDocumento(tipoDocumento)
				.build();
	    
	    return usuarioRepository.save(usuario);
		
	}*/
	
	public Usuario ActualizarPerfil(int id, ActualizarPerfilDTO  dto) {
		
		Usuario usuario = usuarioRepository.findById(id).orElse(null);
		if(usuario==null) return null;
		
		TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(dto.getCodtipdoc()).orElse(null);
		
		usuario.setNomusu(dto.getNomusu());
		usuario.setApepusu(dto.getApepusu());
		usuario.setApemusu(dto.getApemusu());
		usuario.setDocusu(dto.getDocusu());
		usuario.setDirusu(dto.getDirusu());
		usuario.setCelusu(dto.getCelusu());
		usuario.setCorusu(dto.getCorusu());	
		usuario.setTipoDocumento(tipoDocumento);
		
		return usuarioRepository.save(usuario);
		
	}
	/*
	public Usuario ActualizarPerfilAdmin(int id, ActualizarPerfilAdminDTO dto) {
		
		Usuario usuario = usuarioRepository.findById(id).orElse(null);
		if(usuario==null) return null;
		

		usuario.setNomusu(dto.getNomusu());
		usuario.setApepusu(dto.getApepusu());
		usuario.setApemusu(dto.getApemusu());
		usuario.setDocusu(dto.getDocusu());
		usuario.setDirusu(dto.getDirusu());
		usuario.setCelusu(dto.getCelusu());
		usuario.setCorusu(dto.getCorusu());	

		Rol rol = rolRepository.findById(dto.getCodrol()).orElse(null);
		if (rol != null) usuario.setRol(rol);
		
		TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(dto.getCodtipdoc()).orElse(null);
		if(tipoDocumento !=null) usuario.setTipoDocumento(tipoDocumento);

		return usuarioRepository.save(usuario);
		
	}*/
		
public LoginResponse login(LoginRequest req) {
	    Usuario usuario = usuarioRepository
	            .findByCorusu(req.getCorreo())
	            .orElse(null);
	            
	    if(usuario == null) {
	        return new LoginResponse(
	                false,
	                1,
	                "USUARIO NO EXISTE",
	                null,
	                null,
	                null
	        );
	    }
	    
	    if(!passwordEncoder.matches(req.getPassword(), usuario.getPassusu())) {
	        return new LoginResponse(
	                false,
	                2,
	                "PASSWORD INCORRECTA",
	                null,
	                null,
	                null
	        );
	    }
	    
	/*
	    if(usuario.getRol().getCodrol() != req.getCodrol()) {
	        return new LoginResponse(
	                false,
	                5, 
	                "NO TIENES PERMISOS PARA ACCEDER A ESTE PORTAL",
	                null,
	                null,
	                null
	        );
	    }

	    if(usuario.getEstusu().equals("Pendiente")) {
	        return new LoginResponse(
	                false,
	                3,
	                "TU SOLICITUD ESTA PENDIENTE",
	                null,
	                null,
	                null
	        );
	    } 
	    
	    if(usuario.getEstusu().equals("Rechazado")) {
	        return new LoginResponse(
	                false,
	                4,
	                "SOLICITUD RECHAZADA",
	                null,
	                null,
	                null
	        );
	    }*/
	    
	    return new LoginResponse(
	            true,
	            0,
	            "LOGIN OK",
	            usuario.getCodusu(),
	            usuario.getRol().getCodrol(),
	            usuario.getNomusu()
	    );
	}

	public ActualizarPassResponse cambiarPassword(int id, CambiarPasswordDTO dto) {
		
		Usuario usuario = usuarioRepository.findById(id).orElse(null);
		if (usuario == null)
			return new ActualizarPassResponse(false, 1, "NO EXISTE");

		if (!passwordEncoder.matches(dto.getOldPassword(), usuario.getPassusu()))
			return new ActualizarPassResponse(false, 2, "PASSWORD INCORRECTA");

		usuario.setPassusu(passwordEncoder.encode(dto.getNewPassword()));
		usuarioRepository.save(usuario);

		return new ActualizarPassResponse(true, 0, "OK");
		
	}
	
	public void crearUsuariosIniciales() {

	    /*if (!usuarioRepository.findByCorusu("admin@alquileres.com").isPresent()) {
	    

	    Usuario admin = Usuario.builder()
	        .nomusu("Admin")
	        .apepusu("sa")
	        .apemusu("sa")
	        .docusu("00000000")
	        .dirusu("Sistema")
	        .fecusu(LocalDate.now())
	        .celusu("999999999")
	        .corusu("admin@alquileres.com")
	        .passusu(passwordEncoder.encode("admin123"))
	        .estusu("Aprobado")
	        .rol(rolRepository.findById(1).orElse(null))
	        .tipoDocumento(tipoDocumentoRepository.findById(1).orElse(null))
	        .build();

	    usuarioRepository.save(admin);
	    }*/
	    if (!usuarioRepository.findByCorusu("gatoxyyo@gmail.com").isPresent()) {

	        Usuario propietario = Usuario.builder()
	            .nomusu("Propietario")
	            .apepusu("demo")
	            .apemusu("demo")
	            .docusu("11111111")
	            .dirusu("Sistema")
	            .fecusu(LocalDate.now())
	            .celusu("999999999")
	            .corusu("gatoxyyo@gmail.com")
	            .passusu(passwordEncoder.encode("123456"))
	            .estusu("Aprobado")
	            .rol(rolRepository.findById(1).orElse(null))
	            .tipoDocumento(tipoDocumentoRepository.findById(1).orElse(null))
	            .build();

	        usuarioRepository.save(propietario);
	    }
	}
	public Usuario buscarPorDni(String dni) {

	    return usuarioRepository.findByDocusu(dni).orElse(null);

	}
	/*
	public void eliminarSolicitud(Integer codusu){

	    Usuario usuario =
	        usuarioRepository.findById(codusu)
	        .orElseThrow();

	    usuarioRepository.delete(usuario);
	}
	
	@Scheduled(cron = "0 0 1 * * *")
	public void limpiarSolicitudesExpiradas() {

	    LocalDate limite = LocalDate.now().minusDays(7);

	    // Rechazados
	    List<Usuario> rechazados =
	            usuarioRepository
	            .findByEstusuAndFecrechazoBefore(
	                    "Rechazado",
	                    limite);

	    usuarioRepository.deleteAll(rechazados);
	}
/*	public List<SolicitudDTO> listarSolicitudes() {

	    return usuarioRepository.findAll()
	            .stream()
	            .map(u -> {

	                Long diasRestantes = null;

	                if("Rechazado".equals(u.getEstusu())
	                        && u.getFecrechazo() != null) {

	                    diasRestantes =
	                            7 - ChronoUnit.DAYS.between(
	                                    u.getFecrechazo(),
	                                    LocalDate.now());

	                }

	                if(diasRestantes != null
	                        && diasRestantes < 0) {

	                    diasRestantes = 0L;
	                }

	                return new SolicitudDTO(
	                        u.getCodusu(),
	                        u.getNomusu(),
	                        u.getApepusu(),
	                        u.getApemusu(),
	                        u.getDocusu(),
	                        u.getCorusu(),
	                        u.getCelusu(),
	                        u.getFecusu(),
	                        u.getEstusu(),
	                        diasRestantes
	                );

	            }).toList();
	}*/
	
}
