package covoiturage.model;

import java.util.ArrayList;
import java.util.List;

public class Utilisateur extends Personne{
    private String preferences;
    private List<Reservation> reservations;

    public Utilisateur() {

    }


    public Utilisateur(String nom, String prenom, String email, String motDePasse, String telephone) {
        super(nom, prenom, email, motDePasse, telephone);
        this.reservations = new ArrayList<>();
    }

    public Utilisateur(Long id, String nom, String prenom, String email, String motDePasse, String telephone, String preferences) {
        super(id, nom, prenom, email, motDePasse, telephone);
        this.preferences = preferences;
        this.reservations = new ArrayList<>();
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public void addReservation(Reservation reservation){
        this.reservations.add(reservation);
    }

    // Méthodes métier
    public List<Trajet> chercherTrajet(String lieuDepart, String lieuArrivee, String dateDepart){
        System.out.println("Recherche de trajets de " + lieuDepart + " à " + lieuArrivee + " le " + dateDepart);
        return new ArrayList<>();
    }

    public Reservation reserverTrajet(Trajet trajet, int nbPlaces){
        System.out.println("Réservation de " + nbPlaces + " place(s) pour le trajet de " +
                            trajet.getLieuDepart() + " à " + trajet.getLieuArrivee());
        Reservation reservation = new Reservation();
        // À compléter avec la logique métier
        return reservation;
    }

    public void consulterHistorique(){
        System.out.println("Consultation de l'historique des réservation");
        // Afficher les réservations
    }

    @Override
    public String toString(){
        return "Utilisateur{" +
                "id=" + getId() +
                ", nom='" + getNom() + '\'' +
                ", prenom='" + getPrenom() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", telephone='" + getTelephone() + '\'' +
                ", preferences='" + preferences + '\'' +
                '}';
    }


}
