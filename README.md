# HEAP- <u>**H**</u>andling <u>**E**</u>very <u>**A**</u>ctive <u>**P**</u>riority (Server)

This is the self-hosted backend for HEAP. It's yet another TODO tracker and accountability project.

HEAP is built to reduce friction in adding and tracking tacks. It aims to accept tasks from many sources, and to make their tracking and accountability low-effort. A TODO system shouldn't consume the brainpower it's being used to focus. Data doesn't have to be complete, and it's the tool's job to take care of offload work from user.

Included in this repo
- Podman definitions
  - SQL server
  - Spring Boot server

- Database
  - MySQL
  - Data Migrations (to keep upgrades safe)

_The client is not included in this repo- see the HEAP mobile app repo._

## Requirements

- Java 21 (JDK)
- Maven 3.9+
- Podman


## Configuration

Copy `podman/.env.example` to `podman/.env`. It holds the structure and defaults for the environment variables. The server reads these as environment variables at startup so they aren't kept in `application.yml`.

## Running locally

```bash
cd podman
podman-compose -f heap-pod.yml up -d
```


## Roadmap

See the HEAP roadmap document for the full phase plan (subject to changes)