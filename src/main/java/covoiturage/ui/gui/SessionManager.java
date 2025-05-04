package covoiturage.ui.gui;

import covoiturage.model.Administrateur;
import covoiturage.model.Conducteur;
import covoiturage.model.Utilisateur;

public class SessionManager {
    private static Utilisateur currentUser;
    private static Conducteur currentDriver;
    private static Administrateur currentAdmin;

    public static Utilisateur getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(Utilisateur user) {
        currentUser = user;
        // Réinitialiser les autres sessions
        currentDriver = null;
        currentAdmin = null;
    }

    public static Conducteur getCurrentDriver() {
        return currentDriver;
    }

    public static void setCurrentDriver(Conducteur driver) {
        currentDriver = driver;
        // Réinitialiser les autres sessions
        currentUser = null;
        currentAdmin = null;
    }

    public static Administrateur getCurrentAdmin() {
        return currentAdmin;
    }

    public static void setCurrentAdmin(Administrateur admin) {
        currentAdmin = admin;
        // Réinitialiser les autres sessions
        currentUser = null;
        currentDriver = null;
    }

    public static void clearSession() {
        currentUser = null;
        currentDriver = null;
        currentAdmin = null;
    }
}