package covoiturage.ui.gui.components;

import covoiturage.ui.gui.utils.ColorScheme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class ModernTable extends JTable {

    public ModernTable(Object[][] data, String[] columnNames) {
        super(data, columnNames);

        // Configurer l'apparence générale
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setRowHeight(40);
        setFillsViewportHeight(true);
        setSelectionBackground(new Color(41, 128, 185, 40));
        setSelectionForeground(ColorScheme.TEXT);
        setBackground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Configuration de l'en-tête
        JTableHeader header = getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(ColorScheme.PRIMARY);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Alignement et rendu des cellules
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (row % 2 == 0 && !isSelected) {
                    c.setBackground(new Color(245, 247, 250));
                } else if (!isSelected) {
                    c.setBackground(Color.WHITE);
                }

                // Ajouter une bordure pour créer une ligne entre les rangées
                ((JComponent) c).setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.BORDER));

                return c;
            }
        };

        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        // Appliquer le rendu à toutes les colonnes
        for (int i = 0; i < getColumnCount(); i++) {
            getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
}