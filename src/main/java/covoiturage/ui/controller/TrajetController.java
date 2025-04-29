package covoiturage.ui.controller;

import covoiturage.model.Conducteur;
import covoiturage.model.Reservation;
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
import java.util.stream.Collectors;

/**
 * Contrôleur pour gérer les opérations liées aux trajets dans l'interface utilisateur.
 */
public class TrajetController {
    // Constantes pour la mise en forme
    private static final String LIGNE_SEPARATION = "══════════════════════════════════════════════════════════════════════════════";
    private static final String SOUS_LIGNE = "──────────────────────────────────────────────────────────────────────────────";

    private TrajetService trajetService;
    private Scanner scanner;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Constructeur initialisant le contrôleur avec un scanner pour les entrées utilisateur.
     * @param scanner Le scanner pour lire les entrées utilisateur
     */
    public TrajetController(Scanner scanner) {
        this.trajetService = ServiceFactory.getTrajetService();
        this.scanner = scanner;
    }

    /**
     * Affiche un message d'erreur formaté.
     * @param message Le message d'erreur à afficher
     */
    private void afficherMessageErreur(String message) {
        System.out.println("\n⚠️  " + message);
    }

    /**
     * Affiche un message de succès formaté.
     * @param message Le message de succès à afficher
     */
    private void afficherMessageSucces(String message) {
        System.out.println("\n✅  " + message);
    }

    /**
     * Recherche des trajets selon les critères spécifiés par l'utilisateur.
     * @param utilisateur L'utilisateur qui recherche des trajets
     */
    public void rechercherTrajets(Utilisateur utilisateur) {
        System.out.println("\n" + LIGNE_SEPARATION);
        System.out.println("                    RECHERCHE DE TRAJETS");
        System.out.println(SOUS_LIGNE);

        System.out.print("➤ Lieu de départ : ");
        String lieuDepart = scanner.nextLine().trim();

        System.out.print("➤ Lieu d'arrivée : ");
        String lieuArrivee = scanner.nextLine().trim();

        System.out.println("\nRecherche des trajets disponibles...");
        List<Trajet> trajets = trajetService.rechercherTrajetsDisponibles(lieuDepart, lieuArrivee, LocalDateTime.now());

        if (trajets.isEmpty()) {
            afficherMessageErreur("Aucun trajet disponible pour cet itinéraire.");
            return;
        }

        afficherListeTrajets(trajets);
    }

    /**
     * Affiche une liste de trajets formatée.
     * @param trajets La liste des trajets à afficher
     */
    public void afficherListeTrajets(List<Trajet> trajets) {
        // Constantes pour la mise en forme
        final String LINE_TOP = "┌─────┬─────────────────┬─────────────────┬──────────────────────┬──────────────┬────────┬────────────────────────┐";
        final String LINE_MID = "├─────┼─────────────────┼─────────────────┼──────────────────────┼──────────────┼────────┼────────────────────────┤";
        final String LINE_BOT = "└─────┴─────────────────┴─────────────────┴──────────────────────┴──────────────┴────────┴────────────────────────┘";
        final String VERTICAL = "│";

        System.out.println("\n" + "═".repeat(90));
        System.out.println(" ".repeat(40) + "LISTE DES TRAJETS");
        System.out.println("─".repeat(90));

        // Affichage de l'en-tête du tableau
        System.out.println(LINE_TOP);
        System.out.printf("%s %-3s %s %-15s %s %-15s %s %-20s %s %-12s %s %-6s %s %-22s %s%n",
                VERTICAL, "ID", VERTICAL, "DÉPART", VERTICAL, "ARRIVÉE", VERTICAL,
                "DATE/HEURE", VERTICAL, "PRIX", VERTICAL, "PLACES", VERTICAL, "CONDUCTEUR", VERTICAL);
        System.out.println(LINE_MID);

        // Contenu du tableau
        for (Trajet trajet : trajets) {
            String conducteurInfo = trajet.getConducteur() != null ?
                    trajet.getConducteur().getPrenom() + " " + trajet.getConducteur().getNom() : "N/A";

            String statusMark = trajet.isEstAnnule() ? " ⚠️" : "";
            String dateHeureFormattee = trajet.getDateDepart().format(formatter);
            String prixFormatted = String.format("%.2f Dinars", trajet.getPrix());

            System.out.printf("%s %-3d %s %-15s %s %-15s %s %-20s %s %-12s %s %-6d %s %-22s %s%n",
                    VERTICAL, trajet.getId(), VERTICAL,
                    limiterTexte(trajet.getLieuDepart(), 15), VERTICAL,
                    limiterTexte(trajet.getLieuArrivee(), 15), VERTICAL,
                    dateHeureFormattee, VERTICAL,
                    prixFormatted, VERTICAL,
                    trajet.calculerPlacesRestantes(), VERTICAL,
                    limiterTexte(conducteurInfo + statusMark, 22), VERTICAL);
        }

        System.out.println(LINE_BOT);

        // Légende et Statistiques
        if (trajets.stream().anyMatch(Trajet::isEstAnnule)) {
            System.out.println("\n⚠️  : Trajet annulé par le conducteur");
        }

        int totalTrajets = trajets.size();
        int trajetsActifs = (int) trajets.stream().filter(t -> !t.isEstAnnule()).count();
        System.out.println("\n📊 Statistiques: " + totalTrajets + " trajet(s) au total, dont " + trajetsActifs + " actif(s)");

        // Pause pour permettre à l'utilisateur de lire les informations
        System.out.print("\nAppuyez sur Entrée pour continuer...");
        scanner.nextLine();
    }

