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
    private static final String LIGNE_SEPARATION = "══════════════════════════════════════════════════════════════════════════════════════════════════════";
    private static final String SOUS_LIGNE = "──────────────────────────────────────────────────────────────────────────────────────────────────────";

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

        afficherListeTrajetsPaginee(trajets);
    }

    /**
     * Affiche une liste de trajets formatée avec pagination.
     * @param trajets La liste des trajets à afficher
     */
    public void afficherListeTrajetsPaginee(List<Trajet> trajets) {
        final int TRAJETS_PAR_PAGE = 5;
        int pageActuelle = 0;
        int nombrePages = (int) Math.ceil((double) trajets.size() / TRAJETS_PAR_PAGE);

        boolean continuer = true;
        while (continuer) {
            // Calculer l'intervalle à afficher
            int debut = pageActuelle * TRAJETS_PAR_PAGE;
            int fin = Math.min(debut + TRAJETS_PAR_PAGE, trajets.size());

            // Afficher l'en-tête de la page
            System.out.println("\n" + LIGNE_SEPARATION);
            System.out.println("                              LISTE DES TRAJETS");
            System.out.println(SOUS_LIGNE);

            // Afficher l'en-tête du tableau
            System.out.println("┌─────┬───────────────────────┬───────────────────────┬──────────────────────┬──────────────┬────────┬────────────────────────┐");
            System.out.printf("│ %-3s │ %-21s │ %-21s │ %-20s │ %-12s │ %-6s │ %-22s │%n",
                    "ID", "DÉPART", "ARRIVÉE", "DATE/HEURE", "PRIX", "PLACES", "CONDUCTEUR");
            System.out.println("├─────┼───────────────────────┼───────────────────────┼──────────────────────┼──────────────┼────────┼────────────────────────┤");

            // Afficher les trajets pour cette page
            for (int i = debut; i < fin; i++) {
                Trajet trajet = trajets.get(i);
                String conducteurInfo = trajet.getConducteur() != null ?
                        trajet.getConducteur().getPrenom() + " " + trajet.getConducteur().getNom() : "N/A";

                String statusMark = trajet.isEstAnnule() ? " ⚠️" : "";
                System.out.printf("│ %-3d │ %-21s │ %-21s │ %-20s │ %-12s │ %-6d │ %-22s │%n",
                        trajet.getId(),
                        limiterTexte(trajet.getLieuDepart(), 21),
                        limiterTexte(trajet.getLieuArrivee(), 21),
                        trajet.getDateDepart().format(formatter),
                        String.format("%.2f Dinars", trajet.getPrix()),
                        trajet.calculerPlacesRestantes(),
                        limiterTexte(conducteurInfo + statusMark, 22));
            }

            System.out.println("└─────┴───────────────────────┴───────────────────────┴──────────────────────┴──────────────┴────────┴────────────────────────┘");

            // Légende et statistiques
            if (trajets.stream().anyMatch(Trajet::isEstAnnule)) {
                System.out.println("\n⚠️ : Trajet annulé");
            }

            int totalTrajets = trajets.size();
            int trajetsActifs = (int) trajets.stream().filter(t -> !t.isEstAnnule()).count();
            System.out.println("\n📊 Statistiques: " + totalTrajets + " trajet(s) au total, dont " + trajetsActifs + " actif(s)");

            // Section pour les textes tronqués
            boolean hasTruncatedText = false;
            for (int i = debut; i < fin; i++) {
                Trajet trajet = trajets.get(i);
                if (trajet.getLieuDepart().length() > 21 || trajet.getLieuArrivee().length() > 21 ||
                        (trajet.getConducteur() != null &&
                                (trajet.getConducteur().getPrenom() + " " + trajet.getConducteur().getNom()).length() > 22)) {

                    if (!hasTruncatedText) {
                        System.out.println("\nTextes complets (pour les entrées tronquées) :");
                        hasTruncatedText = true;
                    }

                    System.out.println("\nTrajet #" + trajet.getId() + " :");

                    if (trajet.getLieuDepart().length() > 21) {
                        System.out.println("  • Départ: " + trajet.getLieuDepart());
                    }

                    if (trajet.getLieuArrivee().length() > 21) {
                        System.out.println("  • Arrivée: " + trajet.getLieuArrivee());
                    }

                    if (trajet.getConducteur() != null &&
                            (trajet.getConducteur().getPrenom() + " " + trajet.getConducteur().getNom()).length() > 22) {
                        System.out.println("  • Conducteur: " + trajet.getConducteur().getPrenom() + " " + trajet.getConducteur().getNom());
                    }
                }
            }

            // Options de navigation
            System.out.println("\nOptions :");
            if (pageActuelle > 0) System.out.println("P - Page précédente");
            if (pageActuelle < nombrePages - 1) System.out.println("N - Page suivante");
            System.out.println("D - Voir les détails d'un trajet");
            System.out.println("Q - Retour");

            System.out.print("\n➤ Votre choix : ");
            String choix = scanner.nextLine().trim().toUpperCase();

            switch (choix) {
                case "P":
                    if (pageActuelle > 0) pageActuelle--;
                    break;
                case "N":
                    if (pageActuelle < nombrePages - 1) pageActuelle++;
                    break;
                case "D":
                    System.out.print("Entrez l'ID du trajet : ");
                    String idStr = scanner.nextLine().trim();
                    if (InputValidator.isValidInteger(idStr)) {
                        Long trajetId = Long.parseLong(idStr);
                        afficherDetailTrajet(trajetId);
                    } else {
                        afficherMessageErreur("ID invalide.");
                        System.out.print("Appuyez sur Entrée pour continuer...");
                        scanner.nextLine();
                    }
                    break;
                case "Q":
                    continuer = false;
                    break;
                default:
                    afficherMessageErreur("Option invalide.");
                    System.out.print("Appuyez sur Entrée pour continuer...");
                    scanner.nextLine();
                    break;
            }
        }
    }

    /**
     * Méthode de compatibilité pour l'ancienne interface, redirige vers la version paginée
     */
    public void afficherListeTrajets(List<Trajet> trajets) {
        afficherListeTrajetsPaginee(trajets);
    }

    /**
     * Limite la longueur d'un texte, en ajoutant "..." si nécessaire
     */
    private String limiterTexte(String texte, int longueurMax) {
        if (texte == null) return "";
        if (texte.length() <= longueurMax) return texte;
        return texte.substring(0, longueurMax - 3) + "...";
    }


    /**
     * Affiche les détails d'un trajet avec un design moderne et professionnel.
     * @param trajetId L'identifiant du trajet à afficher
     */
    public void afficherDetailTrajet(Long trajetId) {
        Optional<Trajet> optTrajet = trajetService.getTrajetById(trajetId);

        if (optTrajet.isEmpty()) {
            afficherMessageErreur("Trajet non trouvé.");
            return;
        }

        Trajet trajet = optTrajet.get();

        // Dimensions et styles
        final int LARGEUR_TOTALE = 70;  // Largeur fixe pour toutes les lignes
        final String RESET = "\u001B[0m";
        final String BOLD = "\u001B[1m";
        final String BLUE = "\u001B[34m";
        final String CYAN = "\u001B[36m";
        final String GREEN = "\u001B[32m";
        final String YELLOW = "\u001B[33m";

        // Caractères pour les bordures avec style double-ligne pour un look plus élégant
        final String HORIZ = "═";
        final String VERT = "║";
        final String COIN_HG = "╔";
        final String COIN_HD = "╗";
        final String COIN_BG = "╚";
        final String COIN_BD = "╝";
        final String INTER_G = "╠";
        final String INTER_D = "╣";
        final String INTER_H = "╦";
        final String INTER_B = "╩";
        final String INTER_CROSS = "╬";

        // Construction des lignes de cadre avec largeur fixe
        String ligneHaut = COIN_HG + HORIZ.repeat(LARGEUR_TOTALE - 2) + COIN_HD;
        String ligneMilieu = INTER_G + HORIZ.repeat(LARGEUR_TOTALE - 2) + INTER_D;
        String ligneBas = COIN_BG + HORIZ.repeat(LARGEUR_TOTALE - 2) + COIN_BD;

        // En-tête avec titre et date du jour - Calcul précis des espaces
        System.out.println("\n" + ligneHaut);

        // Titre du système avec espaces précis pour alignement
        String systemTitle = BOLD + BLUE + " SYSTÈME DE COVOITURAGE " + RESET;
        int systemTitleLength = " SYSTÈME DE COVOITURAGE ".length(); // Sans les codes ANSI
        int systemRightSpace = LARGEUR_TOTALE - 2 - systemTitleLength;

        System.out.println(VERT + systemTitle + " ".repeat(systemRightSpace) + VERT);
        System.out.println(ligneMilieu);

        // Titre du trajet centré avec précision
        String titreTrajet = "DÉTAILS DU TRAJET #" + trajet.getId();
        int espaceGauche = (LARGEUR_TOTALE - 2 - titreTrajet.length()) / 2;
        int espaceDroite = LARGEUR_TOTALE - 2 - titreTrajet.length() - espaceGauche;

        System.out.println(VERT + " ".repeat(espaceGauche) + BOLD + CYAN + titreTrajet + RESET + " ".repeat(espaceDroite) + VERT);
        System.out.println(ligneMilieu);

        // Section Informations de base - Alignement précis
        String infoTitle = BOLD + " INFORMATIONS ITINÉRAIRE" + RESET;
        int infoTitleLength = " INFORMATIONS ITINÉRAIRE".length(); // Sans les codes ANSI
        int infoRightSpace = LARGEUR_TOTALE - 2 - infoTitleLength;

        System.out.println(VERT + infoTitle + " ".repeat(infoRightSpace) + VERT);
        System.out.println(ligneMilieu);

        // Détails du trajet - Gestion précise de la longueur pour alignement
        afficherDetailPrecis("Départ", trajet.getLieuDepart(), LARGEUR_TOTALE, VERT, GREEN);
        afficherDetailPrecis("Arrivée", trajet.getLieuArrivee(), LARGEUR_TOTALE, VERT, GREEN);
        afficherDetailPrecis("Date et Heure", trajet.getDateDepart().format(formatter), LARGEUR_TOTALE, VERT, YELLOW);

        // Section prix et disponibilité
        System.out.println(ligneMilieu);

        String tarifTitle = BOLD + " TARIFS ET DISPONIBILITÉ" + RESET;
        int tarifTitleLength = " TARIFS ET DISPONIBILITÉ".length(); // Sans les codes ANSI
        int tarifRightSpace = LARGEUR_TOTALE - 2 - tarifTitleLength;

        System.out.println(VERT + tarifTitle + " ".repeat(tarifRightSpace) + VERT);
        System.out.println(ligneMilieu);

        afficherDetailPrecis("Prix", String.format("%.2f Dinars", trajet.getPrix()), LARGEUR_TOTALE, VERT, YELLOW);

        // Affichage des places avec indication visuelle
        String placesInfo = trajet.calculerPlacesRestantes() + "/" + trajet.getNbPlacesDisponibles();
        String statusPlaces;

        if (trajet.calculerPlacesRestantes() == 0) {
            statusPlaces = "COMPLET";
        } else if (trajet.calculerPlacesRestantes() < trajet.getNbPlacesDisponibles() / 3) {
            statusPlaces = "PRESQUE COMPLET";
        } else {
            statusPlaces = "DISPONIBLE";
        }

        afficherStatusPrecis("Places", placesInfo, statusPlaces, LARGEUR_TOTALE, VERT);

        // Section conducteur
        System.out.println(ligneMilieu);

        String conducteurTitle = BOLD + " INFORMATIONS CONDUCTEUR" + RESET;
        int conducteurTitleLength = " INFORMATIONS CONDUCTEUR".length(); // Sans les codes ANSI
        int conducteurRightSpace = LARGEUR_TOTALE - 2 - conducteurTitleLength;

        System.out.println(VERT + conducteurTitle + " ".repeat(conducteurRightSpace) + VERT);
        System.out.println(ligneMilieu);

        if (trajet.getConducteur() != null) {
            String nomComplet = trajet.getConducteur().getPrenom() + " " + trajet.getConducteur().getNom();
            afficherDetailPrecis("Conducteur", nomComplet, LARGEUR_TOTALE, VERT, CYAN);
            afficherDetailPrecis("Téléphone", trajet.getConducteur().getTelephone(), LARGEUR_TOTALE, VERT, CYAN);
            afficherDetailPrecis("Véhicule", trajet.getConducteur().getVehiculeInfo(), LARGEUR_TOTALE, VERT, CYAN);
        } else {
            afficherDetailPrecis("Information", "Conducteur non disponible", LARGEUR_TOTALE, VERT, CYAN);
        }

        // Pied de page avec instructions
        System.out.println(ligneMilieu);
        String instructions = "Appuyez sur Entrée pour continuer";
        espaceGauche = (LARGEUR_TOTALE - 2 - instructions.length()) / 2;
        espaceDroite = LARGEUR_TOTALE - 2 - instructions.length() - espaceGauche;

        System.out.println(VERT + " ".repeat(espaceGauche) + BOLD + instructions + RESET + " ".repeat(espaceDroite) + VERT);
        System.out.println(ligneBas);

        // Attendre que l'utilisateur appuie sur Entrée
        scanner.nextLine();
    }

    /**
     * Affiche une ligne de détail avec label et valeur, en assurant un alignement parfait.
     * Cette méthode calcule précisément les espaces en tenant compte des codes ANSI.
     *
     * @param label Le label à afficher
     * @param valeur La valeur à afficher
     * @param largeurTotale Largeur totale du cadre
     * @param bordure Caractère de bordure
     * @param couleurValeur Code ANSI pour la couleur de la valeur
     */
    private void afficherDetailPrecis(String label, String valeur, int largeurTotale, String bordure, String couleurValeur) {
        final String RESET = "\u001B[0m";
        final String BOLD = "\u001B[1m";

        // Calcul de longueur sans les caractères de formatage
        String texteBrut = label + " : " + valeur;
        int longueurTexte = texteBrut.length();

        // Construction de la ligne avec largeur fixe garantie
        StringBuilder ligne = new StringBuilder(bordure + " ");

        // Ajouter le label en gras
        ligne.append(BOLD).append(label).append(RESET).append(" : ");

        // Ajouter la valeur en couleur
        ligne.append(couleurValeur).append(valeur).append(RESET);

        // Calculer l'espace nécessaire pour atteindre la bordure droite
        int espaceRestant = largeurTotale - 3 - longueurTexte; // -3 pour bordure gauche + espace + bordure droite
        ligne.append(" ".repeat(espaceRestant));

        // Ajouter la bordure droite
        ligne.append(bordure);

        System.out.println(ligne.toString());
    }

    /**
     * Affiche une ligne avec label, valeur et statut, en assurant un alignement parfait.
     *
     * @param label Le label à afficher
     * @param valeur La valeur à afficher
     * @param status Le statut à afficher (sera coloré selon sa valeur)
     * @param largeurTotale Largeur totale du cadre
     * @param bordure Caractère de bordure
     */
    private void afficherStatusPrecis(String label, String valeur, String status, int largeurTotale, String bordure) {
        final String RESET = "\u001B[0m";
        final String BOLD = "\u001B[1m";
        final String GREEN = "\u001B[32m";
        final String YELLOW = "\u001B[33m";
        final String RED = "\u001B[31m";

        // Sélection de la couleur en fonction du statut
        String couleurStatus;
        if (status.equals("DISPONIBLE")) {
            couleurStatus = GREEN;
        } else if (status.equals("PRESQUE COMPLET")) {
            couleurStatus = YELLOW;
        } else {
            couleurStatus = RED;
        }

        // Calculer la longueur totale du texte brut (sans les codes ANSI)
        int longueurLabel = label.length() + 3; // +3 pour " : "
        int longueurValeur = valeur.length();
        int longueurStatus = status.length() + 2; // +2 pour []

        // Calculer l'espace entre la valeur et le statut
        int espaceIntermediaire = largeurTotale - 3 - longueurLabel - longueurValeur - longueurStatus;

        // Construction de la ligne avec alignement précis
        StringBuilder ligne = new StringBuilder(bordure + " ");

        // Ajouter le label en gras
        ligne.append(BOLD).append(label).append(RESET).append(" : ");

        // Ajouter la valeur
        ligne.append(YELLOW).append(valeur).append(RESET);

        // Ajouter l'espace intermédiaire
        ligne.append(" ".repeat(espaceIntermediaire));

        // Ajouter le statut
        ligne.append("[").append(couleurStatus).append(status).append(RESET).append("]");

        // Ajouter la bordure droite
        ligne.append(bordure);

        System.out.println(ligne.toString());
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

        afficherListeTrajetsPaginee(trajetsFuturs);

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

        afficherListeTrajetsPaginee(trajetsActifs);

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

        afficherListeTrajetsPaginee(trajetsAnnules);

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

        afficherListeTrajetsPaginee(tousTrajets);

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

        // Vérifier si le trajet a des réservations associées
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
                System.out.println("Note: Pour supprimer un trajet qui a des réservations, il faut d'abord supprimer ces réservations.");
            }
        } else {
            System.out.println("Suppression abandonnée.");
        }
    }
}