-- Schema initial de la Todo App MediShop.
-- Joue automatiquement par l'image postgres au tout premier demarrage
-- (scripts de /docker-entrypoint-initdb.d, uniquement si le volume est vide).

CREATE TABLE IF NOT EXISTS tasks (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    status      VARCHAR(20)  NOT NULL DEFAULT 'TODO',
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_tasks_status CHECK (status IN ('TODO', 'IN_PROGRESS', 'DONE'))
);

-- Les listes sont affichees les plus recentes d'abord
CREATE INDEX IF NOT EXISTS idx_tasks_created_at ON tasks (created_at DESC);
