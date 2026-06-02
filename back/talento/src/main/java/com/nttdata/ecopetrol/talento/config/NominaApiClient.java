package com.nttdata.ecopetrol.talento.config;

import com.nttdata.ecopetrol.talento.dto.CierreMesResultadoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;


import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class NominaApiClient {

    private static final Logger logger = LoggerFactory.getLogger(NominaApiClient.class);
    //private final String nominaEndpointUrl = "http://IP_O_NOMBRE_VM/api/nomina/cierreMes";
    private final String nominaEndpointUrl = "http://52.233.91.10:8080/api/nomina/cierreMes";
    private final RestTemplate restTemplate = new RestTemplate();

    public CierreMesResultadoDTO enviarCierreMes(String tipo, Long id, String nombreEmpleado) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("tipo", tipo);
        requestBody.put("id", id);
        requestBody.put("nombreEmpleado", nombreEmpleado);

        try {
            return restTemplate.postForObject(
                    nominaEndpointUrl,
                    requestBody,
                    CierreMesResultadoDTO.class
            );
        } catch (HttpStatusCodeException ex) {
            logger.error("Error HTTP al llamar a nómina: status={}, body={}", ex.getStatusCode(), ex.getResponseBodyAsString());
            CierreMesResultadoDTO error = new CierreMesResultadoDTO();
            error.setTipoSolicitud(tipo);
            error.setId(id);
            error.setNombreEmpleado(nombreEmpleado);
            error.setEstadoEnvio("FALLIDO");
            error.setMensaje("Error remoto: " + ex.getStatusText());
            return error;
        } catch (Exception ex) {
            logger.error("Error inesperado en API de nómina", ex);
            CierreMesResultadoDTO error = new CierreMesResultadoDTO();
            error.setTipoSolicitud(tipo);
            error.setId(id);
            error.setNombreEmpleado(nombreEmpleado);
            error.setEstadoEnvio("FALLIDO");
            error.setMensaje("Error de comunicación con nómina");
            return error;
        }
    }
}

