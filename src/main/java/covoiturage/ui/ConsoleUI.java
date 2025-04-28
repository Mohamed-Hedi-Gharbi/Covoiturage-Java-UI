package covoiturage.ui;

import covoiturage.model.Administrateur;
import covoiturage.model.Conducteur;
import covoiturage.model.Trajet;
import covoiturage.model.Utilisateur;
import covoiturage.service.ServiceFactory;
import covoiturage.ui.controller.AdminController;
import covoiturage.ui.controller.ReservationController;
import covoiturage.ui.controller.TrajetController;
import covoiturage.ui.controller.UtilisateurController;
import covoiturage.ui.validator.InputValidator;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleUI {
    private Scanner scanner;
    private UtilisateurController utilisateurController;
    private TrajetController trajetController;
    private ReservationController reservationController;
    private AdminController adminController;

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.utilisateurController = new UtilisateurController(scanner);
        this.trajetController = new TrajetController(scanner);
        this.reservationController = new ReservationController(scanner);
        this.adminController = new AdminController(scanner);
    }

    public void demarrer() {
        boolean continuer = true;

        while (continuer) {
            afficherMenuPrincipal();

            String choix = scanner.nextLine().trim();

            switch (choix) {
                case "1":
                    // Inscription utilisateur
                    Optional<Utilisateur> nouvelUtilisateur = utilisateurController.inscription();
                    if (nouvelUtilisateur.isPresent()) {
                        menuUtilisateur(nouvelUtilisateur.get());
                    }
                    break;
                case "2":
                    // Connexion utilisateur
                    Optional<Utilisateur> utilisateur = utilisateurController.connexion();
                    if (utilisateur.isPresent()) {
                        menuUtilisateur(utilisateur.get());
                    }
                    break;
                case "3":
                    // Connexion conducteur
                    Optional<Conducteur> conducteur = connexionConducteur();
                    if (conducteur.isPresent()) {
                        menuConducteur(conducteur.get());
                    }
                    break;
                case "4":
                    // Connexion administrateur
                    Optional<Administrateur> admin = adminController.connexion();
                    if (admin.isPresent()) {
                        menuAdmin(admin.get());
                    }
                    break;
                case "0":
                    // Quitter
                    System.out.println("Merci d'avoir utilisé notre application de covoiturage. À bientôt !");
                    continuer = false;
                    break;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
                    break;
            }
        }

        scanner.close();
    }

    private void afficherMenuPrincipal() {
        System.out.println("\n====== APPLICATION DE COVOITURAGE ======");
        System.out.println("1. S'inscrire comme utilisateur");
        System.out.println("2. Se connecter comme utilisateur");
        System.out.println("3. Se connecter comme conducteur");
        System.out.println("4. Se connecter comme administrateur");
        System.out.println("0. Quitter");
        System.out.print("\nVotre choix : ");
    }

    private void menuUtilisateur(Utilisateur utilisateur) {
        boolean continuer = true;

        while (continuer) {
            System.out.println("\n===== MENU UTILISATEUR - " + utilisateur.getPrenom() + " " + utilisateur.getNom() + " =====");
            System.out.println("1. Rechercher un trajet");
            System.out.println("2. Réserver un trajet");
            System.out.println("3. Voir mes réservations");
            System.out.println("4. Annuler une réservation");
            System.out.println("5. Payer une réservation");
            System.out.println("6. Modifier mon profil");
            System.out.println("7. Voir mon profil");
            System.out.println("8. Devenir conducteur");
            System.out.println("0. Déconnexion");
            System.out.print("\nVotre choix : ");

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
                    System.out.println("Déconnexion...");
                    continuer = false;
                    break;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
                    break;
            }
        }
    }

    private void menuConducteur(Conducteur conducteur) {
        boolean continuer = true;

        while (continuer) {
            System.out.println("\n===== MENU CONDUCTEUR - " + conducteur.getPrenom() + " " + conducteur.getNom() + " =====");
            System.out.println("1. Proposer un trajet");
            System.out.println("2. Voir mes trajets");
            System.out.println("3. Modifier un trajet");
            System.out.println("4. Annuler un trajet");
            System.out.println("5. Modifier mon profil");
            System.out.println("6. Voir mon profil");
            System.out.println("0. Déconnexion");
            System.out.print("\nVotre choix : ");

            String choix = scanner.nextLine().trim();

            switch (choix) {
                case "1":
                    trajetController.creerTrajet(conducteur);
                    break;
                case "2":
                    List<Trajet> trajets = ServiceFactory.getConducteurService().getTrajetsByConducteur(conducteur.getId());
                    trajetController.afficherListeTrajets(trajets);
                    break;
                case "3":
                    trajetController.modifierTrajet(conducteur);
                    break;
                case "4":
                    trajetController.annulerTrajet(conducteur);
                    break;
                case "5":
                    modifierProfilConducteur(conducteur);
                    break;
                case "6":
                    afficherProfilConducteur(conducteur);
                    break;
                case "0":
                    System.out.println("Déconnexion...");
                    continuer = false;
                    break;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
                    break;
            }
        }
    }

    private void menuAdmin(Administrateur admin) {
        boolean continuer = true;

        while (continuer) {
            System.out.println("\n===== MENU ADMINISTRATEUR - " + admin.getPrenom() + " " + admin.getNom() + " =====");
            System.out.println("1. Gérer les utilisateurs");
            System.out.println("2. Gérer les trajets");
            System.out.println("3. Générer des rapports");
            System.out.println("0. Déconnexion");
            System.out.print("\nVotre choix : ");

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
                    System.out.println("Déconnexion...");
                    continuer = false;
                    break;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
                    break;
            }
        }
    }

    private Optional<Conducteur> connexionConducteur() {
        System.out.println("\n=== CONNEXION CONDUCTEUR ===");

        System.out.println("Email : ");
        String email = scanner.nextLine().trim();


        System.out.println("Mot de passe : ");
        String motDePasse = scanner.nextLine().trim();

        try {
            Optional<Conducteur> conducteur = ServiceFactory.getConducteurService().authentifier(email, motDePasse);
            if (conducteur.isPresent()) {
                System.out.println("Connexion réussie ! Bienvenue " + conducteur.get().getPrenom() + " !");
                return conducteur;
            } else {
                System.out.println("Email ou mot de passe incorrect.");
                return Optional.empty();
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la connexion : " + e.getMessage());
            return Optional.empty();
        }
    }

    private void devenirConducteur(Utilisateur utilisateur) {
        System.out.println("\n=== DEVENIR CONDUCTEUR ===");

        String numeroPermis = "";
        while (numeroPermis.isEmpty()) {
            System.out.print("Numéro de permis de conduire : ");
            numeroPermis = scanner.nextLine().trim();
            if (numeroPermis.isEmpty()) {
                System.out.println("Le numéro de permis ne peut pas être vide.");
            }
        }

        System.out.print("Informations sur votre véhicule (marque, modèle, immatriculation) : ");
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
                System.out.println("Félicitations ! Vous êtes maintenant un conducteur.");
                conducteur.setId(id);
                menuConducteur(conducteur);
            } else {
                System.out.println("Erreur lors de la création du profil conducteur.");
            }
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private void modifierProfilConducteur(Conducteur conducteur){
        System.out.println("\n=== MODIFICATION DU PROFIL CONDUCTEUR ===");

        System.out.println("Laissez vide pour conserver la valeur actuelle.");

        System.out.print("Nom [" + conducteur.getNom() + "] : ");
        String nom = scanner.nextLine().trim();
        if (!nom.isEmpty()) {
            conducteur.setNom(nom);
        }

        System.out.print("Prénom [" + conducteur.getPrenom() + "] : ");
        String prenom = scanner.nextLine().trim();
        if (!prenom.isEmpty()) {
            conducteur.setPrenom(prenom);
        }

        System.out.print("Téléphone [" + conducteur.getTelephone() + "] : ");
        String telephone = scanner.nextLine().trim();
        if (!telephone.isEmpty()) {
            if (InputValidator.isValidTelephone(telephone)) {
                conducteur.setTelephone(telephone);
            } else {
                System.out.println("Format de téléphone invalide, ancienne valeur conservée.");
            }
        }

        System.out.print("Mot de passe (6 caractères minimum, laissez vide pour ne pas changer) : ");
        String motDePasse = scanner.nextLine().trim();
        if (!motDePasse.isEmpty()) {
            if (InputValidator.isValidPassword(motDePasse)) {
                conducteur.setMotDePasse(motDePasse);
            } else {
                System.out.println("Mot de passe trop court, ancien mot de passe conservé.");
            }
        }

        System.out.println("Numéro de permis [" + conducteur.getNumeroPermis() + "] : ");
        String numeroPermis = scanner.nextLine().trim();
        if (!numeroPermis.isEmpty()) {
            conducteur.setNumeroPermis(numeroPermis);
        }

        System.out.print("Informations sur votre véhicule [" + conducteur.getVehiculeInfo() + "] : ");
        String vehiculeInfo = scanner.nextLine().trim();
        if (!vehiculeInfo.isEmpty()) {
            conducteur.setVehiculeInfo(vehiculeInfo);
        }

        try {
            boolean success = ServiceFactory.getConducteurService().modifierConducteur(conducteur);
            if (success) {
                System.out.println("Profil mis à jour avec succès !");
            } else {
                System.out.println("Erreur lors de la mise à jour du profil.");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la mise à jour du profil : " + e.getMessage());
        }
    }

    private void afficherProfilConducteur(Conducteur conducteur) {
        System.out.println("\n=== PROFIL CONDUCTEUR ===");
        System.out.println("Nom: " + conducteur.getNom());
        System.out.println("Prénom: " + conducteur.getPrenom());
        System.out.println("Email: " + conducteur.getEmail());
        System.out.println("Téléphone: " + conducteur.getTelephone());
        System.out.println("Numéro de permis: " + conducteur.getNumeroPermis());
        System.out.println("Véhicule: " + conducteur.getVehiculeInfo());
    }
}
