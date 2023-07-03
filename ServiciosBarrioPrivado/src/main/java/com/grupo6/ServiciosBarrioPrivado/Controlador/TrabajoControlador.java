package com.grupo6.ServiciosBarrioPrivado.Controlador;

import com.grupo6.ServiciosBarrioPrivado.Entidad.AuxComentarioCalificacion;
import com.grupo6.ServiciosBarrioPrivado.Entidad.CategoriaServicio;
import com.grupo6.ServiciosBarrioPrivado.Entidad.Trabajo;
import com.grupo6.ServiciosBarrioPrivado.Entidad.Usuario;

import com.grupo6.ServiciosBarrioPrivado.Enumeracion.EstadoTrabajo;
import com.grupo6.ServiciosBarrioPrivado.Enumeracion.Rol;
import com.grupo6.ServiciosBarrioPrivado.Excepciones.MiException;
import com.grupo6.ServiciosBarrioPrivado.Servicio.CategoriaServicioService;
import com.grupo6.ServiciosBarrioPrivado.Servicio.ProveedorServicio;
import com.grupo6.ServiciosBarrioPrivado.Servicio.TrabajoServicio;
import com.grupo6.ServiciosBarrioPrivado.Servicio.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/trabajo")
public class TrabajoControlador {

    @Autowired
    private TrabajoServicio trabajoServicio;

    @Autowired
    private ProveedorServicio proveedorServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private CategoriaServicioService categoriaServicioService;


    @GetMapping("/registrar/{idCliente}")
    public String registrar(@PathVariable String idCliente, ModelMap modelo){
        List<CategoriaServicio> categoriaServicio = categoriaServicioService.listarTodas();
        modelo.addAttribute("categoriaServicio", categoriaServicio);
        List<Usuario> proveedores = proveedorServicio.listarProveedores();
        Usuario cliente = usuarioServicio.getUsuarioById(idCliente);
        modelo.addAttribute("cliente", cliente);
        modelo.addAttribute("proveedores", proveedores);
        return "registro_trabajo";
    }

    @PostMapping("/registrarConListarProveedoresSegunCategoria/{idCliente}")
    public String registrarFiltrandoProveedores(@PathVariable String idCliente, @RequestParam String categoria,
                                                ModelMap modelo){
        List<CategoriaServicio> categoriaServicio = categoriaServicioService.listarTodas();;
        modelo.addAttribute("categoriaServicio", categoriaServicio);


        modelo.addAttribute("categoria", categoriaServicioService.getCategoriaByName(categoria));
        List<Usuario> proveedores = proveedorServicio.listarPorCategoria(categoriaServicioService.getCategoriaByName(categoria).getId());
        Usuario cliente = usuarioServicio.getUsuarioById(idCliente);
        modelo.addAttribute("cliente", cliente);
        modelo.addAttribute("proveedores", proveedores);
        return "registro_trabajo_filtrado";


    }


    @PostMapping("/registro")
    public String registroTrabajo(@RequestParam String fecha, @RequestParam String idCliente, @RequestParam String idProveedor,
                                  @RequestParam String idCategoria, @RequestParam String detalles, ModelMap modelo){
        try{
            trabajoServicio.registrar(fecha, idCliente, idProveedor, idCategoria, detalles);
            Usuario usuario = usuarioServicio.getUsuarioById(idCliente);
            modelo.addAttribute("usuario", usuario);
            return "inicio";

        } catch (MiException ex){
            modelo.put("error", ex.getMessage());

            List<CategoriaServicio> categoriaServicio = categoriaServicioService.listarTodas();;
            modelo.addAttribute("categoriaServicio", categoriaServicio);
            List<Usuario> proveedores = proveedorServicio.listarProveedores();
            List<Usuario> clientes = usuarioServicio.listarUsuarios();
            modelo.addAttribute("clientes", clientes);
            modelo.addAttribute("proveedores", proveedores);

            return "registro_trabajo";
        }catch (ParseException e){
            modelo.put("error", e.getMessage());

            List<CategoriaServicio> categoriaServicio = categoriaServicioService.listarTodas();;
            modelo.addAttribute("categoriaServicio", categoriaServicio);
            List<Usuario> proveedores = proveedorServicio.listarProveedores();
            List<Usuario> clientes = usuarioServicio.listarUsuarios();
            modelo.addAttribute("clientes", clientes);
            modelo.addAttribute("proveedores", proveedores);
            return "registro_trabajo";
        }
    }

