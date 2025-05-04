package covoiturage.ui.gui.components;

import covoiturage.ui.gui.utils.ColorScheme;
import covoiturage.ui.gui.utils.ComponentFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class NavigationBar extends JPanel {

    public NavigationBar(String userInfo, ActionListener logoutAction) {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.PRIMARY);
        setPreferredSize(new Dimension(getPreferredSize().width, 60));
        setBorder(new EmptyBorder(10, 20, 10, 20));

        // Titre de l'application
        JLabel titleLabel = new JLabel("Application de Covoiturage");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.WEST);

        // Panel pour les informations utilisateur et le bouton de déconnexion
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);

        // Informations utilisateur
        JLabel userLabel = new JLabel(userInfo);
        userLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        userPanel.add(userLabel);

        // Bouton de déconnexion
        JButton logoutButton = ComponentFactory.createButton("Déconnexion", ColorScheme.SECONDARY, ColorScheme.PRIMARY);
        logoutButton.addActionListener(logoutAction);
        userPanel.add(logoutButton);

        add(userPanel, BorderLayout.EAST);
    }
}