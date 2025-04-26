package covoiturage.service;

import covoiturage.dao.DAOFactory;
import covoiturage.dao.PaiementDAO;
import covoiturage.dao.ReservationDAO;
import covoiturage.model.Paiement;
import covoiturage.model.Reservation;
import covoiturage.model.enums.StatutReservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class PaiementService {
    private PaiementDAO paiementDAO;
    private ReservationDAO reservationDAO;

    public PaiementService() {
        this.paiementDAO = DAOFactory.getPaiementDAO();
        this.reservationDAO = DAOFactory.getReservationDAO();
    }

    public Optional<Paiement> getPaiementById(Long id) {
        return paiementDAO.findById(id);
    }

    public List<Paiement> getAllPaiements() {
        return paiementDAO.findAll();
    }

    public Optional<Paiement> getPaiementByReservation(Long reservationId) {
        return paiementDAO.findByReservationId(reservationId);
    }

    public Long effectuerPaiement(Long reservationId, double montant) {
        // Récupération de la réservation
        Optional<Reservation> optReservation = reservationDAO.findById(reservationId);
        if (!optReservation.isPresent()) {
            throw new IllegalArgumentException("La réservation n'existe pas");
        }

        Reservation reservation = optReservation.get();

        // Vérification que la réservation est confirmée
        if (reservation.getStatut() != StatutReservation.CONFIRMEE) {
            throw new IllegalArgumentException("La réservation n'est pas confirmée et ne peut être payée");
        }

        // Vérification qu'il n'y a pas déjà un paiement
        if (paiementDAO.findByReservationId(reservationId).isPresent()) {
            throw new IllegalArgumentException("Un paiement existe déjà pour cette réservation");
        }

        // Création du paiement
        Paiement paiement = new Paiement();
        paiement.setMontant(montant);
        paiement.setDatePaiement(LocalDateTime.now());
        paiement.setReservation(reservation);
        paiement.setEstRembourse(false);

        return paiementDAO.save(paiement);
    }

    public boolean rembourserPaiement(Long paiementId) {
        Optional<Paiement> optPaiement = paiementDAO.findById(paiementId);
        if (!optPaiement.isPresent()) {
            throw new IllegalArgumentException("Le paiement n'existe pas");
        }

        Paiement paiement = optPaiement.get();

        // Vérification que le paiement n'est pas déjà remboursé
        if (paiement.isEstRembourse()) {
            throw new IllegalStateException("Le paiement est déjà remboursé");
        }

        // Mise à jour du statut
        paiement.setEstRembourse(true);

        return paiementDAO.update(paiement);
    }

    public double calculerMontantReservation(Reservation reservation) {
        // Calcul du montant total basé sur le prix du trajet et le nombre de places
        return reservation.getTrajet().getPrix() * reservation.getNbPlaces();
    }
}