    @GetMapping("/registrarRapido/{idProveedor}/{idCliente}")
    public String registrarRapido(@PathVariable String idCliente, @PathVariable String idProveedor, ModelMap modelo){
        Usuario proveedor = proveedorServicio.getProveedorById(idProveedor);
        modelo.addAttribute("categoriaServicio", proveedor.getCategoriaServicio());
        modelo.addAttribute("proveedor", proveedor);
        Usuario cliente = usuarioServicio.getUsuarioById(idCliente);
        modelo.addAttribute("cliente", cliente);

        return "registroRapido_trabajo";
    }

    @GetMapping("/modificar/{id}")
    public String modificarTrabajo(@PathVariable String id, ModelMap modelo){
        Trabajo trabajo = trabajoServicio.getTrabajoById(id);
        modelo.addAttribute("trabajo", trabajo);
        List<CategoriaServicio> categoriaServicio = categoriaServicioService.listarTodas();;
        modelo.addAttribute("categoriaServicio", categoriaServicio);
        List<Usuario> proveedores = proveedorServicio.listarProveedores();
        modelo.addAttribute("proveedores", proveedores);
        List<Usuario> clientes = usuarioServicio.listarUsuarios();
        modelo.addAttribute("clientes", clientes);
        List<EstadoTrabajo> estados = Arrays.stream(EstadoTrabajo.values()).toList();
        modelo.addAttribute("estados", estados);
        return "modificar_trabajo";
    }


    @PostMapping("/modificar/{id}")
    public String modificar(@PathVariable String id,@RequestParam String fecha,@RequestParam String idCliente,
                            @RequestParam String idProveedor, @RequestParam String idCategoria,
                            @RequestParam String detalles, ModelMap modelo) throws MiException, ParseException{
        try{
            trabajoServicio.modificar(id,fecha, idCliente, idProveedor, idCategoria, detalles);

            List<Usuario> proveedores = proveedorServicio.listarProveedores();
            modelo.addAttribute("proveedores", proveedores);
            List<CategoriaServicio> categoriaServicio = categoriaServicioService.listarTodas();;
            modelo.addAttribute("categoriaServicio", categoriaServicio);
            List<Usuario> clientes = usuarioServicio.listarUsuarios();
            modelo.addAttribute("clientes", clientes);
            List<Trabajo> trabajos = trabajoServicio.listarTrabajo();
            modelo.addAttribute("trabajos", trabajos);
            List<EstadoTrabajo> estados = Arrays.stream(EstadoTrabajo.values()).toList();
            modelo.addAttribute("estados", estados);
            return "trabajo_lista";

        }catch(MiException ex){
            Trabajo trabajo = trabajoServicio.getTrabajoById(id);
            modelo.addAttribute("trabajo", trabajo);

            List<CategoriaServicio> categoriaServicio = categoriaServicioService.listarTodas();;
            modelo.addAttribute("categoriaServicio", categoriaServicio);
            List<Usuario> proveedores = proveedorServicio.listarProveedores();
            modelo.addAttribute("proveedores", proveedores);
            List<Usuario> clientes = usuarioServicio.listarUsuarios();
            modelo.addAttribute("clientes", clientes);
            List<EstadoTrabajo> estados = Arrays.stream(EstadoTrabajo.values()).toList();
            modelo.addAttribute("estados", estados);
            modelo.put("error", ex.getMessage());

            return "modificar_trabajo";
        }
    }

    @GetMapping("borrar/{id}")
    public String borrarTrabajo(@PathVariable String id, ModelMap modelo){
        Trabajo trabajo = trabajoServicio.getTrabajoById(id);
        modelo.addAttribute("trabajo", trabajo);
        return "trabajo_borrar";
    }

    @PostMapping("/confirmarBorrar/{id}")
    public String borrar(@PathVariable String id, ModelMap modelo){
        try{
            trabajoServicio.eliminarTrabajo(id);
            return "redirect:../listar";
        }catch(MiException ex){
            modelo.put("error", ex.getMessage());
            return "inicio";
        }
    }


