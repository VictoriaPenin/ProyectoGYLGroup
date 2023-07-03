package com.grupo6.ServiciosBarrioPrivado.Servicio;



import com.grupo6.ServiciosBarrioPrivado.Entidad.CategoriaServicio;
import com.grupo6.ServiciosBarrioPrivado.Entidad.Trabajo;
import com.grupo6.ServiciosBarrioPrivado.Entidad.Usuario;


import com.grupo6.ServiciosBarrioPrivado.Enumeracion.EstadoTrabajo;
import com.grupo6.ServiciosBarrioPrivado.Excepciones.MiException;
import com.grupo6.ServiciosBarrioPrivado.Repositorio.TrabajoRepositorio;
import com.grupo6.ServiciosBarrioPrivado.Repositorio.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class TrabajoServicio {

    @Autowired
    private TrabajoRepositorio trabajoRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private ProveedorServicio proveedorServicio;

    @Autowired
    private CategoriaServicioService categoriaServicioService;

    @Transactional
    public void registrar(String fechaString, String idCliente, String idProveedor, String idCategoria,
                          String detalles) throws MiException, ParseException {
        SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
        Date fecha;
        try {
            fecha = formatoFecha.parse(fechaString);

        }catch(ParseException ex){
            throw ex;
        }

        validar(fecha, idCliente, idProveedor, idCategoria);

        Usuario cliente = usuarioRepositorio.findById(idCliente).get();
        Usuario proveedor = proveedorServicio.getProveedorById(idProveedor);
        CategoriaServicio categoria = categoriaServicioService.getCategoriaById(idCategoria);

        Trabajo trabajo = new Trabajo();

        trabajo.setFecha(fecha);
        trabajo.setCliente(cliente);
        trabajo.setProveedor(proveedor);
        trabajo.setCategoria(categoria);
        trabajo.setDetalles(detalles);
        trabajo.setComentario("");
        trabajo.setCalificacion(0);
        trabajo.setEstado(EstadoTrabajo.valueOf("PENDIENTE"));

        trabajoRepositorio.save(trabajo);
    }



    // MODIFICAR ENTIDAD DE LIBRO DE BIBLIOTECA, COMO REFERENCIA PARA IR A BUSCAR EN NUESTRA ENTIDAD TRABAJO
    // EL CLIENTE Y EL PROVEEDOR


    @Transactional
    public void modificar(String id, String fechaString, String idCliente, String idProveedor, String idCategoria,
                          String detalles) throws MiException, ParseException{
        SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");

        Date fecha;
        try {
            fecha = formatoFecha.parse(fechaString);

        }catch(ParseException ex){
            throw ex;
        }
        validar(fecha, idCliente, idProveedor, idCategoria);

        Optional<Trabajo> respuestaTrabajo = trabajoRepositorio.findById(id);
        Optional<Usuario> respuestaProveedor = usuarioRepositorio.findById(idProveedor);
        Optional<Usuario> respuestaCliente = usuarioRepositorio.findById(idCliente);
        CategoriaServicio categoria = categoriaServicioService.getCategoriaById(idCategoria);

        Usuario proveedor = new Usuario();
        Usuario cliente = new Usuario();

        if (respuestaCliente.isPresent()){
            cliente = respuestaCliente.get();
        }

        if(respuestaProveedor.isPresent()){
            proveedor = respuestaProveedor.get();
        }

        if (respuestaTrabajo.isPresent()){
            Trabajo trabajo = respuestaTrabajo.get();

            trabajo.setFecha(fecha);
            trabajo.setDetalles(detalles);
            trabajo.setCliente(cliente);
            trabajo.setProveedor(proveedor);
            trabajo.setCategoria(categoria);

            trabajoRepositorio.save(trabajo);
        }

    }

    @Transactional
    public void calificarTrabajo(String id, String comentario, Integer calificacion)throws MiException{
        if (id == null || id.isEmpty()){
            throw new MiException("El id ingresado no puede ser nulo o estar vacio");
        }
        Optional<Trabajo> respuesta = trabajoRepositorio.findById(id);
        if(respuesta.isPresent()){
            Trabajo trabajo = respuesta.get();

            if(trabajo.getEstado().toString().equals("FINALIZADO")) {
                trabajo.setComentario(comentario);
                trabajo.setCalificacion(calificacion);


                trabajoRepositorio.save(trabajo);
            }
        }
    }

    @Transactional
    public void finalizarTrabajo(String id) {
        Optional<Trabajo> respuesta = trabajoRepositorio.findById(id);
        if(respuesta.isPresent()){
            Trabajo trabajo = respuesta.get();
            trabajo.setEstado(EstadoTrabajo.valueOf("FINALIZADO"));
            trabajoRepositorio.save(trabajo);
        }
    }

    @Transactional
    public void modificarEstado(String id, EstadoTrabajo estado) {
        Optional<Trabajo> respuesta = trabajoRepositorio.findById(id);
        if(respuesta.isPresent()){
            Trabajo trabajo = respuesta.get();
            trabajo.setEstado(estado);
            trabajoRepositorio.save(trabajo);
        }
    }

    @Transactional
    public void calificarTrabajo(String id, Integer calificacion, String comentarios) {
        Optional<Trabajo> respuesta = trabajoRepositorio.findById(id);
        if(respuesta.isPresent()){
            Trabajo trabajo = respuesta.get();
            trabajo.setCalificacion(calificacion);
            trabajo.setComentario(comentarios);
            trabajoRepositorio.save(trabajo);
            this.agregarCalificacionAlProveedor(calificacion, trabajo.getProveedor().getId());
        }
    }

    @Transactional
    public void eliminarTrabajo(String id)throws MiException{
        if (id == null || id.isEmpty()){
            throw new MiException("El id ingresado no puede ser nulo o estar vacio");
        }
        Optional<Trabajo> respuesta = trabajoRepositorio.findById(id);
        if(respuesta.isPresent()){
            Trabajo trabajo = respuesta.get();
            trabajoRepositorio.delete(trabajo);
        }
    }


    @Transactional
    public void borrarComentario(String id) {
        Optional<Trabajo> respuesta = trabajoRepositorio.findById(id);
        if(respuesta.isPresent()){
            Trabajo trabajo = respuesta.get();
            trabajo.setComentario("");
            trabajoRepositorio.save(trabajo);
        }
    }



    // METODOS DE CONSULTA Y LISTADO


    public List<Trabajo> listarTrabajo() {
        List<Trabajo> trabajos = new ArrayList();
        trabajos = trabajoRepositorio.findAll();
        return trabajos;
    }

    public List<Trabajo> listarPorUsuario(String idUsuario) throws MiException{
        if (idUsuario == null || idUsuario.isEmpty()){
            throw new MiException("El id del usuario no puede ser nulo o estar vacio");
        }
        return trabajoRepositorio.buscarPorUsuario(idUsuario);
    }

    public List<Trabajo> listarPorProveedor(String idProveedor) throws MiException{
        if (idProveedor == null || idProveedor.isEmpty()){
            throw new MiException("El id del proveedor no puede ser nulo o estar vacio");
        }
        return trabajoRepositorio.buscarPorProveedor(idProveedor);
    }

    public List<Trabajo> listarPorCategoria(CategoriaServicio categoria) throws MiException{
        if (categoria == null || categoria.toString().isEmpty()){
            throw new MiException("La categoria no puede ser nulo o estar vacio");
        }
        return trabajoRepositorio.buscarPorCategoria(categoria);
    }

    public List<Trabajo> listarPorEstadoAdmin(EstadoTrabajo estado) throws MiException{
        if (estado == null ){
            throw new MiException("El estado no puede ser nulo o estar vacio");
        }
        return trabajoRepositorio.buscarPorEstado(estado);
    }

    public List<Trabajo> listarPorEstadoUsuario(String idUsuario, EstadoTrabajo estado) throws MiException{
        if (estado == null ){
            throw new MiException("El estado no puede ser nulo o estar vacio");
        }
        return trabajoRepositorio.buscarPorEstadoCliente(idUsuario, estado);
    }

    public List<Trabajo> listarPorEstadoProveedor(String idProveedor,EstadoTrabajo estado) throws MiException{
        if (estado == null ){
            throw new MiException("El estado no puede ser nulo o estar vacio");
        }
        return trabajoRepositorio.buscarPorEstadoProveedor(idProveedor,estado);
    }

    public Trabajo getTrabajoById(String id) {
        return trabajoRepositorio.getOne(id);
    }

    public void validar(Date fecha,String idCliente,String idProveedor,String categoria) throws MiException {

        LocalDate fechaLocalDate = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (fecha == null || fechaLocalDate.isBefore(LocalDate.now())) {
            throw new MiException("La fecha no puede ser nula ni puede ser anterior a la fecha de hoy");
        }

        if (categoria.isEmpty() || categoria == null) {
            throw new MiException("La categoria no puede ser nulo o estar vacio");
        }

        if (idCliente.isEmpty() || idCliente == null) {
            throw new MiException("El comentario no puede ser nulo o estar vacio");
        }

        if (idProveedor.isEmpty() || idProveedor == null) {
            throw new MiException("La calificacion no puede ser nulo o estar vacio");
        }
    }

    private void agregarCalificacionAlProveedor(Integer calificacion, String idProveedor){
        Usuario proveedor = proveedorServicio.getProveedorById(idProveedor);
        List<Double> calificaciones = trabajoRepositorio.buscarPorProveedor(idProveedor).stream().map(t -> Double.valueOf(t.getCalificacion())).collect(Collectors.toList());
        calificaciones.add(Double.valueOf(calificacion));

        if (calificaciones.size() > 1){
            proveedor.setCalificacion(calificaciones.stream().reduce(0.0, Double::sum) / calificaciones.size());
        } else{
            proveedor.setCalificacion(Double.valueOf(calificacion));
        }

    }
}