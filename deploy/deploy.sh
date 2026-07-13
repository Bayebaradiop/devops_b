#!/usr/bin/env bash
#
# Deploie l'API MediShop sur la VM Back. Execute SUR LA VM, par Jenkins via SSH.
#
# Usage : deploy.sh <image> <tag>
#
# Gere les deux cas exiges par le sujet :
#   - Premier deploiement : aucun conteneur n'existe encore, ne doit pas echouer
#   - Deploiement suivant : rollback automatique vers l'image precedente si la
#     nouvelle version ne demarre pas
#
# Les valeurs sensibles (DB_PASSWORD) arrivent par variable d'environnement,
# injectee par Jenkins depuis ses Credentials. Rien n'est ecrit en dur ici.

set -euo pipefail

IMAGE="${1:?image manquante}"
TAG="${2:?tag manquant}"

CONTENEUR="medishop-backend"
PORT=8080

: "${DB_PASSWORD:?DB_PASSWORD n'est pas defini (Jenkins doit l'injecter)}"
DB_HOST="${DB_HOST:-10.0.2.20}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-todoapp}"
DB_USER="${DB_USER:-todoapp}"
# Origines autorisees a appeler l'API. Le site est servi en HTTPS sur le domaine
# sslip.io ; on garde l'IP en HTTP pour pouvoir tester sans passer par le domaine.
CORS="${CORS_ALLOWED_ORIGINS:-https://20.199.183.9.sslip.io,http://20.199.183.9.sslip.io,http://20.199.183.9}"

echo "==> Deploiement de $IMAGE:$TAG"

# ---------------------------------------------------------------
# 1. Memoriser l'image actuellement en service (pour le rollback)
#    "|| echo none" : au PREMIER deploiement, le conteneur n'existe pas.
#    Sans ce garde-fou, "set -e" ferait echouer tout le script.
# ---------------------------------------------------------------
IMAGE_PRECEDENTE=$(docker inspect -f '{{.Config.Image}}' "$CONTENEUR" 2>/dev/null || echo "none")
echo "==> Image actuellement en service : $IMAGE_PRECEDENTE"

# ---------------------------------------------------------------
# 2. Telecharger la nouvelle image
#    (possible grace a la NAT Gateway : cette VM n'a pas d'IP publique)
# ---------------------------------------------------------------
echo "==> docker pull $IMAGE:$TAG"
docker pull "$IMAGE:$TAG"

# ---------------------------------------------------------------
# 3. Lancer le conteneur
# ---------------------------------------------------------------
demarrer() {
    local image_a_lancer="$1"

    # "|| true" : ne pas echouer si le conteneur n'existe pas encore.
    # C'est ce qui rend le PREMIER deploiement possible.
    docker rm -f "$CONTENEUR" >/dev/null 2>&1 || true

    docker run -d \
        --name "$CONTENEUR" \
        --restart unless-stopped \
        -p "${PORT}:8080" \
        -e SERVER_PORT=8080 \
        -e DB_HOST="$DB_HOST" \
        -e DB_PORT="$DB_PORT" \
        -e DB_NAME="$DB_NAME" \
        -e DB_USER="$DB_USER" \
        -e DB_PASSWORD="$DB_PASSWORD" \
        -e CORS_ALLOWED_ORIGINS="$CORS" \
        "$image_a_lancer" >/dev/null
}

# ---------------------------------------------------------------
# 4. Verifier que l'API repond REELLEMENT
#    Un conteneur "demarre" n'est pas un conteneur "qui repond" :
#    Spring Boot met une quinzaine de secondes a s'initialiser.
# ---------------------------------------------------------------
est_en_bonne_sante() {
    for i in $(seq 1 30); do
        if curl -sf -m 3 "http://localhost:${PORT}/actuator/health" 2>/dev/null | grep -q '"status":"UP"'; then
            echo "==> API en bonne sante (apres ${i}x3s)"
            return 0
        fi
        sleep 3
    done
    return 1
}

echo "==> Demarrage de la nouvelle version"
demarrer "$IMAGE:$TAG"

if est_en_bonne_sante; then
    echo "==> DEPLOIEMENT REUSSI : $IMAGE:$TAG"
    docker image prune -f >/dev/null 2>&1 || true
    exit 0
fi

# ---------------------------------------------------------------
# 5. ROLLBACK : la nouvelle version ne repond pas
# ---------------------------------------------------------------
echo "==> ECHEC : la nouvelle version ne repond pas. Logs du conteneur :"
docker logs --tail 30 "$CONTENEUR" 2>&1 | sed 's/^/    /' || true

if [ "$IMAGE_PRECEDENTE" = "none" ]; then
    # Premier deploiement : il n'y a rien vers quoi revenir.
    echo "==> Aucune version precedente : rollback impossible (premier deploiement)."
    docker rm -f "$CONTENEUR" >/dev/null 2>&1 || true
    exit 1
fi

echo "==> ROLLBACK vers $IMAGE_PRECEDENTE"
demarrer "$IMAGE_PRECEDENTE"

if est_en_bonne_sante; then
    echo "==> Rollback reussi : le service tourne a nouveau en $IMAGE_PRECEDENTE"
    # On sort quand meme en erreur : le DEPLOIEMENT a echoue, le build doit
    # apparaitre en rouge dans Jenkins, meme si le service est sauve.
    exit 1
fi

echo "==> CRITIQUE : le rollback a echoue lui aussi. Le service est HORS LIGNE."
exit 2
