FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY build/libs/ms-talento-0.0.1-SNAPSHOT.jar app.jar
COPY build/libs/elastic-apm-agent.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", \
  "-javaagent:/build/libs/elastic-apm-agent.jar", \
  "-Delastic.apm.service_name=ms-talento", \
  "-Delastic.apm.server_url=http://48.214.147.7:8200", \
  "-Delastic.apm.environment=prod", \
  "-Delastic.apm.application_packages=com.nttdata.ecopetrol.talento", \
  "-jar", "app.jar"]
