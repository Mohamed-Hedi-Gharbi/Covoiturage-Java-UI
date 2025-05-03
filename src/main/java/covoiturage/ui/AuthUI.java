package covoiturage.ui;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Scanner;

public class AuthUI {
    /**
     * Affiche une interface graphique sécurisée pour saisir l'email et le mot de passe.
     *
     * @return Un tableau contenant [email, motDePasse], ou null si l'utilisateur annule.
     */
    public String[] lireIdentifiantsSecurises() {
        try {
            // Palette de couleurs modernes
            Color primaryColor = new Color(33, 111, 219);
            Color backgroundColor = new Color(245, 247, 250);
            Color inputBorderColor = new Color(200, 200, 200);
            Color labelColor = new Color(60, 60, 60);

            // Panel principal
            JPanel panel = new JPanel(new BorderLayout(20, 20));
            panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
            panel.setBackground(backgroundColor);

            // Titre
            JLabel titleLabel = new JLabel("🔒 Connexion Sécurisée");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            titleLabel.setForeground(primaryColor);

            // Sous-titre
            JLabel subTitleLabel = new JLabel("Veuillez entrer vos identifiants pour continuer.");
            subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            subTitleLabel.setForeground(labelColor);

            // Header panel
            JPanel headerPanel = new JPanel();
            headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
            headerPanel.setBackground(backgroundColor);
            headerPanel.add(titleLabel);
            headerPanel.add(Box.createVerticalStrut(8));
            headerPanel.add(subTitleLabel);

            // Champs d’entrée
            JTextField emailField = createRoundedTextField("Email", inputBorderColor);
            JPasswordField passwordField = createRoundedPasswordField("Mot de passe", inputBorderColor);

            JPanel formPanel = new JPanel();
            formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
            formPanel.setBackground(backgroundColor);
            formPanel.add(emailField);
            formPanel.add(Box.createVerticalStrut(15));
            formPanel.add(passwordField);

            panel.add(headerPanel, BorderLayout.NORTH);
            panel.add(formPanel, BorderLayout.CENTER);

            // Affichage de la boîte de dialogue
            int result = JOptionPane.showConfirmDialog(
                    null, panel, "Connexion",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword());
                return new String[]{email, password};
            } else {
                return null;
            }
        } catch (HeadlessException e) {
            System.out.println("Mode sans interface graphique. Repli sur saisie console.");
            Scanner scanner = new Scanner(System.in);
            System.out.print("Email : ");
            String email = scanner.nextLine().trim();
            System.out.print("Mot de passe : ");
            String password = scanner.nextLine().trim();
            return new String[]{email, password};
        }
    }

    // Crée un champ de texte stylisé avec bord arrondi et placeholder
    private JTextField createRoundedTextField(String placeholder, Color borderColor) {
        JTextField field = new JTextField();
        styleTextField(field, placeholder, borderColor);
        return field;
    }

    // Crée un champ de mot de passe stylisé avec bord arrondi et placeholder
    private JPasswordField createRoundedPasswordField(String placeholder, Color borderColor) {
        JPasswordField field = new JPasswordField();
        styleTextField(field, placeholder, borderColor);
        return field;
    }

    // Applique le style aux champs de texte
    private void styleTextField(JTextComponent field, String placeholder, Color borderColor) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setBackground(Color.WHITE);
        field.setText(placeholder);
        field.setForeground(Color.GRAY);

        // Placeholder dynamique
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }
}
