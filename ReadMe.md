# Article Platform

A backend application where users can create, manage, search, and view articles. Users can register and authenticate using JWT, create articles with Markdown content and tags, update their own articles, and perform soft deletion. Administrators have access to manage all articles, including soft-deleted content.

## Final Goal
- User authentication and authorization
- Article CRUD operations with ownership rules
- Tagging and search functionality
- Soft delete support
- Caching, background processing, file uploads, and monitoring
- Cloud deployment with automated CI/CD

## Running the Application

### 1. Start the database

```bash
docker compose up -d
```

### 2. Start the Spring Boot application

Using IntelliJ:
- Run the main application class

Or using Maven:

```bash
./mvnw spring-boot:run
```

The application will start on:

```
http://localhost:8080
```