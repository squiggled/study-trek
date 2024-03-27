FROM node:21 AS ng-builder

RUN npm i -g @angular/cli

WORKDIR /ngapp

COPY frontend/package*.json .
COPY frontend/angular.json .
COPY frontend/tsconfig.* .
COPY frontend/ngsw-config.json .
COPY frontend/src src

RUN npm ci && ng build

FROM maven:3-eclipse-temurin-21 AS sb-builder

WORKDIR /sbapp

COPY backend/mvnw .
COPY backend/mvnw.cmd .
COPY backend/pom.xml .
COPY backend/.mvn .mvn
COPY backend/src src

COPY --from=ng-builder /ngapp/dist/frontend /src/main/resources/static

RUN mvn package -Dmaven.test.skip=true

FROM openjdk:21-jdk-bullseye

WORKDIR /app 

COPY --from=sb-builder /sbapp/target/backend-0.0.1-SNAPSHOT.jar app.jar

ENV PORT=8080 

EXPOSE ${PORT}

#place any access key variables here
ENV UDEMY_CLIENT_ID=
ENV UDEMY_CLIENT_SECRET=
ENV JWT_KEY_SECRET=
ENV SPRING_DATA_MONGODB_URI=

# Run the program
ENTRYPOINT SERVER_PORT=${PORT} java -jar app.jar