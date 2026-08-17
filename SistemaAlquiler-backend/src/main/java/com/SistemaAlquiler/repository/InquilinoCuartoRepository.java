package com.SistemaAlquiler.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.SistemaAlquiler.entity.InquilinoCuarto;

@Repository
public interface InquilinoCuartoRepository extends JpaRepository<InquilinoCuarto, Integer> {

    List<InquilinoCuarto> findByEstadoTrue();

    List<InquilinoCuarto> findByEstadoTrueAndUsuarioCodusu(int codusu);
    
    List<InquilinoCuarto> findByCuartoCodcuar(Integer codcuar);
    
    List<InquilinoCuarto> findByCuartoSedeCodsede(Integer codsede);
    
    List<InquilinoCuarto> findByCuartoPisoCodpiso(Integer codpiso);
    
    List<InquilinoCuarto> findByEstadoTrueAndCuartoSedeCodsede(Integer codsede);
    
    List<InquilinoCuarto> findByEstadoTrueAndCuartoPisoCodpiso(Integer codpiso);
    
    List<InquilinoCuarto> findByEstadoTrueAndCuartoCodcuar(Integer codcuar);
    
    @Query("""
            SELECT COUNT(ic) > 0
            FROM InquilinoCuarto ic
            WHERE ic.estado = true
            AND ic.cuarto.codcuar = :codcuar
            """)
    boolean existeContratoActivo(@Param("codcuar") Integer codcuar);
    
    List<InquilinoCuarto> findByEstadoFalseAndUsuarioCodusu(Integer codusu);
    
    Optional<InquilinoCuarto> findByCodasig(Integer codasig);
    
    Optional<InquilinoCuarto> findByCuartoCodcuarAndEstado(Integer codcuar, Boolean estado);
    
    Optional<InquilinoCuarto> findByCuartoCodcuarAndEstado(Integer codcuar, boolean b);
    
    @Query("""
            SELECT COALESCE(SUM(ic.montoTotal),0)
            FROM InquilinoCuarto ic
            WHERE ic.usuario.codusu = :codusu
            AND ic.estado = true
            """)
    Double obtenerIngresosMensuales(Integer codusu);
    
    @Query("""
            SELECT COUNT(DISTINCT ic.cuarto.codcuar)
            FROM InquilinoCuarto ic
            WHERE ic.usuario.codusu = :codusu
            AND ic.estado = true
        """)
    Integer countCuartosOcupados(@Param("codusu") Integer codusu);
    
    @Query("""
            SELECT COALESCE(SUM(ic.montoTotal),0)
            FROM InquilinoCuarto ic
            WHERE ic.usuario.codusu = :codusu
            AND ic.cuarto.sede.codsede = :codsede
            AND ic.estado = true
            """)
    Double obtenerIngresosMensualesPorSede(Integer codusu, Integer codsede);
    
    @Query("""
            SELECT COUNT(DISTINCT ic.cuarto.codcuar)
            FROM InquilinoCuarto ic
            WHERE ic.cuarto.sede.codsede = :codsede
            AND ic.estado = true
            """)
    Integer countCuartosOcupadosPorSede(Integer codsede);
    
    @Query("""
            SELECT COUNT(DISTINCT ic.inquilino.codinq)
            FROM InquilinoCuarto ic
            WHERE ic.cuarto.sede.codsede = :codsede
            AND ic.estado = true
            """)
    Integer countInquilinosPorSede(Integer codsede);

    // ==========================================
    // MÉTODOS PARA EXTENDER MES Y REGISTRAR PAGO
    // ==========================================

    @Modifying
    @Transactional
    @Query(value = "UPDATE inquilino_cuarto SET fechout = DATEADD(MONTH, 1, COALESCE(fechout, fechin, GETDATE())) WHERE codasig = :codasig", nativeQuery = true)
    void extenderFechaPago(@Param("codasig") Integer codasig);

    @Query(value = "SELECT ic.codasig, ic.montoTotal, ic.codusu, ic.codcuar, " +
                   "c.preccuar, s.nombre as nombreSede " +
                   "FROM inquilino_cuarto ic " +
                   "INNER JOIN cuarto c ON ic.codcuar = c.codcuar " +
                   "INNER JOIN sede s ON c.codsede = s.codsede " +
                   "WHERE ic.codasig = :codasig", nativeQuery = true)
    Map<String, Object> obtenerInfoParaPago(@Param("codasig") Integer codasig);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO pago_alquiler (codasig, codusu, cantidad_meses, monto, fechaPago, " +
                   "periodo_inicio, periodo_fin, origen_pago, metodo_pago, estado_pago, " +
                   "numero_operacion, observacion, fecha_registro) " +
                   "VALUES (:codasig, :codusu, 1, :monto, GETDATE(), " +
                   ":periodoInicio, :periodoFin, 'TRANSFERENCIA', 'Transferencia bancaria', 'APROBADO', " +
                   "NEWID(), 'Pago por extensión de mes', GETDATE())", nativeQuery = true)
    void insertarPagoAlquiler(
        @Param("codasig") Integer codasig,
        @Param("codusu") Integer codusu,
        @Param("monto") Double monto,
        @Param("periodoInicio") String periodoInicio,
        @Param("periodoFin") String periodoFin
    );

    @Query(value = "SELECT TOP 1 codpago FROM pago_alquiler WHERE codasig = :codasig ORDER BY fechaPago DESC", nativeQuery = true)
    Integer obtenerUltimoCodPago(@Param("codasig") Integer codasig);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO detalle_pago_alquiler (codpago, codasig, anio, mes, monto, estado, fecha_pago) " +
                   "VALUES (:codpago, :codasig, :anio, :mes, :monto, 'PAGADO', GETDATE())", nativeQuery = true)
    void insertarDetallePagoAlquiler(
        @Param("codpago") Integer codpago,
        @Param("codasig") Integer codasig,
        @Param("anio") Integer anio,
        @Param("mes") Integer mes,
        @Param("monto") Double monto
    );
    
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE inquilino_cuarto 
        SET fechout = CASE 
            WHEN fechout IS NULL THEN DATEADD(MONTH, :meses, fechin)
            ELSE DATEADD(MONTH, :meses, fechout)
        END 
        WHERE codasig = :codasig
        """, nativeQuery = true)
    void extenderFechaPorMeses(
        @Param("codasig") Integer codasig,
        @Param("meses") Integer meses
    );
    
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE detalle_pago_alquiler 
        SET estado = 'PAGADO', fecha_pago = GETDATE(), codpago = :codpago
        WHERE codasig = :codasig 
          AND anio = :anio 
          AND mes = :mes 
          AND estado = 'PENDIENTE'
        """, nativeQuery = true)
    void marcarDetalleComoPagado(
        @Param("codpago") Integer codpago,
        @Param("codasig") Integer codasig,
        @Param("anio") Integer anio,
        @Param("mes") Integer mes
    );
}