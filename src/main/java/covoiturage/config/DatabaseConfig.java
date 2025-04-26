package covoiturage.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {
    private static final String CONFIG_FILE = "database.properties";
    private static final Properties properties = new Properties();
    private static String url;
    private static String username;
    private static String password;

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)){
            if (input == null){
                System.out.println("Impossible de trouver " + CONFIG_FILE);
                throw new RuntimeException("Fichier de configuration non trouvé");
            }

            properties.load(input);
            url         = properties.getProperty("db.url");
            username    = properties.getProperty("db.username");
            password    = properties.getProperty("db.password");

            // chargement du driver PostgreSQL
            Class.forName("org.postgresql.Driver");
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'initialisation de la configuration de la base de données", e);
        }
    }

    private DatabaseConfig(){
        // Constructeur privé pour éviter l'instanciation
    }

    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(url, username, password);
    }
}
