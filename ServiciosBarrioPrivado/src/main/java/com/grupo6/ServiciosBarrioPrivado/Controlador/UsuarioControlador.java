package com.grupo6.ServiciosBarrioPrivado.Controlador;

import com.grupo6.ServiciosBarrioPrivado.Entidad.AuxComentarioCalificacion;
import com.grupo6.ServiciosBarrioPrivado.Entidad.CategoriaServicio;
import com.grupo6.ServiciosBarrioPrivado.Entidad.Usuario;

import com.grupo6.ServiciosBarrioPrivado.Enumeracion.Rol;
import com.grupo6.ServiciosBarrioPrivado.Excepciones.MiException;
import com.grupo6.ServiciosBarrioPrivado.Servicio.CategoriaServicioService;
import com.grupo6.ServiciosBarrioPrivado.Servicio.ProveedorServicio;

import com.grupo6.ServiciosBarrioPrivado.Servicio.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/usuario")
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private ProveedorServicio proveedorServicio;

    @Autowired
    private CategoriaServicioService categoriaServicioService;



    @GetMapping("/registrar")
    public String registrar(){
        return "registro_usuario";
    }


    @PostMapping("/registro")
    public String registro_usuario(@RequestParam String nombre, @RequestParam String apellido,@RequestParam String email,  @RequestParam String telefono, @RequestParam String password,
                                   @RequestParam String password2, ModelMap modelo){
        try{
            usuarioServicio.registrar(nombre, apellido, email, password, password2, telefono);
            return "index";

        } catch (MiException ex){
            modelo.put("error", ex.getMessage());
            modelo.put("nombre", nombre);
            modelo.put("apellido", apellido);
            modelo.put("email", email);
            modelo.put("telefono", telefono);

            return "registro_usuario";
        }
    }

    @GetMapping("/modificar/{id}")
    public String modificarUsuario(@PathVariable String id, ModelMap modelo){
        Usuario usuario = usuarioServicio.getUsuarioById(id);
        modelo.addAttribute("usuario", usuario);
        List<Rol> roles = Arrays.stream(Rol.values()).toList();
        modelo.addAttribute("roles", roles);
        return "modificar_usuario";
    }


