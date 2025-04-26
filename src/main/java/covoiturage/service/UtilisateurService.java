package covoiturage.service;

import covoiturage.dao.DAOFactory;
import covoiturage.dao.UtilisateurDAO;
import covoiturage.model.Utilisateur;

import java.util.List;
import java.util.Optional;

public class UtilisateurService {
    private UtilisateurDAO utilisateurDAO;

    public UtilisateurService() {
        this.utilisateurDAO = DAOFactory.getUtilisateurDAO();
    }

    public Optional<Utilisateur> getUtilisateurById(Long id) {
        return utilisateurDAO.findById(id);
    }

    public Optional<Utilisateur> getUtilisateurByEmail(String email) {
        return utilisateurDAO.findByEmail(email);
    }

    public List<Utilisateur> getAllUtilisateurs() {
        return  utilisateurDAO.findAll();
    }

    public Long creerUtilisateur(Utilisateur utilisateur) {
        if (utilisateurDAO.existsByEmail(utilisateur.getEmail())) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà");
        }

        return utilisateurDAO.save(utilisateur);
    }

    public boolean modifierUtilisateur(Utilisateur utilisateur) {
        Optional<Utilisateur> existingUser = utilisateurDAO.findByEmail(utilisateur.getEmail());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(utilisateur.getId())){
            throw new IllegalArgumentException("Un autre utilisateur avec cet email existe déjà");
        }

        return utilisateurDAO.update(utilisateur);
    }

    public boolean supprimerUtilisateur(Long id) {
        return utilisateurDAO.delete(id);
    }

    public Optional<Utilisateur> authentifier(String email, String motDePasse) {
        Optional<Utilisateur> optUtilisateur = utilisateurDAO.findByEmail(email);
        if (optUtilisateur.isPresent()) {
            Utilisateur utilisateur = optUtilisateur.get();
            if (utilisateur.getMotDePasse().equals(motDePasse)) {
                return Optional.of(utilisateur);
            }
        }
        return Optional.empty();
    }
}
