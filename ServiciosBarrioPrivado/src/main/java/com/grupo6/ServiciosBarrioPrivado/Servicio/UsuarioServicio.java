package com.grupo6.ServiciosBarrioPrivado.Servicio;

import com.grupo6.ServiciosBarrioPrivado.Entidad.Trabajo;
import com.grupo6.ServiciosBarrioPrivado.Entidad.Usuario;
import com.grupo6.ServiciosBarrioPrivado.Enumeracion.Rol;
import com.grupo6.ServiciosBarrioPrivado.Excepciones.MiException;
import com.grupo6.ServiciosBarrioPrivado.Repositorio.UsuarioRepositorio;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private TrabajoServicio trabajoServicio;

    @Autowired
    private ProveedorServicio proveedorServicio;

    @Transactional
    public void registrar(String nombre,String apellido, String email, String password, String password2, String telefono) throws MiException {
        validar(nombre,apellido,email,password,password2, telefono);

        Usuario usuario = new Usuario();

        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setPassword(new BCryptPasswordEncoder().encode(password));
        usuario.setRol(Rol.USER);
        usuario.setTelefono(telefono);
        usuario.setCategoriaServicio(null);
        usuario.setPrecioPorHora(null);

        usuarioRepositorio.save(usuario);
    }


    @Transactional
    public void modificar(String id, String nombre, String apellido, String telefono) throws MiException{
        validarParcial(nombre,apellido,telefono);

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()){
            Usuario usuario = respuesta.get();

            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setTelefono(telefono);

            usuarioRepositorio.save(usuario);
        }


    }

    @Transactional
    public void modificarAdmin(String id, String nombre, String apellido, String telefono, Rol rol) throws MiException{
        validarParcial(nombre,apellido,telefono);

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()){
            Usuario usuario = respuesta.get();

            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setTelefono(telefono);
            usuario.setRol(rol);

            usuarioRepositorio.save(usuario);
        }


    }

    @Transactional
    public void eliminarUsuario(String id)throws MiException{
        if (id == null || id.isEmpty()){
            throw new MiException("El id ingresado no puede ser nulo o estar vacio");
        }
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if(respuesta.isPresent()){
            Usuario usuario = respuesta.get();
            usuarioRepositorio.delete(usuario);
        }
    }


    public List<Trabajo> trabajosDeUnUsuario(String idUsuario) throws MiException{
        return trabajoServicio.listarPorUsuario(idUsuario);
    }

    @Transactional
    public void modificarPerfil(String id, String nombre,String apellido, String telefono) throws MiException{
        this.validarParcial(nombre, apellido, telefono);
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()){
            Usuario usuario = respuesta.get();

            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setTelefono(telefono);

            usuarioRepositorio.save(usuario);
        }

    }

    @Transactional
    public void cambiarARolUsuario(String id, String nombre, String apellido, String telefono) throws MiException{
        validarParcial(nombre,apellido,telefono);

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()){
            Usuario usuario = respuesta.get();
            usuario.setRol(Rol.valueOf("USER"));
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setTelefono(telefono);

            usuarioRepositorio.save(usuario);
        }

    }

    ///// METODOS DE CONSULTA

    public List<Usuario> listarUsuarios(){
        List<Usuario> usuarios = new ArrayList();
        usuarios = usuarioRepositorio.findAll();
        return usuarios;
    }

    public Usuario getUsuarioById(String id){
        return usuarioRepositorio.getOne(id);
    }



    public void validar(String nombre,String apellido, String email, String password, String password2, String telefono) throws MiException{

        if((nombre.trim().isEmpty() || nombre == null)){
            throw new MiException("El nombre no puede ser nulo o estar vacio");
        }

        if(apellido.trim().isEmpty() || apellido == null){
            throw new MiException("El nombre no puede ser nulo o estar vacio");
        }

        if(email.trim().isEmpty() || email == null){
            throw new MiException("El email no puede ser nulo o estar vacio");
        }

        if(password.trim().isEmpty() || password == null || password.length() < 6){
            throw new MiException("La contraseña no puede ser nula o estar vacia y debe tener mas de 5 digitos");
        }

        if (!password.equals(password2)){
            throw new MiException("Las contraseñas ingresadas deben ser iguales");
        }

        if(telefono.trim().isEmpty() || telefono == null){
            throw new MiException("El telefono no puede ser nulo o estar vacio");
        }

    }

    public void validarParcial(String nombre, String apellido, String telefono) throws MiException {

        if (nombre.trim().isEmpty() || nombre == null) {
            throw new MiException("El nombre no puede ser nulo o estar vacio");
        }

        if (apellido.trim().isEmpty() || apellido == null) {
            throw new MiException("El email no puede ser nulo o estar vacio");
        }

        if (telefono.trim().isEmpty() || telefono == null) {
            throw new MiException("El email no puede ser nulo o estar vacio");
        }
    }

    public void validarPerfil(String nombre,String apellido, String telefono, String password, String password2) throws MiException{

        if(nombre.isEmpty() || nombre == null){
            throw new MiException("El nombre no puede ser nulo o estar vacio");
        }

        if(apellido.isEmpty() || apellido == null){
            throw new MiException("El nombre no puede ser nulo o estar vacio");
        }


        if(password.isEmpty() || password == null || password.length() < 6){
            throw new MiException("La contraseña no puede ser nula o estar vacia y debe tener mas de 5 digitos");
        }

        if (!password.equals(password2)){
            throw new MiException("Las contraseñas ingresadas deben ser iguales");
        }

        if(telefono.isEmpty() || telefono == null){
            throw new MiException("El telefono no puede ser nulo o estar vacio");
        }

    }

    @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

            Usuario usuario = usuarioRepositorio.buscarPorEmail(email);

            if (usuario != null){
                List<GrantedAuthority> permisos = new ArrayList();
                GrantedAuthority p = new SimpleGrantedAuthority("ROLE_"+usuario.getRol().toString());
                permisos.add(p);

                ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
                HttpSession session = attr.getRequest().getSession(true);

                session.setAttribute("usuariosesion", usuario);

                return new User(usuario.getEmail(), usuario.getPassword(), permisos);
            } else {
                return null;
            }
    }
}
