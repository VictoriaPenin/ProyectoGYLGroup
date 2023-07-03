package com.grupo6.ServiciosBarrioPrivado.Repositorio;

import com.grupo6.ServiciosBarrioPrivado.Entidad.CategoriaServicio;
import com.grupo6.ServiciosBarrioPrivado.Entidad.Trabajo;

import com.grupo6.ServiciosBarrioPrivado.Enumeracion.EstadoTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrabajoRepositorio extends JpaRepository<Trabajo, String> {


    @Query("SELECT t FROM Trabajo t WHERE t.cliente.id = :idUsuario")
    public List<Trabajo> buscarPorUsuario(@Param("idUsuario") String idUsuario);

    @Query("SELECT t FROM Trabajo t WHERE t.proveedor.id = :idProveedor")
    public List<Trabajo> buscarPorProveedor(@Param("idProveedor") String idProveedor);


    @Query("SELECT t FROM Trabajo t WHERE t.categoria = :categoria")
    public List<Trabajo> buscarPorCategoria(@Param("categoria") CategoriaServicio categoria);

    @Query("SELECT t FROM Trabajo t WHERE t.estado = :estado")
    public List<Trabajo> buscarPorEstado(@Param("estado") EstadoTrabajo estado);

    @Query("SELECT t FROM Trabajo t WHERE t.proveedor.id = :idProveedor and t.estado = :estado")
    public List<Trabajo> buscarPorEstadoProveedor(@Param("idProveedor") String idProveedor,@Param("estado") EstadoTrabajo estado );

    @Query("SELECT t FROM Trabajo t WHERE t.cliente.id = :idUsuario and t.estado = :estado")
    public List<Trabajo> buscarPorEstadoCliente(@Param("idUsuario") String idUsuario,@Param("estado") EstadoTrabajo estado );

}
