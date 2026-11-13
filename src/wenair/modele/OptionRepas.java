package wenair.modele;

/** Un type de repas spécial proposable à un passager, avec son code IATA. */
public class OptionRepas {

    private final int numero;
    private final String nom;
    private final String code;

    public OptionRepas(int numero, String nom, String code) {
        this.numero = numero;
        this.nom = nom;
        this.code = code;
    }

    public int getNumero() { return numero; }
    public String getNom() { return nom; }
    public String getCode() { return code; }

    /** Les types de repas disponibles. */
    public static final OptionRepas[] TOUS = new OptionRepas[] {
            new OptionRepas(1, "Végétarien", "VGML"),
            new OptionRepas(2, "Végétalien", "VEML"),
            new OptionRepas(3, "Sans gluten", "GFML"),
            new OptionRepas(4, "Halal", "MOML"),
            new OptionRepas(5, "Casher", "KSML"),
            new OptionRepas(6, "Sans lactose", "NLML"),
            new OptionRepas(7, "Hindou", "HNML"),
            new OptionRepas(8, "Diabétique", "DBML"),
            new OptionRepas(9, "Enfant", "CHML"),
            new OptionRepas(10, "Bébé", "BBML"),
            new OptionRepas(11, "Autre (contactez le service client)", "SPML")
    };

    @Override
    public String toString() {
        return nom + " (" + code + ")";
    }
}
