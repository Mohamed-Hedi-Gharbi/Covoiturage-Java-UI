package covoiturage.ui.gui.panels;

import covoiturage.model.Administrateur;
import covoiturage.model.Conducteur;
import covoiturage.model.Utilisateur;
import covoiturage.service.ServiceFactory;
import covoiturage.ui.gui.MainFrame;
import covoiturage.ui.gui.SessionManager;
import covoiturage.ui.gui.utils.ColorScheme;
import covoiturage.ui.gui.utils.ComponentFactory;
import covoiturage.ui.gui.utils.ImageUtils;
import covoiturage.ui.validator.InputValidator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Optional;

public class LoginPanel extends JPanel {
    private MainFrame mainFrame;
    private CardLayout cardLayout;
    private JPanel contentPanel;

    // Constantes pour les différentes vues
    private static final String LOGIN_VIEW = "LOGIN";
    private static final String REGISTER_VIEW = "REGISTER";

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        // Layout général
        setLayout(new BorderLayout());
        setBackground(ColorScheme.BACKGROUND);

        // Entête
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Content avec CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(ColorScheme.BACKGROUND);

        // Ajout des vues
        contentPanel.add(createLoginView(), LOGIN_VIEW);
        contentPanel.add(createRegisterView(), REGISTER_VIEW);

        add(contentPanel, BorderLayout.CENTER);

