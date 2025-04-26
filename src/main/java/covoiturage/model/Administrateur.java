package covoiturage.model;

public class Administrateur extends Personne{
    private String role;

    public Administrateur() {
        super();
    }

    public Administrateur(String nom, String prenom, String email, String motDePasse, String telephone, String role) {
        super(nom, prenom, email, motDePasse, telephone);
        this.role = role;
    }

    public Administrateur(Long id, String telephone, String motDePasse, String email, String prenom, String nom, String role) {
        super(id, telephone, motDePasse, email, prenom, nom);
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Méthodes métier
    public void gererUtilisateur(){
        System.out.println("Gestion des utilisateurs par l'administrateur");
    }

    public void gererTrajets(){
        System.out.println("Gestion des trajets par l'administrateur");
    }

    public void genererRapports(){
        System.out.println("Génération de rapports par l'administrateur");
    }


    @Override
    public String toString(){
        return "Administrateur{" +
                "id=" + getId() +
                ", nom='" + getNom() + '\'' +
                ", prenom='" + getPrenom() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", telephone='" + getTelephone() + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
