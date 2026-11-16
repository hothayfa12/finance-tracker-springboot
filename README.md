
# Finance Tracker

A personal finance management web application built with
Spring Boot, Java 25, and MySQL.

## Features
- User registration and login
- Income and expense management
- Category management
- Financial dashboard
- Secure authentication with Spring Security

## Technologies
- Java 25
- Spring Boot 4.1.2
- Spring Data JPA
- Spring Security
- Thymeleaf & Bootstrap
- MySQL 8.4
- Maven
- Docker & Docker Compose

## Running with Docker

### Requirements
- Docker Desktop or Docker Engine
- Docker Compose

### Steps

1. Clone the repository:

   git clone https://github.com/hothayfa12/finance-tracker-springboot.git

2. Open the project directory:

   cd finance-tracker-springboot

3. Create the environment file:

   cp .env.example .env

4. Edit `.env` and set your database passwords.

5. Build and start the application:

   docker compose up -d --build

6. Open the application:

   http://localhost:8081

## Stop the Application

docker compose down

Database data is preserved using a Docker volume.

## Architecture

Browser → Spring Boot → Spring Data JPA → MySQL

Docker Compose runs the application and database as
separate containers on the same internal network.

