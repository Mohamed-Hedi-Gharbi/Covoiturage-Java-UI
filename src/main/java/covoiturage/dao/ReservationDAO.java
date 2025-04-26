package covoiturage.dao;

import covoiturage.config.DatabaseConfig;
import covoiturage.model.Reservation;
import covoiturage.model.enums.StatutReservation;

import javax.xml.crypto.Data;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservationDAO {
    private TrajetDAO trajetDAO = new TrajetDAO();
    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    public Optional<Reservation> findById(Long id) {
        String sql = "SELECT * FROM reservations WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Reservation reservation = new Reservation();
                    reservation.setId(rs.getLong("id"));
                    reservation.setDateReservation(rs.getObject("date_reservation", LocalDateTime.class));
                    reservation.setNbPlaces(rs.getInt("nb_places"));

                    String statusStr = rs.getString("statut");
                    StatutReservation statut = StatutReservation.valueOf(statusStr);
                    reservation.setStatut(statut);

                    reservation.setAnnule(rs.getBoolean("est_annule"));


                    // Récupération de l'utilisateur
                    Long utilisateurId = rs.getLong("utilisateur_id");
                    utilisateurDAO.findById(utilisateurId).ifPresent(reservation::setUtilisateur);

                    // Récupération du trajet
                    Long trajetId = rs.getLong("trajet_id");
                    trajetDAO.findById(trajetId).ifPresent(reservation::setTrajet);

                    return Optional.of(reservation);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<Reservation> findAll() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations";


        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setId(rs.getLong("id"));
                reservation.setDateReservation(rs.getObject("date_reservation", LocalDateTime.class));
                reservation.setNbPlaces(rs.getInt("nb_places"));

                String statutStr = rs.getString("statut");
                StatutReservation statut = StatutReservation.valueOf(statutStr);
                reservation.setStatut(statut);

                reservation.setAnnule(rs.getBoolean("est_annule"));

                // Récupération de l'utilisateur
                Long utilisateurId = rs.getLong("utilisateur_id");
                utilisateurDAO.findById(utilisateurId).ifPresent(reservation::setUtilisateur);

                // Récupération du trajet
                Long trajetId = rs.getLong("trajet_id");
                trajetDAO.findById(trajetId).ifPresent(reservation::setTrajet);

                reservations.add(reservation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservations;
    }

    public List<Reservation> findByUtilisateurId(Long utilisateurId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE utilisateur_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, utilisateurId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = new Reservation();
                    reservation.setId(rs.getLong("id"));
                    reservation.setDateReservation(rs.getObject("date_reservation", LocalDateTime.class));
                    reservation.setNbPlaces(rs.getInt("nb_places"));

                    String statutStr = rs.getString("statut");
                    StatutReservation statut = StatutReservation.valueOf(statutStr);
                    reservation.setStatut(statut);

                    reservation.setAnnule(rs.getBoolean("est_annule"));

                    // Récupération de l'utilisateur
                    utilisateurDAO.findById(utilisateurId).ifPresent(reservation::setUtilisateur);

                    // Récupération du trajet
                    Long trajetId = rs.getLong("trajet_id");
                    trajetDAO.findById(trajetId).ifPresent(reservation::setTrajet);

                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservations;
    }

    public List<Reservation> findByTrajetId(Long trajetId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE trajet_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, trajetId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = new Reservation();
                    reservation.setId(rs.getLong("id"));
                    reservation.setDateReservation(rs.getObject("date_reservation", LocalDateTime.class));
                    reservation.setNbPlaces(rs.getInt("nb_places"));

                    String statutStr = rs.getString("statut");
                    StatutReservation statut = StatutReservation.valueOf(statutStr);
                    reservation.setStatut(statut);

                    reservation.setAnnule(rs.getBoolean("est_annule"));

                    // Récupération de l'utilisateur
                    Long utilisateurId = rs.getLong("utilisateur_id");
                    utilisateurDAO.findById(utilisateurId).ifPresent(reservation::setUtilisateur);

                    // Récupération du trajet
                    trajetDAO.findById(trajetId).ifPresent(reservation::setTrajet);

                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservations;
    }

    public Long save(Reservation reservation) {
        String sql = "INSERT INTO reservations (date_reservation, nb_places, statut, utilisateur_id, trajet_id, est_annule) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setObject(1, reservation.getDateReservation());
            pstmt.setInt(2, reservation.getNbPlaces());
            pstmt.setString(3, reservation.getStatut().name());
            pstmt.setLong(4, reservation.getUtilisateur().getId());
            pstmt.setLong(5, reservation.getTrajet().getId());
            pstmt.setBoolean(6, reservation.isAnnule());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Long id = rs.getLong(1);
                    reservation.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean update(Reservation reservation) {
        String sql = "UPDATE reservations SET date_reservation = ?, nb_places = ?, statut = ?, " +
                     "utilisateur_id = ?, trajet_id = ?, est_annule = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setObject(1, reservation.getDateReservation());
            pstmt.setInt(2, reservation.getNbPlaces());
            pstmt.setString(3, reservation.getStatut().name());
            pstmt.setLong(4, reservation.getUtilisateur().getId());
            pstmt.setLong(5, reservation.getTrajet().getId());
            pstmt.setBoolean(6, reservation.isAnnule());
            pstmt.setLong(7, reservation.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM reservations WHERE id = ?";

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
