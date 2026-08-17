package com.SistemaAlquiler.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SistemaAlquiler.dto.CuartoDTO;
import com.SistemaAlquiler.dto.EditarCuartoDTO;
import com.SistemaAlquiler.dto.auth.LoginInquilinoDTO;
import com.SistemaAlquiler.entity.Cuarto;
import com.SistemaAlquiler.entity.Inquilino;
import com.SistemaAlquiler.entity.InquilinoCuarto;
import com.SistemaAlquiler.entity.Piso;
import com.SistemaAlquiler.entity.Sede;
import com.SistemaAlquiler.entity.Usuario;
import com.SistemaAlquiler.repository.CuartoRepository;
import com.SistemaAlquiler.repository.InquilinoCuartoRepository;
import com.SistemaAlquiler.repository.PisoRepository;
import com.SistemaAlquiler.repository.SedeRepository;
import com.SistemaAlquiler.repository.UsuarioRepository;

@Service
public class CuartoService {
	
	@Autowired
	private CuartoRepository cuartoRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private SedeRepository sedeRepository;
	@Autowired
	private InquilinoCuartoRepository inquilinoCuartoRepository;
	@Autowired
	private PisoRepository pisoRepository;
	// ✅ CORREGIDO: Ahora filtra los cuartos por el propietario logueado
	public List<Cuarto> findAllByUsuario(int codusu){
		return cuartoRepository.findByUsuarioCodusu(codusu);
	}
	
	public EditarCuartoDTO findById(int id){

	    Cuarto c = cuartoRepository.findById(id).orElse(null);

	    if(c == null){
	        return null;
	    }

	    EditarCuartoDTO dto = new EditarCuartoDTO();

	    dto.setCodcuar(c.getCodcuar());

	    dto.setCodsede(c.getSede().getCodsede());
	    dto.setNombreSede(c.getSede().getNombre());

	    dto.setCodpiso(c.getPiso().getCodpiso());
	    dto.setNumeroPiso(c.getPiso().getNumero());

	    dto.setNumcuar(c.getNumcuar());

	    dto.setDircuar(c.getDircuar());

	    dto.setPreccuar(c.getPreccuar());

	    dto.setPasscuar(c.getPasscuar());

	    dto.setDescuar(c.getDescuar());

	    dto.setHabilitado(c.getHabilitado());

	    if(Boolean.FALSE.equals(c.getHabilitado())){
	        dto.setEstcuar("Deshabilitado");
	    }else{
	        dto.setEstcuar(c.getEstcuar());
	    }
	    
	    dto.setHabilitado(c.getHabilitado());
	    
	    return dto;
	}
	
