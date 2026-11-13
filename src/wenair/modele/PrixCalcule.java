package wenair.modele;

/**
 * Résultat du calcul de prix pour un vol et une classe donnés : le prix
 * adulte, le prix enfant, et le prix pour un adulte accompagnant un mineur
 * non accompagné (utilisé seulement quand la classe est "MINEUR UM").
 */
public class PrixCalcule {

    private final double prixAdulte;
    private final double prixEnfant;
    private final double prixMineurAdulte;

    public PrixCalcule(double prixAdulte, double prixEnfant, double prixMineurAdulte) {
        this.prixAdulte = prixAdulte;
        this.prixEnfant = prixEnfant;
        this.prixMineurAdulte = prixMineurAdulte;
    }

    public double getPrixAdulte() { return prixAdulte; }
    public double getPrixEnfant() { return prixEnfant; }
    public double getPrixMineurAdulte() { return prixMineurAdulte; }
}
