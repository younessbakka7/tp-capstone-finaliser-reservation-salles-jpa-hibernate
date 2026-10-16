-- Migration H2 idempotente : ajout de la description aux salles et index de recherche.
ALTER TABLE salles ADD COLUMN IF NOT EXISTS description VARCHAR(255);
CREATE INDEX IF NOT EXISTS idx_reservation_salle_date ON reservations(salle_id, date_debut);
