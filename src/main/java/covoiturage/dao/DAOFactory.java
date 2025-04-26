package covoiturage.dao;

/**
 * Factory pour instancier les DAOs.
 * Utilise le pattern Singleton pour assurer qu'une seule instance de chaque DAO existe.
 */

public class DAOFactory {
    private static final AdministrateurDAO administrateurDAO    = new AdministrateurDAO();
    private static final UtilisateurDAO utilisateurDAO          = new UtilisateurDAO();
    private static final ConducteurDAO conducteurDAO            = new ConducteurDAO();
    private static final TrajetDAO trajetDAO                    = new TrajetDAO();
    private static final ReservationDAO reservationDAO          = new ReservationDAO();
    private static final AvisDAO avisDAO                        = new AvisDAO();
    private static final PaiementDAO paiementDAO                = new PaiementDAO();

    private DAOFactory() {
        // Constructeur privé pour empêcher l'instanciation
    }

    public static AdministrateurDAO getAdministrateurDAO() {
        return administrateurDAO;
    }

    public static UtilisateurDAO getUtilisateurDAO() {
        return utilisateurDAO;
    }

    public static ConducteurDAO getConducteurDAO() {
        return conducteurDAO;
    }

    public static TrajetDAO getTrajetDAO() {
        return trajetDAO;
    }

    public static ReservationDAO getReservationDAO() {
        return reservationDAO;
    }

    public static AvisDAO getAvisDAO() {
        return avisDAO;
    }

    public static PaiementDAO getPaiementDAO() {
        return paiementDAO;
    }
}
