package covoiturage;

import covoiturage.ui.console.ConsoleUI;
import covoiturage.ui.gui.CovoiturageGUI;

public class Main {
    public static void main(String[] args) {
        // Arguments pour choisir l'interface (console ou GUI)
        boolean useGUI = true; // Par défaut, utiliser l'interface graphique

        if (args.length > 0 && args[0].equalsIgnoreCase("console")) {
            useGUI = false;
        }

        if (useGUI) {
            // Lancer l'interface graphique
            CovoiturageGUI.main(args);
        } else {
            // Lancer l'interface console
            ConsoleUI consoleUI = new ConsoleUI();
            consoleUI.demarrer();
        }
    }
}