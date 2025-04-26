package covoiturage.ui.controller;

import covoiturage.model.Utilisateur;
import covoiturage.service.ServiceFactory;
import covoiturage.service.UtilisateurService;
import covoiturage.ui.validator.InputValidator;
import jdk.jshell.execution.Util;

import java.util.Optional;
import java.util.Scanner;

public class UtilisateurController {
    private UtilisateurService utilisateurService;
    private Scanner scanner;

    public UtilisateurController(Scanner scanner) {
        this.utilisateurService = ServiceFactory.getUtilisateurService();
        this.scanner = scanner;
    }

    public Optional<Utilisateur> inscription() {
        System.out.println("\n=== INSCRIPTION UTILISATEUR ===");

        String nom = "";
        while (nom.isEmpty()) {
            System.out.println("Nom : ");
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

        String email = "";
        boolean emailValide = false;
        while (!emailValide) {
            System.out.print("Email : ");
            email = scanner.nextLine().trim();

            if (!InputValidator.isValidEmail(email)) {
                System.out.println("Format d'email invalide.");
                continue;
            }

            if (utilisateurService.getUtilisateurByEmail(email).isPresent()) {
                System.out.println("Cet email est déjà utilisé.");
                continue;
            }

            emailValide = true;
        }

        String motDePasse = "";
        while (motDePasse.isEmpty() || !InputValidator.isValidPassword(motDePasse)) {
            System.out.println("Mot de passe (6 caractères minimum) :");
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

        System.out.print("Préférences (optionnel) : ");
        String preferences = scanner.nextLine().trim();

        Utilisateur utilisateur = new Utilisateur(nom, prenom, email, motDePasse, telephone);
        utilisateur.setPreferences(preferences);

        try {
            Long id = utilisateurService.creerUtilisateur(utilisateur);
            utilisateur.setId(id);
            System.out.println("Inscription réussie !");
            return Optional.of(utilisateur);
        } catch (Exception e) {
            System.out.println("Erreur lors de l'inscription : " + e.getMessage());
            return Optional.empty();
        }
    }


    public Optional<Utilisateur> connexion() {
        System.out.println("\n=== CONNEXION UTILISATEUR ===");

        System.out.print("Email : ");
        String email = scanner.nextLine().trim();

        System.out.print("Mot de passe : ");
        String motDePasse = scanner.nextLine().trim();

        try {
            Optional<Utilisateur> utilisateur = utilisateurService.authentifier(email, motDePasse);
            if (utilisateur.isPresent()) {
                System.out.println("Connexion réussie ! Bienvenue " + utilisateur.get().getPrenom() + " !");
                return utilisateur;
            } else {
                System.out.println("Email ou mot de passe incorrect.");
                return Optional.empty();
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la connexion : " + e.getMessage());
            return Optional.empty();
        }
    }

    public void modifierProfil(Utilisateur utilisateur) {
        System.out.println("\n=== MODIFICATION DU PROFIL ===");

        System.out.println("Laissez vide pour conserver la valeur actuelle.");

        System.out.print("Nom [" + utilisateur.getNom() + "] : ");
        String nom = scanner.nextLine().trim();
        if (!nom.isEmpty()) {
            utilisateur.setNom(nom);
        }

        System.out.print("Prénom [" + utilisateur.getPrenom() + "] : ");
        String prenom = scanner.nextLine().trim();
        if (!prenom.isEmpty()) {
            utilisateur.setPrenom(prenom);
        }

        System.out.print("Téléphone [" + utilisateur.getTelephone() + "] : ");
        String telephone = scanner.nextLine().trim();
        if (!telephone.isEmpty()) {
            if (InputValidator.isValidTelephone(telephone)) {
                utilisateur.setTelephone(telephone);
            } else {
                System.out.println("Format de téléphone invalide, ancienne valeur conservée.");
            }
        }

        System.out.print("Mot de passe (6 caractères minimum, laissez vide pour ne pas changer) : ");
        String motDePasse = scanner.nextLine().trim();
        if (!motDePasse.isEmpty()) {
            if (InputValidator.isValidPassword(motDePasse)) {
                utilisateur.setMotDePasse(motDePasse);
            } else {
                System.out.println("Mot de passe trop court, ancien mot de passe conservé.");
            }
        }

        System.out.print("Préférences [" + utilisateur.getPreferences() + "] : ");
        String preferences = scanner.nextLine().trim();
        if (!preferences.isEmpty()) {
            utilisateur.setPreferences(preferences);
        }

        try {
            boolean success = utilisateurService.modifierUtilisateur(utilisateur);
            if (success) {
                System.out.println("Profil mis à jour avec succès !");
            } else {
                System.out.println("Erreur lors de la mise à jour du profil.");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la mise à jour du profil : " + e.getMessage());
        }
    }

    public void afficherProfil(Utilisateur utilisateur) {
        System.out.println("\n=== PROFIL UTILISATEUR ===");
        System.out.println("Nom: " + utilisateur.getNom());
        System.out.println("Prénom: " + utilisateur.getPrenom());
        System.out.println("Email: " + utilisateur.getEmail());
        System.out.println("Téléphone: " + utilisateur.getTelephone());
        System.out.println("Préférences: " + (utilisateur.getPreferences() != null ? utilisateur.getPreferences() : "Non spécifiées"));
    }

}
