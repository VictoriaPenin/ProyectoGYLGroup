package com.grupo6.ServiciosBarrioPrivado.Controlador;

import com.grupo6.ServiciosBarrioPrivado.Entidad.CategoriaServicio;

import com.grupo6.ServiciosBarrioPrivado.Excepciones.MiException;

import com.grupo6.ServiciosBarrioPrivado.Servicio.CategoriaServicioService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminControlador {

    @Autowired
    private CategoriaServicioService categoriaServicioService;



    @GetMapping("/crearCategoria")
    public String crearCategoria(){
        return "registro_categoria";
    }

    @PostMapping("/crearCategoria")
    public String crear(@RequestParam String nombre, @RequestParam String descripcion, ModelMap modelo) throws MiException {

        try {
            categoriaServicioService.crear(nombre, descripcion);
        }catch(MiException ex){
            modelo.put("error", ex.getMessage());
            return "registro_categoria";
        }

        List<CategoriaServicio> categorias = categoriaServicioService.listarTodas();
        if(categorias.isEmpty()){
            categorias.add(null);
        }
        modelo.addAttribute("categorias", categorias);
        return "categorias_lista";
    }

    @GetMapping("listarCategorias")
    public String listar(ModelMap modelo){
        List<CategoriaServicio> categorias = categoriaServicioService.listarTodas();
        if(categorias.isEmpty()){
            categorias.add(null);
        }
        modelo.addAttribute("categorias", categorias);
        return "categorias_lista";
    }

    @GetMapping("/modificarCategoria/{id}")
    public String modificarCategoria(@PathVariable String id, ModelMap modelo){
        modelo.addAttribute("categoria", categoriaServicioService.getCategoriaById(id));
        return "modificar_categoria";
    }

    @PostMapping("/modificarCategoria/{id}")
    public String modificar(@PathVariable String id, @RequestParam String nombre, @RequestParam String descripcion, ModelMap modelo) throws MiException {

        try {
            categoriaServicioService.modificar(id,nombre, descripcion);
        }catch(MiException ex){
            modelo.put("error", ex.getMessage());
            return "modificar_categoria";
        }

        List<CategoriaServicio> categorias = categoriaServicioService.listarTodas();
        if(categorias.isEmpty()){
            categorias.add(null);
        }
        modelo.addAttribute("categorias", categorias);
        return "categorias_lista";
    }

    @GetMapping("borrarCategoria/{id}")
    public String borrarCategoria(@PathVariable String id, ModelMap modelo){
        CategoriaServicio categoria = categoriaServicioService.getCategoriaById(id);
        modelo.addAttribute("categoria", categoria);
        return "categoria_borrar";
    }

    @PostMapping("/confirmarBorrar/{id}")
    public String borrar(@PathVariable String id, ModelMap modelo) throws MiException{
        try{
            if (categoriaServicioService.trabajadoresDeUnaCategoria(id).isEmpty()){
                categoriaServicioService.eliminarCategoria(id);
                return "redirect:../listarCategorias";
            }
            modelo.put("error", "La categoria que desea borrar de la base de datos, Posee Proveedores asociados. Debera primero eliminar los Proveedores asociados a dicha categoria para luego eliminarla");
            List<CategoriaServicio> categorias = categoriaServicioService.listarTodas();
            if(categorias.isEmpty()){
                categorias.add(null);
            }
            modelo.addAttribute("categorias", categorias);
            return "categorias_lista";
        }catch(MiException ex){
            modelo.put("error", ex.getMessage());
            return "inicio";
        }
    }


}
