package wenair.modele;

/**
 * Une ligne du rapport de chiffre d'affaires par vol : chiffre d'affaires
 * cumulé et nombre de passagers pour un vol donné.
 */
public class LigneChiffreAffaires {

    private final String numeroVol;
    private int nbPassagers;
    private double chiffreAffaires;

    public LigneChiffreAffaires(String numeroVol) {
        this.numeroVol = numeroVol;
        this.nbPassagers = 0;
        this.chiffreAffaires = 0.0;
    }

    public void ajouterPassager(double montant) {
        this.nbPassagers++;
        this.chiffreAffaires += montant;
    }

    public String getNumeroVol() { return numeroVol; }
    public int getNbPassagers() { return nbPassagers; }
    public double getChiffreAffaires() { return chiffreAffaires; }
}
