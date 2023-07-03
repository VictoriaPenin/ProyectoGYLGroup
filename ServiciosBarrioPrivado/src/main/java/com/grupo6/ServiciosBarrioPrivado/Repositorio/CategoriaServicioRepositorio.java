package com.grupo6.ServiciosBarrioPrivado.Repositorio;

import com.grupo6.ServiciosBarrioPrivado.Entidad.CategoriaServicio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoriaServicioRepositorio extends JpaRepository<CategoriaServicio, String> {

    @Query("SELECT c FROM CategoriaServicio c WHERE c.nombre = :nombre")
    public CategoriaServicio findByName(@Param("nombre") String nombre);
}
