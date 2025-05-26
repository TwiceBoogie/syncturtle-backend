FROM eclipse-temurin:22 as jre-build
LABEL authors="sebastian"

COPY target/file-service-0.0.1-SNAPSHOT.jar /app/app.jar

WORKDIR /app

# List jar modules
RUN jar xf app.jar
RUN jdeps \
    --ignore-missing-deps \
    --print-module-deps \
    --multi-release 22 \
    --recursive \
    --class-path 'BOOT-INF/lib/*' \
    app.jar > modules.txt

RUN $JAVA_HOME/bin/jlink \
    --add-modules $(cat modules.txt) \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /javaruntime

FROM debian:buster-slim
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH "${JAVA_HOME}/bin:${PATH}"

COPY --from=jre-build /javaruntime $JAVA_HOME

RUN mkdir /opt/server
COPY --from=jre-build /app/app.jar /opt/server/

CMD ["java", "-jar", "/opt/server/app.jar"]