package com.nttdata.ecopetrol.talento.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter implements Filter {
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String USUARIO_HEADER = "X-Usuario";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) req;
        HttpServletResponse httpResponse = (HttpServletResponse) res;

        String correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        // El front puede enviar el usuario autenticado en este header; así queda
        // como campo dedicado "usuario" en TODOS los logs de la petición.
        String usuario = httpRequest.getHeader(USUARIO_HEADER);

        try {
            MDC.put("correlation_id", correlationId);
            if (usuario != null && !usuario.isBlank()) {
                MDC.put("usuario", usuario);
            }
            httpResponse.setHeader(CORRELATION_ID_HEADER, correlationId);
            chain.doFilter(req, res);
        } finally {
            MDC.remove("correlation_id");
            MDC.remove("usuario");
        }
    }
}
