package covoiturage.ui.gui.panels;

import covoiturage.model.Conducteur;
import covoiturage.model.Utilisateur;
import covoiturage.service.ServiceFactory;
import covoiturage.ui.gui.MainFrame;
import covoiturage.ui.gui.SessionManager;
import covoiturage.ui.gui.components.NavigationBar;
import covoiturage.ui.gui.components.SideBar;
import covoiturage.ui.gui.utils.ColorScheme;
import covoiturage.ui.gui.utils.ComponentFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UserPanel extends JPanel {
    private MainFrame mainFrame;
    private Utilisateur utilisateur;
    private CardLayout cardLayout;
    private JPanel contentPanel;

    // Constantes pour identifier les sous-panneaux
    public static final String DASHBOARD = "DASHBOARD";
    public static final String SEARCH_RIDES = "SEARCH_RIDES";
    public static final String MY_RESERVATIONS = "MY_RESERVATIONS";
    public static final String PROFILE = "PROFILE";

    public UserPanel(MainFrame mainFrame, Utilisateur utilisateur) {
        this.mainFrame = mainFrame;
        this.utilisateur = utilisateur;

        setLayout(new BorderLayout());
        setBackground(ColorScheme.BACKGROUND);

        // Barre de navigation en haut
        NavigationBar navBar = new NavigationBar(
                "Utilisateur: " + utilisateur.getPrenom() + " " + utilisateur.getNom(),
                e -> logout()
        );
        add(navBar, BorderLayout.NORTH);

        // Barre latérale
        SideBar sideBar = createSideBar();
        add(sideBar, BorderLayout.WEST);

        // Panel de contenu principal avec CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Ajout des différents sous-panneaux
        contentPanel.add(createDashboardPanel(), DASHBOARD);
        contentPanel.add(createSearchRidesPanel(), SEARCH_RIDES);
        contentPanel.add(createMyReservationsPanel(), MY_RESERVATIONS);
        contentPanel.add(createProfilePanel(), PROFILE);

        add(contentPanel, BorderLayout.CENTER);

        // Afficher le tableau de bord par défaut
        cardLayout.show(contentPanel, DASHBOARD);
    }

    private SideBar createSideBar() {
        SideBar sideBar = new SideBar();

        // Ajout des éléments de menu
        sideBar.addMenuItem("Tableau de bord", "/images/dashboard_icon.png", e -> cardLayout.show(contentPanel, DASHBOARD));
        sideBar.addMenuItem("Rechercher", "/images/search_icon.png", e -> cardLayout.show(contentPanel, SEARCH_RIDES));
        sideBar.addMenuItem("Mes réservations", "/images/reservation_icon.png", e -> cardLayout.show(contentPanel, MY_RESERVATIONS));
        sideBar.addMenuItem("Mon profil", "/images/profile_icon.png", e -> cardLayout.show(contentPanel, PROFILE));
        sideBar.addMenuItem("Devenir conducteur", "/images/driver_icon.png", e -> becomeDriver());

        return sideBar;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorScheme.BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = ComponentFactory.createTitleLabel("Tableau de bord");
        panel.add(titleLabel, BorderLayout.NORTH);

        // Contenu principal
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(ColorScheme.BACKGROUND);
        content.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Bienvenue
        JLabel welcomeLabel = ComponentFactory.createSubtitleLabel("Bienvenue, " + utilisateur.getPrenom() + " !");
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(welcomeLabel);
        content.add(Box.createRigidArea(new Dimension(0, 20)));

        // Statistiques
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Récupérer les statistiques depuis les services
        int totalReservations = 0; // À remplacer par le service approprié
        int activeReservations = 0; // À remplacer par le service approprié

        // Carte 1: Réservations totales
        JPanel card1 = createStatCard("Réservations totales", String.valueOf(totalReservations), ColorScheme.PRIMARY);
        statsPanel.add(card1);

        // Carte 2: Réservations actives
        JPanel card2 = createStatCard("Réservations actives", String.valueOf(activeReservations), ColorScheme.ACCENT);
        statsPanel.add(card2);

        // Carte 3: Actions rapides
        JPanel card3 = new JPanel();
        card3.setBackground(Color.WHITE);
        card3.setBorder(BorderFactory.createLineBorder(ColorScheme.SECONDARY));
        card3.setLayout(new BoxLayout(card3, BoxLayout.Y_AXIS));

        JLabel actionsLabel = new JLabel("Actions rapides");
        actionsLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        actionsLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        actionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton searchButton = ComponentFactory.createButton("Rechercher un trajet", ColorScheme.PRIMARY, Color.WHITE);
        searchButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchButton.setBorder(new EmptyBorder(5, 10, 5, 10));
        searchButton.addActionListener(e -> cardLayout.show(contentPanel, SEARCH_RIDES));

        JButton profileButton = ComponentFactory.createButton("Modifier mon profil", ColorScheme.SECONDARY, ColorScheme.TEXT);
        profileButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        profileButton.setBorder(new EmptyBorder(5, 10, 5, 10));
        profileButton.addActionListener(e -> cardLayout.show(contentPanel, PROFILE));

        card3.add(actionsLabel);
        card3.add(Box.createRigidArea(new Dimension(0, 10)));
        card3.add(searchButton);
        card3.add(Box.createRigidArea(new Dimension(0, 10)));
        card3.add(profileButton);
        card3.add(Box.createVerticalGlue());

        statsPanel.add(card3);

        content.add(statsPanel);

        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(ColorScheme.SECONDARY));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        titleLabel.setForeground(color);
        titleLabel.setBorder(new EmptyBorder(10, 10, 5, 10));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Dialog", Font.BOLD, 36));
        valueLabel.setForeground(color);
        valueLabel.setBorder(new EmptyBorder(5, 10, 10, 10));
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(valueLabel);

        return card;
    }

    private JPanel createSearchRidesPanel() {
        // Implémentation du panel de recherche de trajets
        // Ce panel devrait utiliser les services existants pour rechercher des trajets
        // et afficher les résultats dans un tableau ou une liste

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorScheme.BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = ComponentFactory.createTitleLabel("Rechercher un trajet");
        panel.add(titleLabel, BorderLayout.NORTH);

        // Formulaire de recherche
        JPanel searchForm = new JPanel(new GridBagLayout());
        searchForm.setBackground(Color.WHITE);
        searchForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorScheme.SECONDARY),
                new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);

        // Lieu de départ
        c.gridx = 0;
        c.gridy = 0;
        searchForm.add(new JLabel("Lieu de départ:"), c);

        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1.0;
        JTextField departField = ComponentFactory.createTextField("Ville de départ");
        searchForm.add(departField, c);

        // Lieu d'arrivée
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.0;
        searchForm.add(new JLabel("Lieu d'arrivée:"), c);

        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1.0;
        JTextField arriveeField = ComponentFactory.createTextField("Ville d'arrivée");
        searchForm.add(arriveeField, c);

        // Bouton de recherche
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.weightx = 0.0;
        c.anchor = GridBagConstraints.CENTER;
        JButton searchButton = ComponentFactory.createButton("Rechercher", ColorScheme.PRIMARY, Color.WHITE);
        searchForm.add(searchButton, c);

        // Ajouter le formulaire au panel
        panel.add(searchForm, BorderLayout.NORTH);

        // Table de résultats (vide par défaut)
        String[] columnNames = {"ID", "Départ", "Arrivée", "Date/Heure", "Prix", "Places", "Conducteur"};
        Object[][] data = {}; // Données vides

        JTable resultTable = new JTable(data, columnNames);
        resultTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        panel.add(scrollPane, BorderLayout.CENTER);

        // Action du bouton de recherche
        searchButton.addActionListener(e -> {
            // Rechercher les trajets avec les services existants
            String depart = departField.getText();
            String arrivee = arriveeField.getText();

            if (depart.isEmpty() || arrivee.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "Veuillez remplir tous les champs de recherche.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Appeler le service de recherche et mettre à jour la table
            // Ce code est à compléter avec l'appel au service approprié
        });

        return panel;
    }

    private JPanel createMyReservationsPanel() {
        // Implémentation du panel des réservations de l'utilisateur
        // Ce panel devrait utiliser les services existants pour récupérer
        // et afficher les réservations de l'utilisateur

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorScheme.BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = ComponentFactory.createTitleLabel("Mes réservations");
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table des réservations
        String[] columnNames = {"ID", "Départ", "Arrivée", "Date/Heure", "Places", "Prix", "Statut"};
        Object[][] data = {}; // Données vides

        JTable reservationsTable = new JTable(data, columnNames);
        reservationsTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(reservationsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel des boutons d'action
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setBackground(ColorScheme.BACKGROUND);

        JButton cancelButton = ComponentFactory.createButton("Annuler", ColorScheme.ERROR, Color.WHITE);
        cancelButton.setEnabled(false); // Désactivé par défaut

        JButton payButton = ComponentFactory.createButton("Payer", ColorScheme.SUCCESS, Color.WHITE);
        payButton.setEnabled(false); // Désactivé par défaut

        // Activer les boutons lorsqu'une ligne est sélectionnée
        reservationsTable.getSelectionModel().addListSelectionListener(e -> {
            boolean rowSelected = reservationsTable.getSelectedRow() != -1;
            cancelButton.setEnabled(rowSelected);
            payButton.setEnabled(rowSelected);
        });

        actionsPanel.add(cancelButton);
        actionsPanel.add(payButton);

        panel.add(actionsPanel, BorderLayout.SOUTH);

        // Charger les réservations de l'utilisateur
        // Ce code est à compléter avec l'appel au service approprié

        return panel;
    }

    private JPanel createProfilePanel() {
        // Implémentation du panel de profil utilisateur
        // Ce panel devrait permettre de visualiser et modifier les informations du profil

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorScheme.BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = ComponentFactory.createTitleLabel("Mon profil");
        panel.add(titleLabel, BorderLayout.NORTH);

        // Formulaire de profil
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorScheme.SECONDARY),
                new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 10, 10, 10);

        // Nom
        c.gridx = 0;
        c.gridy = 0;
        formPanel.add(new JLabel("Nom:"), c);

        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1.0;
        JTextField nomField = new JTextField(utilisateur.getNom(), 20);
        formPanel.add(nomField, c);

        // Prénom
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.0;
        formPanel.add(new JLabel("Prénom:"), c);

        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1.0;
        JTextField prenomField = new JTextField(utilisateur.getPrenom(), 20);
        formPanel.add(prenomField, c);

        // Email
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0.0;
        formPanel.add(new JLabel("Email:"), c);

        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 1.0;
        JTextField emailField = new JTextField(utilisateur.getEmail(), 20);
        emailField.setEditable(false); // L'email ne peut pas être modifié
        formPanel.add(emailField, c);

        // Téléphone
        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 0.0;
        formPanel.add(new JLabel("Téléphone:"), c);

        c.gridx = 1;
        c.gridy = 3;
        c.weightx = 1.0;
        JTextField telephoneField = new JTextField(utilisateur.getTelephone(), 20);
        formPanel.add(telephoneField, c);

        // Mot de passe
        c.gridx = 0;
        c.gridy = 4;
        c.weightx = 0.0;
        formPanel.add(new JLabel("Mot de passe:"), c);

        c.gridx = 1;
        c.gridy = 4;
        c.weightx = 1.0;
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setText("*********"); // Masquer le mot de passe actuel
        formPanel.add(passwordField, c);

        // Préférences
        c.gridx = 0;
        c.gridy = 5;
        c.weightx = 0.0;
        formPanel.add(new JLabel("Préférences:"), c);

        c.gridx = 1;
        c.gridy = 5;
        c.weightx = 1.0;
        JTextField preferencesField = new JTextField(utilisateur.getPreferences(), 20);
        formPanel.add(preferencesField, c);

        panel.add(formPanel, BorderLayout.CENTER);

        // Boutons d'action
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setBackground(ColorScheme.BACKGROUND);

        JButton saveButton = ComponentFactory.createButton("Enregistrer", ColorScheme.PRIMARY, Color.WHITE);

        // Action du bouton d'enregistrement
        saveButton.addActionListener(e -> {
            // Mettre à jour l'utilisateur
            utilisateur.setNom(nomField.getText());
            utilisateur.setPrenom(prenomField.getText());
            utilisateur.setTelephone(telephoneField.getText());

            // Vérifier si le mot de passe a été modifié
            String password = new String(passwordField.getPassword());
            if (!password.equals("*********")) {
                utilisateur.setMotDePasse(password);
            }

            utilisateur.setPreferences(preferencesField.getText());

            try {
                boolean success = ServiceFactory.getUtilisateurService().modifierUtilisateur(utilisateur);
                if (success) {
                    JOptionPane.showMessageDialog(panel,
                            "Profil mis à jour avec succès !",
                            "Succès", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(panel,
                            "Erreur lors de la mise à jour du profil.",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel,
                        "Erreur: " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonsPanel.add(saveButton);

        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void becomeDriver() {
        // Ouvrir une boîte de dialogue pour devenir conducteur
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Devenir conducteur", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);

        // Titre
        JLabel titleLabel = ComponentFactory.createTitleLabel("Devenir conducteur");
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        contentPanel.add(titleLabel, c);

        // Numéro de permis
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        contentPanel.add(new JLabel("Numéro de permis:"), c);

        c.gridx = 1;
        c.gridy = 1;
        JTextField permisField = ComponentFactory.createTextField("Votre numéro de permis");
        contentPanel.add(permisField, c);

        // Informations véhicule
        c.gridx = 0;
        c.gridy = 2;
        contentPanel.add(new JLabel("Informations véhicule:"), c);

        c.gridx = 1;
        c.gridy = 2;
        JTextField vehiculeField = ComponentFactory.createTextField("Marque, modèle, immatriculation");
        contentPanel.add(vehiculeField, c);

        dialog.add(contentPanel, BorderLayout.CENTER);

        // Boutons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = ComponentFactory.createButton("Annuler", Color.LIGHT_GRAY, Color.BLACK);
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton confirmButton = ComponentFactory.createButton("Confirmer", ColorScheme.PRIMARY, Color.WHITE);
        confirmButton.addActionListener(e -> {
            String permis = permisField.getText();
            String vehicule = vehiculeField.getText();

            if (permis.isEmpty() || vehicule.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Veuillez remplir tous les champs.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Créer un conducteur à partir de l'utilisateur actuel
            Conducteur conducteur = new Conducteur(
                    utilisateur.getNom(),
                    utilisateur.getPrenom(),
                    utilisateur.getEmail(),
                    utilisateur.getMotDePasse(),
                    utilisateur.getTelephone(),
                    permis
            );
            conducteur.setVehiculeInfo(vehicule);

            try {
                Long id = ServiceFactory.getConducteurService().creerConducteur(conducteur);
                if (id != null) {
                    JOptionPane.showMessageDialog(dialog,
                            "Félicitations, vous êtes maintenant conducteur !",
                            "Succès", JOptionPane.INFORMATION_MESSAGE);

                    // Mettre à jour la session
                    conducteur.setId(id);
                    SessionManager.setCurrentDriver(conducteur);

                    // Fermer la boîte de dialogue
                    dialog.dispose();

                    // Rediriger vers le panel conducteur
                    DriverPanel driverPanel = new DriverPanel(mainFrame, conducteur);
                    mainFrame.addPanel(driverPanel, MainFrame.DRIVER_PANEL);
                    mainFrame.switchPanel(MainFrame.DRIVER_PANEL);
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Erreur lors de la création du profil conducteur.",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Erreur: " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonsPanel.add(cancelButton);
        buttonsPanel.add(confirmButton);

        dialog.add(buttonsPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void logout() {
        // Déconnexion
        int choice = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir vous déconnecter ?",
                "Déconnexion", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            // Effacer la session
            SessionManager.clearSession();

            // Retourner à l'écran de connexion
            mainFrame.switchPanel(MainFrame.LOGIN_PANEL);
        }
    }
}