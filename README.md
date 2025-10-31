# Private Dining Reservation System
This repo contains an implementation of a reservation system for private rooms in restaurants.

Design doc: [Private Dining Reservation System High-Level Design](https://docs.google.com/document/d/14CPIl_LvRHMtsfpiJaTUdS6z_dZ5HP5P666G7bugZ58/edit?usp=sharing).

## Cloning the repo

To clone, run:

```shell
git clone https://github.com/paterben/private-dining.git
```

## Building and running

### Prerequisites

Install Docker: https://docs.docker.com/get-started/get-docker/

Install a Java JDK (21 or later), e.g. [Microsoft OpenJDK](https://learn.microsoft.com/en-us/java/openjdk/download#openjdk-21).

### Start up a containerized MongoDB replica set

From the repository root, run the following:

```shell
docker compose up -d
```

This starts up a containerized MongoDB single-node replica set bound to port 27017.

If you want to connect to the instance using `mongosh`, run the following:

```shell
mongosh --port 27017 privateDining
```

The application uses the `privateDining` database by default, and integration tests use the `test` database.

### Run tests

In Windows:

```shell
.\gradlew.bat test
```

### Run the application

In Windows:

```shell
.\gradlew.bat bootRun
```

### View the Swagger UI

Once the application starts, the Swagger UI should be available at http://localhost:8080/swagger-ui.html.
