package covoiturage.ui.gui;

import covoiturage.ui.gui.utils.UIConfig;
import javax.swing.*;
import java.awt.*;

public class CovoiturageGUI {
    public static void main(String[] args) {
        // Appliquer les styles globaux
        UIConfig.applyGlobalStyles();

        // Configuration supplémentaire de l'interface utilisateur
        setUIProperties();

        // Lancer l'application dans l'EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            // Afficher un écran de démarrage
            JWindow splashScreen = showSplashScreen();

            // Simuler un chargement
            new Timer(2000, e -> {
                // Fermer l'écran de démarrage
                splashScreen.dispose();

                // Créer et afficher la fenêtre principale
                MainFrame mainFrame = new MainFrame();

                // Afficher la fenêtre principale avec un effet d'animation alternatif
                animateMainFrameDisplay(mainFrame);

                ((Timer) e.getSource()).stop();
            }).start();
        });
    }

    private static void setUIProperties() {
        // Propriétés système pour améliorer le rendu
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Propriétés pour les animations
        System.setProperty("swing.animation.fps", "60");
    }

    private static JWindow showSplashScreen() {
        JWindow splashScreen = new JWindow();

        JPanel content = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fond dégradé
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(41, 128, 185),
                        0, getHeight(), new Color(52, 152, 219)
                );

                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.dispose();
            }
        };
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Logo
        JLabel logoLabel = new JLabel("CovoiturApp");
        logoLabel.setFont(new Font("Montserrat", Font.BOLD, 32));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Barre de progression
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setIndeterminate(true);
        progressBar.setBackground(new Color(255, 255, 255, 100));
        progressBar.setForeground(Color.WHITE);
        progressBar.setBorderPainted(false);

        // Message de chargement
        JLabel loadingLabel = new JLabel("Démarrage de l'application...");
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loadingLabel.setForeground(Color.WHITE);
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Ajouter les composants
        content.add(logoLabel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout(0, 10));
        southPanel.setOpaque(false);
        southPanel.add(progressBar, BorderLayout.CENTER);
        southPanel.add(loadingLabel, BorderLayout.SOUTH);

        content.add(southPanel, BorderLayout.SOUTH);

        // Définir la taille et la position
        splashScreen.setContentPane(content);
        splashScreen.setSize(400, 300);
        splashScreen.setLocationRelativeTo(null);
        splashScreen.setVisible(true);

        return splashScreen;
    }

    // Nouvelle méthode d'animation qui ne dépend pas de l'opacité
    private static void animateMainFrameDisplay(JFrame frame) {
        // Option 1: Animation de taille
        frame.setSize(0, 0);
        frame.setVisible(true);

        final int targetWidth = 1024;
        final int targetHeight = 768;
        final int steps = 20;
        final int delay = 10;

        Timer timer = new Timer(delay, null);
        final int[] currentStep = {0};

        timer.addActionListener(e -> {
            currentStep[0]++;

            if (currentStep[0] <= steps) {
                int newWidth = (int) (targetWidth * (currentStep[0] / (double) steps));
                int newHeight = (int) (targetHeight * (currentStep[0] / (double) steps));

                // Assurez-vous que la taille n'est jamais 0
                frame.setSize(Math.max(newWidth, 50), Math.max(newHeight, 50));
                frame.setLocationRelativeTo(null); // Centrer à chaque étape
            } else {
                frame.setSize(targetWidth, targetHeight);
                frame.setLocationRelativeTo(null);
                ((Timer) e.getSource()).stop();
            }
        });

        timer.start();
    }

    // Méthode originale qui causait l'erreur - remplacée par la nouvelle méthode
    // Cette méthode ne fonctionnera que si vous configurez la fenêtre avec setUndecorated(true)
    private static void fadeIn(JFrame frame) {
        // Pour utiliser cette méthode, il faudrait d'abord appeler:
        // frame.setUndecorated(true);
        // avant d'appeler cette méthode

        frame.setOpacity(0.0f);
        frame.setVisible(true);

        final float[] opacity = {0.0f};

        Timer timer = new Timer(20, e -> {
            opacity[0] += 0.05f;

            if (opacity[0] > 1.0f) {
                opacity[0] = 1.0f;
                ((Timer) e.getSource()).stop();
            }

            frame.setOpacity(opacity[0]);
        });

        timer.start();
    }
}