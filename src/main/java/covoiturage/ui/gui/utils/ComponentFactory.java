package covoiturage.ui.gui.utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ComponentFactory {

    // Crée un bouton stylisé
    public static JButton createButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Dialog", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, 40));

        // Effet de survol
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    // Crée un champ de texte stylisé
    public static JTextField createTextField(String placeholder) {
        JTextField textField = new JTextField(20);
        textField.setPreferredSize(new Dimension(textField.getPreferredSize().width, 40));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorScheme.SECONDARY),
                new EmptyBorder(5, 10, 5, 10)
        ));

        // Placeholder
        textField.setForeground(Color.GRAY);
        textField.setText(placeholder);

        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(ColorScheme.TEXT);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                }
            }
        });

        return textField;
    }

    // Crée un champ de mot de passe stylisé
    public static JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(passwordField.getPreferredSize().width, 40));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorScheme.SECONDARY),
                new EmptyBorder(5, 10, 5, 10)
        ));

        return passwordField;
    }

    // Crée un label titre
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Dialog", Font.BOLD, 24));
        label.setForeground(ColorScheme.PRIMARY);
        return label;
    }

    // Crée un label sous-titre
    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Dialog", Font.PLAIN, 16));
        label.setForeground(ColorScheme.TEXT);
        return label;
    }
}