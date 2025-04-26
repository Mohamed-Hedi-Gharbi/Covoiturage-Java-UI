package covoiturage.dao;

import covoiturage.config.DatabaseConfig;
import covoiturage.model.Conducteur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConducteurDAO {

    public Optional<Conducteur> findById(Long id) {
        String sql = "SELECT * FROM conducteurs WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Conducteur conducteur = new Conducteur();
                    conducteur.setId(rs.getLong("id"));
                    conducteur.setNom(rs.getString("nom"));
                    conducteur.setPrenom(rs.getString("prenom"));
                    conducteur.setEmail(rs.getString("email"));
                    conducteur.setMotDePasse(rs.getString("mot_de_passe"));
                    conducteur.setTelephone(rs.getString("telephone"));
                    conducteur.setNumeroPermis(rs.getString("numero_permis"));
                    conducteur.setVehiculeInfo(rs.getString("vehicule_info"));

                    return Optional.of(conducteur);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    public Optional<Conducteur> findByEMail(String email) {
        String sql = "SELECT * FROM conducteurs WHERE email = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Conducteur conducteur = new Conducteur();
                    conducteur.setId(rs.getLong("id"));
                    conducteur.setNom(rs.getString("nom"));
                    conducteur.setPrenom(rs.getString("prenom"));
                    conducteur.setEmail(rs.getString("email"));
                    conducteur.setMotDePasse(rs.getString("mot_de_passe"));
                    conducteur.setTelephone(rs.getString("telephone"));
                    conducteur.setNumeroPermis(rs.getString("numero_permis"));
                    conducteur.setVehiculeInfo(rs.getString("vehicule_info"));

                    return Optional.of(conducteur);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<Conducteur> findAll() {
        List<Conducteur> conducteurs = new ArrayList<>();
        String sql = "SELECT * FROM conducteurs";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Conducteur conducteur = new Conducteur();
                conducteur.setId(rs.getLong("id"));
                conducteur.setNom(rs.getString("nom"));
                conducteur.setPrenom(rs.getString("prenom"));
                conducteur.setEmail(rs.getString("email"));
                conducteur.setMotDePasse(rs.getString("mot_de_passe"));
                conducteur.setTelephone(rs.getString("telephone"));
                conducteur.setNumeroPermis(rs.getString("numero_permis"));
                conducteur.setVehiculeInfo(rs.getString("vehicule_info"));

                conducteurs.add(conducteur);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conducteurs;
    }

    public Long save(Conducteur conducteur) {
        String sql = "INSERT INTO conducteurs (nom, prenom, email, mot_de_passe, telephone, numero_permis, vehicule_info) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";


        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, conducteur.getNom());
            pstmt.setString(2, conducteur.getPrenom());
            pstmt.setString(3, conducteur.getEmail());
            pstmt.setString(4, conducteur.getMotDePasse());
            pstmt.setString(5, conducteur.getTelephone());
            pstmt.setString(6, conducteur.getNumeroPermis());
            pstmt.setString(7, conducteur.getVehiculeInfo());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Long id = rs.getLong(1);
                    conducteur.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(Conducteur conducteur) {
        String sql = "UPDATE conducteurs SET nom = ?, prenom = ?, email = ?, mot_de_passe = ?, " +
                     "telephone = ?, numero_permis = ?, vehicule_info = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, conducteur.getNom());
            pstmt.setString(2, conducteur.getPrenom());
            pstmt.setString(3, conducteur.getEmail());
            pstmt.setString(4, conducteur.getMotDePasse());
            pstmt.setString(5, conducteur.getTelephone());
            pstmt.setString(6, conducteur.getNumeroPermis());
            pstmt.setString(7, conducteur.getVehiculeInfo());
            pstmt.setLong(8, conducteur.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM conducteurs WHERE id = ?";

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
