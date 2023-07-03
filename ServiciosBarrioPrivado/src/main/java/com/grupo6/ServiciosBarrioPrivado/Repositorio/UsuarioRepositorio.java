package com.grupo6.ServiciosBarrioPrivado.Repositorio;


import com.grupo6.ServiciosBarrioPrivado.Entidad.CategoriaServicio;
import com.grupo6.ServiciosBarrioPrivado.Entidad.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, String> {
    @Query("SELECT u FROM Usuario u WHERE u.email = :email")
    public Usuario buscarPorEmail(@Param("email") String email);


    @Query("SELECT p FROM Usuario p WHERE p.categoriaServicio.id =:idCategoria")
    public List<Usuario> buscarPorCategoria(@Param("idCategoria") String idCategoria);


}