    @GetMapping("/confirmar/{id}")
    public String confirmarTrabajo(@PathVariable String id, ModelMap modelo){
        Trabajo trabajo = trabajoServicio.getTrabajoById(id);
        modelo.addAttribute("estados",Arrays.stream(EstadoTrabajo.values()).toList());
        modelo.addAttribute("trabajo", trabajo);
        return "trabajo_confirmacion";
    }

    @PostMapping("/confirmar/{id}")
    public String confirmarCambioEstado(@PathVariable String id, @RequestParam EstadoTrabajo estado, ModelMap modelo){
        try{
            if (estado.toString() == null || estado.toString().isEmpty()){
                throw new MiException("El estado a actualizar no puede ser nulo");
            }
            trabajoServicio.modificarEstado(id, estado);

            String idProveedor = trabajoServicio.getTrabajoById(id).getProveedor().getId();
            List<CategoriaServicio> categoriaServicio = categoriaServicioService.listarTodas();;
            modelo.addAttribute("categoriaServicio", categoriaServicio);
            List<Trabajo> trabajos = trabajoServicio.listarPorProveedor(idProveedor).stream().filter( t -> t.getEstado().toString().equals("PENDIENTE")).collect(Collectors.toList());
            modelo.addAttribute("trabajos", trabajos);
            List<Usuario> proveedores = proveedorServicio.listarProveedores();
            modelo.addAttribute("proveedores", proveedores);
            modelo.addAttribute("finalizado", true);
            return "trabajo_lista";
        } catch(MiException ex){
            modelo.put("error", ex.getMessage());
            return "trabajo_confirmacion";
        }
    }

    @GetMapping("/calificar/{id}")
    public String calificarTrabajo(@PathVariable String id, ModelMap modelo){
        Trabajo trabajo = trabajoServicio.getTrabajoById(id);
        modelo.addAttribute("trabajo", trabajo);
        modelo.addAttribute("finalizado", true);
        return "trabajo_calificacion";
    }

    @PostMapping("/calificar/{id}")
    public String confirmarCalificacion(@PathVariable String id, @RequestParam Integer calificacion, @RequestParam String comentarios, ModelMap modelo){
        try{
            if (calificacion == null){
                throw new MiException("La calificacion no puede ser nula");
            }

            trabajoServicio.calificarTrabajo(id, calificacion, comentarios);
            Usuario usuario = usuarioServicio.getUsuarioById(trabajoServicio.getTrabajoById(id).getCliente().getId());
            modelo.addAttribute("usuario", usuario);
            return "inicio";
        } catch(MiException ex){
            modelo.put("error", ex.getMessage());
            return "trabajo_confirmacion";
        }
    }


    @GetMapping("/listar")
    public String listarTodos(ModelMap modelo){
        List<Trabajo> trabajos = trabajoServicio.listarTrabajo();
        modelo.addAttribute("trabajos", trabajos);
        List<CategoriaServicio> categoriaServicio = categoriaServicioService.listarTodas();;
        modelo.addAttribute("categoriaServicio", categoriaServicio);
        List<Usuario> proveedores = proveedorServicio.listarProveedores();
        modelo.addAttribute("proveedores", proveedores);
        List<EstadoTrabajo> estados = Arrays.asList(EstadoTrabajo.values());
        modelo.addAttribute("estados", estados);
        return "trabajo_lista";
    }



    @PostMapping("/listarPorCategoria")
    public String filtrarPorCategoria(@RequestParam String categoria, ModelMap modelo) throws MiException{
        List<CategoriaServicio> categoriaServicio = categoriaServicioService.listarTodas();;
        modelo.addAttribute("categoriaServicio", categoriaServicio);
        modelo.addAttribute("finalizado", true);

        if (categoria.equals("Todos")){
            List<Trabajo> trabajos = trabajoServicio.listarTrabajo();
            modelo.addAttribute("trabajos", trabajos);
            List<Usuario> proveedores = proveedorServicio.listarProveedores();
            modelo.addAttribute("proveedores", proveedores);

            return "trabajo_lista";
        } else {
            List<Trabajo> trabajos = trabajoServicio.listarPorCategoria(categoriaServicioService.getCategoriaByName(categoria));
            modelo.addAttribute("trabajos", trabajos);
            modelo.addAttribute("categoriaSeleccionada", categoriaServicioService.getCategoriaByName(categoria));
            List<Usuario> proveedores = proveedorServicio.listarProveedores();
            modelo.addAttribute("proveedores", proveedores);


            return "trabajo_lista";
        }
    }

