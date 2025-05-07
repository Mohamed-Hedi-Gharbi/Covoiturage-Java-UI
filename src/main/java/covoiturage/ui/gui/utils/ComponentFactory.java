package covoiturage.ui.gui.utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class ComponentFactory {

    // Crée un bouton stylisé avec des coins arrondis et animations
    public static JButton createButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, 40));
        button.setBorder(new EmptyBorder(8, 15, 8, 15));

        // Effet de survol avec transition de couleur
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(getDarkerColor(bgColor, 0.1f));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(getDarkerColor(bgColor, 0.2f));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(getDarkerColor(bgColor, 0.1f));
            }
        });

        return button;
    }

    // Crée un champ de texte stylisé avec animation de focus
    public static JTextField createTextField(String placeholder) {
        JTextField textField = new JTextField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Afficher le placeholder seulement si le texte est vide et le champ n'a pas le focus
                if (getText().isEmpty() && !hasFocus() && placeholder != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(ColorScheme.TEXT_LIGHT);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    int padding = (getHeight() - g2.getFontMetrics().getHeight()) / 2;
                    g2.drawString(placeholder, getInsets().left, getHeight() - padding - g2.getFontMetrics().getDescent());
                    g2.dispose();
                }
            }
        };

        textField.setPreferredSize(new Dimension(textField.getPreferredSize().width, 40));
        textField.setBorder(new CompoundBorder(
                new LineBorder(ColorScheme.BORDER, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setForeground(ColorScheme.TEXT);
        textField.setBackground(Color.WHITE);

        // Changer la couleur de la bordure lors du focus
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.setBorder(new CompoundBorder(
                        new LineBorder(ColorScheme.PRIMARY, 2, true),
                        new EmptyBorder(4, 9, 4, 9)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                textField.setBorder(new CompoundBorder(
                        new LineBorder(ColorScheme.BORDER, 1, true),
                        new EmptyBorder(5, 10, 5, 10)
                ));
            }
        });

        return textField;
    }

    // Crée un champ de mot de passe stylisé avec animation de focus
    public static JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(passwordField.getPreferredSize().width, 40));
        passwordField.setBorder(new CompoundBorder(
                new LineBorder(ColorScheme.BORDER, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setForeground(ColorScheme.TEXT);

        // Changer la couleur de la bordure lors du focus
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                passwordField.setBorder(new CompoundBorder(
                        new LineBorder(ColorScheme.PRIMARY, 2, true),
                        new EmptyBorder(4, 9, 4, 9)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                passwordField.setBorder(new CompoundBorder(
                        new LineBorder(ColorScheme.BORDER, 1, true),
                        new EmptyBorder(5, 10, 5, 10)
                ));
            }
        });

        return passwordField;
    }

    // Crée un label titre
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(ColorScheme.PRIMARY);
        return label;
    }

    // Crée un label sous-titre
    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(ColorScheme.TEXT);
        return label;
    }

    // Crée une carte d'information stylisée avec effet de survol
    public static JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(ColorScheme.CARD_BACKGROUND);
        card.setBorder(new LineBorder(ColorScheme.BORDER, 1, true));

        // Ajouter un effet de survol
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(new LineBorder(ColorScheme.PRIMARY, 1, true));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(new LineBorder(ColorScheme.BORDER, 1, true));
            }
        });

        return card;
    }

    // Méthode utilitaire pour assombrir une couleur
    private static Color getDarkerColor(Color color, float factor) {
        int r = Math.max(0, (int)(color.getRed() * (1 - factor)));
        int g = Math.max(0, (int)(color.getGreen() * (1 - factor)));
        int b = Math.max(0, (int)(color.getBlue() * (1 - factor)));
        return new Color(r, g, b);
    }
}