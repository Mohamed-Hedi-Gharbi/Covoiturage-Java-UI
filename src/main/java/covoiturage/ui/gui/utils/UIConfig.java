package covoiturage.ui.gui.utils;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.util.Enumeration;

public class UIConfig {

    public static void applyGlobalStyles() {
        try {
            // Définir la police par défaut pour tous les composants
            setUIFont(new FontUIResource("Segoe UI", Font.PLAIN, 14));

            // Personnaliser les composants Swing
            UIManager.put("Panel.background", Color.WHITE);
            UIManager.put("Button.background", ColorScheme.PRIMARY);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.font", new FontUIResource("Segoe UI", Font.BOLD, 14));
            UIManager.put("Button.focus", new Color(0, 0, 0, 0)); // Supprimer le focus disgracieux

            UIManager.put("TextField.background", Color.WHITE);
            UIManager.put("TextField.foreground", ColorScheme.TEXT);
            UIManager.put("TextField.caretForeground", ColorScheme.PRIMARY);
            UIManager.put("TextField.selectionBackground", new Color(41, 128, 185, 50));
            UIManager.put("TextField.selectionForeground", ColorScheme.TEXT);

            UIManager.put("ComboBox.background", Color.WHITE);
            UIManager.put("ComboBox.foreground", ColorScheme.TEXT);
            UIManager.put("ComboBox.selectionBackground", ColorScheme.PRIMARY);
            UIManager.put("ComboBox.selectionForeground", Color.WHITE);

            UIManager.put("ScrollBar.thumb", ColorScheme.SECONDARY);
            UIManager.put("ScrollBar.thumbDarkShadow", ColorScheme.SECONDARY);
            UIManager.put("ScrollBar.thumbHighlight", ColorScheme.SECONDARY);
            UIManager.put("ScrollBar.thumbShadow", ColorScheme.SECONDARY);
            UIManager.put("ScrollBar.track", Color.WHITE);
            UIManager.put("ScrollBar.trackHighlight", Color.WHITE);

            UIManager.put("TabbedPane.selected", ColorScheme.PRIMARY);
            UIManager.put("TabbedPane.contentAreaColor", Color.WHITE);
            UIManager.put("TabbedPane.focus", ColorScheme.PRIMARY);
            UIManager.put("TabbedPane.highlight", Color.WHITE);
            UIManager.put("TabbedPane.light", Color.WHITE);
            UIManager.put("TabbedPane.selectHighlight", ColorScheme.PRIMARY);
            UIManager.put("TabbedPane.tabAreaBackground", Color.WHITE);
            UIManager.put("TabbedPane.unselectedBackground", Color.WHITE);

            // Définir le look and feel moderne Nimbus
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode pour définir la police par défaut pour tous les composants
    private static void setUIFont(FontUIResource f) {
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }

    // Méthode pour ajouter des effets d'ombre à un composant
    public static void addShadow(JComponent component, int shadowSize) {
        component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(shadowSize, shadowSize, shadowSize, shadowSize),
                component.getBorder()
        ));

        component.setOpaque(false);
        component.setBackground(new Color(0, 0, 0, 0));
    }

    // Méthode pour appliquer un effet de survol à un composant
    public static void applyHoverEffect(JComponent component, Color normalColor, Color hoverColor) {
        component.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                component.setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                component.setBackground(normalColor);
            }
        });
    }
}