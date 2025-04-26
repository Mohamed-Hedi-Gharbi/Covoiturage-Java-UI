package covoiturage.ui.controller;

import covoiturage.model.Conducteur;
import covoiturage.model.Trajet;
import covoiturage.model.Utilisateur;
import covoiturage.service.ServiceFactory;
import covoiturage.service.TrajetService;
import covoiturage.ui.validator.InputValidator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class TrajetController {
    private TrajetService trajetService;
    private Scanner scanner;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public TrajetController(Scanner scanner) {
        this.trajetService = ServiceFactory.getTrajetService();
        this.scanner = scanner;
    }

    public void rechercherTrajets(Utilisateur utilisateur) {
        System.out.println("\n=== RECHERCHE DE TRAJETS ===");

        System.out.print("Lieu de départ : ");
        String lieuDepart = scanner.nextLine().trim();

        System.out.print("Lieu d'arrivée : ");
        String lieuArrivee = scanner.nextLine().trim();

        System.out.println("Recherche des trajets disponibles...");
        List<Trajet> trajets = trajetService.rechercherTrajetsDisponibles(lieuDepart, lieuArrivee, LocalDateTime.now());

        if (trajets.isEmpty()) {
            System.out.println("Aucun trajet disponible pour cet itinéraire.");
            return;
        }

        afficherListeTrajets(trajets);
    }

    public void afficherListeTrajets(List<Trajet> trajets) {
        System.out.println("\n=== LISTE DES TRAJETS ===");
        System.out.println(String.format("%-5s %-15s %-15s %-20s %-8s %-10s %-20s",
                "ID", "DÉPART", "ARRIVÉE", "DATE/HEURE", "PRIX", "PLACES", "CONDUCTEUR"));
        System.out.println("----------------------------------------------------------------------------------------------");

        for (Trajet trajet : trajets) {
            System.out.println(String.format("%-5d %-15s %-15s %-20s %-8.2f %-10d %-20s",
                    trajet.getId(),
                    trajet.getLieuDepart(),
                    trajet.getLieuArrivee(),
                    trajet.getDateDepart().format(formatter),
                    trajet.getPrix(),
                    trajet.calculerPlacesRestantes(),
                    trajet.getConducteur() != null ?
                            trajet.getConducteur().getPrenom() + " " + trajet.getConducteur().getNom() : "N/A"));
        }
    }

    public void afficherDeatilTrajet(Long trajetId) {
        Optional<Trajet> optTrajet = trajetService.getTrajetById(trajetId);

        if (optTrajet.isEmpty()) {
            System.out.println("Trajet non trouvé.");
            return;
        }

        Trajet trajet = optTrajet.get();

        System.out.println("\n=== DÉTAILS DU TRAJET ===");
        System.out.println("ID: " + trajet.getId());
        System.out.println("Départ: " + trajet.getLieuDepart());
        System.out.println("Arrivée: " + trajet.getLieuArrivee());
        System.out.println("Date et heure: " + trajet.getDateDepart().format(formatter));
        System.out.println("Prix: " + trajet.getPrix() + " Dinars");
        System.out.println("Places disponibles: " + trajet.calculerPlacesRestantes() + "/" + trajet.getNbPlacesDisponibles());

        if (trajet.getConducteur() != null) {
            System.out.println("Conducteur: " + trajet.getConducteur().getPrenom() + " " + trajet.getConducteur().getNom());
            System.out.println("Véhicule: " + trajet.getConducteur().getVehiculeInfo());
        }

        if (trajet.isEstAnnule()) {
            System.out.println("ATTENTION: Ce trajet a été annulé.");
        }
    }

    public Trajet creerTrajet(Conducteur conducteur) {
        System.out.println("\n=== CRÉATION D'UN TRAJET ===");

        String lieuDepart = "";
        while (lieuDepart.isEmpty()) {
            System.out.print("Lieu de départ : ");
            lieuDepart = scanner.nextLine().trim();
            if (lieuDepart.isEmpty()) {
                System.out.println("Le lieu de départ ne peut pas être vide.");
            }
        }

        String lieuArrivee = "";
        while (lieuArrivee.isEmpty()) {
            System.out.print("Lieu d'arrivée : ");
            lieuArrivee = scanner.nextLine().trim();
            if (lieuArrivee.isEmpty()) {
                System.out.println("Le lieu d'arrivée ne peut pas être vide.");
            }
        }

        LocalDateTime dateDepart = null;
        while (dateDepart == null) {
            System.out.print("Date et heure de départ (format: dd/MM/yyyy HH:mm) : ");
            String dateStr = scanner.nextLine().trim();

            if (InputValidator.isValidDateTime(dateStr)) {
                dateDepart = InputValidator.parseDateTime(dateStr);

                if (dateDepart.isBefore(LocalDateTime.now())) {
                    System.out.println("La date de départ doit être dans le futur.");
                    dateDepart = null;
                }
            } else {
                System.out.println("Format de date et heure invalide.");
            }
        }

        double prix = 0;
        while (prix <= 0) {
            System.out.print("Prix par place (Dinars) : ");
            String prixStr = scanner.nextLine().trim();

            if (InputValidator.isPositiveDouble(prixStr)) {
                prix = Double.parseDouble(prixStr);
            } else {
                System.out.println("Le prix doit être un nombre positif.");
            }
        }

        int nbPlaces = 0;
        while (nbPlaces <= 0) {
            System.out.print("Nombre de places disponibles : ");
            String nbPlacesStr = scanner.nextLine().trim();

            if (InputValidator.isPositiveInteger(nbPlacesStr)) {
                nbPlaces = Integer.parseInt(nbPlacesStr);
            } else {
                System.out.println("Le nombre de places doit être un entier positif.");
            }
        }

        Trajet trajet = new Trajet(lieuDepart, lieuArrivee, dateDepart, prix, nbPlaces);
        trajet.setConducteur(conducteur);

        try {
            Long id = ServiceFactory.getConducteurService().proposerTrajet(trajet);
            trajet.setId(id);
            System.out.print("Trajet créé avec succès !");
            return trajet;
        } catch (Exception e) {
            System.out.println("Erreur lors de la création du trajet : " + e.getMessage());
            return null;
        }
    }

    public void modifierTrajet(Conducteur conducteur) {
        System.out.println("\n=== MODIFICATION D'UN TRAJET ===");

        System.out.println("Vos trajets à venir :");
        List<Trajet> trajets = ServiceFactory.getConducteurService().getTrajetsByConducteur(conducteur.getId());

        // Filtrer les trajets à venir et non annulés
        trajets = trajets.stream()
                .filter(t -> !t.isEstAnnule() && t.getDateDepart().isAfter(LocalDateTime.now()))
                .toList();

        if (trajets.isEmpty()) {
            System.out.println("Vous n'avez pas de trajets à venir.");
            return;
        }

        afficherListeTrajets(trajets);

        System.out.print("\nEntrez l'ID du trajet à modifier : ");
        String idStr = scanner.nextLine().trim();

        if (!InputValidator.isValidInteger(idStr)) {
            System.out.println("ID invalide.");
            return;
        }

        Long trajetId = Long.parseLong(idStr);
        Optional<Trajet> optTrajet = trajets.stream()
                .filter(t -> t.getId().equals(trajetId))
                .findFirst();


        if (optTrajet.isEmpty()){
            System.out.println("Trajet non trouvé ou vous n'êtes pas le conducteur de ce trajet.");
            return;
        }

        Trajet trajet = optTrajet.get();

        System.out.println("\nLaissez vide pour conserver la valeur actuelle.");

        System.out.print("Lieu de départ [" + trajet.getLieuDepart() + "] : ");
        String lieuDepart = scanner.nextLine().trim();
        if (!lieuDepart.isEmpty()) {
            trajet.setLieuDepart(lieuDepart);
        }

        System.out.print("Lieu d'arrivée [" + trajet.getLieuArrivee() + "] : ");
        String lieuArrivee = scanner.nextLine().trim();
        if (!lieuArrivee.isEmpty()) {
            trajet.setLieuArrivee(lieuArrivee);
        }

        System.out.print("Date et heure de départ [" + trajet.getDateDepart().format(formatter) + "] : ");
        String dateStr = scanner.nextLine().trim();
        if (!dateStr.isEmpty()) {
            if (InputValidator.isValidDateTime(dateStr)) {
                LocalDateTime newDate = InputValidator.parseDateTime(dateStr);
                if (newDate.isAfter(LocalDateTime.now())) {
                    trajet.setDateDepart(newDate);
                } else {
                    System.out.println("La date doit être dans le futur, ancienne date conservée.");
                }
            } else {
                System.out.println("Format de date invalide, ancienne date conservée.");
            }
        }

        System.out.print("Prix [" + trajet.getPrix() + "] : ");
        String prixStr = scanner.nextLine().trim();
        if (!prixStr.isEmpty()) {
            if (InputValidator.isPositiveDouble(prixStr)) {
                trajet.setPrix(Double.parseDouble(prixStr));
            } else {
                System.out.println("Prix invalide, ancien prix conservé.");
            }
        }

        System.out.print("Nombre de places disponibles [" + trajet.getNbPlacesDisponibles() + "] : ");
        String nbPlacesStr = scanner.nextLine().trim();
        if (!nbPlacesStr.isEmpty()) {
            if (InputValidator.isPositiveInteger(nbPlacesStr)) {
                int newPlaces = Integer.parseInt(nbPlacesStr);
                int placesReservees = trajet.getNbPlacesDisponibles() - trajet.calculerPlacesRestantes();

                if (newPlaces >= placesReservees) {
                    trajet.setNbPlacesDisponibles(newPlaces);
                } else {
                    System.out.println("Le nombre de places ne peut pas être inférieur au nombre de réservations existantes (" + placesReservees + ").");
                }
            } else {
                System.out.println("Nombre de places invalide, ancienne valeur conservée.");
            }
        }

        try {
            boolean success = ServiceFactory.getConducteurService().modifierTrajet(trajet);
            if (success) {
                System.out.println("Trajet modifié avec succès !");
            } else {
                System.out.println("Erreur lors de la modification du trajet.");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la modification du trajet : " + e.getMessage());
        }
    }


    public void annulerTrajet(Conducteur conducteur) {
        System.out.println("\n=== ANNULATION D'UN TRAJET ===");

        System.out.println("Vos trajets à venir :");
        List<Trajet> trajets = ServiceFactory.getConducteurService().getTrajetsByConducteur(conducteur.getId());

        // Filtrer les trajets à venir et non annulés
        trajets = trajets.stream()
                .filter(t -> !t.isEstAnnule() && t.getDateDepart().isAfter(LocalDateTime.now()))
                .toList();

        if (trajets.isEmpty()) {
            System.out.println("Vous n'avez pas de trajets à venir.");
            return;
        }

        afficherListeTrajets(trajets);

        System.out.print("\nEntrez l'ID du trajet à annuler : ");
        String idStr = scanner.nextLine().trim();

        if (!InputValidator.isValidInteger(idStr)) {
            System.out.println("ID invalide.");
            return;
        }

        Long trajetId = Long.parseLong(idStr);
        Optional<Trajet> optTrajet = trajets.stream()
                .filter(t -> t.getId().equals(trajetId))
                .findFirst();

        if (!optTrajet.isPresent()) {
            System.out.println("Trajet non trouvé ou vous n'êtes pas le conducteur de ce trajet.");
            return;
        }

        System.out.print("Êtes-vous sûr de vouloir annuler ce trajet ? (o/n) : ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("o") || confirmation.equals("oui")) {
            try {
                boolean success = ServiceFactory.getConducteurService().annulerTrajet(trajetId);
                if (success) {
                    System.out.println("Trajet annulé avec succès !");
                } else {
                    System.out.println("Erreur lors de l'annulation du trajet.");
                }
            } catch (Exception e) {
                System.out.println("Erreur lors de l'annulation du trajet : " + e.getMessage());
            }
        } else {
            System.out.println("Annulation abandonnée.");
        }
    }
}
