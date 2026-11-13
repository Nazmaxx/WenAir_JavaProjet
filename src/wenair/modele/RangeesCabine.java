package wenair.modele;

/**
 * Découpage de la cabine d'un vol en rangées par classe (Business, Extra
 * Leg, Economy, Mineur UM), calculé à partir des capacités du vol.
 */
public class RangeesCabine {

    private final int rangeeFinBusiness;
    private final int rangeeDebutExtraLeg;
    private final int rangeeFinExtraLeg;
    private final int rangeeDebutEco;
    private final int rangeeFinEco;
    private final int rangeeDebutMineur;
    private final int rangeeFinMineur;

    public RangeesCabine(int rangeeFinBusiness, int rangeeDebutExtraLeg, int rangeeFinExtraLeg,
                          int rangeeDebutEco, int rangeeFinEco, int rangeeDebutMineur, int rangeeFinMineur) {
        this.rangeeFinBusiness = rangeeFinBusiness;
        this.rangeeDebutExtraLeg = rangeeDebutExtraLeg;
        this.rangeeFinExtraLeg = rangeeFinExtraLeg;
        this.rangeeDebutEco = rangeeDebutEco;
        this.rangeeFinEco = rangeeFinEco;
        this.rangeeDebutMineur = rangeeDebutMineur;
        this.rangeeFinMineur = rangeeFinMineur;
    }

    public int getRangeeDebutBusiness() { return 1; }
    public int getRangeeFinBusiness() { return rangeeFinBusiness; }
    public int getRangeeDebutExtraLeg() { return rangeeDebutExtraLeg; }
    public int getRangeeFinExtraLeg() { return rangeeFinExtraLeg; }
    public int getRangeeDebutEco() { return rangeeDebutEco; }
    public int getRangeeFinEco() { return rangeeFinEco; }
    public int getRangeeDebutMineur() { return rangeeDebutMineur; }
    public int getRangeeFinMineur() { return rangeeFinMineur; }
}
