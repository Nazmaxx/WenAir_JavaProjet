package wenair.modele;

/**
 * Une ligne de résultat de recherche de vol : le vol, les prix calculés
 * pour la classe demandée (adulte, enfant, et éventuellement mineur non
 * accompagné), et le nombre de places restantes dans cette classe.
 */
public class ResultatRecherche {

    private final Vol vol;
    private final double prixAdulte;
    private final double prixEnfant;
    private final double prixMineur;
    private final double prixMineurAdulte;
    private final int placesRestantes;

    public ResultatRecherche(Vol vol, double prixAdulte, double prixEnfant,
                              double prixMineur, double prixMineurAdulte, int placesRestantes) {
        this.vol = vol;
        this.prixAdulte = prixAdulte;
        this.prixEnfant = prixEnfant;
        this.prixMineur = prixMineur;
        this.prixMineurAdulte = prixMineurAdulte;
        this.placesRestantes = placesRestantes;
    }

    public Vol getVol() { return vol; }
    public double getPrixAdulte() { return prixAdulte; }
    public double getPrixEnfant() { return prixEnfant; }
    public double getPrixMineur() { return prixMineur; }
    public double getPrixMineurAdulte() { return prixMineurAdulte; }
    public int getPlacesRestantes() { return placesRestantes; }
}
