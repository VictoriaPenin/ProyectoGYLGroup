package com.grupo6.ServiciosBarrioPrivado.Entidad;

public class AuxComentarioCalificacion {
    private String comentario;
    private Integer calificacion;

    private String idTrabajo;

    public AuxComentarioCalificacion(String comentario, Integer calificacion, String idTrabajo){
        this.comentario = comentario;
        this.calificacion = calificacion;
        this.idTrabajo = idTrabajo;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Integer getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Integer calificacion) {
        this.calificacion = calificacion;
    }

    public String getIdTrabajo() {
        return idTrabajo;
    }

    public void setIdTrabajo(String idTrabajo) {
        this.idTrabajo = idTrabajo;
    }
}