        // Pied de page
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);

        // Afficher la vue de connexion par défaut
        cardLayout.show(contentPanel, LOGIN_VIEW);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorScheme.PRIMARY);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Logo et titre
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(ColorScheme.PRIMARY);

        // Icône
        ImageIcon carIcon = ImageUtils.loadIcon("/images/car_icon.png", 40, 40);
        if (carIcon != null) {
            JLabel iconLabel = new JLabel(carIcon);
            titlePanel.add(iconLabel);
        }

        // Titre
        JLabel titleLabel = new JLabel("APPLICATION DE COVOITURAGE");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        panel.add(titlePanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLoginView() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ColorScheme.BACKGROUND);
        panel.setBorder(new EmptyBorder(50, 100, 50, 100));

        // Titre du formulaire
        JLabel formTitle = ComponentFactory.createTitleLabel("Connexion");
        formTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(formTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Type d'utilisateur
        JPanel userTypePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        userTypePanel.setBackground(ColorScheme.BACKGROUND);

        JComboBox<String> userTypeCombo = new JComboBox<>(new String[]{"Utilisateur", "Conducteur", "Administrateur"});
        userTypeCombo.setPreferredSize(new Dimension(300, 40));
        userTypePanel.add(userTypeCombo);

        panel.add(userTypePanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Email
        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        emailPanel.setBackground(ColorScheme.BACKGROUND);

        JTextField emailField = ComponentFactory.createTextField("Email");
        emailPanel.add(emailField);

        panel.add(emailPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Mot de passe
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        passwordPanel.setBackground(ColorScheme.BACKGROUND);

        JPasswordField passwordField = ComponentFactory.createPasswordField();
        passwordField.setPreferredSize(new Dimension(300, 40));
        passwordPanel.add(passwordField);

        panel.add(passwordPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Bouton de connexion
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(ColorScheme.BACKGROUND);

        JButton loginButton = ComponentFactory.createButton("Se connecter", ColorScheme.PRIMARY, Color.WHITE);
        loginButton.setPreferredSize(new Dimension(300, 40));

        // Action du bouton de connexion
        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String userType = (String) userTypeCombo.getSelectedItem();

            if (authenticateUser(email, password, userType)) {
                JOptionPane.showMessageDialog(this, "Connexion réussie !");
                // Redirection vers le panel approprié
                switchToDashboard(userType);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Échec de la connexion. Vérifiez vos identifiants.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(loginButton);
        panel.add(buttonPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Lien vers l'inscription
        JPanel registerLinkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerLinkPanel.setBackground(ColorScheme.BACKGROUND);

        JLabel registerLink = new JLabel("Pas encore inscrit ? Créer un compte");
        registerLink.setForeground(ColorScheme.PRIMARY);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Action pour passer à la vue d'inscription
        registerLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cardLayout.show(contentPanel, REGISTER_VIEW);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                registerLink.setText("<html><u>Pas encore inscrit ? Créer un compte</u></html>");
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                registerLink.setText("Pas encore inscrit ? Créer un compte");
            }
        });

        registerLinkPanel.add(registerLink);
        panel.add(registerLinkPanel);

        return panel;
    }

    private JPanel createRegisterView() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ColorScheme.BACKGROUND);
        panel.setBorder(new EmptyBorder(30, 100, 30, 100));

        // Titre du formulaire
        JLabel formTitle = ComponentFactory.createTitleLabel("Inscription");
        formTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(formTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Formulaire dans un JPanel avec GridLayout
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBackground(ColorScheme.BACKGROUND);
        formPanel.setBorder(new EmptyBorder(0, 50, 0, 50));

        // Nom
        formPanel.add(new JLabel("Nom:"));
        JTextField nomField = ComponentFactory.createTextField("Votre nom");
        formPanel.add(nomField);

        // Prénom
        formPanel.add(new JLabel("Prénom:"));
        JTextField prenomField = ComponentFactory.createTextField("Votre prénom");
        formPanel.add(prenomField);

        // Email
        formPanel.add(new JLabel("Email:"));
        JTextField emailField = ComponentFactory.createTextField("Votre email");
        formPanel.add(emailField);

        // Mot de passe
        formPanel.add(new JLabel("Mot de passe:"));
        JPasswordField passwordField = ComponentFactory.createPasswordField();
        formPanel.add(passwordField);

        // Confirmer mot de passe
        formPanel.add(new JLabel("Confirmer:"));
        JPasswordField confirmPasswordField = ComponentFactory.createPasswordField();
        formPanel.add(confirmPasswordField);

        // Téléphone
        formPanel.add(new JLabel("Téléphone:"));
        JTextField telephoneField = ComponentFactory.createTextField("Votre numéro");
        formPanel.add(telephoneField);

        // Préférences
        formPanel.add(new JLabel("Préférences:"));
        JTextField preferencesField = ComponentFactory.createTextField("Vos préférences");
        formPanel.add(preferencesField);

        // Panel pour contenir le formulaire
        JPanel formContainerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        formContainerPanel.setBackground(ColorScheme.BACKGROUND);
        formContainerPanel.add(formPanel);

        panel.add(formContainerPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(ColorScheme.BACKGROUND);

        JButton registerButton = ComponentFactory.createButton("S'inscrire", ColorScheme.PRIMARY, Color.WHITE);
        registerButton.setPreferredSize(new Dimension(200, 40));

        // Action du bouton d'inscription
        registerButton.addActionListener(e -> {
            String nom = nomField.getText();
            String prenom = prenomField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String telephone = telephoneField.getText();
            String preferences = preferencesField.getText();

            // Vérifications
            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty() || telephone.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Veuillez remplir tous les champs obligatoires.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!InputValidator.isValidEmail(email)) {
                JOptionPane.showMessageDialog(this,
                        "Format d'email invalide.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this,
                        "Les mots de passe ne correspondent pas.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!InputValidator.isValidTelephone(telephone)) {
                JOptionPane.showMessageDialog(this,
                        "Le numéro de téléphone doit contenir 8 chiffres.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!InputValidator.isValidPassword(password)) {
                JOptionPane.showMessageDialog(this,
                        "Le mot de passe doit contenir au moins 6 caractères.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Création de l'utilisateur
            Utilisateur utilisateur = new Utilisateur(nom, prenom, email, password, telephone);
            utilisateur.setPreferences(preferences);

            try {
                Long id = ServiceFactory.getUtilisateurService().creerUtilisateur(utilisateur);
                if (id != null) {
                    JOptionPane.showMessageDialog(this,
                            "Inscription réussie ! Vous pouvez maintenant vous connecter.",
                            "Succès", JOptionPane.INFORMATION_MESSAGE);

                    // Retour à la vue de connexion
                    cardLayout.show(contentPanel, LOGIN_VIEW);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Erreur lors de l'inscription.",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur: " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = ComponentFactory.createButton("Annuler", Color.LIGHT_GRAY, Color.BLACK);
        cancelButton.setPreferredSize(new Dimension(200, 40));

        // Action du bouton d'annulation
        cancelButton.addActionListener(e -> {
            cardLayout.show(contentPanel, LOGIN_VIEW);
        });

        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorScheme.SECONDARY);
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel copyrightLabel = new JLabel("© 2025 Application de Covoiturage. Tous droits réservés.");
        copyrightLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(copyrightLabel, BorderLayout.CENTER);

        return panel;
    }

    // Authentification de l'utilisateur
    private boolean authenticateUser(String email, String password, String userType) {
        try {
            switch (userType) {
                case "Utilisateur":
                    Optional<Utilisateur> utilisateur = ServiceFactory.getUtilisateurService().authentifier(email, password);
                    if (utilisateur.isPresent()) {
                        // Stocker l'utilisateur pour la session
                        SessionManager.setCurrentUser(utilisateur.get());
                        return true;
                    }
                    break;

                case "Conducteur":
                    Optional<Conducteur> conducteur = ServiceFactory.getConducteurService().authentifier(email, password);
                    if (conducteur.isPresent()) {
                        // Stocker le conducteur pour la session
                        SessionManager.setCurrentDriver(conducteur.get());
                        return true;
                    }
                    break;

                case "Administrateur":
                    Optional<Administrateur> admin = ServiceFactory.getAdminService().authentifier(email, password);
                    if (admin.isPresent()) {
                        // Stocker l'admin pour la session
                        SessionManager.setCurrentAdmin(admin.get());
                        return true;
                    }
                    break;
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Redirection vers le tableau de bord approprié
    private void switchToDashboard(String userType) {
        switch (userType) {
            case "Utilisateur":
                UserPanel userPanel = new UserPanel(mainFrame, SessionManager.getCurrentUser());
                mainFrame.addPanel(userPanel, MainFrame.USER_PANEL);
                mainFrame.switchPanel(MainFrame.USER_PANEL);
                break;

            case "Conducteur":
                DriverPanel driverPanel = new DriverPanel(mainFrame, SessionManager.getCurrentDriver());
                mainFrame.addPanel(driverPanel, MainFrame.DRIVER_PANEL);
                mainFrame.switchPanel(MainFrame.DRIVER_PANEL);
                break;

            case "Administrateur":
                AdminPanel adminPanel = new AdminPanel(mainFrame, SessionManager.getCurrentAdmin());
                mainFrame.addPanel(adminPanel, MainFrame.ADMIN_PANEL);
                mainFrame.switchPanel(MainFrame.ADMIN_PANEL);
                break;
        }
    }
}