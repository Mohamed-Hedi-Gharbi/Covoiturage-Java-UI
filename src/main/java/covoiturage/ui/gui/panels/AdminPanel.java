package covoiturage.ui.gui.panels;

import covoiturage.model.Administrateur;
import covoiturage.model.Conducteur;
import covoiturage.model.Trajet;
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
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class AdminPanel extends JPanel {
    private MainFrame mainFrame;
    private Administrateur administrateur;
    private CardLayout cardLayout;
    private JPanel contentPanel;

    // Constantes pour identifier les sous-panneaux
    public static final String DASHBOARD = "DASHBOARD";
    public static final String USERS = "USERS";
    public static final String DRIVERS = "DRIVERS";
    public static final String RIDES = "RIDES";
    public static final String REPORTS = "REPORTS";

    public AdminPanel(MainFrame mainFrame, Administrateur administrateur) {
        this.mainFrame = mainFrame;
        this.administrateur = administrateur;

        setLayout(new BorderLayout());
        setBackground(ColorScheme.BACKGROUND);

        // Barre de navigation en haut
        NavigationBar navBar = new NavigationBar(
                "Administrateur: " + administrateur.getPrenom() + " " + administrateur.getNom(),
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
        contentPanel.add(createUsersPanel(), USERS);
        contentPanel.add(createDriversPanel(), DRIVERS);
        contentPanel.add(createRidesPanel(), RIDES);
        contentPanel.add(createReportsPanel(), REPORTS);

        add(contentPanel, BorderLayout.CENTER);

        // Afficher le tableau de bord par défaut
        cardLayout.show(contentPanel, DASHBOARD);
    }

    private SideBar createSideBar() {
        SideBar sideBar = new SideBar();

        // Ajout des éléments de menu
        sideBar.addMenuItem("Tableau de bord", "/images/dashboard_icon.png", e -> cardLayout.show(contentPanel, DASHBOARD));
        sideBar.addMenuItem("Utilisateurs", "/images/users_icon.png", e -> refreshUsersPanel());
        sideBar.addMenuItem("Conducteurs", "/images/drivers_icon.png", e -> refreshDriversPanel());
        sideBar.addMenuItem("Trajets", "/images/car_icon.png", e -> refreshRidesPanel());
        sideBar.addMenuItem("Rapports", "/images/report_icon.png", e -> cardLayout.show(contentPanel, REPORTS));

        return sideBar;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorScheme.BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = ComponentFactory.createTitleLabel("Tableau de bord administrateur");
        panel.add(titleLabel, BorderLayout.NORTH);

        // Contenu principal
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(ColorScheme.BACKGROUND);
        content.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Bienvenue
        JLabel welcomeLabel = ComponentFactory.createSubtitleLabel("Bienvenue, " + administrateur.getPrenom() + " !");
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(welcomeLabel);
        content.add(Box.createRigidArea(new Dimension(0, 20)));

        // Statistiques
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Récupérer les statistiques
        int totalUsers = ServiceFactory.getUtilisateurService().getAllUtilisateurs().size();
        int totalDrivers = ServiceFactory.getConducteurService().getAllConducteurs().size();
        int totalRides = ServiceFactory.getTrajetService().getAllTrajets().size();

        // Carte 1: Utilisateurs
        JPanel card1 = createStatCard("Utilisateurs", String.valueOf(totalUsers), ColorScheme.PRIMARY);
        statsPanel.add(card1);

        // Carte 2: Conducteurs
        JPanel card2 = createStatCard("Conducteurs", String.valueOf(totalDrivers), ColorScheme.ACCENT);
        statsPanel.add(card2);

        // Carte 3: Trajets
        JPanel card3 = createStatCard("Trajets", String.valueOf(totalRides), ColorScheme.INFO);
        statsPanel.add(card3);

        // Carte 4: Actions rapides
        JPanel card4 = new JPanel();
        card4.setBackground(Color.WHITE);
        card4.setBorder(BorderFactory.createLineBorder(ColorScheme.SECONDARY));
        card4.setLayout(new BoxLayout(card4, BoxLayout.Y_AXIS));

        JLabel actionsLabel = new JLabel("Actions rapides");
        actionsLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        actionsLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        actionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton usersButton = ComponentFactory.createButton("Gérer les utilisateurs", ColorScheme.PRIMARY, Color.WHITE);
        usersButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        usersButton.setBorder(new EmptyBorder(5, 10, 5, 10));
        usersButton.addActionListener(e -> refreshUsersPanel());

        JButton ridesButton = ComponentFactory.createButton("Gérer les trajets", ColorScheme.SECONDARY, ColorScheme.TEXT);
        ridesButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        ridesButton.setBorder(new EmptyBorder(5, 10, 5, 10));
        ridesButton.addActionListener(e -> refreshRidesPanel());

        card4.add(actionsLabel);
        card4.add(Box.createRigidArea(new Dimension(0, 10)));
        card4.add(usersButton);
        card4.add(Box.createRigidArea(new Dimension(0, 10)));
        card4.add(ridesButton);
        card4.add(Box.createVerticalGlue());

        statsPanel.add(card4);

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

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorScheme.BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = ComponentFactory.createTitleLabel("Gestion des utilisateurs");
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table des utilisateurs
        String[] columnNames = {"ID", "Nom", "Prénom", "Email", "Téléphone"};

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable usersTable = new JTable(model);
        usersTable.setFillsViewportHeight(true);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(usersTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel des boutons d'action
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setBackground(ColorScheme.BACKGROUND);

        JButton addButton = ComponentFactory.createButton("Ajouter", ColorScheme.SUCCESS, Color.WHITE);
        JButton deleteButton = ComponentFactory.createButton("Supprimer", ColorScheme.ERROR, Color.WHITE);
        deleteButton.setEnabled(false); // Désactivé par défaut

        // Activer le bouton supprimer lorsqu'une ligne est sélectionnée
        usersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                deleteButton.setEnabled(usersTable.getSelectedRow() != -1);
            }
        });

        // Action du bouton ajouter
        addButton.addActionListener(e -> {
            // Ouvrir une boîte de dialogue pour ajouter un utilisateur
            showAddUserDialog();
        });

        // Action du bouton supprimer
        deleteButton.addActionListener(e -> {
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow != -1) {
                Long userId = Long.parseLong(model.getValueAt(selectedRow, 0).toString());
                deleteUser(userId);
            }
        });

        actionsPanel.add(addButton);
        actionsPanel.add(deleteButton);

        panel.add(actionsPanel, BorderLayout.SOUTH);

        // Charger les utilisateurs
        loadUsers(model);

        return panel;
    }

    private void refreshUsersPanel() {
        // Actualiser et afficher le panel des utilisateurs
        cardLayout.show(contentPanel, USERS);

        // Remplacer le contenu par une version actualisée
        JPanel usersPanel = createUsersPanel();
        contentPanel.remove(contentPanel.getComponent(1)); // Supprimer l'ancien panel
        contentPanel.add(usersPanel, USERS, 1); // Ajouter le nouveau panel
        cardLayout.show(contentPanel, USERS);
    }

    private void loadUsers(DefaultTableModel model) {
        // Vider le modèle
        model.setRowCount(0);

        // Récupérer les utilisateurs
        List<Utilisateur> utilisateurs = ServiceFactory.getUtilisateurService().getAllUtilisateurs();

        // Ajouter les utilisateurs au modèle
        for (Utilisateur utilisateur : utilisateurs) {
            model.addRow(new Object[]{
                    utilisateur.getId(),
                    utilisateur.getNom(),
                    utilisateur.getPrenom(),
                    utilisateur.getEmail(),
                    utilisateur.getTelephone()
            });
        }
    }

    private JPanel createDriversPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorScheme.BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = ComponentFactory.createTitleLabel("Gestion des conducteurs");
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table des conducteurs
        String[] columnNames = {"ID", "Nom", "Prénom", "Email", "Téléphone", "N° Permis", "Véhicule"};

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable driversTable = new JTable(model);
        driversTable.setFillsViewportHeight(true);
        driversTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(driversTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel des boutons d'action
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setBackground(ColorScheme.BACKGROUND);

        JButton addButton = ComponentFactory.createButton("Ajouter", ColorScheme.SUCCESS, Color.WHITE);
        JButton deleteButton = ComponentFactory.createButton("Supprimer", ColorScheme.ERROR, Color.WHITE);
        deleteButton.setEnabled(false); // Désactivé par défaut

        // Activer le bouton supprimer lorsqu'une ligne est sélectionnée
        driversTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                deleteButton.setEnabled(driversTable.getSelectedRow() != -1);
            }
        });

        // Action du bouton ajouter
        addButton.addActionListener(e -> {
            // Ouvrir une boîte de dialogue pour ajouter un conducteur
            showAddDriverDialog();
        });

        // Action du bouton supprimer
        deleteButton.addActionListener(e -> {
            int selectedRow = driversTable.getSelectedRow();
            if (selectedRow != -1) {
                Long driverId = Long.parseLong(model.getValueAt(selectedRow, 0).toString());
                deleteDriver(driverId);
            }
        });

        actionsPanel.add(addButton);
        actionsPanel.add(deleteButton);

        panel.add(actionsPanel, BorderLayout.SOUTH);

        // Charger les conducteurs
        loadDrivers(model);

        return panel;
    }

    private void refreshDriversPanel() {
        // Actualiser et afficher le panel des conducteurs
        cardLayout.show(contentPanel, DRIVERS);

        // Remplacer le contenu par une version actualisée
        JPanel driversPanel = createDriversPanel();
        contentPanel.remove(contentPanel.getComponent(2)); // Supprimer l'ancien panel
        contentPanel.add(driversPanel, DRIVERS, 2); // Ajouter le nouveau panel
        cardLayout.show(contentPanel, DRIVERS);
    }

    private void loadDrivers(DefaultTableModel model) {
        // Vider le modèle
        model.setRowCount(0);

        // Récupérer les conducteurs
        List<Conducteur> conducteurs = ServiceFactory.getConducteurService().getAllConducteurs();

        // Ajouter les conducteurs au modèle
        for (Conducteur conducteur : conducteurs) {
            model.addRow(new Object[]{
                    conducteur.getId(),
                    conducteur.getNom(),
                    conducteur.getPrenom(),
                    conducteur.getEmail(),
                    conducteur.getTelephone(),
                    conducteur.getNumeroPermis(),
                    conducteur.getVehiculeInfo()
            });
        }
    }

    private JPanel createRidesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorScheme.BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = ComponentFactory.createTitleLabel("Gestion des trajets");
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table des trajets
        String[] columnNames = {"ID", "Départ", "Arrivée", "Date/Heure", "Prix", "Places", "Conducteur", "Statut"};

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable ridesTable = new JTable(model);
        ridesTable.setFillsViewportHeight(true);
        ridesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(ridesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Boutons d'action
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setBackground(ColorScheme.BACKGROUND);

        JButton viewButton = ComponentFactory.createButton("Voir détails", ColorScheme.INFO, Color.WHITE);
        viewButton.setEnabled(false); // Désactivé par défaut

        JButton deleteButton = ComponentFactory.createButton("Supprimer", ColorScheme.ERROR, Color.WHITE);
        deleteButton.setEnabled(false); // Désactivé par défaut

        // Activer les boutons lorsqu'une ligne est sélectionnée
        ridesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean rowSelected = ridesTable.getSelectedRow() != -1;
                viewButton.setEnabled(rowSelected);
                deleteButton.setEnabled(rowSelected);
            }
        });

        // Action du bouton voir détails
        viewButton.addActionListener(e -> {
            int selectedRow = ridesTable.getSelectedRow();
            if (selectedRow != -1) {
                Long rideId = Long.parseLong(model.getValueAt(selectedRow, 0).toString());
                showRideDetailsDialog(rideId);
            }
        });

        // Action du bouton supprimer
        deleteButton.addActionListener(e -> {
            int selectedRow = ridesTable.getSelectedRow();
            if (selectedRow != -1) {
                Long rideId = Long.parseLong(model.getValueAt(selectedRow, 0).toString());
                deleteRide(rideId);
            }
        });

        actionsPanel.add(viewButton);
        actionsPanel.add(deleteButton);

        panel.add(actionsPanel, BorderLayout.SOUTH);

        // Charger les trajets
        loadRides(model);

        return panel;
    }

    private void refreshRidesPanel() {
        // Actualiser et afficher le panel des trajets
        cardLayout.show(contentPanel, RIDES);

        // Remplacer le contenu par une version actualisée
        JPanel ridesPanel = createRidesPanel();
        contentPanel.remove(contentPanel.getComponent(3)); // Supprimer l'ancien panel
        contentPanel.add(ridesPanel, RIDES, 3); // Ajouter le nouveau panel
        cardLayout.show(contentPanel, RIDES);
    }

    private void loadRides(DefaultTableModel model) {
        // Vider le modèle
        model.setRowCount(0);

        // Récupérer les trajets
        List<Trajet> trajets = ServiceFactory.getTrajetService().getAllTrajets();

        // Ajouter les trajets au modèle
        for (Trajet trajet : trajets) {
            String conducteurInfo = trajet.getConducteur() != null ?
                    trajet.getConducteur().getPrenom() + " " + trajet.getConducteur().getNom() : "N/A";

            model.addRow(new Object[]{
                    trajet.getId(),
                    trajet.getLieuDepart(),
                    trajet.getLieuArrivee(),
                    trajet.getDateDepart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    trajet.getPrix(),
                    trajet.getNbPlacesDisponibles(),
                    conducteurInfo,
                    trajet.isEstAnnule() ? "Annulé" : "Actif"
            });
        }
    }

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorScheme.BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = ComponentFactory.createTitleLabel("Génération de rapports");
        panel.add(titleLabel, BorderLayout.NORTH);

        // Panel principal
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorScheme.SECONDARY),
                new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 10, 10, 10);

        // Description
        JLabel descLabel = new JLabel("Générer des rapports sur l'activité de la plateforme.");
        descLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        content.add(descLabel, c);

        // Rapport utilisateurs
        JButton usersReportButton = ComponentFactory.createButton("Rapport des utilisateurs", ColorScheme.PRIMARY, Color.WHITE);
        usersReportButton.addActionListener(e -> generateUsersReport());
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        content.add(usersReportButton, c);

        JLabel usersReportLabel = new JLabel("Statistiques sur les inscriptions et activités des utilisateurs");
        c.gridx = 1;
        c.gridy = 1;
        content.add(usersReportLabel, c);

        // Rapport trajets
        JButton ridesReportButton = ComponentFactory.createButton("Rapport des trajets", ColorScheme.PRIMARY, Color.WHITE);
        ridesReportButton.addActionListener(e -> generateRidesReport());
        c.gridx = 0;
        c.gridy = 2;
        content.add(ridesReportButton, c);

        JLabel ridesReportLabel = new JLabel("Statistiques sur les trajets proposés, annulés, etc.");
        c.gridx = 1;
        c.gridy = 2;
        content.add(ridesReportLabel, c);

        // Rapport réservations
        JButton reservationsReportButton = ComponentFactory.createButton("Rapport des réservations", ColorScheme.PRIMARY, Color.WHITE);
        reservationsReportButton.addActionListener(e -> generateReservationsReport());
        c.gridx = 0;
        c.gridy = 3;
        content.add(reservationsReportButton, c);

        JLabel reservationsReportLabel = new JLabel("Statistiques sur les réservations effectuées et leur statut");
        c.gridx = 1;
        c.gridy = 3;
        content.add(reservationsReportLabel, c);

        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Ajouter un utilisateur", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);

        // Titre
        JLabel titleLabel = ComponentFactory.createTitleLabel("Nouvel utilisateur");
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        contentPanel.add(titleLabel, c);

        // Nom
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        contentPanel.add(new JLabel("Nom:"), c);

        c.gridx = 1;
        c.gridy = 1;
        JTextField nomField = ComponentFactory.createTextField("Nom");
        contentPanel.add(nomField, c);

        // Prénom
        c.gridx = 0;
        c.gridy = 2;
        contentPanel.add(new JLabel("Prénom:"), c);

        c.gridx = 1;
        c.gridy = 2;
        JTextField prenomField = ComponentFactory.createTextField("Prénom");
        contentPanel.add(prenomField, c);

        // Email
        c.gridx = 0;
        c.gridy = 3;
        contentPanel.add(new JLabel("Email:"), c);

        c.gridx = 1;
        c.gridy = 3;
        JTextField emailField = ComponentFactory.createTextField("Email");
        contentPanel.add(emailField, c);

        // Mot de passe
        c.gridx = 0;
        c.gridy = 4;
        contentPanel.add(new JLabel("Mot de passe:"), c);

        c.gridx = 1;
        c.gridy = 4;
        JPasswordField passwordField = ComponentFactory.createPasswordField();
        contentPanel.add(passwordField, c);

        // Téléphone
        c.gridx = 0;
        c.gridy = 5;
        contentPanel.add(new JLabel("Téléphone:"), c);

        c.gridx = 1;
        c.gridy = 5;
        JTextField telephoneField = ComponentFactory.createTextField("Téléphone");
        contentPanel.add(telephoneField, c);

        // Préférences
        c.gridx = 0;
        c.gridy = 6;
        contentPanel.add(new JLabel("Préférences:"), c);

        c.gridx = 1;
        c.gridy = 6;
        JTextField preferencesField = ComponentFactory.createTextField("Préférences");
        contentPanel.add(preferencesField, c);

        dialog.add(contentPanel, BorderLayout.CENTER);

        // Boutons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = ComponentFactory.createButton("Annuler", Color.LIGHT_GRAY, Color.BLACK);
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton saveButton = ComponentFactory.createButton("Enregistrer", ColorScheme.PRIMARY, Color.WHITE);
        saveButton.addActionListener(e -> {
            // Vérifications
            String nom = nomField.getText();
            String prenom = prenomField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String telephone = telephoneField.getText();
            String preferences = preferencesField.getText();

            // Créer l'utilisateur
            Utilisateur utilisateur = new Utilisateur(nom, prenom, email, password, telephone);
            utilisateur.setPreferences(preferences);

            try {
                Long id = ServiceFactory.getUtilisateurService().creerUtilisateur(utilisateur);
                if (id != null) {
                    JOptionPane.showMessageDialog(dialog,
                            "Utilisateur créé avec succès !",
                            "Succès", JOptionPane.INFORMATION_MESSAGE);

                    // Fermer la boîte de dialogue
                    dialog.dispose();

                    // Actualiser la liste des utilisateurs
                    refreshUsersPanel();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Erreur lors de la création de l'utilisateur.",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Erreur: " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonsPanel.add(cancelButton);
        buttonsPanel.add(saveButton);

        dialog.add(buttonsPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void deleteUser(Long userId) {
        int choice = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer cet utilisateur ?",
                "Suppression d'utilisateur", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                boolean success = ServiceFactory.getUtilisateurService().supprimerUtilisateur(userId);
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Utilisateur supprimé avec succès !",
                            "Succès", JOptionPane.INFORMATION_MESSAGE);

                    // Actualiser la liste des utilisateurs
                    refreshUsersPanel();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Erreur lors de la suppression de l'utilisateur.",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddDriverDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Ajouter un conducteur", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);

        // Titre
        JLabel titleLabel = ComponentFactory.createTitleLabel("Nouveau conducteur");
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        contentPanel.add(titleLabel, c);

        // Nom
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        contentPanel.add(new JLabel("Nom:"), c);

        c.gridx = 1;
        c.gridy = 1;
        JTextField nomField = ComponentFactory.createTextField("Nom");
        contentPanel.add(nomField, c);

        // Prénom
        c.gridx = 0;
        c.gridy = 2;
        contentPanel.add(new JLabel("Prénom:"), c);

        c.gridx = 1;
        c.gridy = 2;
        JTextField prenomField = ComponentFactory.createTextField("Prénom");
        contentPanel.add(prenomField, c);

        // Email
        c.gridx = 0;
        c.gridy = 3;
        contentPanel.add(new JLabel("Email:"), c);

        c.gridx = 1;
        c.gridy = 3;
        JTextField emailField = ComponentFactory.createTextField("Email");
        contentPanel.add(emailField, c);

        // Mot de passe
        c.gridx = 0;
        c.gridy = 4;
        contentPanel.add(new JLabel("Mot de passe:"), c);

        c.gridx = 1;
        c.gridy = 4;
        JPasswordField passwordField = ComponentFactory.createPasswordField();
        contentPanel.add(passwordField, c);

        // Téléphone
        c.gridx = 0;
        c.gridy = 5;
        contentPanel.add(new JLabel("Téléphone:"), c);

        c.gridx = 1;
        c.gridy = 5;
        JTextField telephoneField = ComponentFactory.createTextField("Téléphone");
        contentPanel.add(telephoneField, c);

        // Numéro de permis
        c.gridx = 0;
        c.gridy = 6;
        contentPanel.add(new JLabel("N° de permis:"), c);

        c.gridx = 1;
        c.gridy = 6;
        JTextField permisField = ComponentFactory.createTextField("Numéro de permis");
        contentPanel.add(permisField, c);

        // Informations véhicule
        c.gridx = 0;
        c.gridy = 7;
        contentPanel.add(new JLabel("Véhicule:"), c);

        c.gridx = 1;
        c.gridy = 7;
        JTextField vehiculeField = ComponentFactory.createTextField("Marque, modèle, immatriculation");
        contentPanel.add(vehiculeField, c);

        dialog.add(contentPanel, BorderLayout.CENTER);

        // Boutons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = ComponentFactory.createButton("Annuler", Color.LIGHT_GRAY, Color.BLACK);
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton saveButton = ComponentFactory.createButton("Enregistrer", ColorScheme.PRIMARY, Color.WHITE);
        saveButton.addActionListener(e -> {
            // Vérifications
            String nom = nomField.getText();
            String prenom = prenomField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String telephone = telephoneField.getText();
            String permis = permisField.getText();
            String vehicule = vehiculeField.getText();

            // Créer le conducteur
            Conducteur conducteur = new Conducteur(nom, prenom, email, password, telephone, permis);
            conducteur.setVehiculeInfo(vehicule);

            try {
                Long id = ServiceFactory.getConducteurService().creerConducteur(conducteur);
                if (id != null) {
                    JOptionPane.showMessageDialog(dialog,
                            "Conducteur créé avec succès !",
                            "Succès", JOptionPane.INFORMATION_MESSAGE);

                    // Fermer la boîte de dialogue
                    dialog.dispose();

                    // Actualiser la liste des conducteurs
                    refreshDriversPanel();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Erreur lors de la création du conducteur.",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Erreur: " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonsPanel.add(cancelButton);
        buttonsPanel.add(saveButton);

        dialog.add(buttonsPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void deleteDriver(Long driverId) {
        int choice = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer ce conducteur ? Tous ses trajets seront également supprimés.",
                "Suppression de conducteur", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                boolean success = ServiceFactory.getConducteurService().supprimerConducteur(driverId);
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Conducteur et tous ses trajets supprimés avec succès !",
                            "Succès", JOptionPane.INFORMATION_MESSAGE);

                    // Actualiser la liste des conducteurs
                    refreshDriversPanel();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Erreur lors de la suppression du conducteur.",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace(); // Afficher la trace d'erreur pour le débogage
            }
        }
    }
    private void showRideDetailsDialog(Long rideId) {
        // Récupérer le trajet
        try {
            Optional<Trajet> optTrajet = ServiceFactory.getTrajetService().getTrajetById(rideId);
            if (optTrajet.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Trajet non trouvé.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Trajet trajet = optTrajet.get();

            // Créer une boîte de dialogue pour afficher les détails
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Détails du trajet", true);
            dialog.setLayout(new BorderLayout());
            dialog.setSize(500, 400);
            dialog.setLocationRelativeTo(this);

            JPanel contentPanel = new JPanel(new GridBagLayout());
            contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(5, 5, 5, 5);

            // Titre
            JLabel titleLabel = ComponentFactory.createTitleLabel("Détails du trajet #" + rideId);
            c.gridx = 0;
            c.gridy = 0;
            c.gridwidth = 2;
            contentPanel.add(titleLabel, c);

            // Lieu de départ
            c.gridx = 0;
            c.gridy = 1;
            c.gridwidth = 1;
            contentPanel.add(new JLabel("Lieu de départ:"), c);

            c.gridx = 1;
            c.gridy = 1;
            contentPanel.add(new JLabel(trajet.getLieuDepart()), c);

            // Lieu d'arrivée
            c.gridx = 0;
            c.gridy = 2;
            contentPanel.add(new JLabel("Lieu d'arrivée:"), c);

            c.gridx = 1;
            c.gridy = 2;
            contentPanel.add(new JLabel(trajet.getLieuArrivee()), c);

            // Date et heure de départ
            c.gridx = 0;
            c.gridy = 3;
            contentPanel.add(new JLabel("Date et heure:"), c);

            c.gridx = 1;
            c.gridy = 3;
            contentPanel.add(new JLabel(trajet.getDateDepart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))), c);

            // Prix
            c.gridx = 0;
            c.gridy = 4;
            contentPanel.add(new JLabel("Prix:"), c);

            c.gridx = 1;
            c.gridy = 4;
            contentPanel.add(new JLabel(String.format("%.2f Dinars", trajet.getPrix())), c);

            // Places disponibles
            c.gridx = 0;
            c.gridy = 5;
            contentPanel.add(new JLabel("Places disponibles:"), c);

            c.gridx = 1;
            c.gridy = 5;
            contentPanel.add(new JLabel(String.valueOf(trajet.getNbPlacesDisponibles())), c);

            // Conducteur
            c.gridx = 0;
            c.gridy = 6;
            contentPanel.add(new JLabel("Conducteur:"), c);

            c.gridx = 1;
            c.gridy = 6;
            String conducteurInfo = trajet.getConducteur() != null ?
                    trajet.getConducteur().getPrenom() + " " + trajet.getConducteur().getNom() : "N/A";
            contentPanel.add(new JLabel(conducteurInfo), c);

            // Statut
            c.gridx = 0;
            c.gridy = 7;
            contentPanel.add(new JLabel("Statut:"), c);

            c.gridx = 1;
            c.gridy = 7;
            JLabel statutLabel = new JLabel(trajet.isEstAnnule() ? "Annulé" : "Actif");
            statutLabel.setForeground(trajet.isEstAnnule() ? ColorScheme.ERROR : ColorScheme.SUCCESS);
            contentPanel.add(statutLabel, c);

            dialog.add(contentPanel, BorderLayout.CENTER);

            // Bouton fermer
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            JButton closeButton = ComponentFactory.createButton("Fermer", ColorScheme.SECONDARY, Color.BLACK);
            closeButton.addActionListener(e -> dialog.dispose());

            buttonsPanel.add(closeButton);

            dialog.add(buttonsPanel, BorderLayout.SOUTH);

            dialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRide(Long rideId) {
        int choice = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer ce trajet ? Cette action est irréversible.",
                "Suppression de trajet", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                boolean success = ServiceFactory.getTrajetService().deleteTrajet(rideId);
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Trajet supprimé avec succès !",
                            "Succès", JOptionPane.INFORMATION_MESSAGE);

                    // Actualiser la liste des trajets
                    refreshRidesPanel();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Erreur lors de la suppression du trajet.",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void generateUsersReport() {
        try {
            ServiceFactory.getAdminService().genererRapportUtilisateurs();
            JOptionPane.showMessageDialog(this,
                    "Rapport des utilisateurs généré avec succès !",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la génération du rapport: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateRidesReport() {
        try {
            ServiceFactory.getAdminService().genererRapportTrajets();
            JOptionPane.showMessageDialog(this,
                    "Rapport des trajets généré avec succès !",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la génération du rapport: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateReservationsReport() {
        try {
            ServiceFactory.getAdminService().genererRapportReservations();
            JOptionPane.showMessageDialog(this,
                    "Rapport des réservations généré avec succès !",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la génération du rapport: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
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