	public Cuarto create(CuartoDTO dto) {
		// ✅ VALIDACIÓN: Evitar duplicados (Mismo número y piso para el mismo propietario)
		boolean existe = cuartoRepository.findByNumcuarAndPisoCodpisoAndUsuarioCodusu(
				dto.getNumcuar(), 
				dto.getNumeroPiso(), 
				dto.getCodusu()
		).isPresent();
		
		if (existe) {
			throw new RuntimeException("Ya tienes registrado el cuarto " + dto.getNumcuar() + " en el piso " + dto.getNumeroPiso());
		}

		Usuario usuario = usuarioRepository.findById(dto.getCodusu()).orElse(null);
		Sede sede = sedeRepository.findById(dto.getCodsede())
		        .orElse(null);
		Piso piso = pisoRepository.findById(dto.getNumeroPiso()).orElse(null);
		Cuarto cuarto = Cuarto.builder()
				.numcuar(dto.getNumcuar())
				.passcuar(dto.getPasscuar())
				.preccuar(dto.getPreccuar())
				.feccuar(java.time.LocalDate.now())
				.dircuar(dto.getDircuar())
				.descuar(dto.getDescuar())
				.fotocuar(dto.getFotocuar())
				.estcuar(dto.getEstcuar())
				.habilitado(
					    dto.getHabilitado() == null ? true : dto.getHabilitado()
					)
				.usuario(usuario)
				.sede(sede)
				.piso(piso)
				.build();
		
		return cuartoRepository.save(cuarto);
	}
	public Cuarto createInteligente(CuartoDTO dto) {

	    Piso piso = pisoRepository.findById(dto.getNumeroPiso())
	            .orElseThrow(() -> new RuntimeException("Piso no existe"));

	    Usuario usuario = usuarioRepository.findById(dto.getCodusu())
	            .orElse(null);

	    Sede sede = sedeRepository.findById(dto.getCodsede())
	            .orElse(null);

	    // 🔥 1. buscar último cuarto del piso
	    List<Cuarto> cuartos =
	            cuartoRepository.findByPisoCodpisoOrderByNumcuarAsc(dto.getNumeroPiso());

	    int nuevoNumero;

	    if (cuartos.isEmpty()) {
	        nuevoNumero = 101;
	    } else {
	        nuevoNumero = cuartos.get(cuartos.size() - 1).getNumcuar() + 1;
	    }

	    // 🔐 2. generar código único
	    String pass = generarCodigoUnico();

	    // 🧱 3. crear entidad
	    Cuarto c = new Cuarto();
	    c.setNumcuar(nuevoNumero);
	    c.setPasscuar(pass);
	    c.setPreccuar(dto.getPreccuar());
	    c.setFeccuar(java.time.LocalDate.now());
	    c.setDircuar(dto.getDircuar());
	    c.setDescuar(dto.getDescuar());
	    c.setFotocuar("default.jpg");
	    c.setEstcuar("Disponible");
	    c.setHabilitado(true);
	    c.setUsuario(usuario);
	    c.setSede(sede);
	    c.setPiso(piso);

	    return cuartoRepository.save(c);
	}
	public Cuarto update(int id, EditarCuartoDTO dto){

	    Cuarto cuarto =
	        cuartoRepository.findById(id).orElse(null);

	    if(cuarto==null){
	        return null;
	    }

	    cuarto.setPasscuar(dto.getPasscuar());
	    cuarto.setPreccuar(dto.getPreccuar());
	    cuarto.setDescuar(dto.getDescuar());

	    cuarto = cuartoRepository.save(cuarto);

	    List<InquilinoCuarto> contratos =
	            inquilinoCuartoRepository
	                    .findByEstadoTrueAndCuartoCodcuar(
	                            cuarto.getCodcuar());

	    for (InquilinoCuarto contrato : contratos) {

	        contrato.setMontoTotal(cuarto.getPreccuar());

	        inquilinoCuartoRepository.save(contrato);

	    }

	    return cuarto;
	}
	public void cambiarEstado(int id, String estado) {

	    Cuarto cuarto =
	            cuartoRepository
	            .findById(id)
	            .orElse(null);

	    if(cuarto == null)
	        return;

	    // Si se está inhabilitando
	    if(estado.equals("Inhabilitado")) {

	        cuarto.setEstcuar("Inhabilitado");

	    }
	    // Si se está habilitando
	    else {

	    	boolean ocupado =
	    	        inquilinoCuartoRepository
	    	        .existeContratoActivo(id);

	    	System.out.println("CUARTO "+id+" OCUPADO = "+ocupado);

	        if(ocupado) {

	            cuarto.setEstcuar("Ocupado");

	        } else {

	            cuarto.setEstcuar("Disponible");

	        }
	    }

	    cuartoRepository.save(cuarto);
	}
	public void cambiarHabilitado(int id, boolean estado){

	    Cuarto cuarto = cuartoRepository.findById(id).orElse(null);

	    if(cuarto == null) return;

	    cuarto.setHabilitado(estado);

	    cuartoRepository.save(cuarto);
	}
	// ✅ CORREGIDO: Ahora lista los disponibles que le pertenecen únicamente a este usuario
	public List<Cuarto> listarDisponibles(int codusu){
	    return cuartoRepository.findByEstcuarAndUsuarioCodusuAndHabilitado(
	        "Disponible",
	        codusu,
	        true
	    );
	}
	/*public List<Cuarto> listarPorSede(Integer codsede){

	    return cuartoRepository.findBySedeCodsede(codsede);

	}
	public List<Cuarto> listarPorPiso(
	        Integer codpiso){

	    return cuartoRepository
	            .findByPisoCodpiso(codpiso);
	}*/
	public List<Cuarto> listarPorPiso(Integer codpiso){

	    return cuartoRepository.findByPisoCodpiso(codpiso);

	}
	public List<CuartoDTO> listarCuartosPorSede(Integer codsede){

	    List<Cuarto> cuartos =
	            cuartoRepository.findBySedeCodsede(codsede);

	    return convertirListaDTO(cuartos);
	}

