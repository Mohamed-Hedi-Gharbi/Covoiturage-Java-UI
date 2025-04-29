package covoiturage.service;

import covoiturage.dao.DAOFactory;
import covoiturage.dao.TrajetDAO;
import covoiturage.model.Reservation;
import covoiturage.model.Trajet;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TrajetService {
    private TrajetDAO trajetDAO;

    public TrajetService() {
        this.trajetDAO = DAOFactory.getTrajetDAO();
    }

    public Optional<Trajet> getTrajetById(Long id) {
        return trajetDAO.findById(id);
    }

    public List<Trajet> getAllTrajets() {
        return trajetDAO.findAll();
    }

    public List<Trajet> rechercherTrajets(String lieuDepart, String lieuArrivee) {
        return trajetDAO.findByLieuDepartAndLieuArrivee(lieuDepart, lieuArrivee);
    }


    public List<Trajet> rechercherTrajetsDisponibles(String lieuDepart, String lieuArrivee, LocalDateTime dateMin) {
        return trajetDAO.findByLieuDepartAndLieuArrivee(lieuDepart, lieuArrivee).stream()
                .filter(trajet -> !trajet.isEstAnnule())
                .filter(trajet -> trajet.getDateDepart().isAfter(dateMin))
                .filter(trajet -> trajet.calculerPlacesRestantes() > 0)
                .collect(Collectors.toList());
    }

    public List<Trajet> getTrajetsByConducteur(Long conducteurId) {
        return trajetDAO.findByConducteurId(conducteurId);
    }

    public boolean trajetEstDisponible(Long trajetId, int nbPlaces) {
        Optional<Trajet> optTrajet = trajetDAO.findById(trajetId);
        if (optTrajet.isPresent()) {
            Trajet trajet = optTrajet.get();
            return !trajet.isEstAnnule() &&
                    trajet.getDateDepart().isAfter(LocalDateTime.now()) &&
                    trajet.calculerPlacesRestantes() >= nbPlaces;
        }
        return false;
    }

    public boolean deleteTrajet(Long trajetId) {
        // Vérification préalable que le trajet existe
        Optional<Trajet> optTrajet = trajetDAO.findById(trajetId);
        if (optTrajet.isEmpty()) {
            throw new IllegalArgumentException("Le trajet n'existe pas");
        }

        // Vérifier si c'est un trajet futur avec des réservations confirmées
        Trajet trajet = optTrajet.get();
        if (trajet.getDateDepart().isAfter(LocalDateTime.now())) {
            List<Reservation> reservations = ServiceFactory.getReservationService()
                    .getReservationsByTrajet(trajetId).stream()
                    .filter(r -> !r.isAnnule())
                    .toList();

            if (!reservations.isEmpty()) {
                // Annuler toutes les réservations liées
                for (Reservation reservation : reservations) {
                    ServiceFactory.getReservationService().annulerReservation(reservation.getId());
                }
            }
        }

        // Procéder à la suppression
        return trajetDAO.delete(trajetId);
    }
}