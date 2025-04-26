package covoiturage.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Paiement {
    private Long id;
    private double montant;
    private LocalDateTime datePaiement;
    private Reservation reservation;
    private boolean estRembourse;

    // Constructeurs
    public Paiement() {
        this.datePaiement = LocalDateTime.now();
        this.estRembourse = false;
    }

    public Paiement(double montant, Reservation reservation) {
        this.montant = montant;
        this.datePaiement = LocalDateTime.now();
        this.reservation = reservation;
        this.estRembourse = false;
    }

    public Paiement(Long id, double montant, LocalDateTime datePaiement, Reservation reservation, boolean estRembourse) {
        this.id = id;
        this.montant = montant;
        this.datePaiement = datePaiement;
        this.reservation = reservation;
        this.estRembourse = estRembourse;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public LocalDateTime getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDateTime datePaiement) {
        this.datePaiement = datePaiement;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public boolean isEstRembourse() {
        return estRembourse;
    }

    public void setEstRembourse(boolean estRembourse) {
        this.estRembourse = estRembourse;
    }

    // Méthodes métier
    public boolean effectuer() {
        System.out.println("Paiement effectué pour la réservation " + reservation.getId() + " d'un montant de " + montant);
        return true;
    }

    public boolean rembourser() {
        System.out.println("Remboursement du paiement " + id + " d'un montant de " + montant);
        this.estRembourse = true;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Paiement paiement = (Paiement) o;
        return Objects.equals(id, paiement.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Paiement{" +
                "id=" + id +
                ", montant=" + montant +
                ", datePaiement=" + datePaiement +
                ", reservation=" + (reservation != null ? reservation.getId() : "Aucune") +
                ", estRembourse=" + estRembourse +
                '}';
    }
}