	public List<CuartoDTO> listarCuartosPorPiso(Integer codpiso){

	    List<Cuarto> cuartos =
	            cuartoRepository.findByPisoCodpiso(codpiso);

	    return convertirListaDTO(cuartos);
	}
	public List<CuartoDTO> listarCuartos(int codusu){

	    List<Cuarto> cuartos =
	            cuartoRepository.findByUsuarioCodusu(codusu);

	    return convertirListaDTO(cuartos);
	}
	private List<CuartoDTO> convertirListaDTO(List<Cuarto> cuartos){

	    List<CuartoDTO> lista = new ArrayList<>();

	    for(Cuarto cuarto : cuartos){

	        CuartoDTO dto = new CuartoDTO();
	        dto.setCodcuar(cuarto.getCodcuar());
	        dto.setNumcuar(cuarto.getNumcuar());
	        dto.setDircuar(cuarto.getDircuar());
	        dto.setPreccuar(cuarto.getPreccuar());
	        if(Boolean.FALSE.equals(cuarto.getHabilitado())){
	            dto.setEstcuar("Deshabilitado");
	        }else{
	            dto.setEstcuar(cuarto.getEstcuar());
	        }
	        dto.setHabilitado(cuarto.getHabilitado());

	        String nombreInquilino = "";

	        List<InquilinoCuarto> alquileres =
	                inquilinoCuartoRepository
	                        .findByEstadoTrueAndCuartoCodcuar(
	                                cuarto.getCodcuar());

	        if(!alquileres.isEmpty()){

	            Inquilino inq =
	                    alquileres.get(0).getInquilino();

	            nombreInquilino =
	                    inq.getNominq() + " "
	                    + inq.getApepinq();
	        }

	        dto.setNombreCompleto(nombreInquilino);

	        if(cuarto.getSede()!=null){
	            dto.setNombreSede(cuarto.getSede().getNombre());
	        }

	        if(cuarto.getPiso()!=null){
	            dto.setNumeroPiso(cuarto.getPiso().getNumero());
	        }

	        lista.add(dto);
	    }

	    return lista;
	}
	public List<Cuarto> listarDisponiblesPorPiso(
	        Integer codpiso){

	    return cuartoRepository
	            .findByPisoCodpisoAndEstcuarAndHabilitado(
	            	    codpiso,
	            	    "Disponible",
	            	    true
	            	);

	}
	
	private String generarCodigoUnico() {

	    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	    Random rnd = new Random();

	    String code;

	    do {
	        StringBuilder sb = new StringBuilder();

	        for (int i = 0; i < 6; i++) {
	            sb.append(chars.charAt(rnd.nextInt(chars.length())));
	        }

	        code = sb.toString();

	    } while (cuartoRepository.existsByPasscuar(code));

	    return code;
	}
	//Para el login Inquilino

	public LoginInquilinoDTO loginInquilino(String codigo) {
			
			if (codigo == null || codigo.trim().isEmpty()) {
				throw new RuntimeException("El código es obligatorio");
			}
			
			Cuarto cuarto = cuartoRepository.findByPasscuar(codigo)
					.orElseThrow(()-> new RuntimeException("Código de acceso inválido"));
			
			if(!"Ocupado".equalsIgnoreCase(cuarto.getEstcuar())) {
				throw new RuntimeException("El cuarto no está ocupado");
			}
			
		    InquilinoCuarto contrato = inquilinoCuartoRepository
		            .findByCuartoCodcuarAndEstado(cuarto.getCodcuar(), true)
		            .orElseThrow(() -> new RuntimeException("No existe contrato activo para este cuarto"));
		    
		    Inquilino inquilino = contrato.getInquilino();
		    
		    LoginInquilinoDTO dto = new LoginInquilinoDTO();
		    
		    dto.setCodinq(inquilino.getCodinq());
		    dto.setNominq(inquilino.getNominq());
		    dto.setApepinq(inquilino.getApepinq());
		    dto.setApeminq(inquilino.getApeminq());
		    dto.setDocinq(inquilino.getDocinq());
		    dto.setCelinq(inquilino.getCelinq());
		    dto.setCorinq(inquilino.getCorinq());

		    dto.setCodcuar(cuarto.getCodcuar());
		    dto.setNumcuar(cuarto.getNumcuar());
		    dto.setDircuar(cuarto.getDircuar());
		    dto.setPreccuar(cuarto.getPreccuar());
		    dto.setEstcuar(cuarto.getEstcuar());

		    dto.setCodsede(cuarto.getSede().getCodsede());
		    dto.setNomsede(cuarto.getSede().getNombre());

		    dto.setCodpiso(cuarto.getPiso().getCodpiso());
		    dto.setNumpiso(cuarto.getPiso().getNumero());

		    dto.setCodasig(contrato.getCodasig());
		    dto.setFechin(contrato.getFechin());
		    dto.setFechout(contrato.getFechout());
		    dto.setMontoTotal(contrato.getMontoTotal());
		    dto.setEstadoContrato(contrato.getEstado());

		    return dto;
		    
		}
}