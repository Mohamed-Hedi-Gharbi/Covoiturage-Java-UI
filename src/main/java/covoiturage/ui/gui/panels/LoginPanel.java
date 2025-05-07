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
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

        // Content avec CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(ColorScheme.BACKGROUND);

        // Ajout des vues
        contentPanel.add(createLoginView(), LOGIN_VIEW);
        contentPanel.add(createRegisterView(), REGISTER_VIEW);

        add(contentPanel, BorderLayout.CENTER);

        // Afficher la vue de connexion par défaut
        cardLayout.show(contentPanel, LOGIN_VIEW);
    }

    private JPanel createLoginView() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ColorScheme.BACKGROUND);

        // Panel principal avec effet d'ombre
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Dessiner l'ombre
                int shadowGap = 8;
                int cornerRadius = 20;

                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dessiner l'ombre
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(shadowGap, shadowGap, getWidth() - (2 * shadowGap), getHeight() - (2 * shadowGap), cornerRadius, cornerRadius);

                // Dessiner le fond
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth() - shadowGap, getHeight() - shadowGap, cornerRadius, cornerRadius);

                g2d.dispose();
            }
        };
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(40, 50, 40, 50));

        // Logo et titre
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setOpaque(false);

        ImageIcon logoIcon = ImageUtils.loadIcon("/images/car_icon.png", 64, 64);
        JLabel logoLabel = new JLabel(logoIcon);
        logoPanel.add(logoLabel);

        JLabel titleLabel = new JLabel("CovoiturApp");
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 28));
        titleLabel.setForeground(ColorScheme.PRIMARY);
        logoPanel.add(titleLabel);

        mainPanel.add(logoPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Titre du formulaire
        JLabel formTitle = ComponentFactory.createTitleLabel("Connexion");
        formTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(formTitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Sous-titre
        JLabel subtitleLabel = new JLabel("Connectez-vous pour accéder à votre compte");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(ColorScheme.TEXT_LIGHT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Créer un panel pour le formulaire
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.setMaximumSize(new Dimension(400, 300));

        // Labels d'erreur
        JLabel typeErrorLabel = new JLabel(" ");
        typeErrorLabel.setForeground(ColorScheme.ERROR);
        typeErrorLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        typeErrorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emailErrorLabel = new JLabel(" ");
        emailErrorLabel.setForeground(ColorScheme.ERROR);
        emailErrorLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        emailErrorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel passwordErrorLabel = new JLabel(" ");
        passwordErrorLabel.setForeground(ColorScheme.ERROR);
        passwordErrorLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        passwordErrorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Type d'utilisateur
        JPanel userTypePanel = new JPanel(new BorderLayout(10, 0));
        userTypePanel.setOpaque(false);
        userTypePanel.setMaximumSize(new Dimension(350, 40));
        userTypePanel.setPreferredSize(new Dimension(350, 40));
        userTypePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userTypeIconLabel = new JLabel(ImageUtils.loadIcon("/images/user_type_icon.png", 20, 20));
        userTypeIconLabel.setBorder(new EmptyBorder(0, 5, 0, 5));
        JComboBox<String> userTypeCombo = new JComboBox<>(new String[]{"Utilisateur", "Conducteur", "Administrateur"});
        userTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userTypePanel.add(userTypeIconLabel, BorderLayout.WEST);
        userTypePanel.add(userTypeCombo, BorderLayout.CENTER);

        // Ajouter le panel de type d'utilisateur et son label d'erreur
        formPanel.add(userTypePanel);
        formPanel.add(typeErrorLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Email
        JPanel emailPanel = new JPanel(new BorderLayout(10, 0));
        emailPanel.setOpaque(false);
        emailPanel.setMaximumSize(new Dimension(350, 40));
        emailPanel.setPreferredSize(new Dimension(350, 40));
        emailPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emailIconLabel = new JLabel(ImageUtils.loadIcon("/images/email_icon.png", 20, 20));
        emailIconLabel.setBorder(new EmptyBorder(0, 5, 0, 5));
        JTextField emailField = ComponentFactory.createTextField("Votre email");
        emailPanel.add(emailIconLabel, BorderLayout.WEST);
        emailPanel.add(emailField, BorderLayout.CENTER);

        // Ajouter le panel d'email et son label d'erreur
        formPanel.add(emailPanel);
        formPanel.add(emailErrorLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Mot de passe
        JPanel passwordPanel = new JPanel(new BorderLayout(10, 0));
        passwordPanel.setOpaque(false);
        passwordPanel.setMaximumSize(new Dimension(350, 40));
        passwordPanel.setPreferredSize(new Dimension(350, 40));
        passwordPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel passwordIconLabel = new JLabel(ImageUtils.loadIcon("/images/password_icon.png", 20, 20));
        passwordIconLabel.setBorder(new EmptyBorder(0, 5, 0, 5));
        JPasswordField passwordField = ComponentFactory.createPasswordField();
        passwordPanel.add(passwordIconLabel, BorderLayout.WEST);
        passwordPanel.add(passwordField, BorderLayout.CENTER);

        // Ajouter le panel de mot de passe et son label d'erreur
        formPanel.add(passwordPanel);
        formPanel.add(passwordErrorLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Ajouter le panel de formulaire au panel principal
        mainPanel.add(formPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Bouton de connexion
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);

        JButton loginButton = ComponentFactory.createButton("Se connecter", ColorScheme.PRIMARY, Color.WHITE);
        loginButton.setPreferredSize(new Dimension(350, 45));
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));

        // Action du bouton de connexion
        loginButton.addActionListener(e -> {
            // Réinitialiser les messages d'erreur
            emailErrorLabel.setText(" ");
            passwordErrorLabel.setText(" ");

            // Réinitialiser les bordures
            emailField.setBorder(new CompoundBorder(
                    new LineBorder(ColorScheme.BORDER, 1, true),
                    new EmptyBorder(5, 10, 5, 10)
            ));
            passwordField.setBorder(new CompoundBorder(
                    new LineBorder(ColorScheme.BORDER, 1, true),
                    new EmptyBorder(5, 10, 5, 10)
            ));

            // Récupérer les valeurs
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String userType = (String) userTypeCombo.getSelectedItem();

            // Validation
            boolean isValid = true;

            // Validation de l'email
            if (!InputValidator.isValidEmail(email)) {
                isValid = false;
                emailErrorLabel.setText("Format d'email invalide");
                emailField.setBorder(new LineBorder(ColorScheme.ERROR, 2, true));
            }

            // Validation du mot de passe
            if (password.isEmpty()) {
                isValid = false;
                passwordErrorLabel.setText("Le mot de passe est obligatoire");
                passwordField.setBorder(new LineBorder(ColorScheme.ERROR, 2, true));
            }

            if (isValid) {
                if (authenticateUser(email, password, userType)) {
                    // Effet de transition
                    Timer timer = new Timer(10, event -> {
                        // Code pour l'animation de transition si nécessaire
                        ((Timer) event.getSource()).stop();
                        // Après l'animation
                        JOptionPane.showMessageDialog(mainPanel, "Connexion réussie !");
                        // Redirection vers le panel approprié
                        switchToDashboard(userType);
                    });
                    timer.setInitialDelay(100);
                    timer.start();
                } else {
                    // Animation pour indiquer l'échec
                    passwordField.setBorder(new LineBorder(ColorScheme.ERROR, 2, true));
                    emailField.setBorder(new LineBorder(ColorScheme.ERROR, 2, true));

                    passwordErrorLabel.setText("Identifiants incorrects");

                    // Restaurer les bordures après un délai
                    Timer timer = new Timer(3000, event -> {
                        passwordField.setBorder(new CompoundBorder(
                                new LineBorder(ColorScheme.BORDER, 1, true),
                                new EmptyBorder(5, 10, 5, 10)
                        ));
                        emailField.setBorder(new CompoundBorder(
                                new LineBorder(ColorScheme.BORDER, 1, true),
                                new EmptyBorder(5, 10, 5, 10)
                        ));
                        passwordErrorLabel.setText(" ");
                        ((Timer) event.getSource()).stop();
                    });
                    timer.start();
                }
            }
        });

        buttonPanel.add(loginButton);
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Lien vers l'inscription avec une meilleure présentation
        JPanel registerLinkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerLinkPanel.setOpaque(false);

        JLabel registerLabel = new JLabel("Pas encore inscrit ?");
        registerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        registerLabel.setForeground(ColorScheme.TEXT);
        registerLinkPanel.add(registerLabel);

        JLabel registerLink = new JLabel("Créer un compte");
        registerLink.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerLink.setForeground(ColorScheme.PRIMARY);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Action pour passer à la vue d'inscription
        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                // Animation de transition
                Timer timer = new Timer(20, event -> {
                    // Code pour l'animation si nécessaire
                    ((Timer) event.getSource()).stop();
                    // Après l'animation
                    cardLayout.show(contentPanel, REGISTER_VIEW);
                });
                timer.setInitialDelay(100);
                timer.start();
            }

            @Override
            public void mouseEntered(MouseEvent evt) {
                registerLink.setText("<html><u>Créer un compte</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                registerLink.setText("Créer un compte");
            }
        });

        registerLinkPanel.add(registerLink);
        mainPanel.add(registerLinkPanel);

        // Ajouter le panel principal au conteneur
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.insets = new Insets(50, 50, 50, 50);
        panel.add(mainPanel, gbcMain);

        return panel;
    }

    private JPanel createRegisterView() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ColorScheme.BACKGROUND);

        // Panel principal avec effet d'ombre
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Dessiner l'ombre
                int shadowGap = 8;
                int cornerRadius = 20;

                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dessiner l'ombre
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(shadowGap, shadowGap, getWidth() - (2 * shadowGap), getHeight() - (2 * shadowGap), cornerRadius, cornerRadius);

                // Dessiner le fond
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth() - shadowGap, getHeight() - shadowGap, cornerRadius, cornerRadius);

                g2d.dispose();
            }
        };
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Titre du formulaire avec icône
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);

        ImageIcon registerIcon = ImageUtils.loadIcon("/images/register_icon.png", 32, 32);
        JLabel iconLabel = new JLabel(registerIcon);
        headerPanel.add(iconLabel);

        JLabel formTitle = ComponentFactory.createTitleLabel("Inscription");
        headerPanel.add(formTitle);

        mainPanel.add(headerPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Sous-titre
        JLabel subtitleLabel = new JLabel("Créez votre compte pour commencer à covoiturer");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(ColorScheme.TEXT_LIGHT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Formulaire avec GridBagLayout pour avoir plus de contrôle sur la disposition
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 0, 5);

        // Labels d'erreur pour chaque champ
        JLabel nomErrorLabel = new JLabel(" ");
        nomErrorLabel.setForeground(ColorScheme.ERROR);
        nomErrorLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));

        JLabel prenomErrorLabel = new JLabel(" ");
        prenomErrorLabel.setForeground(ColorScheme.ERROR);
        prenomErrorLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));

        JLabel emailErrorLabel = new JLabel(" ");
        emailErrorLabel.setForeground(ColorScheme.ERROR);
        emailErrorLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));

        JLabel passwordErrorLabel = new JLabel(" ");
        passwordErrorLabel.setForeground(ColorScheme.ERROR);
        passwordErrorLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));

        JLabel confirmPasswordErrorLabel = new JLabel(" ");
        confirmPasswordErrorLabel.setForeground(ColorScheme.ERROR);
        confirmPasswordErrorLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));

        JLabel telephoneErrorLabel = new JLabel(" ");
        telephoneErrorLabel.setForeground(ColorScheme.ERROR);
        telephoneErrorLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));

        // Création des champs du formulaire

        // Nom
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Nom:"), gbc);

        JPanel nomPanel = new JPanel(new BorderLayout(10, 0));
        nomPanel.setOpaque(false);
        JLabel nomIconLabel = new JLabel(ImageUtils.loadIcon("/images/user_icon.png", 20, 20));
        JTextField nomField = ComponentFactory.createTextField("Votre nom");
        nomPanel.add(nomIconLabel, BorderLayout.WEST);
        nomPanel.add(nomField, BorderLayout.CENTER);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        formPanel.add(nomPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(nomErrorLabel, gbc);

        // Prénom
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Prénom:"), gbc);

        JPanel prenomPanel = new JPanel(new BorderLayout(10, 0));
        prenomPanel.setOpaque(false);
        JLabel prenomIconLabel = new JLabel(ImageUtils.loadIcon("/images/user_icon.png", 20, 20));
        JTextField prenomField = ComponentFactory.createTextField("Votre prénom");
        prenomPanel.add(prenomIconLabel, BorderLayout.WEST);
        prenomPanel.add(prenomField, BorderLayout.CENTER);

        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(prenomPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(prenomErrorLabel, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Email:"), gbc);

        JPanel emailPanel = new JPanel(new BorderLayout(10, 0));
        emailPanel.setOpaque(false);
        JLabel emailIconLabel = new JLabel(ImageUtils.loadIcon("/images/email_icon.png", 20, 20));
        JTextField emailField = ComponentFactory.createTextField("Votre email");
        emailPanel.add(emailIconLabel, BorderLayout.WEST);
        emailPanel.add(emailField, BorderLayout.CENTER);

        gbc.gridx = 1;
        gbc.gridy = 4;
        formPanel.add(emailPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        formPanel.add(emailErrorLabel, gbc);

        // Mot de passe
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Mot de passe:"), gbc);

        JPanel passwordPanel = new JPanel(new BorderLayout(10, 0));
        passwordPanel.setOpaque(false);
        JLabel passwordIconLabel = new JLabel(ImageUtils.loadIcon("/images/password_icon.png", 20, 20));
        JPasswordField passwordField = ComponentFactory.createPasswordField();
        passwordPanel.add(passwordIconLabel, BorderLayout.WEST);
        passwordPanel.add(passwordField, BorderLayout.CENTER);

        gbc.gridx = 1;
        gbc.gridy = 6;
        formPanel.add(passwordPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 7;
        formPanel.add(passwordErrorLabel, gbc);

        // Confirmer mot de passe
        gbc.gridx = 0;
        gbc.gridy = 8;
        formPanel.add(new JLabel("Confirmer:"), gbc);

        JPanel confirmPasswordPanel = new JPanel(new BorderLayout(10, 0));
        confirmPasswordPanel.setOpaque(false);
        JLabel confirmPasswordIconLabel = new JLabel(ImageUtils.loadIcon("/images/password_icon.png", 20, 20));
        JPasswordField confirmPasswordField = ComponentFactory.createPasswordField();
        confirmPasswordPanel.add(confirmPasswordIconLabel, BorderLayout.WEST);
        confirmPasswordPanel.add(confirmPasswordField, BorderLayout.CENTER);

        gbc.gridx = 1;
        gbc.gridy = 8;
        formPanel.add(confirmPasswordPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 9;
        formPanel.add(confirmPasswordErrorLabel, gbc);

        // Téléphone
        gbc.gridx = 0;
        gbc.gridy = 10;
        formPanel.add(new JLabel("Téléphone:"), gbc);

        JPanel telephonePanel = new JPanel(new BorderLayout(10, 0));
        telephonePanel.setOpaque(false);
        JLabel telephoneIconLabel = new JLabel(ImageUtils.loadIcon("/images/phone_icon.png", 20, 20));
        JTextField telephoneField = ComponentFactory.createTextField("Votre numéro");
        telephonePanel.add(telephoneIconLabel, BorderLayout.WEST);
        telephonePanel.add(telephoneField, BorderLayout.CENTER);

        gbc.gridx = 1;
        gbc.gridy = 10;
        formPanel.add(telephonePanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 11;
        formPanel.add(telephoneErrorLabel, gbc);

        // Préférences
        gbc.gridx = 0;
        gbc.gridy = 12;
        formPanel.add(new JLabel("Préférences:"), gbc);

        JPanel preferencesPanel = new JPanel(new BorderLayout(10, 0));
        preferencesPanel.setOpaque(false);
        JLabel preferencesIconLabel = new JLabel(ImageUtils.loadIcon("/images/preferences_icon.png", 20, 20));
        JTextField preferencesField = ComponentFactory.createTextField("Vos préférences");
        preferencesPanel.add(preferencesIconLabel, BorderLayout.WEST);
        preferencesPanel.add(preferencesField, BorderLayout.CENTER);

        gbc.gridx = 1;
        gbc.gridy = 12;
        formPanel.add(preferencesPanel, gbc);

        // Panel pour contenir le formulaire
        JPanel formContainerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        formContainerPanel.setOpaque(false);
        formContainerPanel.add(formPanel);

        mainPanel.add(formContainerPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Conditions d'utilisation
        JPanel termsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        termsPanel.setOpaque(false);

        JCheckBox termsCheckbox = new JCheckBox();
        termsCheckbox.setOpaque(false);
        termsPanel.add(termsCheckbox);

        JLabel termsLabel = new JLabel("J'accepte les conditions d'utilisation");
        termsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        termsLabel.setForeground(ColorScheme.TEXT);
        termsPanel.add(termsLabel);

        // Rendre le label cliquable pour les conditions d'utilisation
        termsLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        termsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                termsCheckbox.setSelected(!termsCheckbox.isSelected());
                // Déclencher l'événement de la case à cocher
                termsCheckbox.doClick();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                termsLabel.setText("<html><u>J'accepte les conditions d'utilisation</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                termsLabel.setText("J'accepte les conditions d'utilisation");
            }
        });

        mainPanel.add(termsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Boutons avec meilleur espacement et disposition
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        JButton cancelButton = ComponentFactory.createButton("Annuler", ColorScheme.SECONDARY, ColorScheme.TEXT);
        cancelButton.setPreferredSize(new Dimension(150, 45));

        JButton registerButton = ComponentFactory.createButton("S'inscrire", ColorScheme.PRIMARY, Color.WHITE);
        registerButton.setPreferredSize(new Dimension(200, 45));
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        registerButton.setEnabled(false); // Désactivé par défaut

        // Listener pour la case à cocher des conditions d'utilisation
        termsCheckbox.addActionListener(e -> {
            registerButton.setEnabled(termsCheckbox.isSelected());
        });

        // Action du bouton d'inscription avec validation complète
        registerButton.addActionListener(e -> {
            // Réinitialiser tous les labels d'erreur
            nomErrorLabel.setText(" ");
            prenomErrorLabel.setText(" ");
            emailErrorLabel.setText(" ");
            passwordErrorLabel.setText(" ");
            confirmPasswordErrorLabel.setText(" ");
            telephoneErrorLabel.setText(" ");

            // Réinitialiser les bordures
            nomField.setBorder(new CompoundBorder(
                    new LineBorder(ColorScheme.BORDER, 1, true),
                    new EmptyBorder(5, 10, 5, 10)
            ));
            prenomField.setBorder(new CompoundBorder(
                    new LineBorder(ColorScheme.BORDER, 1, true),
                    new EmptyBorder(5, 10, 5, 10)
            ));
            emailField.setBorder(new CompoundBorder(
                    new LineBorder(ColorScheme.BORDER, 1, true),
                    new EmptyBorder(5, 10, 5, 10)
            ));
            passwordField.setBorder(new CompoundBorder(
                    new LineBorder(ColorScheme.BORDER, 1, true),
                    new EmptyBorder(5, 10, 5, 10)
            ));
            confirmPasswordField.setBorder(new CompoundBorder(
                    new LineBorder(ColorScheme.BORDER, 1, true),
                    new EmptyBorder(5, 10, 5, 10)
            ));
            telephoneField.setBorder(new CompoundBorder(
                    new LineBorder(ColorScheme.BORDER, 1, true),
                    new EmptyBorder(5, 10, 5, 10)
            ));

            // Récupération des valeurs
            String nom = nomField.getText();
            String prenom = prenomField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String telephone = telephoneField.getText();
            String preferences = preferencesField.getText();

            // Validation des champs
            boolean isValid = true;

            // Validation du nom
            if (nom.isEmpty() || nom.equals("Votre nom")) {
                isValid = false;
                nomErrorLabel.setText("Le nom est obligatoire");
                nomField.setBorder(new LineBorder(ColorScheme.ERROR, 2, true));
            }

            // Validation du prénom
            if (prenom.isEmpty() || prenom.equals("Votre prénom")) {
                isValid = false;
                prenomErrorLabel.setText("Le prénom est obligatoire");
                prenomField.setBorder(new LineBorder(ColorScheme.ERROR, 2, true));
            }

            // Validation de l'email
            if (!InputValidator.isValidEmail(email)) {
                isValid = false;
                emailErrorLabel.setText("Format d'email invalide");
                emailField.setBorder(new LineBorder(ColorScheme.ERROR, 2, true));
            }

            // Validation du mot de passe
            if (!InputValidator.isValidPassword(password)) {
                isValid = false;
                passwordErrorLabel.setText("Le mot de passe doit contenir au moins 6 caractères");
                passwordField.setBorder(new LineBorder(ColorScheme.ERROR, 2, true));
            }

            // Validation de la confirmation du mot de passe
            if (!password.equals(confirmPassword)) {
                isValid = false;
                confirmPasswordErrorLabel.setText("Les mots de passe ne correspondent pas");
                confirmPasswordField.setBorder(new LineBorder(ColorScheme.ERROR, 2, true));
            }

            // Validation du téléphone
            if (!InputValidator.isValidTelephone(telephone)) {
                isValid = false;
                telephoneErrorLabel.setText("Le numéro de téléphone doit contenir 8 chiffres");
                telephoneField.setBorder(new LineBorder(ColorScheme.ERROR, 2, true));
            }

            // Si tous les champs sont valides, créer l'utilisateur
            if (isValid) {
                try {
                    // Créer l'utilisateur
                    Utilisateur utilisateur = new Utilisateur(nom, prenom, email, password, telephone);
                    utilisateur.setPreferences(preferences);

                    // Enregistrer l'utilisateur
                    Long id = ServiceFactory.getUtilisateurService().creerUtilisateur(utilisateur);

                    if (id != null) {
                        JOptionPane.showMessageDialog(mainPanel,
                                "Compte créé avec succès ! Vous pouvez maintenant vous connecter.",
                                "Inscription réussie", JOptionPane.INFORMATION_MESSAGE);

                        // Retourner à la vue de connexion
                        cardLayout.show(contentPanel, LOGIN_VIEW);
                    } else {
                        JOptionPane.showMessageDialog(mainPanel,
                                "Erreur lors de la création du compte.",
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainPanel,
                            "Erreur: " + ex.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Action du bouton d'annulation
        cancelButton.addActionListener(e -> {
            // Animation de transition
            Timer timer = new Timer(20, event -> {
                // Code pour l'animation si nécessaire
                ((Timer) event.getSource()).stop();
                // Après l'animation
                cardLayout.show(contentPanel, LOGIN_VIEW);
            });
            timer.setInitialDelay(100);
            timer.start();
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(registerButton);

        mainPanel.add(buttonPanel);

        // Ajouter le panel principal au conteneur
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.insets = new Insets(30, 30, 30, 30);
        panel.add(mainPanel, gbcMain);

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

    // Ajouter cette méthode à la classe LoginPanel
    public void resetFields() {
        // Trouver les composants dans le formulaire de connexion
        Component[] components = this.getComponents();

        // Parcourir les composants à la recherche des champs à réinitialiser
        for (Component component : components) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                resetPanelFields(panel);
            }
        }
    }

    // Méthode pour réinitialiser tous les champs d'un panel
    private void resetPanelFields(JPanel panel) {
        for (Component component : panel.getComponents()) {
            // Si c'est un conteneur, recherche récursive
            if (component instanceof JPanel) {
                resetPanelFields((JPanel) component);
            }
            // Si c'est le JPanel contentPanel qui contient LOGIN_VIEW et REGISTER_VIEW
            else if (component instanceof JTextField textField) {
                if (textField.getText().contains("@")) {
                    textField.setText("Votre email");
                    textField.setForeground(ColorScheme.TEXT_LIGHT);
                } else {
                    textField.setText("");
                }
            }
            else if (component instanceof JComboBox<?> comboBox) {
                if (comboBox.getItemCount() > 0) {
                    comboBox.setSelectedIndex(0);
                }
            }
            else if (component instanceof JCheckBox checkBox) {
                checkBox.setSelected(false);
            }
        }
    }
}