    // Méthode utilitaire pour limiter la longueur du texte
    private String limiterTexte(String texte, int longueurMax) {
        if (texte == null) return "";
        if (texte.length() <= longueurMax) return texte;
        return texte.substring(0, longueurMax - 3) + "...";
    }


    /**
     * Affiche les détails d'un trajet spécifique.
     * @param trajetId L'identifiant du trajet à afficher
     */
    public void afficherDetailTrajet(Long trajetId) {
        Optional<Trajet> optTrajet = trajetService.getTrajetById(trajetId);

        if (optTrajet.isEmpty()) {
            System.out.println("\n⚠️  Trajet non trouvé.");
            return;
        }

        Trajet trajet = optTrajet.get();

        // Largeur fixe pour un meilleur alignement
        final int LARGEUR = 70;
        final String LINE_TOP = "┌" + "─".repeat(LARGEUR - 2) + "┐";
        final String LINE_MID = "├" + "─".repeat(LARGEUR - 2) + "┤";
        final String LINE_BOT = "└" + "─".repeat(LARGEUR - 2) + "┘";
        final String VERTICAL = "│";

        String statut = trajet.isEstAnnule() ? " [ANNULÉ]" : "";
        String titre = "DÉTAILS DU TRAJET" + statut;
        int espaces = (LARGEUR - titre.length() - 2) / 2;
        String titreFormate = VERTICAL + " ".repeat(espaces) + titre + " ".repeat(LARGEUR - titre.length() - espaces - 2) + VERTICAL;

        System.out.println("\n" + LINE_TOP);
        System.out.println(titreFormate);
        System.out.println(LINE_MID);

        // Informations principales
        afficherLigneDetail(VERTICAL, "ID", String.valueOf(trajet.getId()), LARGEUR);
        afficherLigneDetail(VERTICAL, "Départ", trajet.getLieuDepart(), LARGEUR);
        afficherLigneDetail(VERTICAL, "Arrivée", trajet.getLieuArrivee(), LARGEUR);
        afficherLigneDetail(VERTICAL, "Date et Heure", trajet.getDateDepart().format(formatter), LARGEUR);
        afficherLigneDetail(VERTICAL, "Prix", String.format("%.2f Dinars", trajet.getPrix()), LARGEUR);
        afficherLigneDetail(VERTICAL, "Places disponibles", trajet.calculerPlacesRestantes() + "/" + trajet.getNbPlacesDisponibles(), LARGEUR);

        // Section conducteur
        if (trajet.getConducteur() != null) {
            System.out.println(LINE_MID);
            String titreConducteur = "INFORMATIONS CONDUCTEUR";
            int espacesConducteur = (LARGEUR - titreConducteur.length() - 2) / 2;
            System.out.println(VERTICAL + " ".repeat(espacesConducteur) + titreConducteur + " ".repeat(LARGEUR - titreConducteur.length() - espacesConducteur - 2) + VERTICAL);
            System.out.println(LINE_MID);

            String nomComplet = trajet.getConducteur().getPrenom() + " " + trajet.getConducteur().getNom();
            afficherLigneDetail(VERTICAL, "Conducteur", nomComplet, LARGEUR);
            afficherLigneDetail(VERTICAL, "Téléphone", trajet.getConducteur().getTelephone(), LARGEUR);
            afficherLigneDetail(VERTICAL, "Véhicule", trajet.getConducteur().getVehiculeInfo(), LARGEUR);
        }

        // Message d'annulation si nécessaire
        if (trajet.isEstAnnule()) {
            System.out.println(LINE_MID);
            String msgAnnulation = "⚠️  Ce trajet a été annulé par le conducteur";
            int espacesAnnulation = (LARGEUR - msgAnnulation.length() - 2) / 2;
            System.out.println(VERTICAL + " ".repeat(espacesAnnulation) + msgAnnulation + " ".repeat(LARGEUR - msgAnnulation.length() - espacesAnnulation - 2) + VERTICAL);
        }

        System.out.println(LINE_BOT);

        // Pause pour permettre à l'utilisateur de lire les informations
        System.out.print("\nAppuyez sur Entrée pour continuer...");
        scanner.nextLine();
    }

