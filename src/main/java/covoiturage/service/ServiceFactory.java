package covoiturage.service;

public class ServiceFactory {
    private static final UtilisateurService utilisateurService = new UtilisateurService();
    private static final ConducteurService conducteurService = new ConducteurService();
    private static final AdminService adminService = new AdminService();
    private static final TrajetService trajetService = new TrajetService();
    private static final ReservationService reservationService = new ReservationService();
    private static final PaiementService paiementService = new PaiementService();
    private static final AvisService avisService = new AvisService();

    public static UtilisateurService getUtilisateurService() {
        return utilisateurService;
    }

    public static ConducteurService getConducteurService() {
        return conducteurService;
    }

    public static AdminService getAdminService() {
        return adminService;
    }

    public static TrajetService getTrajetService() {
        return trajetService;
    }

    public static ReservationService getReservationService() {
        return reservationService;
    }

    public static PaiementService getPaiementService() {
        return paiementService;
    }

    public static AvisService getAvisService() {
        return avisService;
    }
}
