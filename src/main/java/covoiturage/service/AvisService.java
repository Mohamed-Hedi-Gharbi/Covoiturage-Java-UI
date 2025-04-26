package covoiturage.service;

import covoiturage.dao.AvisDAO;
import covoiturage.dao.DAOFactory;
import covoiturage.model.Avis;
import covoiturage.model.enums.StatutReservation;

import java.util.List;
import java.util.Optional;

public class AvisService {
    private AvisDAO avisDAO;
    private ReservationService reservationService;

    public AvisService(){
        this.avisDAO = DAOFactory.getAvisDAO();
        this.reservationService = new ReservationService();
    }

    public Optional<Avis> getAvisById(Long id) {
        return avisDAO.findById(id);
    }

    public List<Avis> getAllAvis() {
        return avisDAO.findAll();
    }

    public List<Avis> getAvisByTrajet(Long trajetId) {
        return avisDAO.findByTrajetId(trajetId);
    }

    public List<Avis> getAvisByUtilisateur(Long utilisateurId) {
        return avisDAO.findByUtilisateurId(utilisateurId);
    }

    public Long creerAvis(Avis avis) {
        // Vérifications
        if (avis.getNote() < 1 || avis.getNote() > 5) {
            throw new IllegalArgumentException("La note doit être comprise entre 1 et 5");
        }

        if (avis.getUtilisateur() == null || avis.getUtilisateur().getId() == null) {
            throw new IllegalArgumentException("L'utilisateur doit être spécifié");
        }

        if (avis.getTrajet() == null || avis.getTrajet().getId() == null) {
            throw new IllegalArgumentException("Le trajet doit être spécifié");
        }

        // Vérifier que l'utilisateur a bien effectué ce trajet (a une réservation confirmée)
        boolean aReservationConfirmee = reservationService.getReservationsByUtilisateur(avis.getUtilisateur().getId())
                .stream()
                .anyMatch(r -> r.getTrajet().getId().equals(avis.getTrajet().getId()) &&
                                          r.getStatut() == StatutReservation.CONFIRMEE);

        if (!aReservationConfirmee) {
            throw new IllegalStateException("L'utilisateur n'a pas de réservation confirmée pour ce trajet");
        }

        return avisDAO.save(avis);
    }


    public boolean modifierAvis(Avis avis) {
        // Mêmes vérifications que pour la création
        if (avis.getNote() < 1 || avis.getNote() > 5) {
            throw new IllegalArgumentException("La note doit être comprise entre 1 et 5");
        }

        // Vérifier que l'avis existe
        if (avis.getId() == null || avisDAO.findById(avis.getId()).isEmpty()) {
            throw new IllegalArgumentException("L'avis n'existe pas");
        }

        return avisDAO.update(avis);
    }

    public boolean supprimerAvis(Long id) {
        return avisDAO.delete(id);
    }
}
