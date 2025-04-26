package covoiturage.ui.controller;

import covoiturage.model.Reservation;
import covoiturage.model.Trajet;
import covoiturage.model.Utilisateur;
import covoiturage.model.enums.StatutReservation;
import covoiturage.service.ReservationService;
import covoiturage.service.ServiceFactory;
import covoiturage.service.TrajetService;
import covoiturage.ui.validator.InputValidator;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ReservationController {
    private ReservationService reservationService;
    private TrajetService trajetService;
    private Scanner scanner;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");


    public ReservationController(Scanner scanner) {
        this.reservationService = ServiceFactory.getReservationService();
        this.trajetService = ServiceFactory.getTrajetService();
        this.scanner = scanner;
    }


    public void reserverTrajet(Utilisateur utilisateur) {
        System.out.println("\n=== RÉSERVATION DE TRAJET ===");

        TrajetController trajetController = new TrajetController(scanner);
        trajetController.rechercherTrajets(utilisateur);

        System.out.print("\nEntrez l'ID du trajet à réserver (0 pour annuler) : ");
        String idStr = scanner.nextLine().trim();

        if (idStr.equals("0")) {
            System.out.println("Réservation annulée.");
            return;
        }

        if (!InputValidator.isValidInteger(idStr)) {
            System.out.println("ID invalide.");
            return;
        }

        Long trajetId = Long.parseLong(idStr);
        Optional<Trajet> optTrajet = trajetService.getTrajetById(trajetId);

        if (optTrajet.isEmpty()) {
            System.out.println("Trajet non trouvé.");
            return;
        }

        Trajet trajet = optTrajet.get();

        if (trajet.isEstAnnule()) {
            System.out.println("Ce trajet a été annulé.");
            return;
        }

        int placesRestantes = trajet.calculerPlacesRestantes();
        if (placesRestantes <= 0) {
            System.out.println("Il n'y a plus de places disponibles pour ce trajet.");
            return;
        }

        int nbPlaces = 0;
        while (nbPlaces <= 0 || nbPlaces > placesRestantes) {
            System.out.print("Nombre de places à réserver (max " + placesRestantes + ") : ");
            String nbPlacesStr = scanner.nextLine().trim();

            if (InputValidator.isPositiveInteger(nbPlacesStr)) {
                nbPlaces = Integer.parseInt(nbPlacesStr);
                if (nbPlaces > placesRestantes) {
                    System.out.println("Vous ne pouvez pas réserver plus de " + placesRestantes + " places.");
                }
            } else {
                System.out.println("Veuillez entrer un nombre valide.");
            }
        }

        double montantTotal = trajet.getPrix() * nbPlaces;
        System.out.println("Montant total : " + montantTotal + " Dinars");

        System.out.print("Confirmer la réservation ? (o/n) : ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("o") || confirmation.equals("oui")) {
            Reservation reservation = new Reservation(nbPlaces, utilisateur, trajet);

            try {
                Long id = reservationService.creerReservation(reservation);
                if (id != null) {
                    System.out.println("Réservation effectuée avec succès ! ID : " + id);

                    // Paiement de la réservation
                    System.out.print("Souhaitez-vous payer maintenant ? (o/n) : ");
                    String paiementConfirmation = scanner.nextLine().trim().toLowerCase();

                    if (paiementConfirmation.equals("o") || paiementConfirmation.equals("oui")) {
                        payerReservation(id, montantTotal);
                    } else {
                        System.out.println("Vous pourrez payer plus tard. La réservation est en attente de validation par le conducteur.");
                    }
                } else {
                    System.out.println("Erreur lors de la création de la réservation.");
                }
            } catch (Exception e) {
                System.out.println("Erreur lors de la réservation : " + e.getMessage());
            }
        } else {
            System.out.println("Réservation annulée.");
        }
    }

    public void afficherReservations(Utilisateur utilisateur) {
        System.out.println("\n=== MES RÉSERVATIONS ===");

        List<Reservation> reservations = reservationService.getReservationsByUtilisateur(utilisateur.getId());

        if (reservations.isEmpty()) {
            System.out.println("Vous n'avez pas de réservations.");
            return;
        }

        System.out.println(String.format("%-5s %-15s %-15s %-20s %-10s %-10s %-15s",
                "ID", "DÉPART", "ARRIVÉE", "DATE", "PLACES", "PRIX", "STATUT"));
        System.out.println("-----------------------------------------------------------------------------------------");

        for (Reservation reservation : reservations) {
            Trajet trajet = reservation.getTrajet();
            double montantTotal = trajet.getPrix() * reservation.getNbPlaces();

            System.out.println(String.format("%-5d %-15s %-15s %-20s %-10d %-10.2f %-15s",
                    reservation.getId(),
                    trajet.getLieuDepart(),
                    trajet.getLieuArrivee(),
                    trajet.getDateDepart().format(formatter),
                    reservation.getNbPlaces(),
                    montantTotal,
                    reservation.getStatut()));
        }
    }


    public void annulerReservation(Utilisateur utilisateur) {
        System.out.println("\n=== ANNULATION DE RÉSERVATION ===");

        List<Reservation> reservations = reservationService.getReservationsByUtilisateur(utilisateur.getId());

        // Filtrer les réservations non annulées
        reservations = reservations.stream()
                .filter(r -> r.getStatut() != StatutReservation.ANNULEE)
                .toList();

        if (reservations.isEmpty()) {
            System.out.println("Vous n'avez pas de réservations actives.");
            return;
        }

        afficherReservations(utilisateur);

        System.out.print("\nEntrez l'ID de la réservation à annuler : ");
        String idStr = scanner.nextLine().trim();

        if (!InputValidator.isValidInteger(idStr)) {
            System.out.println("ID invalide.");
            return;
        }

        Long reservationId = Long.parseLong(idStr);
        Optional<Reservation> optReservation = reservations.stream()
                .filter(r -> r.getId().equals(reservationId))
                .findFirst();

        if (optReservation.isEmpty()) {
            System.out.println("Réservation non trouvée ou déjà annulée.");
            return;
        }

        System.out.print("Êtes-vous sûr de vouloir annuler cette réservation ? (o/n) : ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("o") || confirmation.equals("oui")) {
            try {
                boolean success = reservationService.annulerReservation(reservationId);
                if (success) {
                    System.out.println("Réservation annulée avec succès !");

                    // Si un paiement a été effectué, proposer un remboursement
                    if (optReservation.get().getPaiement() != null) {
                        System.out.println("Un remboursement sera initié pour cette réservation.");
                        ServiceFactory.getPaiementService().rembourserPaiement(optReservation.get().getPaiement().getId());
                    }
                } else {
                    System.out.println("Erreur lors de l'annulation de la réservation.");
                }
            } catch (Exception e) {
                System.out.println("Erreur lors de l'annulation : " + e.getMessage());
            }
        } else {
            System.out.println("Annulation abandonnée.");
        }
    }

    public void payerReservation(Long reservationId, double montant) {
        System.out.println("\n=== PAIEMENT DE RÉSERVATION ===");

        try {
            Long paiementId = ServiceFactory.getPaiementService().effectuerPaiement(reservationId, montant);
            if (paiementId != null) {
                System.out.println("Paiement effectué avec succès !");
                System.out.println("Numéro de transaction : " + paiementId);
            } else {
                System.out.println("Erreur lors du paiement.");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors du paiement : " + e.getMessage());
        }
    }

    public void payerReservation(Utilisateur utilisateur) {
        System.out.println("\n=== PAIEMENT DE RÉSERVATION ===");

        List<Reservation> reservations = reservationService.getReservationsByUtilisateur(utilisateur.getId());

        // Filtrer les réservations confirmées mais non payées
        reservations = reservations.stream()
                .filter(r -> r.getStatut() == StatutReservation.CONFIRMEE && r.getPaiement() == null)
                .toList();

        if (reservations.isEmpty()) {
            System.out.println("Vous n'avez pas de réservations en attente de paiement.");
            return;
        }

        System.out.println("Réservations en attente de paiement :");
        System.out.println(String.format("%-5s %-15s %-15s %-20s %-10s %-10s",
                "ID", "DÉPART", "ARRIVÉE", "DATE", "PLACES", "PRIX"));
        System.out.println("----------------------------------------------------------------------------");

        for (Reservation reservation : reservations) {
            Trajet trajet = reservation.getTrajet();
            double montantTotal = trajet.getPrix() * reservation.getNbPlaces();

            System.out.println(String.format("%-5d %-15s %-15s %-20s %-10d %-10.2f",
                    reservation.getId(),
                    trajet.getLieuDepart(),
                    trajet.getLieuArrivee(),
                    trajet.getDateDepart().format(formatter),
                    reservation.getNbPlaces(),
                    montantTotal));
        }

        System.out.print("\nEntrez l'ID de la réservation à payer : ");
        String idStr = scanner.nextLine().trim();

        if (!InputValidator.isValidInteger(idStr)) {
            System.out.println("ID invalide.");
            return;
        }

        Long reservationId = Long.parseLong(idStr);
        Optional<Reservation> optReservation = reservations.stream()
                .filter(r -> r.getId().equals(reservationId))
                .findFirst();

        if (optReservation.isEmpty()) {
            System.out.println("Réservation non trouvée ou déjà payée.");
            return;
        }

        Reservation reservation = optReservation.get();
        double montantTotal = reservation.getTrajet().getPrix() * reservation.getNbPlaces();

        System.out.println("Montant à payer : " + montantTotal + " €");
        System.out.print("Confirmer le paiement ? (o/n) : ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("o") || confirmation.equals("oui")) {
            payerReservation(reservationId, montantTotal);
        } else {
            System.out.println("Paiement annulé.");
        }
    }
}
