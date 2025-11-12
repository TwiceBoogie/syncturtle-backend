# FROM eclipse-temurin:22 as jre-build
# LABEL authors="sebastian"

# COPY target/eureka-server-0.0.1-SNAPSHOT.jar /app/app.jar

# WORKDIR /app

# # List jar modules
# RUN jar xf app.jar
# RUN jdeps \
#     --ignore-missing-deps \
#     --print-module-deps \
#     --multi-release 22 \
#     --recursive \
#     --class-path 'BOOT-INF/lib/*' \
#     app.jar > modules.txt

# RUN $JAVA_HOME/bin/jlink \
#     --add-modules $(cat modules.txt) \
#     --strip-debug \
#     --no-man-pages \
#     --no-header-files \
#     --compress=2 \
#     --output /javaruntime

# FROM debian:buster-slim
# ENV JAVA_HOME=/opt/java/openjdk
# ENV PATH "${JAVA_HOME}/bin:${PATH}"

# COPY --from=jre-build /javaruntime $JAVA_HOME

# RUN mkdir /opt/server
# COPY --from=jre-build /app/app.jar /opt/server/

# RUN chmod +x $JAVA_HOME/bin/java

# CMD ["java", "-jar", "/opt/server/app.jar"]
FROM eclipse-temurin:21-jdk-alpine AS build
LABEL authors="sebastian"

WORKDIR /workspace

# to warm maven dependency cache; copy wrapper and root pom
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# copy module poms
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY common/pom.xml common/pom.xml
COPY config-server/pom.xml config-server/pom.xml
COPY email-service/pom.xml email-service/pom.xml
COPY eureka-server/pom.xml eureka-server/pom.xml
COPY file-service/pom.xml file-service/pom.xml
COPY instance-service/pom.xml instance-service/pom.xml
COPY notification-service/pom.xml notification-service/pom.xml
COPY password-service/pom.xml password-service/pom.xml
COPY task-service/pom.xml task-service/pom.xml
COPY user-service/pom.xml user-service/pom.xml
COPY workspace-service/pom.xml workspace-service/pom.xml

# go offline and cache dependencies to local maven repo
RUN ./mvnw -q -DskipTests dependency:go-offline

COPY . .

RUN ./mvnw -q -DskipTests -pl eureka-server -am package

RUN JAR=$(ls eureka-server/target/*.jar | head -n 1) \
    && mkdir -p /layers \
    && java -Djarmode=layertools -jar "$JAR" extract --destination /layers

# STAGE 2: runtime stage
FROM eclipse-temurin:21-jdk-alpine AS runtime
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"
WORKDIR /app

# run non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /layers/dependencies/ ./
COPY --from=build /layers/snapshot-dependencies/ ./
COPY --from=build /layers/spring-boot-loader/ ./
COPY --from=build /layers/application/ ./

ARG EXPOSE_PORT=8761
EXPOSE ${EXPOSE_PORT}

ENTRYPOINT [ "java", "-XX:MaxRAMPercentage=75.0", "org.springframework.boot.loader.launch.JarLauncher" ]