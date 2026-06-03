package com.nttdata.ecopetrol.talento.controller;

import com.nttdata.ecopetrol.talento.config.NominaApiClient;
import com.nttdata.ecopetrol.talento.dto.CierreMesResultadoDTO;
import com.nttdata.ecopetrol.talento.dto.LoginRequest;
import com.nttdata.ecopetrol.talento.enums.CodigoError;
import com.nttdata.ecopetrol.talento.model.*;
import com.nttdata.ecopetrol.talento.repository.*;

import com.nttdata.ecopetrol.talento.services.NotificacionCorreoService;
import com.nttdata.ecopetrol.talento.services.ValidacionVacacionesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
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
    private SolicitudUnificadaRepository solicitudUnificadaRepositoryrepository;

    @Autowired
    private NotificacionCorreoService notificacionCorreoService;

    @Autowired
    private ValidacionVacacionesService validacionVacacionesService;

    @Autowired
    private NominaApiClient nominaApiClient;

    private static final Logger logger = LoggerFactory.getLogger(TalentoController.class);

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody LoginRequest loginRequest) {
        logger.info("Solicitud de login para usuario '{}'", loginRequest != null ? loginRequest.getUsuario() : null);

        if (loginRequest == null || loginRequest.getUsuario() == null) {
            MDC.put("error_code", CodigoError.LOGIN_FALLIDO.getCodigo());
            logger.warn("Login fallido: request o usuario nulo");
            MDC.remove("error_code");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String cleanedUsuario = loginRequest.getUsuario().trim().toLowerCase();
        // Campo dedicado "usuario" en el JSON (el filtro lo limpia al terminar la petición).
        MDC.put("usuario", cleanedUsuario);

        Usuario usuarioEncontrado = usuarioRepository.findAll().stream()
                .filter(u -> u.getUsuario() != null &&
                        u.getUsuario().trim().toLowerCase().equals(cleanedUsuario))
                .findFirst()
                .orElse(null);

        if (usuarioEncontrado != null) {
            // Puedes registrar info de login exitoso (no lleva código de error)
            logger.info("Login exitoso para usuario '{}'", cleanedUsuario);
            String sessionId = UUID.randomUUID().toString();
            return ResponseEntity.ok()
                    .header("X-Session-ID", sessionId)
                    .body(usuarioEncontrado);
        } else {
            MDC.put("error_code", CodigoError.USUARIO_NO_ENCONTRADO.getCodigo());
            logger.warn("Login fallido: usuario '{}' no encontrado", cleanedUsuario);
            MDC.remove("error_code");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    // ----- Endpoints existentes para Vacaciones --------
    @GetMapping("/listarVacaciones")
    public List<Vacaciones> listarTodo() {
        logger.info("Listando todas las vacaciones");
        return vacacionesRepository.findAll();
    }

    @PostMapping("/crearVacaciones")
    public Vacaciones crearVacaciones(@RequestBody Vacaciones vacaciones) {
        logger.info("Creando solicitud de vacaciones para empleado {}", vacaciones.getNumeroEmpleado());
        vacaciones.setEstado("PENDIENTE");
        return vacacionesRepository.save(vacaciones);
    }

    @DeleteMapping("/borrarVacaciones/{id}")
    public void borrarVacaciones(@PathVariable Long id) {
        if (!vacacionesRepository.existsById(id)) {
            MDC.put("error_code", CodigoError.USUARIO_NO_ENCONTRADO.getCodigo()); // O nuevo código "Registro no encontrado"
            logger.warn("Intento de borrar registro de vacaciones que no existe: id={}", id);
            MDC.remove("error_code");
            return;
        }
        logger.info("Eliminando registro de vacaciones con id {}", id);
        vacacionesRepository.deleteById(id);
    }

    // ----- MALAS PRÁCTICAS: Endpoints para Incapacidad en el mismo controlador --------

    @GetMapping("/listarIncapacidades")
    public List<Incapacidad> listarIncapacidades() {
        logger.info("Listando todas las incapacidades");
        return incapacidadRepository.findAll();
    }

    @PostMapping("/crearIncapacidad")
    public Incapacidad crearIncapacidad(@RequestBody Incapacidad incapacidad) {
        logger.info("Creando solicitud de incapacidad para empleado {}", incapacidad.getNumeroEmpleado());
        incapacidad.setEstado("PENDIENTE");
        return incapacidadRepository.save(incapacidad);
    }

    @DeleteMapping("/borrarIncapacidad/{id}")
    public void borrarIncapacidad(@PathVariable Long id) {
        logger.info("Eliminando registro de incapacidad con id {}", id);
        incapacidadRepository.deleteById(id);
    }

    // ----- Endpoints para Calamidad (también malas prácticas y delays) -----

    @GetMapping("/listarCalamidades")
    public List<Calamidad> listarCalamidades() {
        logger.info("Listando todas las calamidades");
        return calamidadRepository.findAll();
    }

    @PostMapping("/crearCalamidad")
    public Calamidad crearCalamidad(@RequestBody Calamidad calamidad) {
        logger.info("Creando solicitud de calamidad para empleado {}", calamidad.getNumeroEmpleado());
        calamidad.setEstado("PENDIENTE");
        return calamidadRepository.save(calamidad);
    }

    @DeleteMapping("/borrarCalamidad/{id}")
    public void borrarCalamidad(@PathVariable Long id) {
        logger.info("Eliminando registro de calamidad con id {}", id);
        calamidadRepository.deleteById(id);
    }

    // --- Endpoints para Dias de Cumpleaños (mal diseño y delays) -----
    @GetMapping("/listarDiasCumpleanios")
    public List<DiaCumpleanio> listarDiasCumpleanios() {
        logger.info("Listando todos los días de cumpleaños");
        return diaCumpleanioRepository.findAll();
    }

    @PostMapping("/crearDiaCumpleanio")
    public DiaCumpleanio crearDiaCumpleanio(@RequestBody DiaCumpleanio diaCumpleanio) {
        logger.info("Creando solicitud de día cumpleaños para empleado {}", diaCumpleanio.getNumeroEmpleado());
        diaCumpleanio.setEstado("PENDIENTE");
        return diaCumpleanioRepository.save(diaCumpleanio);
    }

    @DeleteMapping("/borrarDiaCumpleanio/{id}")
    public void borrarDiaCumpleanio(@PathVariable Long id) {
        logger.info("Eliminando registro de día cumpleaños con id {}", id);
        diaCumpleanioRepository.deleteById(id);
    }

    @PostMapping("/aprobarVacaciones/{id}")
    public ResponseEntity<?> aprobarVacaciones(
            @PathVariable Long id,
            @RequestParam String rol,
            @RequestHeader(value = "X-Session-ID", required = true) String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            MDC.put("error_code", CodigoError.CREDENCIALES_INVALIDAS.getCodigo());
            logger.warn("El X-Session-ID es requerido ");
            MDC.remove("error_code");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session ID requerido");
        }
        logger.info("Intentando aprobar vacaciones [vacacionesId={}, rol={}]", id, rol);

        if (!"LIDER".equals(rol)) {
            MDC.put("error_code", CodigoError.PERMISO_DENEGADO.getCodigo());
            logger.warn("Acceso denegado: Usuario sin permiso de líder [vacacionesId={}, rol={}]", id, rol);
            MDC.remove("error_code");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso restringido a líderes.");
        }

        Vacaciones vac = vacacionesRepository.findById(id).orElse(null);
        if (vac == null) {
            MDC.put("error_code", CodigoError.USUARIO_NO_ENCONTRADO.getCodigo());
            logger.error("Vacaciones no encontradas [vacacionesId={}]", id);
            MDC.remove("error_code");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vacación no encontrada.");
        }

        try {
            logger.info("Validando días disponibles para empleado [numeroEmpleado={}, solicitado={}]", vac.getNumeroEmpleado(), vac.getTotalDias());
            boolean valido = validacionVacacionesService.validarDiasVacacionesDisponibles(vac);
            if (!valido) {
                MDC.put("error_code", CodigoError.SIN_DIAS_SUFI.getCodigo());
                logger.warn("Empleado sin días suficientes para sus vacaciones [numeroEmpleado={}, solicitado={}]", vac.getNumeroEmpleado(), vac.getTotalDias());
                MDC.remove("error_code");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error: El empleado no tiene suficientes días de vacaciones disponibles.");
            }
        } catch (InterruptedException e) {
            MDC.put("error_code", CodigoError.VALIDACION_INTERRUP.getCodigo());
            logger.error("Validación interrumpida para vacaciones [vacacionesId={}]: {}", id, e.getMessage());
            MDC.remove("error_code");
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al validar días disponibles (interrumpido).");
        } catch (Exception e) {
            MDC.put("error_code", CodigoError.ERROR_VALIDACION.getCodigo());
            logger.error("Error inesperado al validar vacaciones [vacacionesId={}]: {}", id, e.getMessage());
            MDC.remove("error_code");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado de validación: " + e.getMessage());
        }

        vac.setEstado("APROBADA");
        vacacionesRepository.save(vac);
        logger.info("Vacaciones aprobadas correctamente [vacacionesId={}, numeroEmpleado={}]", id, vac.getNumeroEmpleado());

        notificacionCorreoService.enviarCorreo(
                "juanpablo.guaquetaanzola@emeal.nttdata.com",
                "Solicitud de Vacaciones Aprobada",
                "Estimado " + vac.getNombreEmpleado() + ", su solicitud ha sido aprobada."
        );
        logger.info("Correo de aprobación enviado a {}", "juanpablo.guaquetaanzola@emeal.nttdata.com");

        return ResponseEntity.ok(vac);
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
                    "juanpablo.guaquetaanzola@emeal.nttdata.com",
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
    public ResponseEntity<?> aprobarIncapacidad(
            @PathVariable Long id,
            @RequestParam String rol,
            @RequestHeader(value = "X-Session-ID", required = true) String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            MDC.put("error_code", CodigoError.CREDENCIALES_INVALIDAS.getCodigo());
            logger.warn("El X-Session-ID es requerido");
            MDC.remove("error_code");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session ID requerido");
        }

        logger.info("Intentando aprobar incapacidad [incapacidadId={}, rol={}]", id, rol);

        if (!"LIDER".equals(rol)) {
            MDC.put("error_code", CodigoError.PERMISO_DENEGADO.getCodigo());
            logger.warn("Acceso denegado: Usuario sin permiso de líder [incapacidadId={}, rol={}]", id, rol);
            MDC.remove("error_code");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso restringido a líderes.");
        }

        Incapacidad incapacidad = incapacidadRepository.findById(id).orElse(null);
        if (incapacidad == null) {
            MDC.put("error_code", CodigoError.USUARIO_NO_ENCONTRADO.getCodigo());
            logger.error("Incapacidad no encontrada [incapacidadId={}]", id);
            MDC.remove("error_code");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Incapacidad no encontrada.");
        }

        try {
            logger.info("Validando días disponibles para incapacidad [numeroEmpleado={}, solicitado={}]",
                    incapacidad.getNumeroEmpleado(), incapacidad.getTotalDias());
            boolean valido = validacionVacacionesService.validarDiasIncapacidadDisponibles(incapacidad);
            if (!valido) {
                MDC.put("error_code", CodigoError.SIN_DIAS_SUFI.getCodigo());
                logger.warn("Empleado sin días suficientes para incapacidad [numeroEmpleado={}, solicitado={}]",
                        incapacidad.getNumeroEmpleado(), incapacidad.getTotalDias());
                MDC.remove("error_code");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error: El empleado no tiene suficientes días de incapacidad disponibles.");
            }
        } catch (InterruptedException e) {
            MDC.put("error_code", CodigoError.VALIDACION_INTERRUP.getCodigo());
            logger.error("Validación interrumpida para incapacidad [incapacidadId={}]: {}", id, e.getMessage());
            MDC.remove("error_code");
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al validar días disponibles (interrumpido).");
        } catch (Exception e) {
            MDC.put("error_code", CodigoError.ERROR_VALIDACION.getCodigo());
            logger.error("Error inesperado al validar incapacidad [incapacidadId={}]: {}", id, e.getMessage());
            MDC.remove("error_code");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado de validación: " + e.getMessage());
        }

        incapacidad.setEstado("APROBADA");
        incapacidadRepository.save(incapacidad);
        logger.info("Incapacidad aprobada correctamente [incapacidadId={}, numeroEmpleado={}]",
                id, incapacidad.getNumeroEmpleado());

        notificacionCorreoService.enviarCorreo(
                "german.edmundo.velasco.bravo@emeal.nttdata.com",
                "Solicitud de Incapacidad Aprobada",
                "Estimado " + incapacidad.getNombreEmpleado() + ", su solicitud ha sido aprobada."
        );
        logger.info("Correo de aprobación enviado a {}", "german.edmundo.velasco.bravo@emeal.nttdata.com");

        return ResponseEntity.ok(incapacidad);
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
                    "german.edmundo.velasco.bravo@emeal.nttdata.com",
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
    public ResponseEntity<?> aprobarCalamidad(
            @PathVariable Long id,
            @RequestParam String rol,
            @RequestHeader(value = "X-Session-ID", required = true) String sessionId) {

        if (sessionId == null || sessionId.isEmpty()) {
            MDC.put("error_code", CodigoError.CREDENCIALES_INVALIDAS.getCodigo());
            logger.warn("El X-Session-ID es requerido");
            MDC.remove("error_code");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session ID requerido");
        }

        logger.info("Intentando aprobar calamidad [calamidadId={}, rol={}]", id, rol);

        if (!"LIDER".equals(rol)) {
            MDC.put("error_code", CodigoError.PERMISO_DENEGADO.getCodigo());
            logger.warn("Acceso denegado: Usuario sin permiso de líder [calamidadId={}, rol={}]", id, rol);
            MDC.remove("error_code");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso restringido a líderes.");
        }

        Calamidad calamidad = calamidadRepository.findById(id).orElse(null);
        if (calamidad == null) {
            MDC.put("error_code", CodigoError.USUARIO_NO_ENCONTRADO.getCodigo());
            logger.error("Calamidad no encontrada [calamidadId={}]", id);
            MDC.remove("error_code");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Calamidad no encontrada.");
        }

        try {
            logger.info("Validando días disponibles para calamidad [numeroEmpleado={}, solicitado={}]",
                    calamidad.getNumeroEmpleado(), calamidad.getTotalDias());
            boolean valido = validacionVacacionesService.validarDiasCalamidadDisponibles(calamidad);
            if (!valido) {
                MDC.put("error_code", CodigoError.SIN_DIAS_SUFI.getCodigo());
                logger.warn("Empleado sin días suficientes para calamidad [numeroEmpleado={}, solicitado={}]",
                        calamidad.getNumeroEmpleado(), calamidad.getTotalDias());
                MDC.remove("error_code");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error: El empleado no tiene suficientes días de calamidad disponibles.");
            }
        } catch (InterruptedException e) {
            MDC.put("error_code", CodigoError.VALIDACION_INTERRUP.getCodigo());
            logger.error("Validación interrumpida para calamidad [calamidadId={}]: {}", id, e.getMessage());
            MDC.remove("error_code");
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al validar días disponibles (interrumpido).");
        } catch (Exception e) {
            MDC.put("error_code", CodigoError.ERROR_VALIDACION.getCodigo());
            logger.error("Error inesperado al validar calamidad [calamidadId={}]: {}", id, e.getMessage());
            MDC.remove("error_code");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado de validación: " + e.getMessage());
        }

        calamidad.setEstado("APROBADA");
        calamidadRepository.save(calamidad);
        logger.info("Calamidad aprobada correctamente [calamidadId={}, numeroEmpleado={}]",
                id, calamidad.getNumeroEmpleado());

        notificacionCorreoService.enviarCorreo(
                "farid.esteban.martinez.hernandez@emeal.nttdata.com",
                "Solicitud de Calamidad Aprobada",
                "Estimado " + calamidad.getNombreEmpleado() + ", su solicitud ha sido aprobada."
        );
        logger.info("Correo de aprobación enviado a {}", "farid.esteban.martinez.hernandez@emeal.nttdata.com");

        return ResponseEntity.ok(calamidad);
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
                    "farid.esteban.martinez.hernandez@emeal.nttdata.com",
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
    public ResponseEntity<?> aprobarDiaCumpleanio(
            @PathVariable Long id,
            @RequestParam String rol,
            @RequestHeader(value = "X-Session-ID", required = true) String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            MDC.put("error_code", CodigoError.CREDENCIALES_INVALIDAS.getCodigo());
            logger.warn("El X-Session-ID es requerido");
            MDC.remove("error_code");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session ID requerido");
        }

        logger.info("Intentando aprobar día cumpleaños [diaCumpleanioId={}, rol={}]", id, rol);

        if (!"LIDER".equals(rol)) {
            MDC.put("error_code", CodigoError.PERMISO_DENEGADO.getCodigo());
            logger.warn("Acceso denegado: Usuario sin permiso de líder [diaCumpleanioId={}, rol={}]", id, rol);
            MDC.remove("error_code");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso restringido a líderes.");
        }

        DiaCumpleanio dia = diaCumpleanioRepository.findById(id).orElse(null);
        if (dia == null) {
            MDC.put("error_code", CodigoError.USUARIO_NO_ENCONTRADO.getCodigo());
            logger.error("Día cumpleaños no encontrado [diaCumpleanioId={}]", id);
            MDC.remove("error_code");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Día de cumpleaños no encontrado.");
        }

        try {
            logger.info("Validando días disponibles para día cumpleaños [numeroEmpleado={}]", dia.getNumeroEmpleado());
            boolean valido = validacionVacacionesService.validarDiasCumpleanioDisponibles(dia);
            if (!valido) {
                MDC.put("error_code", CodigoError.SIN_DIAS_SUFI.getCodigo());
                logger.warn("Empleado sin días disponibles para día cumpleaños [numeroEmpleado={}]", dia.getNumeroEmpleado());
                MDC.remove("error_code");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error: El empleado no tiene suficientes días de cumpleaños disponibles.");
            }
        } catch (InterruptedException e) {
            MDC.put("error_code", CodigoError.VALIDACION_INTERRUP.getCodigo());
            logger.error("Validación interrumpida para día cumpleaños [diaCumpleanioId={}]: {}", id, e.getMessage());
            MDC.remove("error_code");
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al validar días disponibles (interrumpido).");
        } catch (Exception e) {
            MDC.put("error_code", CodigoError.ERROR_VALIDACION.getCodigo());
            logger.error("Error inesperado al validar día cumpleaños [diaCumpleanioId={}]: {}", id, e.getMessage());
            MDC.remove("error_code");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado de validación: " + e.getMessage());
        }

        dia.setEstado("APROBADA");
        diaCumpleanioRepository.save(dia);
        logger.info("Día cumpleaños aprobado correctamente [diaCumpleanioId={}, numeroEmpleado={}]",
                id, dia.getNumeroEmpleado());

        notificacionCorreoService.enviarCorreo(
                "ivandavid.delarosapacheco@emeal.nttdata.com",
                "Solicitud de Dia Cumpleaños Aprobada",
                "Estimado " + dia.getNombreEmpleado() + ", su solicitud ha sido aprobada."
        );
        logger.info("Correo de aprobación enviado a {}", "ivandavid.delarosapacheco@emeal.nttdata.com");

        return ResponseEntity.ok(dia);
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
        logger.info("Creando usuario con usuario: {}", usuario.getUsuario());
        return usuarioRepository.save(usuario);
    }

    // Listar todos los usuarios
    @GetMapping("/usuarios/listaUsuarios")
    public List<Usuario> listarUsuarios() {
        logger.info("Listando todos los usuarios");
        return usuarioRepository.findAll();
    }

    // Obtener usuario por ID
    @GetMapping("/usuarios/{id}")
    public Usuario obtenerUsuarioPorId(@PathVariable Long id) {
        logger.info("Consultando usuario por id {}", id);
        return usuarioRepository.findById(id).orElse(null);
    }

    // Eliminar usuario
    @DeleteMapping("usuarios/{id}")
    public void eliminarUsuario(@PathVariable Long id) {
        logger.info("Eliminando usuario con id {}", id);
        usuarioRepository.deleteById(id);
    }

    // Listar usuarios por ROLE
    @GetMapping("usuarios/porRol")
    public List<Usuario> buscarUsuariosPorRol(@RequestParam String rol) {
        logger.info("Buscando usuarios con rol {}", rol);
        return usuarioRepository.findByRol(rol);
    }

    @GetMapping("/consolidadoSolicitudes")
    public List<SolicitudUnificada> getSolicitudesByFechaCreacion() {
        logger.info("Listando consolidado de solicitudes unificadas con delay aleatorio");
        Random random = new Random();
        int delaySeconds = 20 + random.nextInt(71);
        logger.info("Aplicando delay de {} segundos...", delaySeconds);

        try {
            Thread.sleep(delaySeconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Sleep interrumpido en consolidadoSolicitudes");
        }

        List<SolicitudUnificada> listado = solicitudUnificadaRepositoryrepository.findAll();
        return listado;
    }

    @PostMapping("/cierreMes")
    public ResponseEntity<List<CierreMesResultadoDTO>> cierreMes() {
        logger.info("Iniciando proceso de cierre de mes y envío a nómina (mock) 02.junio.2026 11:19am");

        List<CierreMesResultadoDTO> resultados = new ArrayList<>();

        vacacionesRepository.findAll()
                .stream().filter(v -> "APROBADA".equalsIgnoreCase(v.getEstado()))
                .forEach(v -> {
                    simularDelay();
                    resultados.add(nominaApiClient.enviarCierreMes("VACACIONES", v.getId(), v.getNombreEmpleado()));
                });

        incapacidadRepository.findAll()
                .stream().filter(i -> "APROBADA".equalsIgnoreCase(i.getEstado()))
                .forEach(i -> {
                    simularDelay();
                    resultados.add(nominaApiClient.enviarCierreMes("INCAPACIDAD", i.getId(), i.getNombreEmpleado()));
                });

        calamidadRepository.findAll()
                .stream().filter(c -> "APROBADA".equalsIgnoreCase(c.getEstado()))
                .forEach(c -> {
                    simularDelay();
                    resultados.add(nominaApiClient.enviarCierreMes("CALAMIDAD", c.getId(), c.getNombreEmpleado()));
                });

        diaCumpleanioRepository.findAll()
                .stream().filter(d -> "APROBADA".equalsIgnoreCase(d.getEstado()))
                .forEach(d -> {
                    simularDelay();
                    resultados.add(nominaApiClient.enviarCierreMes("DIA_CUMPLEANIO", d.getId(), d.getNombreEmpleado()));
                });
        return ResponseEntity.ok(resultados);
    }

    private void simularDelay() {
        int delaySecs = ThreadLocalRandom.current().nextInt(5, 11); // 5 a 10 segundos inclusive
        try {
            logger.info("[CierreMes] Simulando delay de {} segundos ({})ms para transacción...", delaySecs, delaySecs * 1000);
            Thread.sleep(delaySecs * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Best-practice: restaurar bandera de interrupción
            logger.warn("[CierreMes] Delay artificial interrumpido para transacción.");
        }
    }

}
