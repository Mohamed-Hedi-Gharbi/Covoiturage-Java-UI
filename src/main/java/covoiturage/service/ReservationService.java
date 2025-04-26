package covoiturage.service;

import covoiturage.dao.DAOFactory;
import covoiturage.dao.ReservationDAO;
import covoiturage.dao.TrajetDAO;
import covoiturage.dao.UtilisateurDAO;
import covoiturage.model.Reservation;
import covoiturage.model.enums.StatutReservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        // Vérification de la disponibilité du trajet
        if (!trajetService.trajetEstDisponible(reservation.getTrajet().getId(), reservation.getNbPlaces())) {
            throw new IllegalArgumentException("Le trajet n'est pas disponible pour le nombre de places demandées");
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

            // Vérification du statut actuel
            if (reservation.getStatut() != StatutReservation.EN_ATTENTE) {
                throw new IllegalArgumentException("La réservation n'est pas en attente et ne peut être confirmée");
            }

            // Vérification de la disponibilité du trajet
            if (!trajetService.trajetEstDisponible(reservation.getTrajet().getId(), reservation.getNbPlaces())) {
                throw new IllegalArgumentException("Le trajet n'a plus assez de places disponibles");
            }

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
