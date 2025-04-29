package covoiturage.dao;


import covoiturage.config.DatabaseConfig;
import covoiturage.model.Trajet;
import org.postgresql.shaded.com.ongres.scram.common.StringPreparation;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrajetDAO {
    private ConducteurDAO conducteurDAO = new ConducteurDAO();

    public Optional<Trajet> findById(Long id) {
        String sql = "SELECT * FROM trajets WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Trajet trajet = new Trajet();
                    trajet.setId(rs.getLong("id"));
                    trajet.setLieuDepart(rs.getString("lieu_depart"));
                    trajet.setLieuArrivee(rs.getString("lieu_arrivee"));
                    trajet.setDateDepart(rs.getObject("date_depart", LocalDateTime.class));
                    trajet.setPrix(rs.getDouble("prix"));
                    trajet.setNbPlacesDisponibles(rs.getInt("nb_places_disponibles"));
                    trajet.setEstAnnule(rs.getBoolean("est_annule"));

                    // Récupération du conducteur
                    Long conducteurId = rs.getLong("conducteur_id");
                    conducteurDAO.findById(conducteurId).ifPresent(trajet::setConducteur);

                    return Optional.of(trajet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }


    public List<Trajet> findAll() {
        List<Trajet> trajets = new ArrayList<>();
        String sql = "SELECT * FROM trajets";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Trajet trajet = new Trajet();
                trajet.setId(rs.getLong("id"));
                trajet.setLieuDepart(rs.getString("lieu_depart"));
                trajet.setLieuArrivee(rs.getString("lieu_arrivee"));
                trajet.setDateDepart(rs.getObject("date_depart", LocalDateTime.class));
                trajet.setPrix(rs.getDouble("prix"));
                trajet.setNbPlacesDisponibles(rs.getInt("nb_places_disponibles"));
                trajet.setEstAnnule(rs.getBoolean("est_annule"));

                // Récupération du conducteur
                Long conducteurId = rs.getLong("conducteur_id");
                conducteurDAO.findById(conducteurId).ifPresent(trajet::setConducteur);

                trajets.add(trajet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return trajets;
    }


    public List<Trajet> findByConducteurId(Long conducteurId) {
        List<Trajet> trajets = new ArrayList<>();
        String sql = "SELECT * FROM trajets WHERE conducteur_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, conducteurId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Trajet trajet = new Trajet();
                    trajet.setId(rs.getLong("id"));
                    trajet.setLieuDepart(rs.getString("lieu_depart"));
                    trajet.setLieuArrivee(rs.getString("lieu_arrivee"));
                    trajet.setDateDepart(rs.getObject("date_depart", LocalDateTime.class));
                    trajet.setPrix(rs.getDouble("prix"));
                    trajet.setNbPlacesDisponibles(rs.getInt("nb_places_disponibles"));
                    trajet.setEstAnnule(rs.getBoolean("est_annule"));

                    // Récupération du conducteur
                    conducteurDAO.findById(conducteurId).ifPresent(trajet::setConducteur);

                    trajets.add(trajet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return trajets;
    }


    public List<Trajet> findByLieuDepartAndLieuArrivee(String lieuDepart, String lieuArrivee) {
        List<Trajet> trajets = new ArrayList<>();
        String sql = "SELECT * FROM trajets WHERE lieu_depart LIKE ? AND lieu_arrivee LIKE ? AND est_annule = false AND date_depart > now()";


        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + lieuDepart + "%");
            pstmt.setString(2, "%" + lieuArrivee + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Trajet trajet = new Trajet();
                    trajet.setId(rs.getLong("id"));
                    trajet.setLieuDepart(rs.getString("lieu_depart"));
                    trajet.setLieuArrivee(rs.getString("lieu_arrivee"));
                    trajet.setDateDepart(rs.getObject("date_depart", LocalDateTime.class));
                    trajet.setPrix(rs.getDouble("prix"));
                    trajet.setNbPlacesDisponibles(rs.getInt("nb_places_disponibles"));
                    trajet.setEstAnnule(rs.getBoolean("est_annule"));

                    // Récupération du conducteur
                    Long conducteurId = rs.getLong("conducteur_id");
                    conducteurDAO.findById(conducteurId).ifPresent(trajet::setConducteur);

                    trajets.add(trajet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return trajets;
    }



    public Long save(Trajet trajet) {
        String sql = "INSERT INTO trajets (lieu_depart, lieu_arrivee, date_depart, prix, nb_places_disponibles, conducteur_id, est_annule)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, trajet.getLieuDepart());
            pstmt.setString(2, trajet.getLieuArrivee());
            pstmt.setObject(3, trajet.getDateDepart());
            pstmt.setDouble(4, trajet.getPrix());
            pstmt.setInt(5, trajet.getNbPlacesDisponibles());
            pstmt.setLong(6, trajet.getConducteur().getId());
            pstmt.setBoolean(7, trajet.isEstAnnule());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Long id = rs.getLong(1); // ← l'ID généré par la base
                    trajet.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public boolean update(Trajet trajet) {
        String sql = "UPDATE trajets SET lieu_depart = ?, lieu_arrivee = ?, date_depart = ?, " +
                "prix = ?, nb_places_disponibles = ?, est_annule = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {


            pstmt.setString(1, trajet.getLieuDepart());
            pstmt.setString(2, trajet.getLieuArrivee());
            pstmt.setObject(3, trajet.getDateDepart());
            pstmt.setDouble(4, trajet.getPrix());
            pstmt.setInt(5, trajet.getNbPlacesDisponibles());
            pstmt.setBoolean(6, trajet.isEstAnnule());
            pstmt.setLong(7, trajet.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean delete(Long id) {
        // Premièrement: supprimer les réservations liées
        String deleteReservationsSQL = "DELETE FROM reservations WHERE trajet_id = ?";

        // Deuxièmement: supprimer les avis liés
        String deleteAvisSQL = "DELETE FROM avis WHERE trajet_id = ?";

        // Troisièmement: supprimer les paiements liés aux réservations du trajet
        String deletePaiementsSQL = "DELETE FROM paiements WHERE reservation_id IN (SELECT id FROM reservations WHERE trajet_id = ?)";

        // Finalement: supprimer le trajet
        String deleteTrajetSQL = "DELETE FROM trajets WHERE id = ?";

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            // Désactiver l'auto-commit pour utiliser une transaction
            conn.setAutoCommit(false);

            // Supprimer d'abord les paiements
            try (PreparedStatement pstmt = conn.prepareStatement(deletePaiementsSQL)) {
                pstmt.setLong(1, id);
                pstmt.executeUpdate();
            }

            // Ensuite supprimer les réservations
            try (PreparedStatement pstmt = conn.prepareStatement(deleteReservationsSQL)) {
                pstmt.setLong(1, id);
                pstmt.executeUpdate();
            }

            // Puis supprimer les avis
            try (PreparedStatement pstmt = conn.prepareStatement(deleteAvisSQL)) {
                pstmt.setLong(1, id);
                pstmt.executeUpdate();
            }

            // Enfin supprimer le trajet
            try (PreparedStatement pstmt = conn.prepareStatement(deleteTrajetSQL)) {
                pstmt.setLong(1, id);
                int rowsAffected = pstmt.executeUpdate();

                // Confirmer la transaction
                conn.commit();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            // En cas d'erreur, annuler la transaction
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            // Rétablir l'auto-commit
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}