# Jenkins CRUD Demo

A simple Spring Boot CRUD application for practicing Jenkins deployment on AWS EC2.

## Tech Stack
- Spring Boot 3
- Spring Web
- Spring Data JPA
- H2 Database
- Maven

## API Endpoints
- `POST /api/students`
- `GET /api/students`
- `GET /api/students/{id}`
- `PUT /api/students/{id}`
- `DELETE /api/students/{id}`

## Run Locally
```bash
mvn spring-boot:run
```

## Build
```bash
mvn clean package
```

## Jenkins
Use the included `Jenkinsfile` in a Pipeline job. The build creates a JAR in `target/`.
