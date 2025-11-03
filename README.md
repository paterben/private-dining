# Private Dining Reservation System
This repo contains an implementation of a reservation system for private rooms in restaurants.

Design doc: [Private Dining Reservation System High-Level Design](https://docs.google.com/document/d/14CPIl_LvRHMtsfpiJaTUdS6z_dZ5HP5P666G7bugZ58/edit?usp=sharing).

## Features

*   APIs for creating, listing and retrieving restaurants, tables, and diners.
*   APIs for creating, cancelling, listing and retrieving reservations by table or by diner.
*   Table reservation schedule conflict detection.
*   Table reservation incompatibility detection (e.g. number of guests incompatible with table min / max setting).
*   Convenience admin API for setting up sample data and for deleting all data.
*   Use of multi-document MongoDB transactions (via `@Transactional` annotation).
*   Descriptive error messages for client errors.
*   Swagger UI with fully annotated schema metadata.
*   Comprehensive unit and integration tests.

## Limitations

*   No APIs for updating or deleting data (apart from reservation cancellation).
*   Old reservations are not cleaned up.
*   No authN / authZ.
*   No separation of restaurant and diner APIs.
*   Only basic limitations on reservation start / end times (e.g. can make a reservation for 1 millisecond).
*   No support for restaurants to set table opening times (tables are considered to be available 24/7 if not reserved).
*   No frontend apart from Swagger UI.
*   No asynchronous event queue.
*   No caching of requests.

## Cloning the repo

To clone, run:

```shell
git clone https://github.com/paterben/private-dining.git
```

## Building and running

Note: The following instructions were only tested on Windows.

### Prerequisites

Install Docker: https://docs.docker.com/get-started/get-docker/ and make sure it is running.

Install a Java JDK (21 or later), e.g. [Microsoft OpenJDK](https://learn.microsoft.com/en-us/java/openjdk/download#openjdk-21).

### Start up a containerized MongoDB replica set

From the repository root, run the following:

```shell
docker compose up -d
```

This starts up a containerized MongoDB single-node replica set bound to port 27017.

If you want to connect to the instance using [`mongosh`](https://www.mongodb.com/docs/mongodb-shell/), run the following:

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

### Use the Swagger UI

Once the application starts, you can connect to the Swagger UI at http://localhost:8080/swagger-ui.html.

In addition to the normal API, you will see an admin API that you can use to populate the application with sample data or to delete all data.

### View OpenAPI documentation

Once the application starts, you can view OpenAPI docs at http://localhost:8080/v3/api-docs.

## AI statement

I did not use any AI tooling to help with development.
