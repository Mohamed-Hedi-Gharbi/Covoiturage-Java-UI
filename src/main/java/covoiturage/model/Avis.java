package covoiturage.model;

import java.util.Objects;

public class Avis {
    private Long id;
    private int note;
    private String commentaire;
    private Utilisateur utilisateur;
    private Trajet trajet;

    // Constructeurs
    public Avis() {
    }

    public Avis(int note, String commentaire, Utilisateur utilisateur, Trajet trajet) {
        this.note = note;
        this.commentaire = commentaire;
        this.utilisateur = utilisateur;
        this.trajet = trajet;
    }

    public Avis(Long id, int note, String commentaire, Utilisateur utilisateur, Trajet trajet) {
        this.id = id;
        this.note = note;
        this.commentaire = commentaire;
        this.utilisateur = utilisateur;
        this.trajet = trajet;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
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

    // Méthodes métier
    public boolean creer() {
        System.out.println("Création d'un avis pour le trajet " + trajet.getId());
        return true;
    }

    public boolean modifier() {
        System.out.println("Modification de l'avis " + id);
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Avis avis = (Avis) o;
        return Objects.equals(id, avis.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Avis{" +
                "id=" + id +
                ", note=" + note +
                ", commentaire='" + commentaire + '\'' +
                ", utilisateur=" + (utilisateur != null ? utilisateur.getPrenom() + " " + utilisateur.getNom() : "Aucun") +
                ", trajet=" + (trajet != null ? trajet.getLieuDepart() + " -> " + trajet.getLieuArrivee() : "Aucun") +
                '}';
    }
}