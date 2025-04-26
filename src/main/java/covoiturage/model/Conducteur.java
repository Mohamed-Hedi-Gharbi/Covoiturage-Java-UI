package covoiturage.model;

import java.util.ArrayList;
import java.util.List;

public class Conducteur extends Personne {
    private String numeroPermis;
    private List<Trajet> trajets;
    private String vehiculeInfo; // Informations basiques sur le véhicule (ex: "Renault Clio - AB-123-CD")

    // Constructeurs
    public Conducteur() {
        super();
        this.trajets = new ArrayList<>();
    }

    public Conducteur(String nom, String prenom, String email, String motDePasse, String telephone, String numeroPermis) {
        super(nom, prenom, email, motDePasse, telephone);
        this.numeroPermis = numeroPermis;
        this.trajets = new ArrayList<>();
    }

    public Conducteur(Long id, String nom, String prenom, String email, String motDePasse, String telephone,
                      String numeroPermis, String vehiculeInfo) {
        super(id, nom, prenom, email, motDePasse, telephone);
        this.numeroPermis = numeroPermis;
        this.vehiculeInfo = vehiculeInfo;
        this.trajets = new ArrayList<>();
    }

    // Getters et Setters
    public String getNumeroPermis() {
        return numeroPermis;
    }

    public void setNumeroPermis(String numeroPermis) {
        this.numeroPermis = numeroPermis;
    }

    public List<Trajet> getTrajets() {
        return trajets;
    }

    public void setTrajets(List<Trajet> trajets) {
        this.trajets = trajets;
    }

    public void addTrajet(Trajet trajet) {
        this.trajets.add(trajet);
    }

    public String getVehiculeInfo() {
        return vehiculeInfo;
    }

    public void setVehiculeInfo(String vehiculeInfo) {
        this.vehiculeInfo = vehiculeInfo;
    }

    // Méthodes métier
    public Trajet proposerTrajet(String lieuDepart, String lieuArrivee, String dateDepart, double prix, int nbPlacesDisponibles) {
        System.out.println("Proposition d'un trajet de " + lieuDepart + " à " + lieuArrivee);
        Trajet trajet = new Trajet();
        // À compléter avec la logique métier
        return trajet;
    }

    public boolean annulerTrajet(Trajet trajet) {
        System.out.println("Annulation du trajet de " + trajet.getLieuDepart() + " à " + trajet.getLieuArrivee());
        return trajets.remove(trajet);
    }

    public boolean modifierTrajet(Trajet trajet) {
        System.out.println("Modification du trajet de " + trajet.getLieuDepart() + " à " + trajet.getLieuArrivee());
        // Logique de modification à implémenter
        return true;
    }

    public boolean validerReservation(Reservation reservation) {
        System.out.println("Validation de la réservation " + reservation.getId());
        // Logique de validation à implémenter
        return true;
    }

    @Override
    public String toString() {
        return "Conducteur{" +
                "id=" + getId() +
                ", nom='" + getNom() + '\'' +
                ", prenom='" + getPrenom() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", telephone='" + getTelephone() + '\'' +
                ", numeroPermis='" + numeroPermis + '\'' +
                ", vehicule='" + vehiculeInfo + '\'' +
                '}';
    }
}