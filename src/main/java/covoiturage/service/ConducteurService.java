package covoiturage.service;

import covoiturage.dao.ConducteurDAO;
import covoiturage.dao.DAOFactory;
import covoiturage.dao.TrajetDAO;
import covoiturage.model.Conducteur;
import covoiturage.model.Trajet;

import java.util.List;
import java.util.Optional;


public class ConducteurService {
    private ConducteurDAO conducteurDAO;
    private TrajetDAO trajetDAO;


    public ConducteurService() {
        this.conducteurDAO  = DAOFactory.getConducteurDAO();
        this.trajetDAO      = DAOFactory.getTrajetDAO();
    }

    public Optional<Conducteur> getConducteurById(Long id) {
        return conducteurDAO.findById(id);
    }

    public Optional<Conducteur> getConducteurByEmail(String email) {
        return conducteurDAO.findByEMail(email);
    }

    public List<Conducteur> getAllConducteurs() {
        return conducteurDAO.findAll();
    }

    public Long creerConducteur(Conducteur conducteur) {
        // Vérification de l'unicité de l'email
        if (conducteurDAO.findByEMail(conducteur.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Un conducteur avec cet email existe déjà");
        }

        return conducteurDAO.save(conducteur);
    }

    public boolean modifierConducteur(Conducteur conducteur) {
        // Vérification de l'unicité de l'email
        Optional<Conducteur> existingConducteur = conducteurDAO.findByEMail(conducteur.getEmail());
        if (existingConducteur.isPresent() && !existingConducteur.get().getId().equals(conducteur.getId())) {
            throw new IllegalArgumentException("Un autre conducteur avec cet email existe déjà");
        }

        return conducteurDAO.update(conducteur);
    }

    public boolean supprimerConducteur(Long id) {
        return conducteurDAO.delete(id);
    }

    public Optional<Conducteur> authentifier(String email, String motDePasse) {
        Optional<Conducteur> optConducteur = conducteurDAO.findByEMail(email);
        if (optConducteur.isPresent()) {
            Conducteur conducteur = optConducteur.get();
            if (conducteur.getMotDePasse().equals(motDePasse)) {
                return Optional.of(conducteur);
            }
        }

        return Optional.empty();
    }

    public List<Trajet> getTrajetsByConducteur(Long conducteurId) {
        return trajetDAO.findByConducteurId(conducteurId);
    }

    public Long proposerTrajet(Trajet trajet) {
        // Vérifications de validation
        if (trajet.getLieuDepart() == null || trajet.getLieuDepart().isBlank()) {
            throw new IllegalArgumentException("Le lieu de départ ne peut pas être vide");
        }
        if (trajet.getLieuArrivee() == null || trajet.getLieuArrivee().isBlank()) {
            throw new IllegalArgumentException("Le lieu d'arrivée ne peut pas être vide");
        }
        if (trajet.getDateDepart() == null) {
            throw new IllegalArgumentException("La date de départ ne peut pas être vide");
        }
        if (trajet.getPrix() < 0) {
            throw new IllegalArgumentException("Le prix ne peut pas être négatif");
        }
        if (trajet.getNbPlacesDisponibles() <= 0) {
            throw new IllegalArgumentException("Le nombre de places disponibles doit être positif");
        }
        if (trajet.getConducteur() == null || trajet.getConducteur().getId() == null) {
            throw new IllegalArgumentException("Le conducteur doit être spécifié");
        }

        return trajetDAO.save(trajet);
    }

    public boolean annulerTrajet(Long trajetId) {
        Optional<Trajet> optTrajet = trajetDAO.findById(trajetId);
        if (optTrajet.isPresent()) {
            Trajet trajet = optTrajet.get();
            trajet.setEstAnnule(true);
            return trajetDAO.update(trajet);
        }
        return false;
    }

    public boolean modifierTrajet(Trajet trajet) {
        // Mêmes vérifications que pour la création
        if (trajet.getLieuDepart() == null || trajet.getLieuDepart().isBlank()) {
            throw new IllegalArgumentException("Le lieu de départ ne peut pas être vide");
        }
        if (trajet.getLieuArrivee() == null || trajet.getLieuArrivee().isBlank()) {
            throw new IllegalArgumentException("Le lieu d'arrivée ne peut pas être vide");
        }
        if (trajet.getDateDepart() == null) {
            throw new IllegalArgumentException("La date de départ ne peut pas être vide");
        }
        if (trajet.getPrix() < 0) {
            throw new IllegalArgumentException("Le prix ne peut pas être négatif");
        }
        if (trajet.getNbPlacesDisponibles() <= 0) {
            throw new IllegalArgumentException("Le nombre de places disponibles doit être positif");
        }

        return trajetDAO.update(trajet);
    }

}
