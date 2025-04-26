package covoiturage.model;


import covoiturage.model.enums.StatutReservation;

import java.time.LocalDateTime;
import java.util.Objects;

public class Reservation {
    private Long id;
    private LocalDateTime dateReservation;
    private int nbPlaces;
    private StatutReservation statut;
    private Utilisateur utilisateur;
    private Trajet trajet;
    private boolean annule;
    private Paiement paiement;

    // Constructeurs
    public Reservation() {
        this.dateReservation = LocalDateTime.now();
        this.statut = StatutReservation.EN_ATTENTE;
        this.annule = false;
    }

    public Reservation(int nbPlaces, Utilisateur utilisateur, Trajet trajet) {
        this.dateReservation = LocalDateTime.now();
        this.nbPlaces = nbPlaces;
        this.statut = StatutReservation.EN_ATTENTE;
        this.utilisateur = utilisateur;
        this.trajet = trajet;
        this.annule = false;
    }

    public Reservation(Long id, LocalDateTime dateReservation, int nbPlaces,
                       StatutReservation statut, Utilisateur utilisateur, Trajet trajet) {
        this.id = id;
        this.dateReservation = dateReservation;
        this.nbPlaces = nbPlaces;
        this.statut = statut;
        this.utilisateur = utilisateur;
        this.trajet = trajet;
        this.annule = false;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDateTime dateReservation) {
        this.dateReservation = dateReservation;
    }

    public int getNbPlaces() {
        return nbPlaces;
    }

    public void setNbPlaces(int nbPlaces) {
        this.nbPlaces = nbPlaces;
    }

    public StatutReservation getStatut() {
        return statut;
    }

    public void setStatut(StatutReservation statut) {
        this.statut = statut;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Trajet getTrajet() {
        return trajet;
    }

    public void setTrajet(Trajet trajet) {
        this.trajet = trajet;
    }

    public boolean isAnnule() {
        return annule;
    }

    public void setAnnule(boolean annule) {
        this.annule = annule;
    }

    public Paiement getPaiement() {
        return paiement;
    }

    public void setPaiement(Paiement paiement) {
        this.paiement = paiement;
    }

    // Méthodes métier
    public boolean confirmer() {
        if (this.statut == StatutReservation.EN_ATTENTE) {
            this.statut = StatutReservation.CONFIRMEE;
            return true;
        }
        return false;
    }

    public boolean annuler() {
        if (this.statut != StatutReservation.ANNULEE) {
            this.statut = StatutReservation.ANNULEE;
            this.annule = true;
            return true;
        }
        return false;
    }

    public boolean payer() {
        if (this.statut == StatutReservation.CONFIRMEE && paiement == null) {
            // Logique de paiement à implémenter
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", dateReservation=" + dateReservation +
                ", nbPlaces=" + nbPlaces +
                ", statut=" + statut +
                ", utilisateur=" + (utilisateur != null ? utilisateur.getPrenom() + " " + utilisateur.getNom() : "Aucun") +
                ", trajet=" + (trajet != null ? trajet.getLieuDepart() + " -> " + trajet.getLieuArrivee() : "Aucun") +
                ", annule=" + annule +
                '}';
    }
}
