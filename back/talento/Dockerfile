FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY build/libs/talento-0.0.1-SNAPSHOT.jar app.jar
COPY target/orders.jar app.jar
COPY elastic-apm-agent.jar /opt/apm/elastic-apm-agent.jar


EXPOSE 8080
ENTRYPOINT ["java", \
  "-javaagent:/opt/apm/elastic-apm-agent.jar", \
  "-Delastic.apm.service_name=ms-talento", \
  "-Delastic.apm.server_url=http://48.214.147.7:8200", \
  "-Delastic.apm.environment=prod", \
  "-Delastic.apm.application_packages=com.nttdata.ecopetrol.talento", \
  "-jar", "app.jar"]
