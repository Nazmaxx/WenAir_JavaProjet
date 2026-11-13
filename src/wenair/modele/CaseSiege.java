package wenair.modele;

/** Une case de la carte des sièges : position, disponibilité et supplément éventuel. */
public class CaseSiege {

    private final int rangee;
    private final char colonne;
    private final String numeroSiege;
    private final boolean disponible;
    private final double prix;

    public CaseSiege(int rangee, char colonne, String numeroSiege, boolean disponible, double prix) {
        this.rangee = rangee;
        this.colonne = colonne;
        this.numeroSiege = numeroSiege;
        this.disponible = disponible;
        this.prix = prix;
    }

    public int getRangee() { return rangee; }
    public char getColonne() { return colonne; }
    public String getNumeroSiege() { return numeroSiege; }
    public boolean isDisponible() { return disponible; }
    public double getPrix() { return prix; }

    /** Vrai si la colonne fait partie du groupe de gauche (A, B ou C). */
    public boolean estColonneGauche() {
        return colonne == 'A' || colonne == 'B' || colonne == 'C';
    }

    @Override
    public String toString() {
        return numeroSiege + (disponible ? " (libre, " + prix + " EUR)" : " (pris)");
    }
}
