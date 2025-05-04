package covoiturage.ui.gui.utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {

    public static Image loadImage(String path) {
        try {
            InputStream is = ImageUtils.class.getResourceAsStream(path);
            if (is == null) {
                System.err.println("Ressource non trouvée: " + path);
                return createPlaceholderImage(24, 24);
            }
            return ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
            return createPlaceholderImage(24, 24);
        }
    }

    public static ImageIcon loadIcon(String path, int width, int height) {
        Image img = loadImage(path);
        // Pas besoin de vérifier null car loadImage retourne toujours une image (placeholder si nécessaire)
        return new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    // Crée une image de remplacement de la taille spécifiée
    private static Image createPlaceholderImage(int width, int height) {
        BufferedImage placeholder = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = placeholder.createGraphics();
        g2d.setColor(ColorScheme.PRIMARY);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        return placeholder;
    }
}