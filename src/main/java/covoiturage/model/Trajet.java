package covoiturage.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Trajet {
    private Long id;
    private String lieuDepart;
    private String lieuArrivee;
    private LocalDateTime dateDepart;
    private double prix;
    private int nbPlacesDisponibles;
    private Conducteur conducteur;
    private List<Reservation> reservations;
    private List<Avis> avis;
    private boolean estAnnule;

    public Trajet() {
        this.reservations = new ArrayList<>();
        this.avis = new ArrayList<>();
    }

    public Trajet(String lieuDepart, String lieuArrivee, LocalDateTime dateDepart, double prix, int nbPlacesDisponibles) {
        this.lieuDepart = lieuDepart;
        this.lieuArrivee = lieuArrivee;
        this.dateDepart = dateDepart;
        this.prix = prix;
        this.nbPlacesDisponibles = nbPlacesDisponibles;
        this.reservations = new ArrayList<>();
        this.avis = new ArrayList<>();
        this.estAnnule = false;
    }

    public Trajet(Long id, String lieuDepart, String lieuArrivee, LocalDateTime dateDepart, double prix,
                  int nbPlacesDisponibles, Conducteur conducteur) {
        this.id = id;
        this.lieuDepart = lieuDepart;
        this.lieuArrivee = lieuArrivee;
        this.dateDepart = dateDepart;
        this.prix = prix;
        this.nbPlacesDisponibles = nbPlacesDisponibles;
        this.conducteur = conducteur;
        this.reservations = new ArrayList<>();
        this.avis = new ArrayList<>();
        this.estAnnule = false;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLieuDepart() {
        return lieuDepart;
    }

    public void setLieuDepart(String lieuDepart) {
        this.lieuDepart = lieuDepart;
    }

    public String getLieuArrivee() {
        return lieuArrivee;
    }

    public void setLieuArrivee(String lieuArrivee) {
        this.lieuArrivee = lieuArrivee;
    }

    public LocalDateTime getDateDepart() {
        return dateDepart;
    }

    public void setDateDepart(LocalDateTime dateDepart) {
        this.dateDepart = dateDepart;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getNbPlacesDisponibles() {
        return nbPlacesDisponibles;
    }

    public void setNbPlacesDisponibles(int nbPlacesDisponibles) {
        this.nbPlacesDisponibles = nbPlacesDisponibles;
    }

    public Conducteur getConducteur() {
        return conducteur;
    }

    public void setConducteur(Conducteur conducteur) {
        this.conducteur = conducteur;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public void addReservation(Reservation reservation) {
        this.reservations.add(reservation);
    }

    public List<Avis> getAvis() {
        return avis;
    }

    public void setAvis(List<Avis> avis) {
        this.avis = avis;
    }

    public void addAvis(Avis avis) {
        this.avis.add(avis);
    }

    public boolean isEstAnnule() {
        return estAnnule;
    }

    public void setEstAnnule(boolean estAnnule) {
        this.estAnnule = estAnnule;
    }

    public boolean creer() {
        System.out.println("Création d'un trajet de " + lieuDepart + " à " + lieuArrivee);
        return true;
    }

    public boolean modifier() {
        System.out.println("Modification du trajet de " + lieuDepart + " à " + lieuArrivee);
        return true;
    }

    public boolean annuler() {
        System.out.println("Annulation du trajet de " + lieuDepart + " à " + lieuArrivee);
        this.estAnnule = true;
        return true;
    }

    public int calculerPlacesRestantes() {
        int placesReservees = reservations.stream()
                .filter(r -> !r.isAnnule())
                .mapToInt(Reservation::getNbPlaces)
                .sum();
        return nbPlacesDisponibles - placesReservees;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trajet trajet = (Trajet) o;
        return Objects.equals(id, trajet.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Trajet{" +
                "id=" + id +
                ", lieuDepart='" + lieuDepart + '\'' +
                ", lieuArrivee='" + lieuArrivee + '\'' +
                ", dateDepart=" + dateDepart +
                ", prix=" + prix +
                ", nbPlacesDisponibles=" + nbPlacesDisponibles +
                ", conducteur=" + (conducteur != null ? conducteur.getPrenom() + " " + conducteur.getNom() : "Aucun") +
                ", estAnnule=" + estAnnule +
                '}';
    }


}
