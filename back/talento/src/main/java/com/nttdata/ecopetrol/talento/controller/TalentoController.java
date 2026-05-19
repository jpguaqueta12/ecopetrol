package com.nttdata.ecopetrol.talento.controller;

import com.nttdata.ecopetrol.talento.dto.LoginRequest;
import com.nttdata.ecopetrol.talento.model.*;
import com.nttdata.ecopetrol.talento.repository.*;

import com.nttdata.ecopetrol.talento.services.NotificacionCorreoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/talento")
public class TalentoController {

    @Autowired
    private VacacionesRepository vacacionesRepository;

    @Autowired
    private IncapacidadRepository incapacidadRepository;

    @Autowired
    private CalamidadRepository calamidadRepository;

    @Autowired
    private DiaCumpleanioRepository diaCumpleanioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private NotificacionCorreoService notificacionCorreoService;

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody LoginRequest loginRequest) {

        if (loginRequest == null || loginRequest.getUsuario() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        String cleanedUsuario = loginRequest.getUsuario().trim().toLowerCase();

        Usuario usuarioEncontrado = usuarioRepository.findAll().stream()
                .filter(u -> u.getUsuario() != null &&
                        u.getUsuario().trim().toLowerCase().equals(cleanedUsuario))
                .findFirst()
                .orElse(null);

        if (usuarioEncontrado != null) {
            return ResponseEntity.ok(usuarioEncontrado);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // ----- Endpoints existentes para Vacaciones --------
    @GetMapping("/listarVacaciones")
    public List<Vacaciones> listarTodo() {
        return vacacionesRepository.findAll();
    }

    @PostMapping("/crearVacaciones")
    public Vacaciones crearVacaciones(@RequestBody Vacaciones vacaciones) {
        vacaciones.setEstado("PENDIENTE");
        return vacacionesRepository.save(vacaciones);
    }

    @DeleteMapping("/borrarVacaciones/{id}")
    public void borrarVacaciones(@PathVariable Long id) {
        vacacionesRepository.deleteById(id);
    }

    // ----- MALAS PRÁCTICAS: Endpoints para Incapacidad en el mismo controlador --------

    @GetMapping("/listarIncapacidades")
    public List<Incapacidad> listarIncapacidades() {
        return incapacidadRepository.findAll();
    }

    @PostMapping("/crearIncapacidad")
    public Incapacidad crearIncapacidad(@RequestBody Incapacidad incapacidad) {
        incapacidad.setEstado("PENDIENTE");
        return incapacidadRepository.save(incapacidad);
    }

    @DeleteMapping("/borrarIncapacidad/{id}")
    public void borrarIncapacidad(@PathVariable Long id) {
        incapacidadRepository.deleteById(id);
    }

    // ----- Endpoints para Calamidad (también malas prácticas y delays) -----

    @GetMapping("/listarCalamidades")
    public List<Calamidad> listarCalamidades() {
        return calamidadRepository.findAll();
    }

    @PostMapping("/crearCalamidad")
    public Calamidad crearCalamidad(@RequestBody Calamidad calamidad) {
        calamidad.setEstado("PENDIENTE");
        return calamidadRepository.save(calamidad);
    }

    @DeleteMapping("/borrarCalamidad/{id}")
    public void borrarCalamidad(@PathVariable Long id) {
        calamidadRepository.deleteById(id);
    }

    // --- Endpoints para Dias de Cumpleaños (mal diseño y delays) -----
    @GetMapping("/listarDiasCumpleanios")
    public List<DiaCumpleanio> listarDiasCumpleanios() {
        return diaCumpleanioRepository.findAll();
    }

    @PostMapping("/crearDiaCumpleanio")
    public DiaCumpleanio crearDiaCumpleanio(@RequestBody DiaCumpleanio diaCumpleanio) {
        diaCumpleanio.setEstado("PENDIENTE");
        return diaCumpleanioRepository.save(diaCumpleanio);
    }

    @DeleteMapping("/borrarDiaCumpleanio/{id}")
    public void borrarDiaCumpleanio(@PathVariable Long id) {
        diaCumpleanioRepository.deleteById(id);
    }

    @PostMapping("/aprobarVacaciones/{id}")
    public Vacaciones aprobarVacaciones(@PathVariable Long id, @RequestParam String rol) {

        if (!"LIDER".equals(rol)) {
            // No arroja excepción ni retorna error (grave), simplemente ignora
            return null;
        }
        Vacaciones vac = vacacionesRepository.findById(id).orElse(null);
        if (vac != null) {
            vac.setEstado("APROBADA");
            vacacionesRepository.save(vac);

            // Notificación dummy
            notificacionCorreoService.enviarCorreo(
                    "ivandavid.delarosapacheco@emeal.nttdata.com",
                    "Solicitud de Vacaciones Aprobada",
                    "Estimado " + vac.getNombreEmpleado() + ", su solicitud ha sido aprobada."
            );
        }
        return vac;
    }

    @PostMapping("/rechazarVacaciones/{id}")
    public Vacaciones rechazarVacaciones(@PathVariable Long id, @RequestParam String rol) {

        if (!"LIDER".equals(rol)) {
            return null;
        }
        Vacaciones vac = vacacionesRepository.findById(id).orElse(null);
        if (vac != null) {
            vac.setEstado("RECHAZADA");
            vacacionesRepository.save(vac);

            // Notificación dummy
            notificacionCorreoService.enviarCorreo(
                    "ivandavid.delarosapacheco@emeal.nttdata.com",
                    "Solicitud de Vacaciones Rechazada",
                    "Estimado " + vac.getNombreEmpleado() + ", su solicitud ha sido rechazada."
            );
        }
        return vac;
    }

    @GetMapping("/listarVacacionesPendientes")
    public List<Vacaciones> listarPendientes() {
        List<Vacaciones> todas = vacacionesRepository.findAll();
        // Filtro ineficiente (debería ser query JPA), y sin paginación
        return todas.stream()
                .filter(v -> "PENDIENTE".equals(v.getEstado()))
                .collect(Collectors.toList());
    }

    @PostMapping("/aprobarIncapacidad/{id}")
    public Incapacidad aprobarIncapacidad(@PathVariable Long id, @RequestParam String rol) {

        if (!"LIDER".equals(rol)) {
            // No arroja excepción ni retorna error (grave), simplemente ignora
            return null;
        }
        Incapacidad incapacidad = incapacidadRepository.findById(id).orElse(null);
        if (incapacidad != null) {
            incapacidad.setEstado("APROBADA");
            incapacidadRepository.save(incapacidad);

            // Notificación dummy
            notificacionCorreoService.enviarCorreo(
                    "ivandavid.delarosapacheco@emeal.nttdata.com",
                    "Solicitud de Incapacidad Aprobada",
                    "Estimado " + incapacidad.getNombreEmpleado() + ", su solicitud ha sido aprobada."
            );
        }
        return incapacidad;
    }

    @PostMapping("/rechazarIncapacidad/{id}")
    public Incapacidad rechazarIncapacidad(@PathVariable Long id, @RequestParam String rol) {

        if (!"LIDER".equals(rol)) {
            return null;
        }
        Incapacidad inc = incapacidadRepository.findById(id).orElse(null);
        if (inc != null) {
            inc.setEstado("RECHAZADA");
            incapacidadRepository.save(inc);

            // Notificación dummy
            notificacionCorreoService.enviarCorreo(
                    "ivandavid.delarosapacheco@emeal.nttdata.com",
                    "Solicitud de Incapacidad Rechazada",
                    "Estimado " + inc.getNombreEmpleado() + ", su solicitud ha sido Rechazada."
            );
        }
        return inc;
    }

    @GetMapping("/listarIncapacidadesPendientes")
    public List<Incapacidad> listarIncapacidadesPendientes() {
        List<Incapacidad> todas = incapacidadRepository.findAll();
        // Filtro ineficiente (debería ser query JPA), y sin paginación
        return todas.stream()
                .filter(i -> "PENDIENTE".equals(i.getEstado()))
                .collect(Collectors.toList());
    }

    @PostMapping("/aprobarCalamidad/{id}")
    public Calamidad aprobarCalamidad(@PathVariable Long id, @RequestParam String rol) {

        if (!"LIDER".equals(rol)) {
            // No arroja excepción ni retorna error (grave), simplemente ignora
            return null;
        }
        Calamidad calamidad = calamidadRepository.findById(id).orElse(null);
        if (calamidad != null) {
            calamidad.setEstado("APROBADA");
            calamidadRepository.save(calamidad);

            // Notificación dummy
            notificacionCorreoService.enviarCorreo(
                    "ivandavid.delarosapacheco@emeal.nttdata.com",
                    "Solicitud de Calamidad Aprobada",
                    "Estimado " + calamidad.getNombreEmpleado() + ", su solicitud ha sido aprobada."
            );
        }
        return calamidad;
    }

    @PostMapping("/rechazarCalamidad/{id}")
    public Calamidad rechazarCalamidad(@PathVariable Long id, @RequestParam String rol) {

        if (!"LIDER".equals(rol)) {
            return null;
        }
        Calamidad calam = calamidadRepository.findById(id).orElse(null);
        if (calam != null) {
            calam.setEstado("RECHAZADA");
            calamidadRepository.save(calam);

            // Notificación dummy
            notificacionCorreoService.enviarCorreo(
                    "ivandavid.delarosapacheco@emeal.nttdata.com",
                    "Solicitud de Calamidad Rechazada",
                    "Estimado " + calam.getNombreEmpleado() + ", su solicitud ha sido rechazada."
            );
        }
        return calam;
    }

    @GetMapping("/listarCalamidadesPendientes")
    public List<Calamidad> listarCalamidadesPendientes() {
        List<Calamidad> todas = calamidadRepository.findAll();
        return todas.stream()
                .filter(c -> "PENDIENTE".equals(c.getEstado()))
                .collect(Collectors.toList());
    }

    @PostMapping("/aprobarDiaCumpleanio/{id}")
    public DiaCumpleanio aprobarDiaCumpleanio(@PathVariable Long id, @RequestParam String rol) {

        if (!"LIDER".equals(rol)) {
            return null;
        }
        DiaCumpleanio dia = diaCumpleanioRepository.findById(id).orElse(null);
        if (dia != null) {
            dia.setEstado("APROBADA");
            diaCumpleanioRepository.save(dia);

            // Notificación dummy
            notificacionCorreoService.enviarCorreo(
                    "ivandavid.delarosapacheco@emeal.nttdata.com",
                    "Solicitud de Dia Cumpleaños Aprobada",
                    "Estimado " + dia.getNombreEmpleado() + ", su solicitud ha sido aprobada."
            );
        }
        return dia;
    }

    @PostMapping("/rechazarDiaCumpleanio/{id}")
    public DiaCumpleanio rechazarDiaCumpleanio(@PathVariable Long id, @RequestParam String rol) {

        if (!"LIDER".equals(rol)) {
            return null;
        }
        DiaCumpleanio dia = diaCumpleanioRepository.findById(id).orElse(null);
        if (dia != null) {
            dia.setEstado("RECHAZADA");
            diaCumpleanioRepository.save(dia);

            // Notificación dummy
            notificacionCorreoService.enviarCorreo(
                    "ivandavid.delarosapacheco@emeal.nttdata.com",
                    "Solicitud de Dia Cumpleaños Rechazada",
                    "Estimado " + dia.getNombreEmpleado() + ", su solicitud ha sido rechazada."
            );
        }
        return dia;
    }

    @GetMapping("/listarDiasCumpleaniosPendientes")
    public List<DiaCumpleanio> listarDiasCumpleaniosPendientes() {
        List<DiaCumpleanio> todos = diaCumpleanioRepository.findAll();
        return todos.stream()
                .filter(d -> "PENDIENTE".equals(d.getEstado()))
                .collect(Collectors.toList());
    }

    @PostMapping("/usuarios/crear")
    public Usuario crearUsuario(@RequestBody Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // Listar todos los usuarios
    @GetMapping("/usuarios/listaUsuarios")
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // Obtener usuario por ID
    @GetMapping("/usuarios/{id}")
    public Usuario obtenerUsuarioPorId(@PathVariable Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    // Eliminar usuario
    @DeleteMapping("usuarios/{id}")
    public void eliminarUsuario(@PathVariable Long id) {
        usuarioRepository.deleteById(id);
    }

    // Listar usuarios por ROLE
    @GetMapping("usuarios/porRol")
    public List<Usuario> buscarUsuariosPorRol(@RequestParam String rol) {
        return usuarioRepository.findByRol(rol);
    }
}
