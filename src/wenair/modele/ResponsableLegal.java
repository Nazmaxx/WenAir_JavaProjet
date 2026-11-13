package wenair.modele;

/**
 * Coordonnées de la personne qui dépose et de celle qui récupère un mineur
 * non accompagné (UM) à l'aéroport.
 */
public class ResponsableLegal {

    private String nomDeposant;
    private String telDeposant;
    private String nomReceveur;
    private String telReceveur;

    public ResponsableLegal() {
    }

    public ResponsableLegal(String nomDeposant, String telDeposant, String nomReceveur, String telReceveur) {
        this.nomDeposant = nomDeposant;
        this.telDeposant = telDeposant;
        this.nomReceveur = nomReceveur;
        this.telReceveur = telReceveur;
    }

    /** Vrai si aucune des quatre informations n'est renseignée. */
    public boolean estVide() {
        return nomDeposant == null && telDeposant == null && nomReceveur == null && telReceveur == null;
    }

    public String getNomDeposant() { return nomDeposant; }
    public void setNomDeposant(String nomDeposant) { this.nomDeposant = nomDeposant; }

    public String getTelDeposant() { return telDeposant; }
    public void setTelDeposant(String telDeposant) { this.telDeposant = telDeposant; }

    public String getNomReceveur() { return nomReceveur; }
    public void setNomReceveur(String nomReceveur) { this.nomReceveur = nomReceveur; }

    public String getTelReceveur() { return telReceveur; }
    public void setTelReceveur(String telReceveur) { this.telReceveur = telReceveur; }
}
