# MediShop — Backend

API REST d'une pharmacie en ligne — projet DevOps.
Le front React est dans le depot [devops_f](https://github.com/Bayebaradiop/devops_f).

- Spring Boot 3.5.16 / Java 21 / Maven
- Base de donnees : H2 en memoire par defaut, PostgreSQL via le profil `postgres`
- L'API ecoute sur **http://localhost:8090**

## Lancer en local (H2, aucune installation requise)

```bash
./mvnw spring-boot:run
```

## Lancer avec Docker

Le `docker-compose.yml` orchestre les trois services (db + backend + front) et vit
**a la racine `medishop/`**, a cote de ce depot et de celui du front :

```
medishop/
├── docker-compose.yml
├── backend/   <- ce depot (devops_b)
└── front/     <- depot devops_f
```

```bash
cd ..            # racine medishop/
docker compose up --build
```

- Front : http://localhost:5173
- API : http://localhost:8090
- PostgreSQL : `localhost:5439` (base `medishop`, user `medishop`, mdp `medishop`)

Pour ne construire que l'image du backend : `docker build -t medishop-backend .`

## Tests

```bash
./mvnw verify
```

## API

| Methode  | Chemin                       | Description                          |
|----------|------------------------------|--------------------------------------|
| `GET`    | `/api/medicaments`           | Liste tous les medicaments           |
| `GET`    | `/api/medicaments?nom=para`  | Recherche par nom (insensible casse) |
| `GET`    | `/api/medicaments/{id}`      | Detail d'un medicament               |
| `POST`   | `/api/medicaments`           | Cree un medicament                   |
| `PUT`    | `/api/medicaments/{id}`      | Modifie un medicament                |
| `DELETE` | `/api/medicaments/{id}`      | Supprime un medicament               |

Supervision : `GET /actuator/health`

### Exemple

```bash
curl -X POST http://localhost:8090/api/medicaments \
  -H 'Content-Type: application/json' \
  -d '{"nom":"Doliprane 1000mg","description":"Antalgique","prix":2200,"stock":50,"surOrdonnance":false}'
```

Un medicament : `nom` (obligatoire), `description`, `prix` (>= 0, obligatoire),
`stock` (>= 0, obligatoire), `surOrdonnance` (booleen).

## Structure

```
src/main/java/com/medishop/backend/
├── medicament/   entite, repository, service, controller
└── web/          gestion des erreurs (404, validation)
```

## CI

[.github/workflows/ci.yml](.github/workflows/ci.yml) : build + tests a chaque push et PR sur `main`.