    @PostMapping("/listarPorEstado/{idUsuario}-{rol}")
    public String filtrarPorEstado(@PathVariable String idUsuario, @PathVariable Rol rol, @RequestParam String estado, ModelMap modelo) throws MiException{
        List<CategoriaServicio> categoriaServicio = categoriaServicioService.listarTodas();;
        modelo.addAttribute("categoriaServicio", categoriaServicio);
        List<EstadoTrabajo> estados = Arrays.asList(EstadoTrabajo.values());
        modelo.addAttribute("estados", estados);

        List<Usuario> proveedores = proveedorServicio.listarProveedores();
        modelo.addAttribute("proveedores", proveedores);

        if(rol.toString().equals("ADMIN")){
            if (estado.equals("Todos")){
                List<Trabajo> trabajos = trabajoServicio.listarTrabajo();
                modelo.addAttribute("trabajos", trabajos);

            } else {
                List<Trabajo> trabajos = trabajoServicio.listarPorEstadoAdmin(EstadoTrabajo.valueOf(estado));
                modelo.addAttribute("trabajos", trabajos);
                modelo.addAttribute("estadoSeleccionado", estado);

            }
        } else if(rol.toString().equals("USER")){
            if (estado.equals("Todos")){
                List<Trabajo> trabajos = trabajoServicio.listarPorUsuario(idUsuario);
                modelo.addAttribute("trabajos", trabajos);

            } else{
                List<Trabajo> trabajos = trabajoServicio.listarPorEstadoUsuario(idUsuario, EstadoTrabajo.valueOf(estado));
                modelo.addAttribute("trabajos", trabajos);
            }
        } else {
            if (estado.equals("Todos")){
                List<Trabajo> trabajos = trabajoServicio.listarPorProveedor(idUsuario);
                modelo.addAttribute("trabajos", trabajos);

            } else {
                List<Trabajo> trabajos = trabajoServicio.listarPorEstadoProveedor(idUsuario, EstadoTrabajo.valueOf(estado));
                modelo.addAttribute("trabajos", trabajos);
            }
        }

        return "trabajo_lista";
    }

    @GetMapping("/listarPorUsuario/{idCliente}")
    public String listarPorCliente(@PathVariable String idCliente, ModelMap modelo) throws MiException{

        List<CategoriaServicio> categoriaServicio = categoriaServicioService.listarTodas();;
        modelo.addAttribute("categoriaServicio", categoriaServicio);
        List<EstadoTrabajo> estados = Arrays.asList(EstadoTrabajo.values());
        modelo.addAttribute("estados", estados);

        List<Trabajo> trabajos = trabajoServicio.listarPorUsuario(idCliente);
        modelo.addAttribute("trabajos", trabajos);
        List<Usuario> proveedores = proveedorServicio.listarProveedores();
        modelo.addAttribute("proveedores", proveedores);

        return "trabajo_lista";

    }

    @GetMapping("/listarPorUsuarioFinalizado/{idCliente}")
    public String listarPorClienteFinalizado(@PathVariable String idCliente, ModelMap modelo) throws MiException{

        List<CategoriaServicio> categoriaServicio = categoriaServicioService.listarTodas();;
        modelo.addAttribute("categoriaServicio", categoriaServicio);

        List<Trabajo> trabajos = trabajoServicio.listarPorUsuario(idCliente).stream().filter(t -> t.getEstado().toString().equals("FINALIZADO") || t.getEstado().toString().equals("CANCELADO") ).collect(Collectors.toList());
        modelo.addAttribute("trabajos", trabajos);
        List<Usuario> proveedores = proveedorServicio.listarProveedores();
        modelo.addAttribute("proveedores", proveedores);
        modelo.addAttribute("finalizado", true);
        return "trabajo_lista";

    }

    @PostMapping("/listarPorUsuario/{idCliente}")
    public String filtrarPorCliente(@PathVariable String idCliente, ModelMap modelo) throws MiException{

        List<CategoriaServicio> categoriaServicio = categoriaServicioService.listarTodas();;
        modelo.addAttribute("categoriaServicio", categoriaServicio);
        List<EstadoTrabajo> estados = Arrays.asList(EstadoTrabajo.values());
        modelo.addAttribute("estados", estados);

        List<Trabajo> trabajos = trabajoServicio.listarPorUsuario(idCliente);
        modelo.addAttribute("trabajos", trabajos);
        List<Usuario> proveedores = proveedorServicio.listarProveedores();
        modelo.addAttribute("proveedores", proveedores);
        return "trabajo_lista";

    }


