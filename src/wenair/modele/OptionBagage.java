package wenair.modele;

/** Une option d'achat de bagage supplémentaire : un poids en kg pour un prix fixe. */
public class OptionBagage {

    private final int poidsKg;
    private final double prix;

    public OptionBagage(int poidsKg, double prix) {
        this.poidsKg = poidsKg;
        this.prix = prix;
    }

    public int getPoidsKg() { return poidsKg; }
    public double getPrix() { return prix; }

    @Override
    public String toString() {
        return "+" + poidsKg + "kg (" + String.format("%.2f", prix) + " EUR)";
    }
}
