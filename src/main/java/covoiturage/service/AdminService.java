package covoiturage.service;

import covoiturage.dao.AdministrateurDAO;
import covoiturage.dao.DAOFactory;
import covoiturage.model.Administrateur;

import java.util.List;
import java.util.Optional;

public class AdminService {
    private AdministrateurDAO administrateurDAO;

    public AdminService() {
        this.administrateurDAO = DAOFactory.getAdministrateurDAO();
    }

    public Optional<Administrateur> getAdminById(Long id) {
        return administrateurDAO.findById(id);
    }

    public Optional<Administrateur> getAdminByEmail(String email) {
        return administrateurDAO.findByEmail(email);
    }

    public List<Administrateur> getAllAdmins() {
        return administrateurDAO.findAll();
    }

    public Long creerAdmin(Administrateur admin) {
        if (administrateurDAO.findByEmail(admin.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Un administrateur avec cet email existe déjà");
        }

        return administrateurDAO.save(admin);
    }

    public boolean modifierAdmin(Administrateur admin) {
        Optional<Administrateur> existingAdmin = administrateurDAO.findByEmail(admin.getEmail());
        if (existingAdmin.isPresent() && !existingAdmin.get().getId().equals(admin.getId())) {
            throw new IllegalArgumentException("Un autre administrateur avec cet email existe déjà");
        }

        return administrateurDAO.update(admin);
    }

    public boolean supprimerAdmin(Long id) {
        return administrateurDAO.delete(id);
    }

    public Optional<Administrateur> authentifier(String email, String motDePasse) {
        Optional<Administrateur> optAdmin = administrateurDAO.findByEmail(email);
        if (optAdmin.isPresent()) {
            Administrateur admin = optAdmin.get();
            if (admin.getMotDePasse().equals(motDePasse)) {
                return Optional.of(admin);
            }
        }

        return Optional.empty();
    }


    // Méthodes de génération de rapports
    public void genererRapportUtilisateurs() {
        // Implémentation de la génération de rapports sur les utilisateurs
        System.out.println("Génération du rapport des utilisateurs");
    }

    public void genererRapportTrajets() {
        // Implémentation de la génération de rapports sur les trajets
        System.out.println("Génération du rapport des trajets");
    }

    public void genererRapportReservations() {
        // Implémentation de la génération de rapports sur les réservations
        System.out.println("Génération du rapport des réservations");
    }
    
}
