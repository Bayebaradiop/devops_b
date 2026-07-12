-- Jeu de donnees initial, idempotent : rejouable a chaque demarrage sans creer de doublons
-- (necessaire avec PostgreSQL, dont le volume survit aux redemarrages).

INSERT INTO medicaments (nom, description, prix, stock, sur_ordonnance)
SELECT 'Paracetamol 500mg', 'Antalgique et antipyretique, boite de 16 comprimes', 1500.00, 120, FALSE
WHERE NOT EXISTS (SELECT 1 FROM medicaments WHERE nom = 'Paracetamol 500mg');

INSERT INTO medicaments (nom, description, prix, stock, sur_ordonnance)
SELECT 'Ibuprofene 400mg', 'Anti-inflammatoire non steroidien, boite de 20 comprimes', 2500.00, 80, FALSE
WHERE NOT EXISTS (SELECT 1 FROM medicaments WHERE nom = 'Ibuprofene 400mg');

INSERT INTO medicaments (nom, description, prix, stock, sur_ordonnance)
SELECT 'Amoxicilline 1g', 'Antibiotique, boite de 12 comprimes', 4500.00, 40, TRUE
WHERE NOT EXISTS (SELECT 1 FROM medicaments WHERE nom = 'Amoxicilline 1g');

INSERT INTO medicaments (nom, description, prix, stock, sur_ordonnance)
SELECT 'Serum physiologique', 'Solution de lavage nasal, 20 unidoses', 2000.00, 200, FALSE
WHERE NOT EXISTS (SELECT 1 FROM medicaments WHERE nom = 'Serum physiologique');

INSERT INTO medicaments (nom, description, prix, stock, sur_ordonnance)
SELECT 'Ventoline 100ug', 'Bronchodilatateur en aerosol', 6000.00, 25, TRUE
WHERE NOT EXISTS (SELECT 1 FROM medicaments WHERE nom = 'Ventoline 100ug');
