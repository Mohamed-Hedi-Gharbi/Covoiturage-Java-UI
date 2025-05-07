package covoiturage.ui.gui.panels;

import covoiturage.model.Conducteur;
import covoiturage.model.Reservation;
import covoiturage.model.Trajet;
import covoiturage.service.ServiceFactory;
import covoiturage.ui.gui.MainFrame;
import covoiturage.ui.gui.SessionManager;
import covoiturage.ui.gui.components.NavigationBar;
import covoiturage.ui.gui.components.SideBar;
import covoiturage.ui.gui.utils.ColorScheme;
import covoiturage.ui.gui.utils.ComponentFactory;
import covoiturage.ui.validator.InputValidator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DriverPanel extends JPanel {
    private MainFrame mainFrame;
    private Conducteur conducteur;
    private CardLayout cardLayout;
    private JPanel contentPanel;

    // Constantes pour identifier les sous-panneaux
    public static final String DASHBOARD = "DASHBOARD";
    public static final String MY_RIDES = "MY_RIDES";
    public static final String ADD_RIDE = "ADD_RIDE";
    public static final String RESERVATIONS = "RESERVATIONS";
    public static final String PROFILE = "PROFILE";

    // Formatter pour les dates
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public DriverPanel(MainFrame mainFrame, Conducteur conducteur) {
        this.mainFrame = mainFrame;
        this.conducteur = conducteur;

        setLayout(new BorderLayout());
        setBackground(ColorScheme.BACKGROUND);

        // Barre de navigation en haut
        NavigationBar navBar = new NavigationBar(
                "Conducteur: " + conducteur.getPrenom() + " " + conducteur.getNom(),
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
        contentPanel.add(createMyRidesPanel(), MY_RIDES);
        contentPanel.add(createAddRidePanel(), ADD_RIDE);
        contentPanel.add(createReservationsPanel(), RESERVATIONS);
        contentPanel.add(createProfilePanel(), PROFILE);

        add(contentPanel, BorderLayout.CENTER);

        // Afficher le tableau de bord par défaut
        cardLayout.show(contentPanel, DASHBOARD);
    }

    private SideBar createSideBar() {
        SideBar sideBar = new SideBar();

        // Ajout des éléments de menu
        sideBar.addMenuItem("Tableau de bord", "/images/dashboard_icon.png", e -> cardLayout.show(contentPanel, DASHBOARD));
        sideBar.addMenuItem("Mes trajets", "/images/car_icon.png", e -> refreshMyRidesPanel());
        sideBar.addMenuItem("Proposer un trajet", "/images/add_icon.png", e -> cardLayout.show(contentPanel, ADD_RIDE));
        sideBar.addMenuItem("Gérer réservations", "/images/reservation_icon.png", e -> refreshReservationsPanel());
        sideBar.addMenuItem("Mon profil", "/images/profile_icon.png", e -> cardLayout.show(contentPanel, PROFILE));

        return sideBar;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorScheme.BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = ComponentFactory.createTitleLabel("Tableau de bord conducteur");
        panel.add(titleLabel, BorderLayout.NORTH);

        // Contenu principal
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(ColorScheme.BACKGROUND);
        content.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Bienvenue
        JLabel welcomeLabel = ComponentFactory.createSubtitleLabel("Bienvenue, " + conducteur.getPrenom() + " !");
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(welcomeLabel);
        content.add(Box.createRigidArea(new Dimension(0, 20)));

        // Statistiques
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Récupérer les statistiques
        List<Trajet> trajets = ServiceFactory.getConducteurService().getTrajetsByConducteur(conducteur.getId());
        int totalTrajets = trajets.size();
        int trajetsActifs = (int) trajets.stream()
                .filter(t -> !t.isEstAnnule() && t.getDateDepart().isAfter(LocalDateTime.now()))
                .count();

        // Carte 1: Trajets proposés
        JPanel card1 = createStatCard("Trajets proposés", String.valueOf(totalTrajets), ColorScheme.PRIMARY);
        statsPanel.add(card1);

        // Carte 2: Trajets actifs
        JPanel card2 = createStatCard("Trajets actifs", String.valueOf(trajetsActifs), ColorScheme.ACCENT);
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

        JButton addRideButton = ComponentFactory.createButton("Proposer un trajet", ColorScheme.PRIMARY, Color.WHITE);
        addRideButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addRideButton.setBorder(new EmptyBorder(5, 10, 5, 10));
        addRideButton.addActionListener(e -> cardLayout.show(contentPanel, ADD_RIDE));

        JButton reservationsButton = ComponentFactory.createButton("Gérer les réservations", ColorScheme.SECONDARY, ColorScheme.TEXT);
        reservationsButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        reservationsButton.setBorder(new EmptyBorder(5, 10, 5, 10));
        reservationsButton.addActionListener(e -> refreshReservationsPanel());

        card3.add(actionsLabel);
        card3.add(Box.createRigidArea(new Dimension(0, 10)));
        card3.add(addRideButton);
        card3.add(Box.createRigidArea(new Dimension(0, 10)));
        card3.add(reservationsButton);
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

    private JPanel createMyRidesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorScheme.BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = ComponentFactory.createTitleLabel("Mes trajets");
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table des trajets
        String[] columnNames = {"ID", "Départ", "Arrivée", "Date/Heure", "Prix", "Places", "Statut"};

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable trajetTable = new JTable(model);
        trajetTable.setFillsViewportHeight(true);
        trajetTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(trajetTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel des boutons d'action
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setBackground(ColorScheme.BACKGROUND);

        JButton modifyButton = ComponentFactory.createButton("Modifier", ColorScheme.INFO, Color.WHITE);
        modifyButton.setEnabled(false); // Désactivé par défaut

        JButton cancelButton = ComponentFactory.createButton("Annuler", ColorScheme.ERROR, Color.WHITE);
        cancelButton.setEnabled(false); // Désactivé par défaut

        JButton activateButton = ComponentFactory.createButton("Réactiver", ColorScheme.SUCCESS, Color.WHITE);
        activateButton.setEnabled(false); // Désactivé par défaut

        JButton deleteButton = ComponentFactory.createButton("Supprimer", ColorScheme.ERROR, Color.WHITE);
        deleteButton.setEnabled(false); // Désactivé par défaut

        // Activer les boutons lorsqu'une ligne est sélectionnée
        trajetTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean rowSelected = trajetTable.getSelectedRow() != -1;
                modifyButton.setEnabled(rowSelected);
                cancelButton.setEnabled(rowSelected);
                activateButton.setEnabled(rowSelected);
                deleteButton.setEnabled(rowSelected);

                if (rowSelected) {
                    // Récupérer le statut du trajet sélectionné
                    String statut = (String) model.getValueAt(trajetTable.getSelectedRow(), 6);

                    // Activer/désactiver les boutons en fonction du statut
                    boolean estAnnule = statut.equals("Annulé");
                    cancelButton.setEnabled(!estAnnule);
                    activateButton.setEnabled(estAnnule);
                }
            }
        });

        // Action du bouton modifier
        modifyButton.addActionListener(e -> {
            int selectedRow = trajetTable.getSelectedRow();
            if (selectedRow != -1) {
                Long trajetId = Long.parseLong(model.getValueAt(selectedRow, 0).toString());
                showModifyRideDialog(trajetId);
            }
        });

        // Action du bouton annuler
        cancelButton.addActionListener(e -> {
            int selectedRow = trajetTable.getSelectedRow();
            if (selectedRow != -1) {
                Long trajetId = Long.parseLong(model.getValueAt(selectedRow, 0).toString());
                cancelTrajet(trajetId);
            }
        });

        // Action du bouton réactiver
        activateButton.addActionListener(e -> {
            int selectedRow = trajetTable.getSelectedRow();
            if (selectedRow != -1) {
                Long trajetId = Long.parseLong(model.getValueAt(selectedRow, 0).toString());
                reactivateTrajet(trajetId);
            }
        });

        // Action du bouton supprimer
        deleteButton.addActionListener(e -> {
            int selectedRow = trajetTable.getSelectedRow();
            if (selectedRow != -1) {
                Long trajetId = Long.parseLong(model.getValueAt(selectedRow, 0).toString());
                deleteTrajet(trajetId);
            }
        });

        actionsPanel.add(modifyButton);
        actionsPanel.add(cancelButton);
        actionsPanel.add(activateButton);
        actionsPanel.add(deleteButton);

        panel.add(actionsPanel, BorderLayout.SOUTH);

        // Charger les trajets du conducteur
        loadTrajets(model);

        return panel;
    }

    private void refreshMyRidesPanel() {
        // Actualiser et afficher le panel des trajets
        cardLayout.show(contentPanel, MY_RIDES);

        // Remplacer le contenu par une version actualisée
        JPanel myRidesPanel = createMyRidesPanel();
        contentPanel.remove(contentPanel.getComponent(1)); // Supprimer l'ancien panel
        contentPanel.add(myRidesPanel, MY_RIDES, 1); // Ajouter le nouveau panel
        cardLayout.show(contentPanel, MY_RIDES);
    }

    private void loadTrajets(DefaultTableModel model) {
        // Vider le modèle
        model.setRowCount(0);

        // Récupérer les trajets du conducteur
        List<Trajet> trajets = ServiceFactory.getConducteurService().getTrajetsByConducteur(conducteur.getId());

        // Ajouter les trajets au modèle
        for (Trajet trajet : trajets) {
            model.addRow(new Object[]{
                    trajet.getId(),
                    trajet.getLieuDepart(),
                    trajet.getLieuArrivee(),
                    trajet.getDateDepart().format(formatter),
                    trajet.getPrix(),
                    trajet.getNbPlacesDisponibles(),
                    trajet.isEstAnnule() ? "Annulé" : "Actif"
            });
        }
    }

    private JPanel createAddRidePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorScheme.BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = ComponentFactory.createTitleLabel("Proposer un trajet");
        panel.add(titleLabel, BorderLayout.NORTH);

        // Formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorScheme.SECONDARY),
                new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 10, 10, 10);

        // Lieu de départ
        c.gridx = 0;
        c.gridy = 0;
        formPanel.add(new JLabel("Lieu de départ:"), c);

        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1.0;
        JTextField departField = ComponentFactory.createTextField("Ville de départ");
        formPanel.add(departField, c);

        // Lieu d'arrivée
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.0;
        formPanel.add(new JLabel("Lieu d'arrivée:"), c);

        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1.0;
        JTextField arriveeField = ComponentFactory.createTextField("Ville d'arrivée");
        formPanel.add(arriveeField, c);

        // Date et heure de départ
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0.0;
        formPanel.add(new JLabel("Date et heure:"), c);

        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 1.0;
        JTextField dateField = ComponentFactory.createTextField("Format: dd/MM/yyyy HH:mm");
        formPanel.add(dateField, c);

        // Prix
        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 0.0;
        formPanel.add(new JLabel("Prix (Dinars):"), c);

        c.gridx = 1;
        c.gridy = 3;
        c.weightx = 1.0;
        JTextField prixField = ComponentFactory.createTextField("Prix par place");
        formPanel.add(prixField, c);

        // Nombre de places
        c.gridx = 0;
        c.gridy = 4;
        c.weightx = 0.0;
        formPanel.add(new JLabel("Places disponibles:"), c);

        c.gridx = 1;
        c.gridy = 4;
        c.weightx = 1.0;
        JTextField placesField = ComponentFactory.createTextField("Nombre de places");
        formPanel.add(placesField, c);

        panel.add(formPanel, BorderLayout.CENTER);

        // Boutons d'action
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setBackground(ColorScheme.BACKGROUND);

        JButton cancelButton = ComponentFactory.createButton("Annuler", Color.LIGHT_GRAY, Color.BLACK);
        cancelButton.addActionListener(e -> {
            // Réinitialiser les champs
            departField.setText("Ville de départ");
            arriveeField.setText("Ville d'arrivée");
            dateField.setText("Format: dd/MM/yyyy HH:mm");
            prixField.setText("Prix par place");
            placesField.setText("Nombre de places");

            // Retour au tableau de bord
            cardLayout.show(contentPanel, DASHBOARD);
        });

        JButton saveButton = ComponentFactory.createButton("Enregistrer", ColorScheme.PRIMARY, Color.WHITE);
        saveButton.addActionListener(e -> {
            // Vérifications
            String depart = departField.getText();
            String arrivee = arriveeField.getText();
            String date = dateField.getText();
            String prixStr = prixField.getText();
            String placesStr = placesField.getText();

            if (depart.isEmpty() || depart.equals("Ville de départ") ||
                    arrivee.isEmpty() || arrivee.equals("Ville d'arrivée") ||
                    date.isEmpty() || date.equals("Format: dd/MM/yyyy HH:mm") ||
                    prixStr.isEmpty() || prixStr.equals("Prix par place") ||
                    placesStr.isEmpty() || placesStr.equals("Nombre de places")) {
                JOptionPane.showMessageDialog(panel,
                        "Veuillez remplir tous les champs.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Vérifier le format de la date
            if (!InputValidator.isValidDateTime(date)) {
                JOptionPane.showMessageDialog(panel,
                        "Format de date et heure invalide. Utilisez le format dd/MM/yyyy HH:mm",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Vérifier que la date est dans le futur
            LocalDateTime dateDepart = InputValidator.parseDateTime(date);
            if (dateDepart.isBefore(LocalDateTime.now())) {
                JOptionPane.showMessageDialog(panel,
                        "La date de départ doit être dans le futur.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Vérifier le prix
            if (!InputValidator.isPositiveDouble(prixStr)) {
                JOptionPane.showMessageDialog(panel,
                        "Le prix doit être un nombre positif.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Vérifier le nombre de places
            if (!InputValidator.isPositiveInteger(placesStr)) {
                JOptionPane.showMessageDialog(panel,
                        "Le nombre de places doit être un entier positif.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Créer le trajet
            Trajet trajet = new Trajet(
                    depart,
                    arrivee,
                    dateDepart,
                    Double.parseDouble(prixStr),
                    Integer.parseInt(placesStr)
            );
            trajet.setConducteur(conducteur);

            try {
                Long id = ServiceFactory.getConducteurService().proposerTrajet(trajet);
                if (id != null) {
                    JOptionPane.showMessageDialog(panel,
                            "Trajet créé avec succès !",
                            "Succès", JOptionPane.INFORMATION_MESSAGE);

                    // Réinitialiser les champs
                    departField.setText("Ville de départ");
                    arriveeField.setText("Ville d'arrivée");
                    dateField.setText("Format: dd/MM/yyyy HH:mm");
                    prixField.setText("Prix par place");
                    placesField.setText("Nombre de places");

                    // Actualiser et afficher mes trajets
                    refreshMyRidesPanel();
                } else {
                    JOptionPane.showMessageDialog(panel,
                            "Erreur lors de la création du trajet.",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel,
                        "Erreur: " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonsPanel.add(cancelButton);
        buttonsPanel.add(saveButton);

        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorScheme.BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = ComponentFactory.createTitleLabel("Gestion des réservations");
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table des réservations
        String[] columnNames = {"ID", "Trajet", "Date", "Passager", "Places", "Statut"};

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable reservationsTable = new JTable(model);
        reservationsTable.setFillsViewportHeight(true);
        reservationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(reservationsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel des boutons d'action
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setBackground(ColorScheme.BACKGROUND);

        JButton acceptButton = ComponentFactory.createButton("Accepter", ColorScheme.SUCCESS, Color.WHITE);
        acceptButton.setEnabled(false); // Désactivé par défaut

        JButton rejectButton = ComponentFactory.createButton("Refuser", ColorScheme.ERROR, Color.WHITE);
        rejectButton.setEnabled(false); // Désactivé par défaut

        // Activer les boutons lorsqu'une ligne est sélectionnée
        reservationsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean rowSelected = reservationsTable.getSelectedRow() != -1;

                if (rowSelected) {
                    // Récupérer le statut de la réservation sélectionnée
                    String statut = (String) model.getValueAt(reservationsTable.getSelectedRow(), 5);

                    // Activer/désactiver les boutons en fonction du statut
                    boolean enAttente = statut.equals("EN_ATTENTE");
                    acceptButton.setEnabled(enAttente);
                    rejectButton.setEnabled(enAttente);
                } else {
                    acceptButton.setEnabled(false);
                    rejectButton.setEnabled(false);
                }
            }
        });

        // Action du bouton accepter
        acceptButton.addActionListener(e -> {
            int selectedRow = reservationsTable.getSelectedRow();
            if (selectedRow != -1) {
                Long reservationId = Long.parseLong(model.getValueAt(selectedRow, 0).toString());
                acceptReservation(reservationId);
            }
        });

        // Action du bouton refuser
        rejectButton.addActionListener(e -> {
            int selectedRow = reservationsTable.getSelectedRow();
            if (selectedRow != -1) {
                Long reservationId = Long.parseLong(model.getValueAt(selectedRow, 0).toString());
                rejectReservation(reservationId);
            }
        });

        actionsPanel.add(acceptButton);
        actionsPanel.add(rejectButton);

        panel.add(actionsPanel, BorderLayout.SOUTH);

        // Charger les réservations
        List<Trajet> trajetsConducteur = ServiceFactory.getConducteurService().getTrajetsByConducteur(conducteur.getId());
        List<Reservation> reservations = new ArrayList<>();

        // Collecter toutes les réservations pour les trajets du conducteur
        for (Trajet trajet : trajetsConducteur) {
            List<Reservation> reservationsTrajet = ServiceFactory.getReservationService().getReservationsByTrajet(trajet.getId());
            reservations.addAll(reservationsTrajet);
        }

        // Remplir le modèle de tableau avec les réservations
        for (Reservation reservation : reservations) {
            String trajetInfo = reservation.getTrajet().getLieuDepart() + " → " + reservation.getTrajet().getLieuArrivee();
            String dateTrajet = reservation.getTrajet().getDateDepart().format(formatter);
            String passagerInfo = reservation.getUtilisateur().getPrenom() + " " + reservation.getUtilisateur().getNom();

            model.addRow(new Object[]{
                    reservation.getId(),
                    trajetInfo,
                    dateTrajet,
                    passagerInfo,
                    reservation.getNbPlaces(),
                    reservation.getStatut().toString()
            });
        }

        return panel;
    }


    private void refreshReservationsPanel() {
        // Actualiser et afficher le panel des réservations
        cardLayout.show(contentPanel, RESERVATIONS);

        // Remplacer le contenu par une version actualisée
        JPanel reservationsPanel = createReservationsPanel();
        contentPanel.remove(contentPanel.getComponent(3)); // Supprimer l'ancien panel
        contentPanel.add(reservationsPanel, RESERVATIONS, 3); // Ajouter le nouveau panel
        cardLayout.show(contentPanel, RESERVATIONS);
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorScheme.BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = ComponentFactory.createTitleLabel("Mon profil conducteur");
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
        JTextField nomField = new JTextField(conducteur.getNom(), 20);
        formPanel.add(nomField, c);

        // Prénom
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.0;
        formPanel.add(new JLabel("Prénom:"), c);

        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1.0;
        JTextField prenomField = new JTextField(conducteur.getPrenom(), 20);
        formPanel.add(prenomField, c);

        // Email
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0.0;
        formPanel.add(new JLabel("Email:"), c);

        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 1.0;
        JTextField emailField = new JTextField(conducteur.getEmail(), 20);
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
        JTextField telephoneField = new JTextField(conducteur.getTelephone(), 20);
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

        // Numéro de permis
        c.gridx = 0;
        c.gridy = 5;
        c.weightx = 0.0;
        formPanel.add(new JLabel("N° de permis:"), c);

        c.gridx = 1;
        c.gridy = 5;
        c.weightx = 1.0;
        JTextField permisField = new JTextField(conducteur.getNumeroPermis(), 20);
        formPanel.add(permisField, c);

        // Informations véhicule
        c.gridx = 0;
        c.gridy = 6;
        c.weightx = 0.0;
        formPanel.add(new JLabel("Véhicule:"), c);

        c.gridx = 1;
        c.gridy = 6;
        c.weightx = 1.0;
        JTextField vehiculeField = new JTextField(conducteur.getVehiculeInfo(), 20);
        formPanel.add(vehiculeField, c);

        panel.add(formPanel, BorderLayout.CENTER);

        // Boutons d'action
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setBackground(ColorScheme.BACKGROUND);

        JButton saveButton = ComponentFactory.createButton("Enregistrer", ColorScheme.PRIMARY, Color.WHITE);

        // Action du bouton d'enregistrement
        saveButton.addActionListener(e -> {
            // Mettre à jour le conducteur
            conducteur.setNom(nomField.getText());
            conducteur.setPrenom(prenomField.getText());
            conducteur.setTelephone(telephoneField.getText());

            // Vérifier si le mot de passe a été modifié
            String password = new String(passwordField.getPassword());
            if (!password.equals("*********")) {
                conducteur.setMotDePasse(password);
            }

            conducteur.setNumeroPermis(permisField.getText());
            conducteur.setVehiculeInfo(vehiculeField.getText());

            try {
                boolean success = ServiceFactory.getConducteurService().modifierConducteur(conducteur);
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

    private void showModifyRideDialog(Long trajetId) {
        // Récupérer le trajet à modifier
        try {
            Optional<Trajet> optTrajet = ServiceFactory.getTrajetService().getTrajetById(trajetId);
            if (optTrajet.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Trajet non trouvé.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Trajet trajet = optTrajet.get();

            // Créer une boîte de dialogue pour la modification
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Modifier un trajet", true);
            dialog.setLayout(new BorderLayout());
            dialog.setSize(500, 400);
            dialog.setLocationRelativeTo(this);

            JPanel contentPanel = new JPanel(new GridBagLayout());
            contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(5, 5, 5, 5);

            // Titre
            JLabel titleLabel = ComponentFactory.createTitleLabel("Modifier le trajet #" + trajetId);
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
            JTextField departField = new JTextField(trajet.getLieuDepart(), 20);
            contentPanel.add(departField, c);

            // Lieu d'arrivée
            c.gridx = 0;
            c.gridy = 2;
            contentPanel.add(new JLabel("Lieu d'arrivée:"), c);

            c.gridx = 1;
            c.gridy = 2;
            JTextField arriveeField = new JTextField(trajet.getLieuArrivee(), 20);
            contentPanel.add(arriveeField, c);

            // Date et heure de départ
            c.gridx = 0;
            c.gridy = 3;
            contentPanel.add(new JLabel("Date et heure:"), c);

            c.gridx = 1;
            c.gridy = 3;
            JTextField dateField = new JTextField(trajet.getDateDepart().format(formatter), 20);
            contentPanel.add(dateField, c);

            // Prix
            c.gridx = 0;
            c.gridy = 4;
            contentPanel.add(new JLabel("Prix (Dinars):"), c);

            c.gridx = 1;
            c.gridy = 4;
            JTextField prixField = new JTextField(String.valueOf(trajet.getPrix()), 20);
            contentPanel.add(prixField, c);

            // Nombre de places
            c.gridx = 0;
            c.gridy = 5;
            contentPanel.add(new JLabel("Places disponibles:"), c);

            c.gridx = 1;
            c.gridy = 5;
            JTextField placesField = new JTextField(String.valueOf(trajet.getNbPlacesDisponibles()), 20);
            contentPanel.add(placesField, c);

            dialog.add(contentPanel, BorderLayout.CENTER);

            // Boutons
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            JButton cancelButton = ComponentFactory.createButton("Annuler", Color.LIGHT_GRAY, Color.BLACK);
            cancelButton.addActionListener(e -> dialog.dispose());

            JButton saveButton = ComponentFactory.createButton("Enregistrer", ColorScheme.PRIMARY, Color.WHITE);
            saveButton.addActionListener(e -> {
                // Vérifications
                String depart = departField.getText();
                String arrivee = arriveeField.getText();
                String date = dateField.getText();
                String prixStr = prixField.getText();
                String placesStr = placesField.getText();

                if (depart.isEmpty() || arrivee.isEmpty() || date.isEmpty() || prixStr.isEmpty() || placesStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Veuillez remplir tous les champs.",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Vérifier le format de la date
                if (!InputValidator.isValidDateTime(date)) {
                    JOptionPane.showMessageDialog(dialog,
                            "Format de date et heure invalide. Utilisez le format dd/MM/yyyy HH:mm",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Vérifier que la date est dans le futur
                LocalDateTime dateDepart = InputValidator.parseDateTime(date);
                if (dateDepart.isBefore(LocalDateTime.now())) {
                    JOptionPane.showMessageDialog(dialog,
                            "La date de départ doit être dans le futur.",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Vérifier le prix
                if (!InputValidator.isPositiveDouble(prixStr)) {
                    JOptionPane.showMessageDialog(dialog,
                            "Le prix doit être un nombre positif.",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Vérifier le nombre de places
                if (!InputValidator.isPositiveInteger(placesStr)) {
                    JOptionPane.showMessageDialog(dialog,
                            "Le nombre de places doit être un entier positif.",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Mettre à jour le trajet
                trajet.setLieuDepart(depart);
                trajet.setLieuArrivee(arrivee);
                trajet.setDateDepart(dateDepart);
                trajet.setPrix(Double.parseDouble(prixStr));
                trajet.setNbPlacesDisponibles(Integer.parseInt(placesStr));

                try {
                    boolean success = ServiceFactory.getConducteurService().modifierTrajet(trajet);
                    if (success) {
                        JOptionPane.showMessageDialog(dialog,
                                "Trajet modifié avec succès !",
                                "Succès", JOptionPane.INFORMATION_MESSAGE);

                        // Fermer la boîte de dialogue
                        dialog.dispose();

                        // Actualiser la liste des trajets
                        refreshMyRidesPanel();
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                                "Erreur lors de la modification du trajet.",
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelTrajet(Long trajetId) {
        int choice = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir annuler ce trajet ?",
                "Annulation de trajet", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                boolean success = ServiceFactory.getConducteurService().annulerTrajet(trajetId);
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Trajet annulé avec succès !",
                            "Succès", JOptionPane.INFORMATION_MESSAGE);

                    // Actualiser la liste des trajets
                    refreshMyRidesPanel();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Erreur lors de l'annulation du trajet.",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void reactivateTrajet(Long trajetId) {
        int choice = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir réactiver ce trajet ?",
                "Réactivation de trajet", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                boolean success = ServiceFactory.getConducteurService().reactiverTrajet(trajetId);
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Trajet réactivé avec succès !",
                            "Succès", JOptionPane.INFORMATION_MESSAGE);

                    // Actualiser la liste des trajets
                    refreshMyRidesPanel();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Erreur lors de la réactivation du trajet.",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteTrajet(Long trajetId) {
        int choice = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer définitivement ce trajet ? Cette action est irréversible.",
                "Suppression de trajet", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                boolean success = ServiceFactory.getConducteurService().supprimerTrajet(trajetId);
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Trajet supprimé avec succès !",
                            "Succès", JOptionPane.INFORMATION_MESSAGE);

                    // Actualiser la liste des trajets
                    refreshMyRidesPanel();
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

    private void acceptReservation(Long reservationId) {
        try {
            boolean success = ServiceFactory.getReservationService().confirmerReservation(reservationId);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Réservation acceptée avec succès !",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);

                // Actualiser la liste des réservations
                refreshReservationsPanel();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'acceptation de la réservation.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rejectReservation(Long reservationId) {
        try {
            boolean success = ServiceFactory.getReservationService().annulerReservation(reservationId);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Réservation refusée avec succès !",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);

                // Actualiser la liste des réservations
                refreshReservationsPanel();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors du refus de la réservation.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur: " + e.getMessage(),
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