package covoiturage.ui.gui;

import covoiturage.ui.gui.panels.LoginPanel;
import covoiturage.ui.gui.utils.ColorScheme;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Constantes pour identifier les différentes vues
    public static final String LOGIN_PANEL = "LOGIN";
    public static final String USER_PANEL = "USER";
    public static final String DRIVER_PANEL = "DRIVER";
    public static final String ADMIN_PANEL = "ADMIN";

    public MainFrame() {
        // Configuration de la fenêtre
        setTitle("Application de Covoiturage");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialisation du layout et du panel principal
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Ajout des différents panels
        contentPanel.add(new LoginPanel(this), LOGIN_PANEL);

        // Le panel de connexion est affiché par défaut
        add(contentPanel);

        // Afficher l'écran de connexion
        cardLayout.show(contentPanel, LOGIN_PANEL);
    }

    // Méthode pour changer de vue
    public void switchPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
    }

    // Méthode pour ajouter un panel
    public void addPanel(JPanel panel, String name) {
        contentPanel.add(panel, name);
    }
}