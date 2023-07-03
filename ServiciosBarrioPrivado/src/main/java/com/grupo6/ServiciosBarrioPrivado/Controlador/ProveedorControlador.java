package com.grupo6.ServiciosBarrioPrivado.Controlador;

import com.grupo6.ServiciosBarrioPrivado.Entidad.CategoriaServicio;
import com.grupo6.ServiciosBarrioPrivado.Entidad.Trabajo;
import com.grupo6.ServiciosBarrioPrivado.Entidad.Usuario;

import com.grupo6.ServiciosBarrioPrivado.Enumeracion.Rol;
import com.grupo6.ServiciosBarrioPrivado.Excepciones.MiException;
import com.grupo6.ServiciosBarrioPrivado.Servicio.CategoriaServicioService;
import com.grupo6.ServiciosBarrioPrivado.Servicio.ProveedorServicio;

import com.grupo6.ServiciosBarrioPrivado.Servicio.UsuarioServicio;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/proveedor")
public class ProveedorControlador {

    @Autowired
    private ProveedorServicio proveedorServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private CategoriaServicioService categoriaServicioService;


    @GetMapping("/registrar")
    public String registrar(ModelMap modelo){
        List<CategoriaServicio> categoriaServicio = categoriaServicioService.listarTodas();
        modelo.addAttribute("categoriaServicio", categoriaServicio);
        return "registro_proveedor";
    }