    // Méthode utilitaire pour afficher une ligne avec label et valeur
    private void afficherLigneDetail(String vertical, String label, String valeur, int largeurTotale) {
        // Calcul pour garantir l'alignement
        int largeurDisponible = largeurTotale - 4; // Moins les caractères verticaux et espaces
        int largeurLabel = 20; // Largeur fixe pour le label

        if (largeurLabel > largeurDisponible / 2) {
            largeurLabel = largeurDisponible / 3;
        }

        int largeurValeur = largeurDisponible - largeurLabel;

        // Formatage de la ligne
        String labelFormate = String.format("%-" + largeurLabel + "s", label);

        // Gestion du texte trop long pour la valeur
        String valeurFormatee;
        if (valeur.length() > largeurValeur) {
            valeurFormatee = valeur.substring(0, largeurValeur - 3) + "...";
        } else {
            valeurFormatee = valeur;
        }

        System.out.printf("%s %-" + largeurLabel + "s : %-" + largeurValeur + "s %s%n",
                vertical, labelFormate, valeurFormatee, vertical);
    }

    /**
     * Permet à un conducteur de créer un nouveau trajet.
     * @param conducteur Le conducteur qui crée le trajet
     * @return Le trajet créé, ou null en cas d'échec
     */
    public Trajet creerTrajet(Conducteur conducteur) {
        System.out.println("\n" + LIGNE_SEPARATION);
        System.out.println("                    CRÉATION D'UN TRAJET");
        System.out.println(SOUS_LIGNE);

        String lieuDepart = "";
        while (lieuDepart.isEmpty()) {
            System.out.print("➤ Lieu de départ : ");
            lieuDepart = scanner.nextLine().trim();
            if (lieuDepart.isEmpty()) {
                afficherMessageErreur("Le lieu de départ ne peut pas être vide.");
            }
        }

        String lieuArrivee = "";
        while (lieuArrivee.isEmpty()) {
            System.out.print("➤ Lieu d'arrivée : ");
            lieuArrivee = scanner.nextLine().trim();
            if (lieuArrivee.isEmpty()) {
                afficherMessageErreur("Le lieu d'arrivée ne peut pas être vide.");
            }
        }

        LocalDateTime dateDepart = null;
        while (dateDepart == null) {
            System.out.print("➤ Date et heure de départ (format: dd/MM/yyyy HH:mm) : ");
            String dateStr = scanner.nextLine().trim();

            if (InputValidator.isValidDateTime(dateStr)) {
                dateDepart = InputValidator.parseDateTime(dateStr);

                if (dateDepart.isBefore(LocalDateTime.now())) {
                    afficherMessageErreur("La date de départ doit être dans le futur.");
                    dateDepart = null;
                }
            } else {
                afficherMessageErreur("Format de date et heure invalide. Utilisez le format dd/MM/yyyy HH:mm");
            }
        }

        double prix = 0;
        while (prix <= 0) {
            System.out.print("➤ Prix par place (Dinars) : ");
            String prixStr = scanner.nextLine().trim();

            if (InputValidator.isPositiveDouble(prixStr)) {
                prix = Double.parseDouble(prixStr);
            } else {
                afficherMessageErreur("Le prix doit être un nombre positif.");
            }
        }

        int nbPlaces = 0;
        while (nbPlaces <= 0) {
            System.out.print("➤ Nombre de places disponibles : ");
            String nbPlacesStr = scanner.nextLine().trim();

            if (InputValidator.isPositiveInteger(nbPlacesStr)) {
                nbPlaces = Integer.parseInt(nbPlacesStr);
            } else {
                afficherMessageErreur("Le nombre de places doit être un entier positif.");
            }
        }

        Trajet trajet = new Trajet(lieuDepart, lieuArrivee, dateDepart, prix, nbPlaces);
        trajet.setConducteur(conducteur);

        try {
            Long id = ServiceFactory.getConducteurService().proposerTrajet(trajet);
            trajet.setId(id);
            afficherMessageSucces("Trajet créé avec succès !");
            return trajet;
        } catch (Exception e) {
            afficherMessageErreur("Erreur lors de la création du trajet : " + e.getMessage());
            return null;
        }
    }

