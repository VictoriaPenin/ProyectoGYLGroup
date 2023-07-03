package com.grupo6.ServiciosBarrioPrivado.Entidad;


import com.grupo6.ServiciosBarrioPrivado.Enumeracion.Rol;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String nombre;
    private String apellido;
    @Column(unique = true)
    private String email;
    private String telefono;

    private String password;
    @Enumerated(EnumType.STRING)
    private Rol rol;

    private Integer precioPorHora;


    @ManyToOne
    private CategoriaServicio categoriaServicio;

    private Double calificacion;

    private String imagen;


    public Usuario() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNombreCompleto(){
        return this.nombre+" "+this.apellido;
    }

    public Integer getPrecioPorHora() {
        return precioPorHora;
    }

    public void setPrecioPorHora(Integer precioPorHora) {
        this.precioPorHora = precioPorHora;
    }

    public CategoriaServicio getCategoriaServicio() {
        return categoriaServicio;
    }

    public void setCategoriaServicio(CategoriaServicio categoriaServicio) {
        this.categoriaServicio = categoriaServicio;
    }

    public Double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Double calificacion) {
        this.calificacion = calificacion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}