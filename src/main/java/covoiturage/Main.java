package covoiturage;

import covoiturage.ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        System.out.println("Démarrage de l'application de covoiturage...");

        // Initialisation de l'interface utilisateur console
        ConsoleUI ui = new ConsoleUI();

        // Démarrage de l'application
        ui.demarrer();
    }
}