    /**
     * Permet à un conducteur de modifier un de ses trajets.
     * @param conducteur Le conducteur qui modifie le trajet
     */
    public void modifierTrajet(Conducteur conducteur) {
        System.out.println("\n" + LIGNE_SEPARATION);
        System.out.println("                    MODIFICATION D'UN TRAJET");
        System.out.println(SOUS_LIGNE);

        // Récupérer tous les trajets du conducteur
        List<Trajet> trajets = ServiceFactory.getConducteurService().getTrajetsByConducteur(conducteur.getId());

        if (trajets.isEmpty()) {
            afficherMessageErreur("Vous n'avez pas de trajets.");
            return;
        }

        // Filtrer les trajets à venir (actifs et annulés)
        List<Trajet> trajetsFuturs = trajets.stream()
                .filter(t -> t.getDateDepart().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());

        if (trajetsFuturs.isEmpty()) {
            afficherMessageErreur("Vous n'avez pas de trajets à venir.");
            return;
        }

        System.out.println("Vos trajets à venir :");

        // En-tête du tableau
        System.out.println(String.format("%-5s │ %-15s │ %-15s │ %-20s │ %-8s │ %-10s │ %-20s",
                "ID", "DÉPART", "ARRIVÉE", "DATE/HEURE", "PRIX", "PLACES", "STATUT"));
        System.out.println("──────┼─────────────────┼─────────────────┼──────────────────────┼──────────┼────────────┼─────────────────────");

        // Contenu du tableau
        for (Trajet t : trajetsFuturs) {
            String statut = t.isEstAnnule() ? "ANNULÉ" : "ACTIF";
            System.out.println(String.format("%-5d │ %-15s │ %-15s │ %-20s │ %8.2f Dinars │ %-10d │ %-20s",
                    t.getId(),
                    t.getLieuDepart(),
                    t.getLieuArrivee(),
                    t.getDateDepart().format(formatter),
                    t.getPrix(),
                    t.getNbPlacesDisponibles(),
                    statut));
        }

        System.out.println(SOUS_LIGNE);

        System.out.print("\n➤ Entrez l'ID du trajet à modifier : ");
        String idStr = scanner.nextLine().trim();

        if (!InputValidator.isValidInteger(idStr)) {
            afficherMessageErreur("ID invalide.");
            return;
        }

        Long trajetId = Long.parseLong(idStr);
        Optional<Trajet> optTrajet = trajetsFuturs.stream()
                .filter(t -> t.getId().equals(trajetId))
                .findFirst();

        if (optTrajet.isEmpty()) {
            afficherMessageErreur("Trajet non trouvé ou vous n'êtes pas le conducteur de ce trajet.");
            return;
        }

        Trajet trajet = optTrajet.get();

        // Si le trajet est annulé, proposer de le réactiver
        if (trajet.isEstAnnule()) {
            System.out.print("\n➤ Ce trajet est actuellement annulé. Voulez-vous le réactiver ? (o/n) : ");
            String reactivation = scanner.nextLine().trim().toLowerCase();
            if (reactivation.equals("o") || reactivation.equals("oui")) {
                trajet.setEstAnnule(false);
                afficherMessageSucces("Le trajet a été réactivé.");
            }
        }

        System.out.println("\n" + SOUS_LIGNE);
        System.out.println("Laissez vide pour conserver la valeur actuelle.");

        System.out.print("\n➤ Lieu de départ [" + trajet.getLieuDepart() + "] : ");
        String lieuDepart = scanner.nextLine().trim();
        if (!lieuDepart.isEmpty()) {
            trajet.setLieuDepart(lieuDepart);
        }

        System.out.print("➤ Lieu d'arrivée [" + trajet.getLieuArrivee() + "] : ");
        String lieuArrivee = scanner.nextLine().trim();
        if (!lieuArrivee.isEmpty()) {
            trajet.setLieuArrivee(lieuArrivee);
        }

        System.out.print("➤ Date et heure de départ [" + trajet.getDateDepart().format(formatter) + "] : ");
        String dateStr = scanner.nextLine().trim();
        if (!dateStr.isEmpty()) {
            if (InputValidator.isValidDateTime(dateStr)) {
                LocalDateTime newDate = InputValidator.parseDateTime(dateStr);
                if (newDate.isAfter(LocalDateTime.now())) {
                    trajet.setDateDepart(newDate);
                } else {
                    afficherMessageErreur("La date doit être dans le futur, ancienne date conservée.");
                }
            } else {
                afficherMessageErreur("Format de date invalide, ancienne date conservée.");
            }
        }

        System.out.print("➤ Prix [" + trajet.getPrix() + "] : ");
        String prixStr = scanner.nextLine().trim();
        if (!prixStr.isEmpty()) {
            if (InputValidator.isPositiveDouble(prixStr)) {
                trajet.setPrix(Double.parseDouble(prixStr));
            } else {
                afficherMessageErreur("Prix invalide, ancien prix conservé.");
            }
        }

        System.out.print("➤ Nombre de places disponibles [" + trajet.getNbPlacesDisponibles() + "] : ");
        String nbPlacesStr = scanner.nextLine().trim();
        if (!nbPlacesStr.isEmpty()) {
            if (InputValidator.isPositiveInteger(nbPlacesStr)) {
                int newPlaces = Integer.parseInt(nbPlacesStr);
                int placesReservees = trajet.getNbPlacesDisponibles() - trajet.calculerPlacesRestantes();

                if (newPlaces >= placesReservees) {
                    trajet.setNbPlacesDisponibles(newPlaces);
                } else {
                    afficherMessageErreur("Le nombre de places ne peut pas être inférieur au nombre de réservations existantes (" + placesReservees + ").");
                }
            } else {
                afficherMessageErreur("Nombre de places invalide, ancienne valeur conservée.");
            }
        }

        try {
            boolean success = ServiceFactory.getConducteurService().modifierTrajet(trajet);
            if (success) {
                afficherMessageSucces("Trajet modifié avec succès !");
            } else {
                afficherMessageErreur("Erreur lors de la modification du trajet.");
            }
        } catch (Exception e) {
            afficherMessageErreur("Erreur lors de la modification du trajet : " + e.getMessage());
        }
    }