//    @PostMapping("/modificar/{id}")
//    public String modificar(@PathVariable String id,  @RequestParam String nombre, @RequestParam String apellido,
//                            @RequestParam String telefono, ModelMap modelo) throws MiException{
//        try{
//            usuarioServicio.modificar(id,nombre, apellido, telefono);
//            List<Usuario> usuarios = usuarioServicio.listarUsuarios().stream().filter(u -> u.getRol().toString().equals("USER")).collect(Collectors.toList());
//            modelo.addAttribute("usuarios", usuarios);
//            return "usuario_lista";
//        }catch(MiException ex){
//            Usuario usuario = usuarioServicio.getUsuarioById(id);
//            modelo.addAttribute("usuario", usuario);
//            modelo.put("error", ex.getMessage());
//            return "modificar_usuario";
//        }
//    }

    @PostMapping("/modificar/{id}")
    public String modificar(@PathVariable String id,  @RequestParam String nombre, @RequestParam String apellido,
                            @RequestParam String telefono, Rol rol, ModelMap modelo) throws MiException{
        try{
            if (rol != null) {
                usuarioServicio.modificarAdmin(id,nombre, apellido, telefono, rol);
            } else {
                usuarioServicio.modificar(id, nombre, apellido, telefono);
            }
            List<Usuario> usuarios = usuarioServicio.listarUsuarios().stream().filter(u -> u.getRol().toString().equals("USER")).collect(Collectors.toList());
            modelo.addAttribute("usuarios", usuarios);
            return "usuario_lista";
        }catch(MiException ex){
            Usuario usuario = usuarioServicio.getUsuarioById(id);
            modelo.addAttribute("usuario", usuario);
            modelo.put("error", ex.getMessage());
            return "modificar_usuario";
        }
    }

    @GetMapping("/modificarPerfilUsuario/{id}")
    public String modificarPerfilUsuario(@PathVariable String id, ModelMap modelo){
        Usuario usuario = usuarioServicio.getUsuarioById(id);
        modelo.addAttribute("usuario", usuario);
        return "modificar_perfil_usuario";
    }


    @PostMapping("/modificarPerfilUsuario/{id}")
    public String modificarPerfilU(@PathVariable String id,  @RequestParam String nombre, @RequestParam String apellido,
                                   @RequestParam String telefono,
                                   ModelMap modelo) {
        try{
            usuarioServicio.modificarPerfil(id,nombre, apellido, telefono);
            modelo.addAttribute("usuario", usuarioServicio.getUsuarioById(id));
            return "inicio";
        }catch(MiException ex){
            Usuario usuario = usuarioServicio.getUsuarioById(id);
            modelo.addAttribute("usuario", usuario);
            modelo.put("error", ex.getMessage());
            return "modificar_perfil_usuario";
        }
    }


    @GetMapping("borrar/{id}")
    public String borrarUsuario(@PathVariable String id, ModelMap modelo){
        Usuario usuario = usuarioServicio.getUsuarioById(id);
        modelo.addAttribute("usuario", usuario);
        return "usuario_borrar";
    }

    @PostMapping("/confirmarBorrar/{id}")
    public String borrar(@PathVariable String id, ModelMap modelo) throws MiException{
        try{
            if (usuarioServicio.trabajosDeUnUsuario(id).isEmpty()){
                usuarioServicio.eliminarUsuario(id);
                return "redirect:../listar";
            }
            modelo.put("error", "El Usuario que desea borrar de la base de datos, Posee Trabajos asociados. Debera primero eliminar los trabajos asociados a dicho usuario para luego eliminarlo");
            return "inicio";
        }catch(MiException ex){
            modelo.put("error", ex.getMessage());
            return "inicio";
        }
    }


    @GetMapping("/listar")
    public String listarTodos(ModelMap modelo){
        List<Usuario> usuarios = usuarioServicio.listarUsuarios().stream().filter(u -> u.getRol().toString().equals("USER")).collect(Collectors.toList());
        modelo.addAttribute("usuarios", usuarios);
        return "usuario_lista";
    }

    @GetMapping("/perfil/{id}/{rol}")
    public String perfil(@PathVariable Rol rol, @PathVariable String id, ModelMap modelo){
        Usuario usuario;
        if (rol.toString().equals("PROVEEDOR")){
            usuario = proveedorServicio.getProveedorById(id);
            modelo.addAttribute("usuario", usuario);

            try{
                List<AuxComentarioCalificacion> resultados = proveedorServicio.trabajosFinalizadosDeUnProveedor(id).stream()
                        .map(t -> new AuxComentarioCalificacion(t.getComentario(), t.getCalificacion(), t.getId())).collect(Collectors.toList());
                if (resultados.isEmpty()) {
                    resultados.add(new AuxComentarioCalificacion("Sin Comentarios", 0, null));
                }
                modelo.addAttribute("cantCalificaciones", proveedorServicio.cantidadCalificacionesDeUnProveedor(id));
                modelo.addAttribute("cantComentarios", proveedorServicio.cantidadComentariosDeUnProveedor(id));
                modelo.addAttribute("resultados", resultados);
            } catch(MiException ex){
                modelo.put("error", ex.getMessage());
            }

        } else if (rol.toString().equals("USER")){
            usuario = usuarioServicio.getUsuarioById(id);
            modelo.addAttribute("usuario", usuario);
        } else {
            // si el usuario es ADMIN
            return "perfil_admin";
        }

        return "perfil";
    }

    @GetMapping("/cambiarRolAProveedor/{idCliente}")
    public String confirmacionCambiarRol(@PathVariable String idCliente, ModelMap modelo){
        Usuario usuario = usuarioServicio.getUsuarioById(idCliente);
        modelo.addAttribute("usuario", usuario);
        List<CategoriaServicio> categoriaServicio = categoriaServicioService.listarTodas();
        modelo.addAttribute("categoriaServicio", categoriaServicio);
        return "cambio_rol_a_proveedor";
    }



    @PostMapping("/cambiarRolAProveedor/{idCliente}")
    public String cambiarRol(@PathVariable String idCliente,  @RequestParam String nombre, @RequestParam String apellido,
                             @RequestParam String telefono,@RequestParam String idCategoria,
                             @RequestParam Integer precioPorHora, ModelMap modelo){
        try{
            proveedorServicio.cambiarARolProveedor(idCliente,nombre,apellido,telefono,idCategoria,precioPorHora);
            try{
                List<AuxComentarioCalificacion> resultados = proveedorServicio.trabajosFinalizadosDeUnProveedor(idCliente)
                        .stream().map(t -> new AuxComentarioCalificacion(t.getComentario(), t.getCalificacion(), t.getId())).collect(Collectors.toList());
                if (resultados.isEmpty()) {
                    resultados.add(new AuxComentarioCalificacion("Sin Comentarios", 0, null));
                }
                modelo.addAttribute("cantCalificaciones", proveedorServicio.cantidadCalificacionesDeUnProveedor(idCliente));
                modelo.addAttribute("cantComentarios", proveedorServicio.cantidadComentariosDeUnProveedor(idCliente));
                modelo.addAttribute("resultados", resultados);
            } catch(MiException ex){
                modelo.put("error", ex.getMessage());
            } finally {
                Usuario usuario = proveedorServicio.getProveedorById(idCliente);
                modelo.addAttribute("usuario", usuario);
                return "perfil_rol_logout";
            }
        } catch (MiException ex){
            modelo.put("error", ex.getMessage());
            Usuario usuario = proveedorServicio.getProveedorById(idCliente);
            modelo.addAttribute("usuario", usuario);
            return "perfil_rol_logout";
        }
    }

}
