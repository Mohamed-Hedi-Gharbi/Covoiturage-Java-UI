package covoiturage.ui.gui.components;

import covoiturage.ui.gui.utils.ColorScheme;
import covoiturage.ui.gui.utils.ImageUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class NavigationBar extends JPanel {

    public NavigationBar(String userInfo, ActionListener logoutAction) {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.PRIMARY);
        setPreferredSize(new Dimension(getPreferredSize().width, 70));
        setBorder(new EmptyBorder(10, 20, 10, 20));

        // Logo et titre de l'application
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);

        // Logo
        ImageIcon logo = ImageUtils.loadIcon("/images/car_icon.png", 32, 32);
        JLabel logoLabel = new JLabel(logo);
        titlePanel.add(logoLabel);

        // Titre avec effet de dégradé
        JLabel titleLabel = new JLabel("CovoiturApp") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // Créer un dégradé
                GradientPaint gp = new GradientPaint(
                        0, 0, Color.WHITE,
                        0, getHeight(), new Color(220, 230, 255)
                );

                g2d.setPaint(gp);
                g2d.setFont(new Font("Montserrat", Font.BOLD, 22));

                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString("CovoiturApp", 0, fm.getAscent());

                g2d.dispose();
            }
        };
        titleLabel.setPreferredSize(new Dimension(150, 40));
        titlePanel.add(titleLabel);

        add(titlePanel, BorderLayout.WEST);

        // Panel pour les informations utilisateur et le bouton de déconnexion
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        // Icône utilisateur
        JLabel userIconLabel = new JLabel(ImageUtils.loadIcon("/images/profile_icon.png", 24, 24));
        userPanel.add(userIconLabel);

        // Informations utilisateur avec nom en gras
        String[] userParts = userInfo.split(": ");
        JLabel userLabel = new JLabel("<html><body>" + userParts[0] + ": <b>" + userParts[1] + "</b></body></html>");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        userPanel.add(userLabel);

        // Séparateur vertical
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setPreferredSize(new Dimension(1, 30));
        separator.setForeground(new Color(255, 255, 255, 100));
        userPanel.add(separator);

        // Bouton de déconnexion avec icône
        JButton logoutButton = new JButton("Déconnexion");
        logoutButton.setIcon(ImageUtils.loadIcon("/images/logout_icon.png", 16, 16));
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logoutButton.setForeground(ColorScheme.PRIMARY);
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(logoutAction);
        userPanel.add(logoutButton);

        add(userPanel, BorderLayout.EAST);
    }
}