    /**
     * Permet à un conducteur d'annuler un de ses trajets.
     * @param conducteur Le conducteur qui annule le trajet
     */
    public void annulerTrajet(Conducteur conducteur) {
        System.out.println("\n" + LIGNE_SEPARATION);
        System.out.println("                    ANNULATION D'UN TRAJET");
        System.out.println(SOUS_LIGNE);

        // Récupérer tous les trajets du conducteur
        List<Trajet> tousTrajets = ServiceFactory.getConducteurService().getTrajetsByConducteur(conducteur.getId());

        // Filtrer les trajets actifs (non annulés) à venir
        List<Trajet> trajetsActifs = tousTrajets.stream()
                .filter(t -> !t.isEstAnnule() && t.getDateDepart().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());

        if (trajetsActifs.isEmpty()) {
            afficherMessageErreur("Vous n'avez pas de trajets actifs à annuler.");
            return;
        }

        System.out.println("Vos trajets actifs à venir :");

        // En-tête du tableau
        System.out.println(String.format("%-5s │ %-15s │ %-15s │ %-20s │ %-8s │ %-10s",
                "ID", "DÉPART", "ARRIVÉE", "DATE/HEURE", "PRIX", "PLACES"));
        System.out.println("──────┼─────────────────┼─────────────────┼──────────────────────┼──────────┼────────────");

        // Contenu du tableau
        for (Trajet t : trajetsActifs) {
            System.out.println(String.format("%-5d │ %-15s │ %-15s │ %-20s │ %8.2f Dinars │ %-10d",
                    t.getId(),
                    t.getLieuDepart(),
                    t.getLieuArrivee(),
                    t.getDateDepart().format(formatter),
                    t.getPrix(),
                    t.calculerPlacesRestantes()));
        }

        System.out.println(SOUS_LIGNE);

        System.out.print("\n➤ Entrez l'ID du trajet à annuler : ");
        String idStr = scanner.nextLine().trim();

        if (!InputValidator.isValidInteger(idStr)) {
            afficherMessageErreur("ID invalide.");
            return;
        }

        Long trajetId = Long.parseLong(idStr);
        Optional<Trajet> optTrajet = trajetsActifs.stream()
                .filter(t -> t.getId().equals(trajetId))
                .findFirst();

        if (optTrajet.isEmpty()) {
            afficherMessageErreur("Trajet non trouvé ou vous n'êtes pas le conducteur de ce trajet.");
            return;
        }

        System.out.print("\n⚠️  Êtes-vous sûr de vouloir annuler ce trajet ? (o/n) : ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("o") || confirmation.equals("oui")) {
            try {
                boolean success = ServiceFactory.getConducteurService().annulerTrajet(trajetId);
                if (success) {
                    afficherMessageSucces("Trajet annulé avec succès !");
                } else {
                    afficherMessageErreur("Erreur lors de l'annulation du trajet.");
                }
            } catch (Exception e) {
                afficherMessageErreur("Erreur lors de l'annulation du trajet : " + e.getMessage());
            }
        } else {
            System.out.println("Annulation abandonnée.");
        }
    }

