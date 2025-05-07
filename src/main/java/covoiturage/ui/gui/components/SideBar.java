package covoiturage.ui.gui.components;

import covoiturage.ui.gui.utils.ColorScheme;
import covoiturage.ui.gui.utils.ImageUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class SideBar extends JPanel {
    private List<MenuItemPanel> menuItems = new ArrayList<>();
    private MenuItemPanel selectedItem = null;
    private Color hoverColor = new Color(41, 128, 185, 40);

    public SideBar() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(250, getPreferredSize().height));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, ColorScheme.BORDER),
                new EmptyBorder(25, 15, 25, 15)
        ));

        // Logo en haut de la barre latérale
        JLabel logoLabel = new JLabel("CovoiturApp", JLabel.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoLabel.setForeground(ColorScheme.PRIMARY);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setPreferredSize(new Dimension(200, 50));
        logoLabel.setMaximumSize(new Dimension(200, 50));
        add(logoLabel);

        // Séparateur
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(200, 2));
        separator.setForeground(ColorScheme.BORDER);
        add(separator);
        add(Box.createRigidArea(new Dimension(0, 20)));
    }

    public void addMenuItem(String text, String iconPath, ActionListener action) {
        MenuItemPanel menuItem = new MenuItemPanel(text, iconPath, action);
        menuItems.add(menuItem);
        add(menuItem);

        // Sélectionner le premier élément par défaut
        if (menuItems.size() == 1) {
            selectMenuItem(menuItem);
        }

        // Ajouter un espace entre les éléments
        add(Box.createRigidArea(new Dimension(0, 5)));
    }

    private void selectMenuItem(MenuItemPanel item) {
        // Désélectionner l'élément actuel
        if (selectedItem != null) {
            selectedItem.setSelected(false);
        }

        // Sélectionner le nouvel élément
        selectedItem = item;
        selectedItem.setSelected(true);
    }

    // Panel pour un élément de menu
    private class MenuItemPanel extends JPanel {
        private boolean selected = false;
        private boolean hover = false;
        private JLabel iconLabel;
        private JLabel textLabel;

        public MenuItemPanel(String text, String iconPath, ActionListener action) {
            setLayout(new BorderLayout(10, 0));
            setBackground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(12, 15, 12, 15));
            setMaximumSize(new Dimension(220, 50));

            // Icône
            iconLabel = new JLabel();
            iconLabel.setPreferredSize(new Dimension(24, 24));

            ImageIcon icon = ImageUtils.loadIcon(iconPath, 24, 24);
            if (icon != null) {
                iconLabel.setIcon(icon);
            }

            // Texte
            textLabel = new JLabel(text);
            textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            textLabel.setForeground(ColorScheme.TEXT);

            add(iconLabel, BorderLayout.WEST);
            add(textLabel, BorderLayout.CENTER);

            // Gérer les événements de souris
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectMenuItem(MenuItemPanel.this);
                    action.actionPerformed(null);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!selected) {
                        hover = true;
                        repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!selected) {
                        hover = false;
                        repaint();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Dessiner le fond
            if (selected) {
                g2d.setColor(ColorScheme.PRIMARY);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            } else if (hover) {
                g2d.setColor(hoverColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            } else {
                g2d.setColor(getBackground());
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }

            g2d.dispose();
            super.paintComponent(g);
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            if (selected) {
                textLabel.setForeground(Color.WHITE);
                textLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
                // Changer l'icône pour une version blanche si nécessaire
            } else {
                textLabel.setForeground(ColorScheme.TEXT);
                textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                // Restaurer l'icône originale
            }
            repaint();
        }
    }
}