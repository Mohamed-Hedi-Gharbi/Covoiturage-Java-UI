package covoiturage.model;

import java.util.Objects;

public class Personne {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String telephone;

    public Personne() {
    }

    public Personne(String nom, String prenom, String email, String motDePasse, String telephone) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.telephone = telephone;
    }

    public Personne(Long id, String telephone, String motDePasse, String email, String prenom, String nom) {
        this.id = id;
        this.telephone = telephone;
        this.motDePasse = motDePasse;
        this.email = email;
        this.prenom = prenom;
        this.nom = nom;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }


    // Méthodes de l'interface utilisateur
    public void sInscrire() {
        System.out.println("Inscription de " + prenom + " " + nom);
    }

    public boolean authentifier(String email, String motDePasse) {
        return this.email.equals(email) && this.motDePasse.equals(motDePasse);
    }

    public void modifierProfil() {
        System.out.println("Modification du profil de " + prenom + " " + nom);
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Personne personne = (Personne) o;
        return Objects.equals(id, personne.id) && Objects.equals(email, personne.email);
    }

    @Override
    public int hashCode(){
        return Objects.hash(id, email);
    }

    @Override
    public String toString(){
        return "Personne{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                '}';
    }

}
