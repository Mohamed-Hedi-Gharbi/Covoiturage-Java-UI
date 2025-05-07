package covoiturage.ui.gui.panels;

import covoiturage.model.Conducteur;
import covoiturage.model.Reservation;
import covoiturage.model.Trajet;
import covoiturage.model.Utilisateur;
import covoiturage.model.enums.StatutReservation;
import covoiturage.service.ServiceFactory;
import covoiturage.ui.gui.MainFrame;
import covoiturage.ui.gui.SessionManager;
import covoiturage.ui.gui.components.NavigationBar;
import covoiturage.ui.gui.components.SideBar;
import covoiturage.ui.gui.utils.ColorScheme;
import covoiturage.ui.gui.utils.ComponentFactory;
import covoiturage.ui.gui.utils.ImageUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


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
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(ColorScheme.BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titre avec animation de bienvenue
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(ColorScheme.BACKGROUND);

        JLabel titleLabel = ComponentFactory.createTitleLabel("Tableau de bord");

        // Heure du jour
        String greeting;
        int hour = LocalTime.now().getHour();
        if (hour < 12) {
            greeting = "Bonjour";
        } else if (hour < 18) {
            greeting = "Bon après-midi";
        } else {
            greeting = "Bonsoir";
        }

        JLabel welcomeLabel = ComponentFactory.createSubtitleLabel(greeting + ", " + utilisateur.getPrenom() + " !");
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(welcomeLabel, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Contenu principal avec disposition flexible
        JPanel mainContent = new JPanel(new BorderLayout(20, 20));
        mainContent.setOpaque(false);

        // Panel de statistiques amélioré
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);

        // Récupérer les statistiques réelles
        List<Reservation> userReservations = ServiceFactory.getReservationService().getReservationsByUtilisateur(utilisateur.getId());
        int totalReservations = userReservations.size();
        int activeReservations = (int) userReservations.stream()
                .filter(r -> !r.isAnnule() && r.getStatut() != StatutReservation.ANNULEE)
                .count();

        // Carte 1: Réservations totales - Version améliorée
        JPanel card1 = createAnimatedStatCard("Réservations totales",
                String.valueOf(totalReservations),
                ColorScheme.PRIMARY,
                "/images/stats_icon.png");
        statsPanel.add(card1);

        // Carte 2: Réservations actives
        JPanel card2 = createAnimatedStatCard("Réservations actives",
                String.valueOf(activeReservations),
                ColorScheme.ACCENT,
                "/images/calendar_icon.png");
        statsPanel.add(card2);

        // Carte 3: Actions rapides
        JPanel card3 = new JPanel();
        card3.setLayout(new BoxLayout(card3, BoxLayout.Y_AXIS));
        card3.setBackground(Color.WHITE);
        card3.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER, 1, true));

        JPanel titleContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        titleContainer.setOpaque(false);
        titleContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel actionsIcon = new JLabel(ImageUtils.loadIcon("/images/actions_icon.png", 24, 24));
        JLabel actionsLabel = new JLabel("Actions rapides");
        actionsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        actionsLabel.setForeground(ColorScheme.PRIMARY);

        titleContainer.add(actionsIcon);
        titleContainer.add(actionsLabel);

        card3.add(titleContainer);
        card3.add(Box.createRigidArea(new Dimension(0, 15)));

        // Boutons améliorés
        JButton searchButton = ComponentFactory.createButton("Rechercher un trajet", ColorScheme.PRIMARY, Color.WHITE);
        searchButton.setIcon(ImageUtils.loadIcon("/images/search_icon.png", 20, 20));
        searchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchButton.setMaximumSize(new Dimension(250, 40));
        searchButton.addActionListener(e -> cardLayout.show(contentPanel, SEARCH_RIDES));

        JButton profileButton = ComponentFactory.createButton("Modifier mon profil", ColorScheme.SECONDARY, ColorScheme.TEXT);
        profileButton.setIcon(ImageUtils.loadIcon("/images/profile_edit_icon.png", 20, 20));
        profileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        profileButton.setMaximumSize(new Dimension(250, 40));
        profileButton.addActionListener(e -> cardLayout.show(contentPanel, PROFILE));

        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));
        buttonContainer.setOpaque(false);
        buttonContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonContainer.add(searchButton);
        buttonContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonContainer.add(profileButton);

        card3.add(buttonContainer);
        card3.add(Box.createVerticalGlue());

        // Animation sur hover pour card3
        card3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card3.setBorder(BorderFactory.createLineBorder(ColorScheme.PRIMARY, 1, true));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card3.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER, 1, true));
            }
        });

        statsPanel.add(card3);

        mainContent.add(statsPanel, BorderLayout.NORTH);

        // Activité récente - Nouveau panel avec données réelles
        JPanel recentActivityPanel = new JPanel(new BorderLayout(0, 10));
        recentActivityPanel.setBackground(Color.WHITE);
        recentActivityPanel.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER, 1, true));

        JLabel activityTitleLabel = new JLabel("Activité récente");
        activityTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        activityTitleLabel.setForeground(ColorScheme.PRIMARY);
        activityTitleLabel.setBorder(new EmptyBorder(15, 15, 5, 15));

        recentActivityPanel.add(activityTitleLabel, BorderLayout.NORTH);

        // Liste d'activités récentes
        JPanel activityListPanel = new JPanel();
        activityListPanel.setLayout(new BoxLayout(activityListPanel, BoxLayout.Y_AXIS));
        activityListPanel.setBackground(Color.WHITE);

        // Récupérer les données réelles d'activités
        List<String> activityItems = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Limiter à 3 activités récentes (pour la vue du dashboard)
        List<Reservation> recentReservations = userReservations.stream()
                .sorted((r1, r2) -> r2.getDateReservation().compareTo(r1.getDateReservation()))
                .limit(3)
                .collect(Collectors.toList());

        for (Reservation reservation : recentReservations) {
            String activity;
            if (reservation.isAnnule() || reservation.getStatut() == StatutReservation.ANNULEE) {
                activity = "Vous avez annulé une réservation pour " +
                        reservation.getTrajet().getLieuDepart() + " → " +
                        reservation.getTrajet().getLieuArrivee() + " du " +
                        reservation.getTrajet().getDateDepart().format(formatter);
            } else if (reservation.getStatut() == StatutReservation.CONFIRMEE) {
                activity = "Votre réservation pour " +
                        reservation.getTrajet().getLieuDepart() + " → " +
                        reservation.getTrajet().getLieuArrivee() + " du " +
                        reservation.getTrajet().getDateDepart().format(formatter) +
                        " a été confirmée";
            } else {
                activity = "Vous avez réservé un trajet " +
                        reservation.getTrajet().getLieuDepart() + " → " +
                        reservation.getTrajet().getLieuArrivee() + " pour le " +
                        reservation.getTrajet().getDateDepart().format(formatter);
            }
            activityItems.add(activity);
        }

        // Si aucune activité n'a été trouvée
        if (activityItems.isEmpty()) {
            activityItems.add("Aucune activité récente");
        }

        // Créer les éléments d'interface pour les activités
        for (String item : activityItems) {
            JPanel activityItem = createActivityItem(item);
            activityListPanel.add(activityItem);
            activityListPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
        }

        JScrollPane scrollPane = new JScrollPane(activityListPanel);
        scrollPane.setBorder(null);
        recentActivityPanel.add(scrollPane, BorderLayout.CENTER);

        // Bouton "Voir tout"
        JButton viewAllButton = new JButton("Voir toute l'activité");
        viewAllButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        viewAllButton.setForeground(ColorScheme.PRIMARY);
        viewAllButton.setBackground(Color.WHITE);
        viewAllButton.setBorderPainted(false);
        viewAllButton.setFocusPainted(false);
        viewAllButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewAllButton.setBorder(new EmptyBorder(10, 15, 10, 15));

        // Ajouter l'action pour voir toutes les activités
        viewAllButton.addActionListener(e -> showAllActivities());

        recentActivityPanel.add(viewAllButton, BorderLayout.SOUTH);

        mainContent.add(recentActivityPanel, BorderLayout.CENTER);

        panel.add(mainContent, BorderLayout.CENTER);

        return panel;
    }


    // Méthode pour créer une carte de statistique animée
    private JPanel createAnimatedStatCard(String title, String value, Color color, String iconPath) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER, 1, true));

        // Entête avec icône
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        headerPanel.setOpaque(false);

        JLabel iconLabel = new JLabel(ImageUtils.loadIcon(iconPath, 24, 24));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(color);

        headerPanel.add(iconLabel);
        headerPanel.add(titleLabel);

        card.add(headerPanel, BorderLayout.NORTH);

        // Valeur au centre avec animation
        JLabel valueLabel = new JLabel("0");
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

        card.add(valueLabel, BorderLayout.CENTER);

        // Animation du compteur
        int targetValue = Integer.parseInt(value);
        Timer timer = new Timer(50, new ActionListener() {
            int currentValue = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentValue < targetValue) {
                    currentValue++;
                    valueLabel.setText(String.valueOf(currentValue));
                } else {
                    ((Timer)e.getSource()).stop();
                }
            }
        });
        timer.start();

        // Animation sur hover
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(color, 1, true));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER, 1, true));
            }
        });

        return card;
    }

    // Méthode pour créer un élément d'activité avec la date réelle
    private JPanel createActivityItem(String text) {
        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setOpaque(false);
        item.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel iconLabel = new JLabel(ImageUtils.loadIcon("/images/activity_icon.png", 16, 16));
        iconLabel.setBorder(new EmptyBorder(0, 0, 0, 5));

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel timeLabel = new JLabel("Il y a 2 heures");  // Cette valeur sera remplacée dans la méthode showAllActivities
        timeLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        timeLabel.setForeground(ColorScheme.TEXT_LIGHT);

        JPanel textPanel = new JPanel(new BorderLayout(5, 5));
        textPanel.setOpaque(false);
        textPanel.add(textLabel, BorderLayout.NORTH);
        textPanel.add(timeLabel, BorderLayout.SOUTH);

        item.add(iconLabel, BorderLayout.WEST);
        item.add(textPanel, BorderLayout.CENTER);

        // Animation au survol
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                item.setBackground(new Color(245, 247, 250));
                item.setOpaque(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                item.setOpaque(false);
            }
        });

        return item;
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

    // Nouvelle méthode pour afficher toutes les activités
    private void showAllActivities() {
        // Créer une boîte de dialogue pour afficher toutes les activités
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Toutes les activités", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        // Récupérer toutes les réservations
        List<Reservation> allReservations = ServiceFactory.getReservationService().getReservationsByUtilisateur(utilisateur.getId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Panneau pour la liste d'activités
        JPanel activityListPanel = new JPanel();
        activityListPanel.setLayout(new BoxLayout(activityListPanel, BoxLayout.Y_AXIS));
        activityListPanel.setBackground(Color.WHITE);

        // Trier les réservations par date (les plus récentes d'abord)
        allReservations.sort((r1, r2) -> r2.getDateReservation().compareTo(r1.getDateReservation()));

        // Si aucune activité
        if (allReservations.isEmpty()) {
            JLabel noActivityLabel = new JLabel("Aucune activité à afficher");
            noActivityLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            noActivityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            noActivityLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
            activityListPanel.add(noActivityLabel);
        } else {
            // Créer une entrée pour chaque réservation
            for (Reservation reservation : allReservations) {
                String activity;
                String timeAgo = formatTimeAgo(reservation.getDateReservation());

                if (reservation.isAnnule() || reservation.getStatut() == StatutReservation.ANNULEE) {
                    activity = "Vous avez annulé une réservation pour " +
                            reservation.getTrajet().getLieuDepart() + " → " +
                            reservation.getTrajet().getLieuArrivee() + " du " +
                            reservation.getTrajet().getDateDepart().format(formatter);
                } else if (reservation.getStatut() == StatutReservation.CONFIRMEE) {
                    activity = "Votre réservation pour " +
                            reservation.getTrajet().getLieuDepart() + " → " +
                            reservation.getTrajet().getLieuArrivee() + " du " +
                            reservation.getTrajet().getDateDepart().format(formatter) +
                            " a été confirmée";
                } else {
                    activity = "Vous avez réservé un trajet " +
                            reservation.getTrajet().getLieuDepart() + " → " +
                            reservation.getTrajet().getLieuArrivee() + " pour le " +
                            reservation.getTrajet().getDateDepart().format(formatter);
                }

                // Créer un panel personnalisé pour chaque activité avec la date
                JPanel activityPanel = new JPanel(new BorderLayout(10, 0));
                activityPanel.setOpaque(false);
                activityPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

                JLabel iconLabel = new JLabel(ImageUtils.loadIcon("/images/activity_icon.png", 16, 16));
                iconLabel.setBorder(new EmptyBorder(0, 0, 0, 5));

                JLabel textLabel = new JLabel(activity);
                textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

                JLabel timeLabel = new JLabel(timeAgo);
                timeLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                timeLabel.setForeground(ColorScheme.TEXT_LIGHT);

                JPanel textPanel = new JPanel(new BorderLayout(5, 5));
                textPanel.setOpaque(false);
                textPanel.add(textLabel, BorderLayout.NORTH);
                textPanel.add(timeLabel, BorderLayout.SOUTH);

                activityPanel.add(iconLabel, BorderLayout.WEST);
                activityPanel.add(textPanel, BorderLayout.CENTER);

                // Animation au survol
                activityPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        activityPanel.setBackground(new Color(245, 247, 250));
                        activityPanel.setOpaque(true);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        activityPanel.setOpaque(false);
                    }
                });

                activityListPanel.add(activityPanel);
                activityListPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
            }
        }

        JScrollPane scrollPane = new JScrollPane(activityListPanel);
        scrollPane.setBorder(null);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Bouton pour fermer la boîte de dialogue
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = ComponentFactory.createButton("Fermer", ColorScheme.SECONDARY, Color.BLACK);
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // Méthode pour formater le temps écoulé
    private String formatTimeAgo(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(dateTime, now).toMinutes();

        if (minutes < 60) {
            return "Il y a " + minutes + " minute" + (minutes > 1 ? "s" : "");
        } else if (minutes < 24 * 60) {
            long hours = minutes / 60;
            return "Il y a " + hours + " heure" + (hours > 1 ? "s" : "");
        } else {
            long days = minutes / (24 * 60);
            return "Il y a " + days + " jour" + (days > 1 ? "s" : "");
        }
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
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable resultTable = new JTable(model);
        resultTable.setFillsViewportHeight(true);
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        panel.add(scrollPane, BorderLayout.CENTER);

        // Bouton de réservation
        JButton reserveButton = ComponentFactory.createButton("Réserver", ColorScheme.PRIMARY, Color.WHITE);
        reserveButton.setEnabled(false);

        // Activer le bouton de réservation lorsqu'une ligne est sélectionnée
        resultTable.getSelectionModel().addListSelectionListener(e -> {
            reserveButton.setEnabled(resultTable.getSelectedRow() != -1);
        });

        // Action du bouton de réservation
        reserveButton.addActionListener(e -> {
            int selectedRow = resultTable.getSelectedRow();
            if (selectedRow != -1) {
                try {
                    // Convertir l'ID en long et s'assurer qu'il est valide
                    Object idObj = model.getValueAt(selectedRow, 0);
                    if (idObj != null) {
                        Long trajetId = Long.parseLong(idObj.toString());
                        System.out.println("Réservation du trajet ID: " + trajetId); // Log pour déboguer
                        showReservationDialog(trajetId);
                    } else {
                        JOptionPane.showMessageDialog(panel,
                                "ID de trajet invalide",
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panel,
                            "Format d'ID incorrect: " + ex.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel,
                            "Erreur lors de la réservation: " + ex.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        // Action du bouton de recherche
        searchButton.addActionListener(e -> {
            // Rechercher les trajets avec les services existants
            String depart = departField.getText().trim(); // Ajout de trim() pour éliminer les espaces
            String arrivee = arriveeField.getText().trim(); // Ajout de trim() pour éliminer les espaces

            if (depart.isEmpty() || arrivee.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "Veuillez remplir tous les champs de recherche.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Ajouter une trace pour le débogage
            System.out.println("Recherche de trajets : " + depart + " → " + arrivee);

            // Récupérer tous les trajets disponibles
            List<Trajet> allTrajets = ServiceFactory.getTrajetService().getAllTrajets();

            // Filtrer manuellement les trajets avec une recherche insensible à la casse
            List<Trajet> trajetsDisponibles = allTrajets.stream()
                    .filter(trajet -> trajet.getLieuDepart().trim().toLowerCase().equalsIgnoreCase(depart.toLowerCase()))
                    .filter(trajet -> trajet.getLieuArrivee().trim().toLowerCase().equalsIgnoreCase(arrivee.toLowerCase()))
                    .filter(trajet -> !trajet.isEstAnnule())
                    .filter(trajet -> trajet.getDateDepart().isAfter(LocalDateTime.now()))
                    .filter(trajet -> trajet.calculerPlacesRestantes() > 0)
                    .collect(Collectors.toList());

            // Afficher le nombre de trajets trouvés pour le débogage
            System.out.println("Nombre de trajets trouvés : " + trajetsDisponibles.size());

            // Mettre à jour la table avec les résultats
            model.setRowCount(0); // Effacer les données existantes

            for (Trajet trajet : trajetsDisponibles) {
                String conducteurInfo = trajet.getConducteur() != null ?
                        trajet.getConducteur().getPrenom() + " " + trajet.getConducteur().getNom() : "N/A";

                // Ajouter une trace pour chaque trajet pour le débogage
                System.out.println("Ajout du trajet ID=" + trajet.getId() + ", " +
                        trajet.getLieuDepart() + " → " + trajet.getLieuArrivee() + ", " +
                        trajet.getDateDepart());

                model.addRow(new Object[]{
                        trajet.getId(),
                        trajet.getLieuDepart(),
                        trajet.getLieuArrivee(),
                        trajet.getDateDepart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                        String.format("%.2f", trajet.getPrix()),
                        trajet.calculerPlacesRestantes() + "/" + trajet.getNbPlacesDisponibles() + " places disponibles",
                        conducteurInfo
                });
            }

            // Si aucun résultat, afficher un message
            if (trajetsDisponibles.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "Aucun trajet ne correspond à votre recherche.",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Informer l'utilisateur du nombre de trajets trouvés
                JOptionPane.showMessageDialog(panel,
                        trajetsDisponibles.size() + " trajet(s) trouvé(s).",
                        "Résultats de recherche", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Ajouter le bouton au panel d'actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setBackground(ColorScheme.BACKGROUND);
        actionsPanel.add(reserveButton);
        panel.add(actionsPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Méthode complémentaire pour la réservation d'un trajet
    private void showReservationDialog(Long trajetId) {
        try {
            // Récupérer le trajet
            Optional<Trajet> optTrajet = ServiceFactory.getTrajetService().getTrajetById(trajetId);
            if (optTrajet.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Trajet non trouvé.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Trajet trajet = optTrajet.get();

            // Vérifier si le trajet est disponible
            if (trajet.isEstAnnule() || trajet.getDateDepart().isBefore(LocalDateTime.now())) {
                JOptionPane.showMessageDialog(this,
                        "Ce trajet n'est plus disponible.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Vérifier s'il reste des places
            if (trajet.calculerPlacesRestantes() <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Impossible de réserver ce trajet : toutes les places sont déjà prises (" +
                                "0/" + trajet.getNbPlacesDisponibles() + " places disponibles).",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Créer une boîte de dialogue pour la réservation
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Réserver un trajet", true);
            dialog.setLayout(new BorderLayout());
            dialog.setSize(400, 350);
            dialog.setLocationRelativeTo(this);

            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
            formPanel.setBackground(Color.WHITE);

            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(5, 5, 5, 5);

            // Détails du trajet
            c.gridx = 0;
            c.gridy = 0;
            c.gridwidth = 2;
            JLabel titleLabel = ComponentFactory.createTitleLabel("Réservation de trajet");
            formPanel.add(titleLabel, c);

            c.gridx = 0;
            c.gridy = 1;
            c.gridwidth = 1;
            formPanel.add(new JLabel("Trajet:"), c);

            c.gridx = 1;
            c.gridy = 1;
            JLabel trajetLabel = new JLabel(trajet.getLieuDepart() + " → " + trajet.getLieuArrivee());
            formPanel.add(trajetLabel, c);

            c.gridx = 0;
            c.gridy = 2;
            formPanel.add(new JLabel("Date:"), c);

            c.gridx = 1;
            c.gridy = 2;
            JLabel dateLabel = new JLabel(trajet.getDateDepart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            formPanel.add(dateLabel, c);

            c.gridx = 0;
            c.gridy = 3;
            formPanel.add(new JLabel("Prix par place:"), c);

            c.gridx = 1;
            c.gridy = 3;
            JLabel prixLabel = new JLabel(String.format("%.2f Dinars", trajet.getPrix()));
            formPanel.add(prixLabel, c);

            c.gridx = 0;
            c.gridy = 4;
            formPanel.add(new JLabel("Places disponibles:"), c);

            c.gridx = 1;
            c.gridy = 4;
            int placesRestantes = trajet.calculerPlacesRestantes();
            JLabel placesLabel = new JLabel(String.valueOf(placesRestantes));
            formPanel.add(placesLabel, c);

            c.gridx = 0;
            c.gridy = 5;
            formPanel.add(new JLabel("Nombre de places:"), c);

            c.gridx = 1;
            c.gridy = 5;
            JSpinner placesSpinner = new JSpinner(new SpinnerNumberModel(1, 1, placesRestantes, 1));
            formPanel.add(placesSpinner, c);

            c.gridx = 0;
            c.gridy = 6;
            formPanel.add(new JLabel("Total:"), c);

            c.gridx = 1;
            c.gridy = 6;
            JLabel totalLabel = new JLabel(String.format("%.2f Dinars", trajet.getPrix()));
            formPanel.add(totalLabel, c);

            // Actualiser le total en fonction du nombre de places
            placesSpinner.addChangeListener(e -> {
                int nbPlaces = (int) placesSpinner.getValue();
                totalLabel.setText(String.format("%.2f Dinars", trajet.getPrix() * nbPlaces));
            });

            dialog.add(formPanel, BorderLayout.CENTER);

            // Boutons
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonsPanel.setBackground(Color.WHITE);

            JButton cancelButton = ComponentFactory.createButton("Annuler", Color.LIGHT_GRAY, Color.BLACK);
            cancelButton.addActionListener(e -> dialog.dispose());

            JButton confirmButton = ComponentFactory.createButton("Confirmer", ColorScheme.PRIMARY, Color.WHITE);
            confirmButton.addActionListener(e -> {
                int nbPlaces = (int) placesSpinner.getValue();

                if (nbPlaces > trajet.calculerPlacesRestantes()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Erreur : Vous avez demandé " + nbPlaces + " places, mais seulement " +
                                    trajet.calculerPlacesRestantes() + "/" + trajet.getNbPlacesDisponibles() +
                                    " places sont disponibles pour ce trajet.",
                            "Places insuffisantes", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Créer la réservation
                Reservation reservation = new Reservation();
                reservation.setTrajet(trajet);
                reservation.setUtilisateur(utilisateur);
                reservation.setNbPlaces(nbPlaces);
                reservation.setDateReservation(LocalDateTime.now());

                try {
                    // Utiliser le service approprié pour créer la réservation
                    Long reservationId = ServiceFactory.getReservationService().creerReservation(reservation);

                    if (reservationId != null) {
                        JOptionPane.showMessageDialog(dialog,
                                "Réservation effectuée avec succès ! En attente de confirmation du conducteur.",
                                "Succès", JOptionPane.INFORMATION_MESSAGE);

                        dialog.dispose();

                        // Rafraîchir le tableau des réservations
                        cardLayout.show(contentPanel, MY_RESERVATIONS);
                        // Re-créer le panel des réservations pour afficher la nouvelle réservation
                        contentPanel.remove(contentPanel.getComponent(2)); // Supprimer l'ancien panel
                        contentPanel.add(createMyReservationsPanel(), MY_RESERVATIONS, 2); // Ajouter le nouveau panel
                        cardLayout.show(contentPanel, MY_RESERVATIONS);
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                                "Erreur lors de la création de la réservation.",
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Erreur: " + ex.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace(); // Afficher la trace de l'erreur dans la console
                }
            });

            buttonsPanel.add(cancelButton);
            buttonsPanel.add(confirmButton);

            dialog.add(buttonsPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Afficher la trace de l'erreur dans la console
        }
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
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable reservationsTable = new JTable(model);
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

        // Charger les réservations de l'utilisateur
        List<Reservation> reservations = ServiceFactory.getReservationService().getReservationsByUtilisateur(utilisateur.getId());

        // Remplir le modèle de tableau avec les réservations
        for (Reservation reservation : reservations) {
            model.addRow(new Object[]{
                    reservation.getId(),
                    reservation.getTrajet().getLieuDepart(),
                    reservation.getTrajet().getLieuArrivee(),
                    reservation.getTrajet().getDateDepart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    reservation.getNbPlaces(),
                    String.format("%.2f", reservation.getTrajet().getPrix() * reservation.getNbPlaces()),
                    reservation.getStatut().toString()
            });
        }

        // Ajouter un listener pour activer/désactiver les boutons selon l'état de la réservation
        reservationsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && reservationsTable.getSelectedRow() != -1) {
                int selectedRow = reservationsTable.getSelectedRow();
                String statut = (String) model.getValueAt(selectedRow, 6);

                cancelButton.setEnabled(!statut.equals("ANNULEE"));
                payButton.setEnabled(statut.equals("CONFIRMEE"));
            } else {
                cancelButton.setEnabled(false);
                payButton.setEnabled(false);
            }
        });

        // Actions pour les boutons
        cancelButton.addActionListener(e -> {
            int selectedRow = reservationsTable.getSelectedRow();
            if (selectedRow != -1) {
                Long reservationId = Long.parseLong(model.getValueAt(selectedRow, 0).toString());
                try {
                    boolean success = ServiceFactory.getReservationService().annulerReservation(reservationId);
                    if (success) {
                        JOptionPane.showMessageDialog(panel,
                                "Réservation annulée avec succès !",
                                "Succès", JOptionPane.INFORMATION_MESSAGE);

                        // Rafraîchir le tableau
                        model.setValueAt("ANNULEE", selectedRow, 6);
                        cancelButton.setEnabled(false);
                        payButton.setEnabled(false);
                    } else {
                        JOptionPane.showMessageDialog(panel,
                                "Échec de l'annulation de la réservation.",
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel,
                            "Erreur: " + ex.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        payButton.addActionListener(e -> {
            int selectedRow = reservationsTable.getSelectedRow();
            if (selectedRow != -1) {
                Long reservationId = Long.parseLong(model.getValueAt(selectedRow, 0).toString());
                String prixStr = model.getValueAt(selectedRow, 5).toString();
                double prix = Double.parseDouble(prixStr.replace(",", "."));

                try {
                    Long paiementId = ServiceFactory.getPaiementService().effectuerPaiement(reservationId, prix);
                    if (paiementId != null) {
                        JOptionPane.showMessageDialog(panel,
                                "Paiement effectué avec succès !",
                                "Succès", JOptionPane.INFORMATION_MESSAGE);

                        // Désactiver le bouton de paiement
                        payButton.setEnabled(false);
                    } else {
                        JOptionPane.showMessageDialog(panel,
                                "Échec du paiement.",
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel,
                            "Erreur: " + ex.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        actionsPanel.add(cancelButton);
        actionsPanel.add(payButton);

        panel.add(actionsPanel, BorderLayout.SOUTH);

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