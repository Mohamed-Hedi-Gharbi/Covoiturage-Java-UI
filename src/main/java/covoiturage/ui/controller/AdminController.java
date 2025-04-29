package covoiturage.ui.controller;

import covoiturage.model.Administrateur;
import covoiturage.model.Conducteur;
import covoiturage.model.Trajet;
import covoiturage.model.Utilisateur;
import covoiturage.service.*;
import covoiturage.ui.ConsoleUI;
import covoiturage.ui.validator.InputValidator;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;

public class AdminController {
    private AdminService adminService;
    private UtilisateurService utilisateurService;
    private ConducteurService conducteurService;
    private TrajetService trajetService;
    private Scanner scanner;
    private ConsoleUI consoleUI;

    public AdminController(Scanner scanner, ConsoleUI consoleUI) {
        this.adminService = ServiceFactory.getAdminService();
        this.utilisateurService = ServiceFactory.getUtilisateurService();
        this.conducteurService = ServiceFactory.getConducteurService();
        this.trajetService = ServiceFactory.getTrajetService();
        this.scanner = scanner;
        this.consoleUI = consoleUI;
    }

    public Optional<Administrateur> connexion() {
        System.out.println("\n=== CONNEXION ADMINISTRATEUR ===");

        System.out.print("Email : ");
        String email = scanner.nextLine().trim();

        // Utiliser la méthode sécurisée via ConsoleUI
        String motDePasse = consoleUI.lireMotDePasseSecurise("Mot de passe : ");

        try {
            Optional<Administrateur> admin = adminService.authentifier(email, motDePasse);
            if (admin.isPresent()) {
                System.out.println("Connexion réussie ! Bienvenue " + admin.get().getPrenom() + " !");
                return admin;
            } else {
                System.out.println("Email ou mot de passe incorrect.");
                return Optional.empty();
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la connexion : " + e.getMessage());
            return Optional.empty();
        }
    }


    public void gererUtilisateurs() {
        System.out.println("\n=== GESTION DES UTILISATEURS ===");

        System.out.println("1. Lister tous les utilisateurs");
        System.out.println("2. Lister tous les conducteurs");
        System.out.println("3. Créer un nouvel administrateur");
        System.out.println("4. Supprimer un utilisateur");
        System.out.println("0. Retour");

        System.out.print("\nVotre choix : ");
        String choix = scanner.nextLine().trim();

        switch (choix) {
            case "1":
                afficherUtilisateurs();
                break;
            case "2":
                afficherConducteurs();
                break;
            case "3":
                creerAdmin();
                break;
            case "4":
                supprimerUtilisateur();
                break;
            case "0":
                return;
            default:
                System.out.println("Choix invalide.");
                break;
        }
    }

    public void afficherUtilisateurs() {
        System.out.println("\n=== LISTE DES UTILISATEURS ===");

        List<Utilisateur> utilisateurs = utilisateurService.getAllUtilisateurs();

        if (utilisateurs.isEmpty()) {
            System.out.println("Aucun utilisateur enregistré.");
            return;
        }

        System.out.println(String.format("%-5s %-15s %-15s %-30s %-15s",
                "ID", "NOM", "PRÉNOM", "EMAIL", "TÉLÉPHONE"));
        System.out.println("---------------------------------------------------------------------------------");

        for (Utilisateur utilisateur : utilisateurs) {
            System.out.println(String.format("%-5d %-15s %-15s %-30s %-15s",
                    utilisateur.getId(),
                    utilisateur.getNom(),
                    utilisateur.getPrenom(),
                    utilisateur.getEmail(),
                    utilisateur.getTelephone()));
        }
    }

    public void afficherConducteurs() {
        System.out.println("\n=== LISTE DES CONDUCTEURS ===");

        List<Conducteur> conducteurs = conducteurService.getAllConducteurs();

        if (conducteurs.isEmpty()) {
            System.out.println("Aucun conducteur enregistré.");
            return;
        }

        System.out.println(String.format("%-5s %-15s %-15s %-30s %-15s %-20s",
                "ID", "NOM", "PRÉNOM", "EMAIL", "TÉLÉPHONE", "NUMÉRO PERMIS"));
        System.out.println("---------------------------------------------------------------------------------------------------");

        for (Conducteur conducteur : conducteurs) {
            System.out.println(String.format("%-5d %-15s %-15s %-30s %-15s %-20s",
                    conducteur.getId(),
                    conducteur.getNom(),
                    conducteur.getPrenom(),
                    conducteur.getEmail(),
                    conducteur.getTelephone(),
                    conducteur.getNumeroPermis()));
        }
    }

    public void creerAdmin() {
        System.out.println("\n=== CRÉATION D'UN ADMINISTRATEUR ===");

        String nom = "";
        while (nom.isEmpty()) {
            System.out.print("Nom : ");
            nom = scanner.nextLine().trim();
            if (nom.isEmpty()) {
                System.out.println("Le nom ne peut pas être vide.");
            }
        }

        String prenom = "";
        while (prenom.isEmpty()) {
            System.out.print("Prénom : ");
            prenom = scanner.nextLine().trim();
            if (prenom.isEmpty()) {
                System.out.println("Le prénom ne peut pas être vide.");
            }
        }

        String email        = "";
        boolean emailValide = false;
        while (!emailValide) {
            System.out.print("Email : ");
            email = scanner.nextLine().trim();

            if (!InputValidator.isValidEmail(email)) {
                System.out.println("Format d'email invalide.");
                continue;
            }

            if (adminService.getAdminByEmail(email).isPresent()) {
                System.out.println("Cet email est déjà utilisé.");
                continue;
            }

            emailValide = true;
        }

        String motDePasse = "";
        while (motDePasse.isEmpty() || !InputValidator.isValidPassword(motDePasse)) {
            System.out.print("Mot de passe (6 caractères minimum) : ");
            motDePasse = scanner.nextLine().trim();

            if (!InputValidator.isValidPassword(motDePasse)) {
                System.out.println("Le mot de passe doit contenir au moins 6 caractères.");
            }
        }

        String telephone = "";
        while (telephone.isEmpty() || !InputValidator.isValidTelephone(telephone)) {
            System.out.print("Téléphone (8 chiffres) : ");
            telephone = scanner.nextLine().trim();

            if (!InputValidator.isValidTelephone(telephone)) {
                System.out.println("Le numéro de téléphone doit contenir 8 chiffres.");
            }
        }

        System.out.print("Rôle : ");
        String role = scanner.nextLine().trim();
        if (role.isEmpty()) {
            role = "Admin";
        }

        Administrateur admin = new Administrateur(nom, prenom, email, motDePasse, telephone, role);

        try {
            Long id = adminService.creerAdmin(admin);
            if (id != null) {
                System.out.println("Administrateur créé avec succès ! ID : " + id);
            } else {
                System.out.println("Erreur lors de la création de l'administrateur.");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la création de l'administrateur : " + e.getMessage());
        }
    }

    public void supprimerUtilisateur() {
        System.out.println("\n=== SUPPRESSION D'UN UTILISATEUR ===");

        System.out.println("1. Supprimer un utilisateur");
        System.out.println("2. Supprimer un conducteur");
        System.out.println("3. Supprimer un administrateur");
        System.out.println("0. Retour");

        System.out.print("\nVotre choix : ");
        String choix = scanner.nextLine().trim();

        switch (choix) {
            case "1":
                afficherUtilisateurs();
                supprimerPersonne("utilisateur", id -> utilisateurService.supprimerUtilisateur(id));
                break;
            case "2":
                afficherConducteurs();
                supprimerPersonne("conducteur", id -> conducteurService.supprimerConducteur(id));
                break;
            case "3":
                afficherAdministrateurs();
                supprimerPersonne("administrateur", id -> adminService.supprimerAdmin(id));
                break;
            case "0":
                return;
            default:
                System.out.println("Choix invalide.");
                break;
        }
    }

    private void afficherAdministrateurs() {
        System.out.println("\n=== LISTE DES ADMINISTRATEURS ===");

        List<Administrateur> admins = adminService.getAllAdmins();

        if (admins.isEmpty()) {
            System.out.println("Aucun administrateur enregistré.");
            return;
        }

        System.out.println(String.format("%-5s %-15s %-15s %-30s %-15s %-15s",
                "ID", "NOM", "PRÉNOM", "EMAIL", "TÉLÉPHONE", "RÔLE"));
        System.out.println("-----------------------------------------------------------------------------------------");

        for (Administrateur admin : admins) {
            System.out.println(String.format("%-5d %-15s %-15s %-30s %-15s %-15s",
                    admin.getId(),
                    admin.getNom(),
                    admin.getPrenom(),
                    admin.getEmail(),
                    admin.getTelephone(),
                    admin.getRole()));
        }
    }

    private void supprimerPersonne(String type, Function<Long, Boolean> supprimerFunction) {
        System.out.print("\nEntrez l'ID du " + type + " à supprimer : ");
        String idStr = scanner.nextLine().trim();

        if (!InputValidator.isValidInteger(idStr)) {
            System.out.println("ID invalide.");
            return;
        }

        Long id = Long.parseLong(idStr);

        System.out.print("Êtes-vous sûr de vouloir supprimer ce " + type + " ? (o/n) : ");
        String confiramation = scanner.nextLine().trim().toLowerCase();

        if (confiramation.equals("o") || confiramation.equals("oui")) {
            try {
                boolean success = supprimerFunction.apply(id);
                if (success) {
                    System.out.println(type.substring(0, 1).toUpperCase() + type.substring(1) + " supprimé avec succès !");
                } else {
                    System.out.println("Erreur lors de la suppression du " + type + ".");
                }
            } catch (Exception e) {
                System.out.println("Erreur lors de la suppression : " + e.getMessage());
            }
        } else {
            System.out.println("Suppression abandonnée.");
        }
    }

    public void gererTrajets() {
        System.out.println("\n=== GESTION DES TRAJETS ===");

        List<Trajet> trajets = trajetService.getAllTrajets();

        if (trajets.isEmpty()) {
            System.out.println("Aucun trajet enregistré.");
            return;
        }

        TrajetController trajetController = new TrajetController(scanner);
        trajetController.afficherListeTrajets(trajets);

        System.out.println("\n1. Voir les détails d'un trajet");
        System.out.println("2. Supprimer un trajet");
        System.out.println("0. Retour");

        System.out.print("\nVotre choix : ");
        String choix = scanner.nextLine().trim();

        switch (choix) {
            case "1":
                voirDetailsTrajet();
                break;
            case "2":
                supprimerTrajet();
                break;
            case "0":
                return;
            default:
                System.out.println("Choix invalide.");
                break;
        }
    }

    private void voirDetailsTrajet() {
        System.out.print("\nEntrez l'ID du trajet : ");
        String idStr = scanner.nextLine().trim();

        if (!InputValidator.isValidInteger(idStr)) {
            System.out.println("ID invalide.");
            return;
        }

        Long trajetId = Long.parseLong(idStr);
        TrajetController trajetController = new TrajetController(scanner);
        trajetController.afficherDetailTrajet(trajetId);
    }

    private void supprimerTrajet() {
        System.out.print("\nEntrez l'ID du trajet à supprimer : ");
        String idStr = scanner.nextLine().trim();

        if (!InputValidator.isValidInteger(idStr)) {
            System.out.println("ID invalide.");
            return;
        }

        Long trajetId = Long.parseLong(idStr);

        System.out.print("Êtes-vous sûr de vouloir supprimer ce trajet ? (o/n) : ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("o") || confirmation.equals("oui")) {
            try {
                // Appeler directement le service de gestion des trajets pour la suppression
                boolean success = ServiceFactory.getTrajetService().deleteTrajet(trajetId);
                if (success) {
                    System.out.println("Trajet supprimé avec succès !");
                } else {
                    System.out.println("Erreur lors de la suppression du trajet.");
                }
            } catch (Exception e) {
                System.out.println("Erreur lors de la suppression : " + e.getMessage());
            }
        } else {
            System.out.println("Suppression abandonnée.");
        }
    }

    public void genererRapports() {
        System.out.println("\n=== GÉNÉRATION DE RAPPORTS ===");

        System.out.println("1. Rapport des utilisateurs");
        System.out.println("2. Rapport des trajets");
        System.out.println("3. Rapport des réservations");
        System.out.println("0. Retour");

        System.out.print("\nVotre choix : ");
        String choix = scanner.nextLine().trim();

        switch (choix) {
            case "1":
                adminService.genererRapportUtilisateurs();
                System.out.println("Rapport des utilisateurs généré avec succès.");
                break;
            case "2":
                adminService.genererRapportTrajets();
                System.out.println("Rapport des trajets généré avec succès.");
                break;
            case "3":
                adminService.genererRapportReservations();
                System.out.println("Rapport des réservations généré avec succès.");
                break;
            case "0":
                return;
            default:
                System.out.println("Choix invalide.");
                break;
        }
    }
}
