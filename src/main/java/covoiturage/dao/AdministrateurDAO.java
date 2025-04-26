package covoiturage.dao;

import covoiturage.config.DatabaseConfig;
import covoiturage.model.Administrateur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdministrateurDAO {

    public Optional<Administrateur> findById(Long id) {
        String sql = "SELECT * FROM administrateur WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Administrateur admin = new Administrateur();
                    admin.setId(rs.getLong("id"));
                    admin.setNom(rs.getString("nom"));
                    admin.setPrenom(rs.getString("prenom"));
                    admin.setEmail(rs.getString("email"));
                    admin.setMotDePasse(rs.getString("mot_de_passe"));
                    admin.setTelephone(rs.getString("telephone"));
                    admin.setRole(rs.getString("role"));

                    return Optional.of(admin);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Administrateur> findByEmail(String email) {
        String sql = "SELECT * FROM administrateurs WHERE email = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Administrateur admin = new Administrateur();
                    admin.setId(rs.getLong("id"));
                    admin.setNom(rs.getString("nom"));
                    admin.setPrenom(rs.getString("prenom"));
                    admin.setEmail(rs.getString("email"));
                    admin.setMotDePasse(rs.getString("mot_de_passe"));
                    admin.setTelephone(rs.getString("telephone"));
                    admin.setRole(rs.getString("role"));

                    return Optional.of(admin);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<Administrateur> findAll() {
        List<Administrateur> admins = new ArrayList<>();
        String sql = "SELECT * FROM administrateurs";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Administrateur admin = new Administrateur();
                admin.setId(rs.getLong("id"));
                admin.setNom(rs.getString("nom"));
                admin.setPrenom(rs.getString("prenom"));
                admin.setEmail(rs.getString("email"));
                admin.setMotDePasse(rs.getString("mot_de_passe"));
                admin.setTelephone(rs.getString("telephone"));
                admin.setRole(rs.getString("role"));

                admins.add(admin);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admins;
    }

    public Long save(Administrateur admin) {
        String sql = "INSERT INTO administrateurs (nom, prenom, email, mot_de_passe, telephone, role) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, admin.getNom());
            pstmt.setString(2, admin.getPrenom());
            pstmt.setString(3, admin.getEmail());
            pstmt.setString(4, admin.getMotDePasse());
            pstmt.setString(5, admin.getTelephone());
            pstmt.setString(6, admin.getRole());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Long id = rs.getLong(1);
                    admin.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean update(Administrateur admin) {
        String sql = "UPDATE administrateurs SET nom = ?, prenom = ?, email = ?, mot_de_passe = ?, " +
                "telephone = ?, role = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, admin.getNom());
            pstmt.setString(2, admin.getPrenom());
            pstmt.setString(3, admin.getEmail());
            pstmt.setString(4, admin.getMotDePasse());
            pstmt.setString(5, admin.getTelephone());
            pstmt.setString(6, admin.getRole());
            pstmt.setLong(7, admin.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM administrateurs WHERE id = ?";

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