    @PostMapping("/registro")
    public String registro_proveedor(@RequestParam String nombre, @RequestParam String apellido, @RequestParam String email, @RequestParam String password,
                                     @RequestParam String password2, @RequestParam String telefono,
                                     @RequestParam CategoriaServicio categoria, @RequestParam Integer precioPorHora,
                                     @RequestParam("file") MultipartFile imagen, ModelMap modelo){
        try{
            if(!imagen.isEmpty()) {
                Path directorioImagenes = Paths.get("src//main//resources//static/pictures");
                String rutaAbsoluta = directorioImagenes.toFile().getAbsolutePath();


                try {
                    byte[] bytesImg = imagen.getBytes();
                    Path rutaCompleta = Paths.get(rutaAbsoluta + "//" + imagen.getOriginalFilename());
                    Files.write(rutaCompleta, bytesImg);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            proveedorServicio.registrar(nombre, apellido, email, password, password2, telefono, categoria.getId(), precioPorHora, imagen);
            return "index";

        } catch (MiException ex){
            modelo.put("error", ex.getMessage());
            modelo.put("nombre", nombre);
            modelo.put("apellido", apellido);
            modelo.put("email", email);
            modelo.put("telefono", telefono);
            List<CategoriaServicio> categoriaServicio = categoriaServicioService.listarTodas();
            modelo.addAttribute("categoriaServicio", categoriaServicio);
            modelo.put("precioPorhora", precioPorHora);

            return "registro_proveedor";
        }
    }

    @GetMapping("/modificar/{id}")
    public String modificarProveedor(@PathVariable String id, ModelMap modelo){
        Usuario proveedor = proveedorServicio.getProveedorById(id);
        modelo.addAttribute("proveedor", proveedor);
        List<CategoriaServicio> categoriaServicio = categoriaServicioService.listarTodas();
        modelo.addAttribute("categoriaServicio", categoriaServicio);
        List<Rol> roles = Arrays.stream(Rol.values()).toList();
        modelo.addAttribute("roles", roles);
        return "modificar_proveedor";
    }


    @PostMapping("/modificar/{id}")
    public String modificar(@PathVariable String id,  @RequestParam String nombre, @RequestParam String apellido,
                            @RequestParam String telefono,@RequestParam String idCategoria,
                            @RequestParam Integer precioPorHora, Rol rol, @RequestParam("file") MultipartFile imagen, ModelMap modelo) throws MiException{
        try{
            if (rol != null){
                proveedorServicio.modificarAdmin(id,nombre, apellido, telefono, idCategoria, precioPorHora, rol);
            } else {
                Path directorioImagenes = Paths.get("src//main//resources//static/pictures");
                String rutaAbsoluta = directorioImagenes.toFile().getAbsolutePath();

//                proveedorServicio.modificarImagen(id, imagen.getOriginalFilename());

                try {
                    byte[] bytesImg = imagen.getBytes();
                    Path rutaCompleta = Paths.get(rutaAbsoluta + "//" + imagen.getOriginalFilename());
                    Files.write(rutaCompleta, bytesImg);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                proveedorServicio.modificar(id,nombre, apellido, telefono, idCategoria, precioPorHora, imagen);
            }
            List<Usuario> proveedores = proveedorServicio.listarProveedores();
            modelo.addAttribute("proveedores", proveedores);
            List<CategoriaServicio> categoriaServicio = categoriaServicioService.listarTodas();
            modelo.addAttribute("categoriaServicio", categoriaServicio);
            return "proveedor_lista";
        }catch(MiException ex){
            Usuario proveedor = proveedorServicio.getProveedorById(id);
            modelo.addAttribute("proveedor", proveedor);
            List<CategoriaServicio> categoriaServicio = categoriaServicioService.listarTodas();
            modelo.addAttribute("categoriaServicio", categoriaServicio);
            modelo.put("error", ex.getMessage());
            return "modificar_proveedor";
        }
    }

    @GetMapping("borrar/{id}")
    public String borrarProveedor(@PathVariable String id, ModelMap modelo){
        Usuario proveedor = proveedorServicio.getProveedorById(id);
        modelo.addAttribute("proveedor", proveedor);
        return "proveedor_borrar";
    }

    @PostMapping("/confirmarBorrar/{id}")
    public String borrar(@PathVariable String id, ModelMap modelo) throws MiException{
        try{
            if (proveedorServicio.trabajosDeUnProveedor(id).isEmpty()){
                proveedorServicio.eliminarProveedor(id);
                return "redirect:../listar";
            }
            modelo.put("error", "El Proveedor que desea borrar de la base de datos, Posee Trabajos asociados. Debera primero eliminar los trabajos asociados a dicho proveedor para luego eliminarlo");
            return "inicio";
        }catch(MiException ex){
            modelo.put("error", ex.getMessage());
            return "inicio";
        }
    }


    @GetMapping("/modificarPerfilProveedor/{id}")
    public String modificarPerfilProveedor(@PathVariable String id, ModelMap modelo){
        Usuario proveedor = proveedorServicio.getProveedorById(id);
        modelo.addAttribute("proveedor", proveedor);
        List<CategoriaServicio> categoriaServicio = categoriaServicioService.listarTodas();
        modelo.addAttribute("categoriaServicio", categoriaServicio);
        try{
            List<Trabajo> trabajos = proveedorServicio.trabajosDeUnProveedor(id);
            modelo.addAttribute("trabajos", trabajos);
            return "modificar_perfil_proveedor";
        } catch(MiException ex){
            modelo.put("error", ex.getMessage());
            return "modificar_perfil_proveedor";
        }

    }


    @PostMapping("/modificarPerfilProveedor/{id}")
    public String modificarPerfilP(@PathVariable String id, @RequestParam String nombre, @RequestParam String apellido,
                                   @RequestParam String telefono, @RequestParam String idCategoria,
                                   @RequestParam Integer precioPorHora, @RequestParam("file") MultipartFile imagen,
                                   ModelMap modelo) {
        try{
            proveedorServicio.modificarPerfil(id,nombre, apellido, telefono, idCategoria, precioPorHora);
            if(!imagen.isEmpty()) {
                Path directorioImagenes = Paths.get("src//main//resources//static/pictures");
                String rutaAbsoluta = directorioImagenes.toFile().getAbsolutePath();

                proveedorServicio.modificarImagen(id, imagen.getOriginalFilename());

                try {
                    byte[] bytesImg = imagen.getBytes();
                    Path rutaCompleta = Paths.get(rutaAbsoluta + "//" + imagen.getOriginalFilename());
                    Files.write(rutaCompleta, bytesImg);

                } catch (IOException e) {
                    e.printStackTrace();
                    modelo.put("error", e.getMessage());
                    Usuario proveedor = proveedorServicio.getProveedorById(id);
                    modelo.addAttribute("proveedor", proveedor);
                    List<CategoriaServicio> categoriaServicio = categoriaServicioService.listarTodas();
                    modelo.addAttribute("categoriaServicio", categoriaServicio);
                    return "modificar_perfil_proveedor";
                }
            }
            modelo.addAttribute("usuario", proveedorServicio.getProveedorById(id));
            return "inicio";

        }catch(MiException ex){
            Usuario proveedor = proveedorServicio.getProveedorById(id);
            modelo.addAttribute("proveedor", proveedor);
            List<CategoriaServicio> categoriaServicio = categoriaServicioService.listarTodas();
            modelo.addAttribute("categoriaServicio", categoriaServicio);
            modelo.put("error", ex.getMessage());
            return "modificar_perfil_proveedor";
        }
    }




    @GetMapping("/listar")
    public String listarTodos(ModelMap modelo){
        List<Usuario> proveedores = proveedorServicio.listarProveedores();
        modelo.addAttribute("proveedores", proveedores);
        List<CategoriaServicio> categoriaServicio = categoriaServicioService.listarTodas();
        modelo.addAttribute("categoriaServicio", categoriaServicio);
        modelo.addAttribute("provServicio", proveedorServicio);
        return "proveedor_lista";
    }

    @PostMapping("/listarPorCategoria")
    public String listarPorCategoria(@RequestParam String categoria, ModelMap modelo){
        List<CategoriaServicio> categoriaServicio = categoriaServicioService.listarTodas();
        modelo.addAttribute("categoriaServicio", categoriaServicio);


        if (categoria.equals("Todos")){
            List<Usuario> proveedores = proveedorServicio.listarProveedores();
            modelo.addAttribute("proveedores", proveedores);
            modelo.addAttribute("provServicio", proveedorServicio);
            return "proveedor_lista";
        } else {
            List<Usuario> proveedores = proveedorServicio.listarPorCategoria(categoriaServicioService.getCategoriaByName(categoria).getId());
            modelo.addAttribute("proveedores", proveedores);
            modelo.addAttribute("categoriaSeleccionada", categoriaServicioService.getCategoriaByName(categoria));
            modelo.addAttribute("provServicio", proveedorServicio);
            return "proveedor_lista";
        }
    }

    @GetMapping("/cambiarRolAUsuario/{idProveedor}")
    public String confirmacionCambiarRol(@PathVariable String idProveedor, ModelMap modelo){
        Usuario proveedor = proveedorServicio.getProveedorById(idProveedor);
        modelo.addAttribute("proveedor", proveedor);
        return "cambio_rol_a_usuario";
    }

    @PostMapping("/cambiarRolAUsuario/{idProveedor}")
    public String cambiarRol(@PathVariable String idProveedor,  @RequestParam String nombre, @RequestParam String apellido,
                         @RequestParam String telefono, ModelMap modelo){

        try{
            usuarioServicio.cambiarARolUsuario(idProveedor,nombre,apellido,telefono);
        } catch (MiException ex){
            modelo.put("error", ex.getMessage());
        } finally {
            Usuario usuario = usuarioServicio.getUsuarioById(idProveedor);
            modelo.addAttribute("usuario", usuario);
            return "perfil_rol_logout";
        }


    }



}
