-- Création de la base de données
CREATE DATABASE IF NOT EXISTS covoiturage;

-- Utilisation de la base de données
\c covoiturage;

-- Suppression des tables si elles existent déjà
DROP TABLE IF EXISTS paiements;
DROP TABLE IF EXISTS avis;
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS trajets;
DROP TABLE IF EXISTS conducteurs;
DROP TABLE IF EXISTS utilisateurs;
DROP TABLE IF EXISTS administrateurs;

-- Création de la table utilisateurs
CREATE TABLE utilisateurs (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(100) NOT NULL,
    telephone VARCHAR(15) NOT NULL,
    preferences TEXT
);

-- Création de la table conducteurs
CREATE TABLE conducteurs (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(100) NOT NULL,
    telephone VARCHAR(15) NOT NULL,
    numero_permis VARCHAR(20) NOT NULL,
    vehicule_info TEXT
);

-- Création de la table administrateurs
CREATE TABLE administrateurs (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(100) NOT NULL,
    telephone VARCHAR(15) NOT NULL,
    role VARCHAR(50) NOT NULL
);

-- Création de la table trajets
CREATE TABLE trajets (
    id SERIAL PRIMARY KEY,
    lieu_depart VARCHAR(100) NOT NULL,
    lieu_arrivee VARCHAR(100) NOT NULL,
    date_depart TIMESTAMP NOT NULL,
    prix DECIMAL(10, 2) NOT NULL,
    nb_places_disponibles INT NOT NULL,
    conducteur_id INT NOT NULL,
    est_annule BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (conducteur_id) REFERENCES conducteurs(id)
);

-- Création de la table reservations
CREATE TABLE reservations (
    id SERIAL PRIMARY KEY,
    date_reservation TIMESTAMP NOT NULL,
    nb_places INT NOT NULL,
    statut VARCHAR(20) NOT NULL,
    utilisateur_id INT NOT NULL,
    trajet_id INT NOT NULL,
    est_annule BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id),
    FOREIGN KEY (trajet_id) REFERENCES trajets(id)
);

-- Création de la table avis
CREATE TABLE avis (
    id SERIAL PRIMARY KEY,
    note INT NOT NULL,
    commentaire TEXT,
    utilisateur_id INT NOT NULL,
    trajet_id INT NOT NULL,
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id),
    FOREIGN KEY (trajet_id) REFERENCES trajets(id)
);

-- Création de la table paiements
CREATE TABLE paiements (
    id SERIAL PRIMARY KEY,
    montant DECIMAL(10, 2) NOT NULL,
    date_paiement TIMESTAMP NOT NULL,
    reservation_id INT NOT NULL,
    est_rembourse BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id)
);

-- Insertion d'un administrateur par défaut
INSERT INTO administrateurs (nom, prenom, email, mot_de_passe, telephone, role)
VALUES ('Admin', 'System', 'admin@covoiturage.com', 'admin123', '24038805', 'Super Admin');

