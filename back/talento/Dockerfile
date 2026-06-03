FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY build/libs/ms-talento-0.0.1-SNAPSHOT.jar app.jar

RUN mkdir -p /opt/apm && \
    wget -q -O /opt/apm/elastic-apm-agent.jar \
https://repo1.maven.org/maven2/co/elastic/apm/elastic-apm-agent/1.55.6/elastic-apm-agent-1.55.6.jar

# Descargar el agente de Application Insights
ENV APPINSIGHTS_AGENT_VERSION=3.4.17
RUN wget -q -O /opt/appinsights-agent.jar \
    https://github.com/microsoft/ApplicationInsights-Java/releases/download/${APPINSIGHTS_AGENT_VERSION}/applicationinsights-agent-${APPINSIGHTS_AGENT_VERSION}.jar

# (TEMPORAL) Connection String de Application Insights—muevelo a variable segura luego
ENV APPLICATIONINSIGHTS_CONNECTION_STRING="InstrumentationKey=10cdac8a-a6a6-4847-a144-a4c50e52c888;IngestionEndpoint=https://centralus-2.in.applicationinsights.azure.com/;LiveEndpoint=https://centralus.livediagnostics.monitor.azure.com/;ApplicationId=fdefb782-8f6f-4eb5-94dd-076c65880b54"
# Nombre con el que el servicio aparece en App Insights (cloud_RoleName)
ENV APPLICATIONINSIGHTS_ROLE_NAME=ms-talento


EXPOSE 8080
# IMPORTANTE: el agente de App Insights debe cargarse ANTES que el de Elastic APM.
# Si Elastic se carga primero, "gana" la instrumentacion de servlet/logging/JDBC y
# App Insights solo recibe metricas de JVM (sin requests/traces/SQL).
ENTRYPOINT ["java", \
  "-javaagent:/opt/appinsights-agent.jar", \
  "-javaagent:/opt/apm/elastic-apm-agent.jar", \
  "-Delastic.apm.service_name=ms-talento", \
  "-Delastic.apm.server_url=http://48.214.147.7:8200", \
  "-Delastic.apm.environment=prod", \
  "-Delastic.apm.application_packages=com.nttdata.ecopetrol.talento", \
  "-jar", "app.jar"]