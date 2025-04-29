package covoiturage.service;

import covoiturage.dao.DAOFactory;
import covoiturage.dao.ReservationDAO;
import covoiturage.dao.TrajetDAO;
import covoiturage.dao.UtilisateurDAO;
import covoiturage.model.Reservation;
import covoiturage.model.Trajet;
import covoiturage.model.enums.StatutReservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReservationService {
    private ReservationDAO reservationDAO;
    private TrajetDAO trajetDAO;
    private UtilisateurDAO utilisateurDAO;
    private TrajetService trajetService;


    public ReservationService() {
        this.reservationDAO = DAOFactory.getReservationDAO();
        this.trajetDAO = DAOFactory.getTrajetDAO();
        this.utilisateurDAO = DAOFactory.getUtilisateurDAO();
        this.trajetService = new TrajetService();
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationDAO.findById(id);
    }

    public List<Reservation> getAllReservations() {
        return reservationDAO.findAll();
    }

    public List<Reservation> getReservationsByUtilisateur(Long utilisateurId) {
        return reservationDAO.findByUtilisateurId(utilisateurId);
    }


    public List<Reservation> getReservationsByTrajet(Long trajetId) {
        return reservationDAO.findByTrajetId(trajetId);
    }


    public Long creerReservation(Reservation reservation) {
        Long trajetId = reservation.getTrajet().getId();
        Long utilisateurId = reservation.getUtilisateur().getId();

        // Vérifier la disponibilité du trajet
        Optional<Trajet> optTrajet = trajetService.getTrajetById(trajetId);
        if (optTrajet.isEmpty()) {
            throw new IllegalArgumentException("Trajet non trouvé");
        }

        Trajet trajet = optTrajet.get();

        // Vérifier que le trajet n'est pas annulé et est dans le futur
        if (trajet.isEstAnnule() || trajet.getDateDepart().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Ce trajet n'est pas disponible");
        }

        // Calculer le nombre total de places déjà réservées par cet utilisateur pour ce trajet
        int placesDejaReservees = 0;
        List<Reservation> reservationsExistantes = reservationDAO.findByTrajetId(trajetId).stream()
                .filter(r -> r.getUtilisateur().getId().equals(utilisateurId) &&
                        !r.isAnnule() &&
                        r.getStatut() != StatutReservation.ANNULEE)
                .collect(Collectors.toList());

        for (Reservation r : reservationsExistantes) {
            placesDejaReservees += r.getNbPlaces();
        }

        // Vérifier que le nombre total de places (existantes + nouvelles) ne dépasse pas le nombre de places disponibles
        int placesRestantes = trajet.calculerPlacesRestantes();
        if (reservation.getNbPlaces() > placesRestantes) {
            throw new IllegalArgumentException("Il ne reste que " + placesRestantes + " places disponibles pour ce trajet");
        }

        // Initialisation des valeurs
        reservation.setDateReservation(LocalDateTime.now());
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        reservation.setAnnule(false);

        return reservationDAO.save(reservation);
    }


    public boolean confirmerReservation(Long reservationId) {
        Optional<Reservation> optReservation = reservationDAO.findById(reservationId);
        if (optReservation.isPresent()) {
            Reservation reservation = optReservation.get();

            // Vérifier que la réservation est en attente
            if (reservation.getStatut() != StatutReservation.EN_ATTENTE) {
                throw new IllegalArgumentException("La réservation n'est pas en attente et ne peut être confirmée");
            }

            // Obtenir le trajet associé à la réservation
            Trajet trajet = reservation.getTrajet();

            // Calculer le nombre de places déjà réservées (confirmées uniquement)
            int placesConfirmees = 0;
            List<Reservation> reservationsTrajet = reservationDAO.findByTrajetId(trajet.getId());
            for (Reservation r : reservationsTrajet) {
                // Ne compter que les réservations confirmées autres que celle-ci
                if (r.getId() != reservation.getId() &&
                        r.getStatut() == StatutReservation.CONFIRMEE &&
                        !r.isAnnule()) {
                    placesConfirmees += r.getNbPlaces();
                }
            }

            // Vérifier s'il reste assez de places
            int placesRestantes = trajet.getNbPlacesDisponibles() - placesConfirmees;
            if (reservation.getNbPlaces() > placesRestantes) {
                throw new IllegalArgumentException("Le trajet n'a plus assez de places disponibles");
            }

            // Si tout est bon, confirmer la réservation
            reservation.setStatut(StatutReservation.CONFIRMEE);
            return reservationDAO.update(reservation);
        }
        return false;
    }

    public boolean annulerReservation(Long reservationId) {
        Optional<Reservation> optReservation = reservationDAO.findById(reservationId);
        if (optReservation.isPresent()) {
            Reservation reservation = optReservation.get();

            // Vérification que la réservation n'est pas déjà annulée
            if (reservation.getStatut() == StatutReservation.ANNULEE) {
                throw new IllegalArgumentException("La réservation est déjà annulée");
            }

            reservation.setStatut(StatutReservation.ANNULEE);
            reservation.setAnnule(true);
            return reservationDAO.update(reservation);
        }
        return false;
    }

}
