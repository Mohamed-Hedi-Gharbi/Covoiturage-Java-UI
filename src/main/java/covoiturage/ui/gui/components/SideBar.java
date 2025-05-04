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

    public SideBar() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(ColorScheme.SECONDARY);
        setPreferredSize(new Dimension(220, getPreferredSize().height));
        setBorder(new EmptyBorder(20, 0, 20, 0));
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

        public MenuItemPanel(String text, String iconPath, ActionListener action) {
            setLayout(new BorderLayout());
            setBackground(ColorScheme.SECONDARY);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 15, 10, 15));

            // Icône
            JLabel iconLabel = new JLabel();
            iconLabel.setPreferredSize(new Dimension(24, 24));

            ImageIcon icon = ImageUtils.loadIcon(iconPath, 24, 24);
            if (icon != null) {
                iconLabel.setIcon(icon);
            }

            // Texte
            JLabel textLabel = new JLabel(text);
            textLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
            textLabel.setBorder(new EmptyBorder(0, 10, 0, 0));

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
                        setBackground(ColorScheme.SECONDARY.darker());
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!selected) {
                        setBackground(ColorScheme.SECONDARY);
                    }
                }
            });
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            if (selected) {
                setBackground(ColorScheme.PRIMARY);
                for (Component c : getComponents()) {
                    if (c instanceof JLabel) {
                        ((JLabel) c).setForeground(Color.WHITE);
                    }
                }
            } else {
                setBackground(ColorScheme.SECONDARY);
                for (Component c : getComponents()) {
                    if (c instanceof JLabel) {
                        ((JLabel) c).setForeground(ColorScheme.TEXT);
                    }
                }
            }
        }
    }
}