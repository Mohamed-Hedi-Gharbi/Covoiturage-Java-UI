package covoiturage.ui.controller;

import covoiturage.model.Conducteur;
import covoiturage.model.Reservation;
import covoiturage.model.Trajet;
import covoiturage.model.Utilisateur;
import covoiturage.model.enums.StatutReservation;
import covoiturage.service.ReservationService;
import covoiturage.service.ServiceFactory;
import covoiturage.service.TrajetService;
import covoiturage.ui.validator.InputValidator;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

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

        // Calculer le nombre de places déjà réservées par cet utilisateur
        int placesDejaReservees = 0;
        List<Reservation> reservationsUtilisateur = reservationService.getReservationsByUtilisateur(utilisateur.getId())
                .stream()
                .filter(r -> r.getTrajet().getId().equals(trajetId) &&
                        !r.isAnnule() &&
                        r.getStatut() != StatutReservation.ANNULEE)
                .collect(Collectors.toList());

        for (Reservation r : reservationsUtilisateur) {
            placesDejaReservees += r.getNbPlaces();
        }

        int placesRestantes = trajet.calculerPlacesRestantes();
        if (placesRestantes <= 0) {
            System.out.println("Il n'y a plus de places disponibles pour ce trajet.");
            return;
        }

        System.out.println("Vous avez déjà réservé " + placesDejaReservees + " place(s) pour ce trajet.");
        System.out.println("Il reste " + placesRestantes + " place(s) disponible(s).");

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

    // Dans ReservationController.java
    public void gererReservationsEnAttente(Conducteur conducteur) {
        System.out.println("\n=== GESTION DES RÉSERVATIONS ===");

        // Récupérer les trajets du conducteur
        List<Trajet> trajets = ServiceFactory.getConducteurService().getTrajetsByConducteur(conducteur.getId());

        if (trajets.isEmpty()) {
            System.out.println("Vous n'avez pas encore proposé de trajets.");
            return;
        }

        // Récupérer toutes les réservations pour les trajets du conducteur
        List<Reservation> toutesReservations = new ArrayList<>();

        for (Trajet trajet : trajets) {
            toutesReservations.addAll(ServiceFactory.getReservationService().getReservationsByTrajet(trajet.getId()));
        }

        // Filtrer uniquement les réservations en attente
        List<Reservation> reservationsEnAttente = toutesReservations.stream()
                .filter(r -> r.getStatut() == StatutReservation.EN_ATTENTE)
                .collect(Collectors.toList());

        if (reservationsEnAttente.isEmpty()) {
            System.out.println("Vous n'avez pas de réservations en attente.");
            return;
        }

        // Afficher les réservations en attente
        System.out.println(String.format("%-5s %-15s %-15s %-20s %-10s %-20s %-15s",
                "ID", "DÉPART", "ARRIVÉE", "DATE", "PLACES", "PASSAGER", "STATUT"));
        System.out.println("------------------------------------------------------------------------------------------------------------------------");

        for (Reservation reservation : reservationsEnAttente) {
            Trajet trajet = reservation.getTrajet();
            String passager = reservation.getUtilisateur().getPrenom() + " " + reservation.getUtilisateur().getNom();

            System.out.println(String.format("%-5d %-15s %-15s %-20s %-10d %-20s %-15s",
                    reservation.getId(),
                    trajet.getLieuDepart(),
                    trajet.getLieuArrivee(),
                    trajet.getDateDepart().format(formatter),
                    reservation.getNbPlaces(),
                    passager,
                    reservation.getStatut()));
        }

        System.out.print("\nEntrez l'ID de la réservation à gérer (0 pour revenir) : ");
        String idStr = scanner.nextLine().trim();

        if (idStr.equals("0")) {
            return;
        }

        if (!InputValidator.isValidInteger(idStr)) {
            System.out.println("ID invalide.");
            return;
        }

        Long reservationId = Long.parseLong(idStr);
        Optional<Reservation> optReservation = reservationsEnAttente.stream()
                .filter(r -> r.getId().equals(reservationId))
                .findFirst();

        if (optReservation.isEmpty()) {
            System.out.println("Réservation non trouvée.");
            return;
        }

        Reservation reservation = optReservation.get();

        System.out.println("\nRéservation #" + reservationId);
        System.out.println("Passager: " + reservation.getUtilisateur().getPrenom() + " " + reservation.getUtilisateur().getNom());
        System.out.println("Téléphone: " + reservation.getUtilisateur().getTelephone());
        System.out.println("Trajet: " + reservation.getTrajet().getLieuDepart() + " → " + reservation.getTrajet().getLieuArrivee());
        System.out.println("Date: " + reservation.getTrajet().getDateDepart().format(formatter));
        System.out.println("Places demandées: " + reservation.getNbPlaces());

        System.out.println("\n1. Accepter la réservation");
        System.out.println("2. Refuser la réservation");
        System.out.println("0. Retour");

        System.out.print("\nVotre choix : ");
        String choix = scanner.nextLine().trim();

        try {
            switch (choix) {
                case "1":
                    boolean accepte = ServiceFactory.getReservationService().confirmerReservation(reservationId);
                    if (accepte) {
                        System.out.println("Réservation acceptée avec succès !");
                    } else {
                        System.out.println("Erreur lors de l'acceptation de la réservation.");
                    }
                    break;
                case "2":
                    boolean refuse = ServiceFactory.getReservationService().annulerReservation(reservationId);
                    if (refuse) {
                        System.out.println("Réservation annulée avec succès !");
                    } else {
                        System.out.println("Erreur lors d'annulation de la réservation.");
                    }
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Choix invalide.");
                    break;
            }
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
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

        System.out.println("Montant à payer : " + montantTotal + " Dinars");
        System.out.print("Confirmer le paiement ? (o/n) : ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("o") || confirmation.equals("oui")) {
            payerReservation(reservationId, montantTotal);
        } else {
            System.out.println("Paiement annulé.");
        }
    }
}
