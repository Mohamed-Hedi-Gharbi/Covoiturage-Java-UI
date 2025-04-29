package covoiturage.dao;

import covoiturage.config.DatabaseConfig;
import covoiturage.model.Paiement;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaiementDAO {
    private ReservationDAO reservationDAO = new ReservationDAO();

    public Optional<Paiement> findById(Long id) {
        String sql = "SELECT * FROM paiements WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Paiement paiement = new Paiement();
                    paiement.setId(rs.getLong("id"));
                    paiement.setMontant(rs.getDouble("montant"));
                    paiement.setDatePaiement(rs.getObject("date_paiement", LocalDateTime.class));
                    paiement.setEstRembourse(rs.getBoolean("est_rembourse"));

                    // Récupération de la réservation
                    Long reservationId = rs.getLong("reservation_id");
                    reservationDAO.findById(reservationId).ifPresent(paiement::setReservation);

                    return Optional.of(paiement);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public Optional<Paiement> findByReservationId(Long reservationId) {
        String sql = "SELECT * FROM paiements WHERE reservation_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, reservationId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Paiement paiement = new Paiement();
                    paiement.setId(rs.getLong("id"));
                    paiement.setMontant(rs.getDouble("montant"));
                    paiement.setDatePaiement(rs.getObject("date_paiement", LocalDateTime.class));
                    paiement.setEstRembourse(rs.getBoolean("est_rembourse"));

                    // Récupération de la réservation
                    reservationDAO.findById(reservationId).ifPresent(paiement::setReservation);

                    return Optional.of(paiement);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }


    public List<Paiement> findAll() {
        List<Paiement> paiements = new ArrayList<>();
        String sql = "SELECT * FROM paiements";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Paiement paiement = new Paiement();
                paiement.setId(rs.getLong("id"));
                paiement.setMontant(rs.getDouble("montant"));
                paiement.setDatePaiement(rs.getObject("date_paiement", LocalDateTime.class));
                paiement.setEstRembourse(rs.getBoolean("est_rembourse"));

                // Récupération de la réservation
                Long reservationId = rs.getLong("reservation_id");
                reservationDAO.findById(reservationId).ifPresent(paiement::setReservation);

                paiements.add(paiement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return paiements;
    }

    public Long save(Paiement paiement) {
        String sql = "INSERT INTO paiements (montant, date_paiement, reservation_id, est_rembourse) " +
                "VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, paiement.getMontant());
            pstmt.setObject(2, paiement.getDatePaiement());
            pstmt.setLong(3, paiement.getReservation().getId());
            pstmt.setBoolean(4, paiement.isEstRembourse());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Long id = rs.getLong(1);
                    paiement.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean update(Paiement paiement) {
        String sql = "UPDATE paiements SET montant = ?, date_paiement = ?, reservation_id = ?, est_rembourse = ? " +
                "WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, paiement.getMontant());
            pstmt.setObject(2, paiement.getDatePaiement());
            pstmt.setLong(3, paiement.getReservation().getId());
            pstmt.setBoolean(4, paiement.isEstRembourse());
            pstmt.setLong(5, paiement.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean delete(Long id) {
        String sql = "DELETE FROM paiements WHERE id = ?";

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
