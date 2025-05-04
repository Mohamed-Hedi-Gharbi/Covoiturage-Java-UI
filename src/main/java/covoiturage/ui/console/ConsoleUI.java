package covoiturage.ui.console;

import covoiturage.model.Administrateur;
import covoiturage.model.Conducteur;
import covoiturage.model.Trajet;
import covoiturage.model.Utilisateur;
import covoiturage.service.ServiceFactory;
import covoiturage.ui.AuthUI;
import covoiturage.ui.controller.*;
import covoiturage.ui.validator.InputValidator;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Interface console de l'application de covoiturage.
 * Gère les interactions avec l'utilisateur via le terminal.
 */
public class ConsoleUI {
    // Constantes pour la mise en forme
    private static final String LIGNE_SEPARATION = "══════════════════════════════════════════════════════════════════════════════";
    private static final String SOUS_LIGNE = "──────────────────────────────────────────────────────────────────────────────";

    // Composants de l'interface
    private final Scanner scanner;
    private final UtilisateurController utilisateurController;
    private final TrajetController trajetController;
    private final ReservationController reservationController;
    private final AdminController adminController;
    private AuthUI authUI;

    /**
     * Constructeur initialisant les contrôleurs nécessaires.
     */
    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.utilisateurController = new UtilisateurController(scanner, this);
        this.trajetController = new TrajetController(scanner);
        this.reservationController = new ReservationController(scanner);
        this.adminController = new AdminController(scanner, this);
        this.authUI = new AuthUI();
    }

    /**
     * Point d'entrée de l'application.
     * Démarre l'interface utilisateur et gère la navigation principale.
     */
    public void demarrer() {
        afficherBienvenue();
        boolean continuer = true;

        while (continuer) {
            afficherMenuPrincipal();

            String choix = scanner.nextLine().trim();

            switch (choix) {
                case "1":
                    // Inscription utilisateur
                    Optional<Utilisateur> nouvelUtilisateur = utilisateurController.inscription();
                    nouvelUtilisateur.ifPresent(this::menuUtilisateur);
                    break;
                case "2":
                    // Connexion utilisateur
                    Optional<Utilisateur> utilisateur = utilisateurController.connexion();
                    utilisateur.ifPresent(this::menuUtilisateur);
                    break;
                case "3":
                    // Connexion conducteur
                    Optional<Conducteur> conducteur = connexionConducteur();
                    conducteur.ifPresent(this::menuConducteur);
                    break;
                case "4":
                    // Connexion administrateur
                    Optional<Administrateur> admin = adminController.connexion();
                    admin.ifPresent(this::menuAdmin);
                    break;
                case "0":
                    // Quitter
                    afficherAuRevoir();
                    continuer = false;
                    break;
                default:
                    afficherMessageErreur("Option invalide. Veuillez réessayer.");
                    break;
            }
        }

        scanner.close();
    }

    /**
     * Affiche un message de bienvenue au démarrage de l'application.
     */
    private void afficherBienvenue() {
        System.out.println("\n" + LIGNE_SEPARATION);
        System.out.println("               🚗 BIENVENUE DANS L'APPLICATION DE COVOITURAGE 🚗");
        System.out.println("    Économisez de l'argent, réduisez votre empreinte carbone et faites des rencontres");
        System.out.println(LIGNE_SEPARATION);
    }

    /**
     * Affiche un message d'au revoir lors de la fermeture de l'application.
     */
    private void afficherAuRevoir() {
        System.out.println("\n" + LIGNE_SEPARATION);
        System.out.println("         Merci d'avoir utilisé notre application de covoiturage.");
        System.out.println("                        À bientôt sur nos routes! 🚗");
        System.out.println(LIGNE_SEPARATION);
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
     * Affiche le menu principal de l'application.
     */
    private void afficherMenuPrincipal() {
        System.out.println("\n" + LIGNE_SEPARATION);
        System.out.println("                    APPLICATION DE COVOITURAGE - MENU PRINCIPAL");
        System.out.println(LIGNE_SEPARATION);
        System.out.println("1. S'inscrire comme utilisateur");
        System.out.println("2. Se connecter comme utilisateur");
        System.out.println("3. Se connecter comme conducteur");
        System.out.println("4. Se connecter comme administrateur");
        System.out.println("0. Quitter");
        System.out.println(SOUS_LIGNE);
        System.out.print("➤ Votre choix : ");
    }

    /**
     * Affiche et gère le menu utilisateur.
     * @param utilisateur L'utilisateur connecté
     */
    private void menuUtilisateur(Utilisateur utilisateur) {
        boolean continuer = true;

        while (continuer) {
            System.out.println("\n" + LIGNE_SEPARATION);
            System.out.println("                    MENU UTILISATEUR - " + utilisateur.getPrenom() + " " + utilisateur.getNom());
            System.out.println(LIGNE_SEPARATION);
            System.out.println("1. Rechercher un trajet");
            System.out.println("2. Réserver un trajet");
            System.out.println("3. Voir mes réservations");
            System.out.println("4. Annuler une réservation");
            System.out.println("5. Payer une réservation");
            System.out.println("6. Modifier mon profil");
            System.out.println("7. Voir mon profil");
            System.out.println("8. Devenir conducteur");
            System.out.println("0. Déconnexion");
            System.out.println(SOUS_LIGNE);
            System.out.print("➤ Votre choix : ");

            String choix = scanner.nextLine().trim();

            switch (choix) {
                case "1":
                    trajetController.rechercherTrajets(utilisateur);
                    break;
                case "2":
                    reservationController.reserverTrajet(utilisateur);
                    break;
                case "3":
                    reservationController.afficherReservations(utilisateur);
                    break;
                case "4":
                    reservationController.annulerReservation(utilisateur);
                    break;
                case "5":
                    reservationController.payerReservation(utilisateur);
                    break;
                case "6":
                    utilisateurController.modifierProfil(utilisateur);
                    break;
                case "7":
                    utilisateurController.afficherProfil(utilisateur);
                    break;
                case "8":
                    devenirConducteur(utilisateur);
                    continuer = false; // Redirection vers menu conducteur
                    break;
                case "0":
                    afficherMessageSucces("Déconnexion effectuée avec succès.");
                    continuer = false;
                    break;
                default:
                    afficherMessageErreur("Option invalide. Veuillez réessayer.");
                    break;
            }
        }
    }

    /**
     * Affiche et gère le menu conducteur.
     * @param conducteur Le conducteur connecté
     */
    private void menuConducteur(Conducteur conducteur) {
        boolean continuer = true;

        while (continuer) {
            System.out.println("\n" + LIGNE_SEPARATION);
            System.out.println("                    MENU CONDUCTEUR - " + conducteur.getPrenom() + " " + conducteur.getNom());
            System.out.println(LIGNE_SEPARATION);
            System.out.println("1. Proposer un trajet");
            System.out.println("2. Voir mes trajets");
            System.out.println("3. Modifier un trajet");
            System.out.println("4. Annuler un trajet");
            System.out.println("5. Réactiver un trajet annulé");
            System.out.println("6. Supprimer un trajet");
            System.out.println("7. Modifier mon profil");
            System.out.println("8. Voir mon profil");
            System.out.println("9. Gérer les réservations");
            System.out.println("0. Déconnexion");
            System.out.println(SOUS_LIGNE);
            System.out.print("➤ Votre choix : ");

            String choix = scanner.nextLine().trim();

            switch (choix) {
                case "1":
                    trajetController.creerTrajet(conducteur);
                    break;
                case "2":
                    List<Trajet> trajets = ServiceFactory.getConducteurService().getTrajetsByConducteur(conducteur.getId());
                    if (trajets.isEmpty()) {
                        afficherMessageErreur("Vous n'avez pas encore proposé de trajets.");
                    } else {
                        trajetController.afficherListeTrajets(trajets);
                    }
                    break;
                case "3":
                    trajetController.modifierTrajet(conducteur);
                    break;
                case "4":
                    trajetController.annulerTrajet(conducteur);
                    break;
                case "5":
                    trajetController.reactiverTrajet(conducteur);
                    break;
                case "6":
                    trajetController.supprimerTrajet(conducteur);
                    break;
                case "7":
                    modifierProfilConducteur(conducteur);
                    break;
                case "8":
                    afficherProfilConducteur(conducteur);
                    break;
                case "9":
                    reservationController.gererReservationsEnAttente(conducteur);
                    break;
                case "0":
                    afficherMessageSucces("Déconnexion effectuée avec succès.");
                    continuer = false;
                    break;
                default:
                    afficherMessageErreur("Option invalide. Veuillez réessayer.");
                    break;
            }
        }
    }

    /**
     * Affiche et gère le menu administrateur.
     * @param admin L'administrateur connecté
     */
    private void menuAdmin(Administrateur admin) {
        boolean continuer = true;

        while (continuer) {
            System.out.println("\n" + LIGNE_SEPARATION);
            System.out.println("                    MENU ADMINISTRATEUR - " + admin.getPrenom() + " " + admin.getNom());
            System.out.println(LIGNE_SEPARATION);
            System.out.println("1. Gérer les utilisateurs");
            System.out.println("2. Gérer les trajets");
            System.out.println("3. Générer des rapports");
            System.out.println("0. Déconnexion");
            System.out.println(SOUS_LIGNE);
            System.out.print("➤ Votre choix : ");

            String choix = scanner.nextLine().trim();

            switch (choix) {
                case "1":
                    adminController.gererUtilisateurs();
                    break;
                case "2":
                    adminController.gererTrajets();
                    break;
                case "3":
                    adminController.genererRapports();
                    break;
                case "0":
                    afficherMessageSucces("Déconnexion effectuée avec succès.");
                    continuer = false;
                    break;
                default:
                    afficherMessageErreur("Option invalide. Veuillez réessayer.");
                    break;
            }
        }
    }


    /**
     * Gère la connexion d'un conducteur.
     * @return Un Optional contenant le conducteur connecté, ou vide si échec
     */
    private Optional<Conducteur> connexionConducteur() {
        System.out.println("\n" + LIGNE_SEPARATION);
        System.out.println("                    CONNEXION CONDUCTEUR");
        System.out.println(SOUS_LIGNE);

        String[] identifiants = authUI.lireIdentifiantsSecurises();
        if (identifiants == null) {
            System.out.println("Connexion annulée.");
            return Optional.empty();
        }
        String email = identifiants[0];
        String motDePasse = identifiants[1];

        try {
            Optional<Conducteur> conducteur = ServiceFactory.getConducteurService().authentifier(email, motDePasse);
            if (conducteur.isPresent()) {
                afficherMessageSucces("Connexion réussie ! Bienvenue " + conducteur.get().getPrenom() + " !");
                return conducteur;
            } else {
                afficherMessageErreur("Email ou mot de passe incorrect.");
                return Optional.empty();
            }
        } catch (Exception e) {
            afficherMessageErreur("Erreur lors de la connexion : " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Permet à un utilisateur de devenir conducteur.
     * @param utilisateur L'utilisateur qui souhaite devenir conducteur
     */
    private void devenirConducteur(Utilisateur utilisateur) {
        System.out.println("\n" + LIGNE_SEPARATION);
        System.out.println("                    DEVENIR CONDUCTEUR");
        System.out.println(SOUS_LIGNE);

        String numeroPermis = "";
        while (numeroPermis.isEmpty()) {
            System.out.print("➤ Numéro de permis de conduire : ");
            numeroPermis = scanner.nextLine().trim();
            if (numeroPermis.isEmpty()) {
                afficherMessageErreur("Le numéro de permis ne peut pas être vide.");
            }
        }

        System.out.print("➤ Informations sur votre véhicule (marque, modèle, immatriculation) : ");
        String vehiculeInfo = scanner.nextLine().trim();

        // Création du conducteur à partir des informations de l'utilisateur
        Conducteur conducteur = new Conducteur(
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getMotDePasse(),
                utilisateur.getTelephone(),
                numeroPermis
        );
        conducteur.setVehiculeInfo(vehiculeInfo);

        try {
            Long id = ServiceFactory.getConducteurService().creerConducteur(conducteur);
            if (id != null) {
                afficherMessageSucces("Félicitations ! Vous êtes maintenant un conducteur.");
                conducteur.setId(id);
                menuConducteur(conducteur);
            } else {
                afficherMessageErreur("Erreur lors de la création du profil conducteur.");
            }
        } catch (Exception e) {
            afficherMessageErreur("Erreur : " + e.getMessage());
        }
    }

    /**
     * Permet à un conducteur de modifier son profil.
     * @param conducteur Le conducteur qui souhaite modifier son profil
     */
    private void modifierProfilConducteur(Conducteur conducteur) {
        System.out.println("\n" + LIGNE_SEPARATION);
        System.out.println("                    MODIFICATION DU PROFIL CONDUCTEUR");
        System.out.println(SOUS_LIGNE);
        System.out.println("Laissez vide pour conserver la valeur actuelle.");

        System.out.print("➤ Nom [" + conducteur.getNom() + "] : ");
        String nom = scanner.nextLine().trim();
        if (!nom.isEmpty()) {
            conducteur.setNom(nom);
        }

        System.out.print("➤ Prénom [" + conducteur.getPrenom() + "] : ");
        String prenom = scanner.nextLine().trim();
        if (!prenom.isEmpty()) {
            conducteur.setPrenom(prenom);
        }

        System.out.print("➤ Téléphone [" + conducteur.getTelephone() + "] : ");
        String telephone = scanner.nextLine().trim();
        if (!telephone.isEmpty()) {
            if (InputValidator.isValidTelephone(telephone)) {
                conducteur.setTelephone(telephone);
            } else {
                afficherMessageErreur("Format de téléphone invalide, ancienne valeur conservée.");
            }
        }

        System.out.print("➤ Mot de passe (6 caractères minimum, laissez vide pour ne pas changer) : ");
        String motDePasse = scanner.nextLine().trim();
        if (!motDePasse.isEmpty()) {
            if (InputValidator.isValidPassword(motDePasse)) {
                conducteur.setMotDePasse(motDePasse);
            } else {
                afficherMessageErreur("Mot de passe trop court, ancien mot de passe conservé.");
            }
        }

        System.out.print("➤ Numéro de permis [" + conducteur.getNumeroPermis() + "] : ");
        String numeroPermis = scanner.nextLine().trim();
        if (!numeroPermis.isEmpty()) {
            conducteur.setNumeroPermis(numeroPermis);
        }

        System.out.print("➤ Informations sur votre véhicule [" + conducteur.getVehiculeInfo() + "] : ");
        String vehiculeInfo = scanner.nextLine().trim();
        if (!vehiculeInfo.isEmpty()) {
            conducteur.setVehiculeInfo(vehiculeInfo);
        }

        try {
            boolean success = ServiceFactory.getConducteurService().modifierConducteur(conducteur);
            if (success) {
                afficherMessageSucces("Profil mis à jour avec succès !");
            } else {
                afficherMessageErreur("Erreur lors de la mise à jour du profil.");
            }
        } catch (Exception e) {
            afficherMessageErreur("Erreur lors de la mise à jour du profil : " + e.getMessage());
        }
    }

    /**
     * Affiche le profil d'un conducteur.
     * @param conducteur Le conducteur dont on affiche le profil
     */
    private void afficherProfilConducteur(Conducteur conducteur) {
        System.out.println("\n" + LIGNE_SEPARATION);
        System.out.println("                    PROFIL CONDUCTEUR");
        System.out.println(SOUS_LIGNE);
        System.out.println("🧑 Nom: " + conducteur.getNom());
        System.out.println("🧑 Prénom: " + conducteur.getPrenom());
        System.out.println("📧 Email: " + conducteur.getEmail());
        System.out.println("📱 Téléphone: " + conducteur.getTelephone());
        System.out.println("🪪 Numéro de permis: " + conducteur.getNumeroPermis());
        System.out.println("🚗 Véhicule: " + conducteur.getVehiculeInfo());
        System.out.println(SOUS_LIGNE);

        // Pause pour permettre à l'utilisateur de lire les informations
        System.out.print("Appuyez sur Entrée pour continuer...");
        scanner.nextLine();
    }
}