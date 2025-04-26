package covoiturage.dao;

import covoiturage.config.DatabaseConfig;
import covoiturage.model.Avis;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class AvisDAO {
    private TrajetDAO trajetDAO             = new TrajetDAO();
    private UtilisateurDAO utilisateurDAO   = new UtilisateurDAO();


    public Optional<Avis> findById(Long id) {
        String sql = "SELECT * FROM avis WHERE id = ?";


        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {


            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Avis avis = new Avis();
                    avis.setId(rs.getLong("id"));
                    avis.setNote(rs.getInt("note"));
                    avis.setCommentaire(rs.getString("commentaire"));

                    // Récupération de l'utilisateur
                    Long utilisateurId = rs.getLong("utilisateur_id");
                    utilisateurDAO.findById(utilisateurId).ifPresent(avis::setUtilisateur);

                    // Récupération du trajet
                    Long trajetId = rs.getLong("trajet_id");
                    trajetDAO.findById(trajetId).ifPresent(avis::setTrajet);

                    return Optional.of(avis);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public List<Avis> findAll() {
        List<Avis> avis = new ArrayList<>();
        String sql = "SELECT * FROM avis";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Avis a = new Avis();
                a.setId(rs.getLong("id"));
                a.setNote(rs.getInt("note"));
                a.setCommentaire(rs.getString("commentaire"));

                // Récupération de l'utilisateur
                Long utilisateurId = rs.getLong("utilisateur_id");
                utilisateurDAO.findById(utilisateurId).ifPresent(a::setUtilisateur);

                // Récupération du trajet
                Long trajetId = rs.getLong("trajet_id");
                trajetDAO.findById(trajetId).ifPresent(a::setTrajet);

                avis.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return avis;
    }

    public List<Avis> findByTrajetId(Long trajetId) {
        List<Avis> avis = new ArrayList<>();
        String sql = "SELECT * FROM avis WHERE trajet_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, trajetId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Avis a = new Avis();
                    a.setId(rs.getLong("id"));
                    a.setNote(rs.getInt("note"));
                    a.setCommentaire(rs.getString("commentaire"));

                    // Récupération de l'utilisateur
                    Long utilisateurId = rs.getLong("utilisateur_id");
                    utilisateurDAO.findById(utilisateurId).ifPresent(a::setUtilisateur);

                    // Récupération du trajet
                    trajetDAO.findById(trajetId).ifPresent(a::setTrajet);

                    avis.add(a);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return avis;
    }

    public List<Avis> findByUtilisateurId(Long utilisateurId) {
        List<Avis> avis = new ArrayList<>();
        String sql = "SELECT * FROM avis WHERE utilisateur_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, utilisateurId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Avis a = new Avis();
                    a.setId(rs.getLong("id"));
                    a.setNote(rs.getInt("note"));
                    a.setCommentaire(rs.getString("commentaire"));

                    // Récupération de l'utilisateur
                    utilisateurDAO.findById(utilisateurId).ifPresent(a::setUtilisateur);

                    // Récupération du trajet
                    Long trajetId = rs.getLong("trajet_id");
                    trajetDAO.findById(trajetId).ifPresent(a::setTrajet);

                    avis.add(a);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return avis;
    }

    public Long save(Avis avis) {
        String sql = "INSERT INTO avis (note, commentaire, utilisateur_id, trajet_id) VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, avis.getNote());
            pstmt.setString(2, avis.getCommentaire());
            pstmt.setLong(3, avis.getUtilisateur().getId());
            pstmt.setLong(4, avis.getTrajet().getId());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Long id = rs.getLong(1);
                    avis.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean update(Avis avis) {
        String sql = "UPDATE avis SET note = ?, commentaire = ?, utilisateur_id = ?, trajet_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, avis.getNote());
            pstmt.setString(2, avis.getCommentaire());
            pstmt.setLong(3, avis.getUtilisateur().getId());
            pstmt.setLong(4, avis.getTrajet().getId());
            pstmt.setLong(5, avis.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM avis WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}



























