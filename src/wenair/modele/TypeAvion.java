package wenair.modele;

/**
 * Un modèle d'avion proposable à l'admin lors de la création/modification
 * d'un vol : nom, places totales et répartition par classe.
 */
public class TypeAvion {

    private final String nom;
    private final int totalPlaces;
    private final int placesBusiness;
    private final int placesExtraLeg;
    private final int placesEco;
    private final int placesMineur;

    public TypeAvion(String nom, int totalPlaces, int placesBusiness, int placesExtraLeg,
                      int placesEco, int placesMineur) {
        this.nom = nom;
        this.totalPlaces = totalPlaces;
        this.placesBusiness = placesBusiness;
        this.placesExtraLeg = placesExtraLeg;
        this.placesEco = placesEco;
        this.placesMineur = placesMineur;
    }

    public String getNom() { return nom; }
    public int getTotalPlaces() { return totalPlaces; }
    public int getPlacesBusiness() { return placesBusiness; }
    public int getPlacesExtraLeg() { return placesExtraLeg; }
    public int getPlacesEco() { return placesEco; }
    public int getPlacesMineur() { return placesMineur; }

    /** Les types d'avion disponibles. */
    public static final TypeAvion[] TOUS = new TypeAvion[] {
            new TypeAvion("Airbus A220-300 Passenger (Ancien CS3)", 147, 20, 6, 113, 10),
            new TypeAvion("Airbus A318", 138, 34, 6, 92, 6),
            new TypeAvion("Airbus A319", 130, 18, 6, 97, 9),
            new TypeAvion("Airbus A320", 161, 10, 12, 121, 18),
            new TypeAvion("Airbus A320", 174, 36, 10, 119, 9),
            new TypeAvion("Airbus A321", 192, 36, 12, 135, 9),
            new TypeAvion("EMBRAER 190-100", 74, 18, 0, 50, 6)
    };

    /** Texte court affiché dans la liste déroulante. */
    @Override
    public String toString() {
        return nom + "  (" + totalPlaces + " places)";
    }

    /** Détail des places par classe, pour le libellé affiché sous la liste déroulante. */
    public String detailPlaces() {
        return placesBusiness + " Business / " + placesExtraLeg + " Extra Leg / "
                + placesEco + " Economy / " + placesMineur + " Mineur";
    }
}