    /**
     * Permet à un conducteur de réactiver un de ses trajets annulés.
     * @param conducteur Le conducteur qui réactive le trajet
     */
    public void reactiverTrajet(Conducteur conducteur) {
        System.out.println("\n" + LIGNE_SEPARATION);
        System.out.println("                    RÉACTIVATION D'UN TRAJET ANNULÉ");
        System.out.println(SOUS_LIGNE);

        // Récupérer tous les trajets du conducteur
        List<Trajet> tousTrajets = ServiceFactory.getConducteurService().getTrajetsByConducteur(conducteur.getId());

        // Filtrer les trajets annulés à venir
        List<Trajet> trajetsAnnules = tousTrajets.stream()
                .filter(t -> t.isEstAnnule() && t.getDateDepart().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());

        if (trajetsAnnules.isEmpty()) {
            afficherMessageErreur("Vous n'avez pas de trajets annulés à réactiver.");
            return;
        }

        System.out.println("Vos trajets annulés à venir :");

        // En-tête du tableau
        System.out.println(String.format("%-5s │ %-15s │ %-15s │ %-20s │ %-8s │ %-10s",
                "ID", "DÉPART", "ARRIVÉE", "DATE/HEURE", "PRIX", "PLACES"));
        System.out.println("──────┼─────────────────┼─────────────────┼──────────────────────┼──────────┼────────────");

        // Contenu du tableau
        for (Trajet t : trajetsAnnules) {
            System.out.println(String.format("%-5d │ %-15s │ %-15s │ %-20s │ %8.2f Dinars │ %-10d",
                    t.getId(),
                    t.getLieuDepart(),
                    t.getLieuArrivee(),
                    t.getDateDepart().format(formatter),
                    t.getPrix(),
                    t.getNbPlacesDisponibles()));
        }

        System.out.println(SOUS_LIGNE);

        System.out.print("\n➤ Entrez l'ID du trajet à réactiver : ");
        String idStr = scanner.nextLine().trim();

        if (!InputValidator.isValidInteger(idStr)) {
            afficherMessageErreur("ID invalide.");
            return;
        }

        Long trajetId = Long.parseLong(idStr);
        Optional<Trajet> optTrajet = trajetsAnnules.stream()
                .filter(t -> t.getId().equals(trajetId))
                .findFirst();

        if (optTrajet.isEmpty()) {
            afficherMessageErreur("Trajet non trouvé ou vous n'êtes pas le conducteur de ce trajet.");
            return;
        }

        System.out.print("\n➤ Êtes-vous sûr de vouloir réactiver ce trajet ? (o/n) : ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("o") || confirmation.equals("oui")) {
            try {
                boolean success = ServiceFactory.getConducteurService().reactiverTrajet(trajetId);
                if (success) {
                    afficherMessageSucces("Trajet réactivé avec succès !");
                } else {
                    afficherMessageErreur("Erreur lors de la réactivation du trajet.");
                }
            } catch (Exception e) {
                afficherMessageErreur("Erreur lors de la réactivation du trajet : " + e.getMessage());
            }
        } else {
            System.out.println("Réactivation abandonnée.");
        }
    }

