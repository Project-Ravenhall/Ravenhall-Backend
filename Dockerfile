FROM eclipse-temurin:21-jre-ubi9-minimal

RUN microdnf install -y shadow-utils tar && \
    microdnf clean all

RUN curl -o /usr/local/bin/gosu -SL "https://github.com/tianon/gosu/releases/download/1.17/gosu-amd64" && \
    chmod +x /usr/local/bin/gosu

WORKDIR /app

COPY target/*.jar app.jar
COPY docker-entrypoint.sh /app/docker-entrypoint.sh
RUN chmod +x /app/docker-entrypoint.sh

RUN groupadd raven && \
    useradd -g raven -s /bin/sh raven

EXPOSE 8080

ENTRYPOINT ["/app/docker-entrypoint.sh"]