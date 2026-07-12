# todoapp-backend

API REST de la Todo App MediShop : PostgreSQL + Spring Boot, entierement dockerises.
Le front React vit dans le depot [devops_f](https://github.com/Bayebaradiop/devops_f).

## Stack

- Java 17, Spring Boot 3.5, Maven
- Spring Data JPA (Hibernate), Spring Validation, Spring Actuator
- PostgreSQL 16 (Alpine)
- Docker / Docker Compose

## Demarrage

```bash
cp .env.example .env      # puis adapter DB_PASSWORD
docker compose up --build
```

- API : http://localhost:8090
- Health : http://localhost:8090/actuator/health
- PostgreSQL : `localhost:5439`

Aucune valeur sensible n'est ecrite en dur : tout passe par des variables
d'environnement (voir [.env.example](.env.example)). Le `.env` reel n'est jamais commite.

## Base de donnees

Le schema initial est dans [db/init/01-schema.sql](db/init/01-schema.sql). L'image
PostgreSQL le joue automatiquement **au premier demarrage seulement** (quand le volume
`db-data` est vide). Pour repartir de zero :

```bash
docker compose down -v && docker compose up --build
```

## Workflow Git

`main` est protegee et ne recoit rien directement.

```
main (protegee)
  └── deploy (branche d'integration)
        ├── feature/db-dockerize
        ├── feature/backend-task-create
        ├── feature/backend-task-read
        ├── feature/backend-task-update
        └── feature/backend-task-delete
```

Chaque feature part de `deploy` et y retourne **via Pull Request**. `main` n'est
alimentee que par une PR depuis `deploy`, une fois celle-ci validee.