    /**
     * Permet à un conducteur de supprimer définitivement un de ses trajets.
     * @param conducteur Le conducteur qui supprime le trajet
     */
    public void supprimerTrajet(Conducteur conducteur) {
        System.out.println("\n" + LIGNE_SEPARATION);
        System.out.println("                    SUPPRESSION D'UN TRAJET");
        System.out.println(SOUS_LIGNE);

        // Récupérer tous les trajets du conducteur
        List<Trajet> tousTrajets = ServiceFactory.getConducteurService().getTrajetsByConducteur(conducteur.getId());

        if (tousTrajets.isEmpty()) {
            afficherMessageErreur("Vous n'avez pas de trajets.");
            return;
        }

        System.out.println("Tous vos trajets :");

        // En-tête du tableau
        System.out.println(String.format("%-5s │ %-15s │ %-15s │ %-20s │ %-8s │ %-10s │ %-10s",
                "ID", "DÉPART", "ARRIVÉE", "DATE/HEURE", "PRIX", "PLACES", "STATUT"));
        System.out.println("──────┼─────────────────┼─────────────────┼──────────────────────┼──────────┼────────────┼────────────");

        // Contenu du tableau
        for (Trajet t : tousTrajets) {
            String statut = t.isEstAnnule() ? "ANNULÉ" : "ACTIF";
            String datePassee = t.getDateDepart().isBefore(LocalDateTime.now()) ? " (passé)" : "";
            System.out.println(String.format("%-5d │ %-15s │ %-15s │ %-20s │ %8.2f Dinars │ %-10d │ %-10s%s",
                    t.getId(),
                    t.getLieuDepart(),
                    t.getLieuArrivee(),
                    t.getDateDepart().format(formatter),
                    t.getPrix(),
                    t.getNbPlacesDisponibles(),
                    statut,
                    datePassee));
        }

        System.out.println(SOUS_LIGNE);

        System.out.print("\n➤ Entrez l'ID du trajet à supprimer : ");
        String idStr = scanner.nextLine().trim();

        if (!InputValidator.isValidInteger(idStr)) {
            afficherMessageErreur("ID invalide.");
            return;
        }

        Long trajetId = Long.parseLong(idStr);
        Optional<Trajet> optTrajet = tousTrajets.stream()
                .filter(t -> t.getId().equals(trajetId))
                .findFirst();

        if (optTrajet.isEmpty()) {
            afficherMessageErreur("Trajet non trouvé ou vous n'êtes pas le conducteur de ce trajet.");
            return;
        }

        // Vérifier si le trajet a des réservations
        List<Reservation> reservations = ServiceFactory.getReservationService().getReservationsByTrajet(trajetId);
        if (!reservations.isEmpty()) {
            System.out.println("\n⚠️  ATTENTION: Ce trajet a " + reservations.size() + " réservation(s).");
            System.out.println("En supprimant ce trajet, toutes les réservations associées seront aussi supprimées.");
        }

        System.out.print("\n⚠️  ATTENTION: Cette action est irréversible. Êtes-vous sûr de vouloir supprimer définitivement ce trajet ? (o/n) : ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("o") || confirmation.equals("oui")) {
            try {
                boolean success = ServiceFactory.getConducteurService().supprimerTrajet(trajetId);
                if (success) {
                    afficherMessageSucces("Trajet supprimé avec succès !");
                } else {
                    afficherMessageErreur("Erreur lors de la suppression du trajet.");
                }
            } catch (Exception e) {
                afficherMessageErreur("Erreur lors de la suppression du trajet : " + e.getMessage());
            }
        } else {
            System.out.println("Suppression abandonnée.");
        }
    }
}