    @GetMapping("/listarPorProveedorPendiente/{idProveedor}")
    public String listarPorProveedorPendiente(@PathVariable String idProveedor, ModelMap modelo) throws MiException{

        List<CategoriaServicio> categoriaServicio = categoriaServicioService.listarTodas();;
        modelo.addAttribute("categoriaServicio", categoriaServicio);

        List<Trabajo> trabajos = trabajoServicio.listarPorProveedor(idProveedor).stream().filter( t -> t.getEstado().toString().equals("PENDIENTE")).collect(Collectors.toList());
        modelo.addAttribute("trabajos", trabajos);
        List<Usuario> proveedores = proveedorServicio.listarProveedores();
        modelo.addAttribute("proveedores", proveedores);
        modelo.addAttribute("finalizado", true);
        return "trabajo_lista";

    }

    @GetMapping("/listarPorProveedorFinalizado/{idProveedor}")
    public String listarPorProveedorFinalizado(@PathVariable String idProveedor, ModelMap modelo) throws MiException{

        List<CategoriaServicio> categoriaServicio = categoriaServicioService.listarTodas();;
        modelo.addAttribute("categoriaServicio", categoriaServicio);

        List<Trabajo> trabajos = trabajoServicio.listarPorProveedor(idProveedor).stream().filter( t -> t.getEstado().toString().equals("FINALIZADO") || t.getEstado().toString().equals("CANCELADO")).collect(Collectors.toList());
        modelo.addAttribute("trabajos", trabajos);
        List<Usuario> proveedores = proveedorServicio.listarProveedores();
        modelo.addAttribute("proveedores", proveedores);
        modelo.addAttribute("finalizado", true);
        return "trabajo_lista";

    }

    @PostMapping("/listarPorProveedor/{idProveedor}")
    public String filtrarPorProveedor(@PathVariable String idProveedor, ModelMap modelo) throws MiException{

        List<CategoriaServicio> categoriaServicio = categoriaServicioService.listarTodas();;
        modelo.addAttribute("categoriaServicio", categoriaServicio);



        List<Trabajo> trabajos = trabajoServicio.listarPorProveedor(idProveedor);
        modelo.addAttribute("trabajos", trabajos);
        List<Usuario> proveedores = proveedorServicio.listarProveedores();
        modelo.addAttribute("proveedores", proveedores);

        return "trabajo_lista";
    }

    @GetMapping("/mostrarComentarios/{idProveedor}")
    public String mostrarComentarios(@PathVariable String idProveedor, ModelMap modelo){
        try {
            List<AuxComentarioCalificacion> resultados = trabajoServicio.listarPorProveedor(idProveedor).stream()
                    .map(t -> new AuxComentarioCalificacion(t.getComentario(), t.getCalificacion(), t.getId())).collect(Collectors.toList());
            if (resultados.isEmpty()) {
                resultados.add(new AuxComentarioCalificacion("Sin Comentarios", 0, null));
            }
            Usuario proveedor = proveedorServicio.getProveedorById(idProveedor);
            modelo.addAttribute("resultados", resultados);
            modelo.addAttribute("proveedor", proveedor);
            return "proveedor_comentarios";
        }  catch(MiException ex){
                modelo.put("error", ex.getMessage());
                return "inicio";
            }
    }

    @GetMapping("borrarComentarioTrabajo/{idTrabajo}")
    public String borrarComentarioT(@PathVariable String idTrabajo, ModelMap modelo){
        Trabajo trabajo = trabajoServicio.getTrabajoById(idTrabajo);
        modelo.addAttribute("trabajo", trabajo);
        return "trabajo_borrar_comentario";
    }

    @PostMapping("/confirmarBorrarComentarioProveedor/{idTrabajo}")
    public String borrarComentario(@PathVariable String idTrabajo, ModelMap modelo) throws MiException{
        trabajoServicio.borrarComentario(idTrabajo);

        return "redirect:/proveedor/listar";
    }



}
