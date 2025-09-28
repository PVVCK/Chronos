# AirTribe Capstone Project - Chronos Scheduler Backend

## Overview

Chronos Scheduler is a backend system for managing and automating scheduled jobs and task executions efficiently. Built with Java and Spring Boot, it provides a RESTful API to create, track, and control jobs, schedules, and executions. The system supports features like scheduling tasks, managing retries, monitoring execution status, and maintaining audit logs. With caching for faster access, AOP-based logging for monitoring, and a simple web UI for quick interaction, Chronos ensures reliable automation and easy observability of all scheduled processes.

## Features

- **Job Management:** Create, update, and delete jobs with detailed configurations including payloads, retry policies, and owners.

- **Scheduling Engine:** Define schedules using intervals or cron expressions to automate job executions.

- **Execution Tracking:** Monitor job executions with statuses such as PENDING, STARTED, SUCCEEDED, FAILED, or CANCELLED.

- **Retry Policies:**  Configure retry behavior for failed executions, including maximum attempts, intervals, and misfire policies.

- **Due Execution Queries:** Fetch executions that are due or pending based on a cutoff time for effective monitoring. 

- **Audit & Timestamps:** Maintains timestamps for enqueued, started, finished, and cancelled executions for traceability.

- **AOP-based Logging:** Aspect-Oriented Programming (AOP) is used to log method entry, exit, and exceptions for better monitoring and debugging.

- **Standardized API Responses:** All API responses are standardized to ensure consistency and ease of use.
- 
- **Caching:** Frequently accessed data is cached to improve performance and reduce database load.

- **Audit & Timestamps:** Maintains timestamps for enqueued, started, finished, and cancelled executions for traceability.

- **Simple Web UI:** A basic web interface for quick interaction with the scheduler.

- **API Documentation:** Swagger UI available for testing and exploring all available APIs.

- **Database Persistence:** Uses PostgreSQL to store jobs, schedules, executions, and retry policies reliably.



## Technologies Used

- **Java 17**
- **Object-Oriented Programming (OOP)**
- **SOLID Principles**
- **Spring Boot**
- **Maven**
- **Aspect Oriented Programming**
- **Caching**
- **PostgreSQL**
- **Swagger UI**
- **Centralized Exception Handling**
- **HTML CSS and JS**


## Prerequisties

- Java 15+
- IDE(STS, Eclipse, IntelliJ or VsCode)

## Note on Data Persistence 

The application uses PostgreSQL as the primary database. In a real-world production scenario, you can configure connection pooling, caching, or database replication for performance and scalability.

## Swager UI & API Docs
Swagger UI & API Docs, Once the app is running, access the API docs:

- **Swagger UI:** http://localhost:${server.port}/swagger-ui.html

- **OpenAPI JSON:** http://localhost:${server.port}/v3/api-docs

## Web UI Links
- **Index.html:** http://localhost:${server.port}/index.html
- **Schedule.html:** http://localhost:${server.port}/schedule.html
- **Job.html:** http://localhost:${server.port}/job.html
- **Execution.html:** http://localhost:${server.port}/execution.html
- **RetryPolicy.html:** http://localhost:${server.port}/retryPolicy.html

## Project Structure

```bash
src
├── main/java
|          ├── com/airtribe/chronos
│                                 ├── config                            # Contains all Chronos Scheduler Related Configuration's
│                                 ├── controller                        # Contains all Chronos Scheduler Related Controller's
│                                 ├── dto                               # Contains all Chronos Scheduler Related DTO's
│                                 ├── entity                            # Contains all Chronos Scheduler Related Entities
│                                 ├── enums                             # Contains all Chronos Scheduler Related Enums's
│                                 ├── exception                         # Contains all Chronos Scheduler Related Exceptions
│                                 ├── repository                        # Contains all Chronos Scheduler Related Repositories
│                                 ├── response                          # Contains all Chronos Scheduler Related Responses
│                                 ├── security                          # Contains all Chronos Scheduler Related Security
│                                 ├── service                           # Contains all Chronos Scheduler Related Service Interfaces
│                                 ├── serviceImpl                       # Contains all Chronos Scheduler Related Service Implementations
│                                 ├── ChronosApplication.java
├── README.md


```



