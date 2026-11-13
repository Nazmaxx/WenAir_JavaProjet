package wenair.modele;

import java.util.Objects;

/**
 * Un vol programmé par la compagnie (horaire, appareil, capacités et tarif
 * de base).
 *
 * Un même numéro de vol peut être opéré à plusieurs dates : les places
 * restantes se calculent donc toujours pour un couple (vol, date).
 */
public class Vol {

    private String depAeroport;
    private String depTerminal;
    private String depVille;
    private String arrAeroport;
    private String arrTerminal;
    private String arrVille;
    private String heureDepart;
    private String heureArrivee;
    private String numeroVol;
    private String avion;
    private String compagnie;
    private String duree;
    private int totalPlaces;
    private int placesBusiness;
    private int placesExtraLeg;
    private int placesEco;
    private int placesMineur;
    private double prixBase;

    public Vol() {
    }

    public Vol(String depAeroport, String depTerminal, String depVille,
               String arrAeroport, String arrTerminal, String arrVille,
               String heureDepart, String heureArrivee, String numeroVol,
               String avion, String compagnie, String duree,
               int totalPlaces, int placesBusiness, int placesExtraLeg,
               int placesEco, int placesMineur, double prixBase) {
        this.depAeroport = depAeroport;
        this.depTerminal = depTerminal;
        this.depVille = depVille;
        this.arrAeroport = arrAeroport;
        this.arrTerminal = arrTerminal;
        this.arrVille = arrVille;
        this.heureDepart = heureDepart;
        this.heureArrivee = heureArrivee;
        this.numeroVol = numeroVol;
        this.avion = avion;
        this.compagnie = compagnie;
        this.duree = duree;
        this.totalPlaces = totalPlaces;
        this.placesBusiness = placesBusiness;
        this.placesExtraLeg = placesExtraLeg;
        this.placesEco = placesEco;
        this.placesMineur = placesMineur;
        this.prixBase = prixBase;
    }

    /**
     * Nombre de places pour une classe tarifaire donnée (0 si la classe
     * n'est pas reconnue). Classes possibles : "ECONOMY", "BUSINESS",
     * "EXTRA LEG", "MINEUR UM" (comparaison insensible à la casse).
     */
    public int placesPourClasse(String classe) {
        if (classe == null) {
            return 0;
        }
        if (classe.equalsIgnoreCase("BUSINESS")) {
            return placesBusiness;
        } else if (classe.equalsIgnoreCase("EXTRA LEG")) {
            return placesExtraLeg;
        } else if (classe.equalsIgnoreCase("ECONOMY")) {
            return placesEco;
        } else if (classe.equalsIgnoreCase("MINEUR UM") || classe.equalsIgnoreCase("MINEUR")) {
            return placesMineur;
        }
        return 0; // classe inconnue, ou "BEBE" (pas de siège dédié)
    }

    // --- getters / setters ---

    public String getDepAeroport() { return depAeroport; }
    public void setDepAeroport(String depAeroport) { this.depAeroport = depAeroport; }

    public String getDepTerminal() { return depTerminal; }
    public void setDepTerminal(String depTerminal) { this.depTerminal = depTerminal; }

    public String getDepVille() { return depVille; }
    public void setDepVille(String depVille) { this.depVille = depVille; }

    public String getArrAeroport() { return arrAeroport; }
    public void setArrAeroport(String arrAeroport) { this.arrAeroport = arrAeroport; }

    public String getArrTerminal() { return arrTerminal; }
    public void setArrTerminal(String arrTerminal) { this.arrTerminal = arrTerminal; }

    public String getArrVille() { return arrVille; }
    public void setArrVille(String arrVille) { this.arrVille = arrVille; }

    public String getHeureDepart() { return heureDepart; }
    public void setHeureDepart(String heureDepart) { this.heureDepart = heureDepart; }

    public String getHeureArrivee() { return heureArrivee; }
    public void setHeureArrivee(String heureArrivee) { this.heureArrivee = heureArrivee; }

    public String getNumeroVol() { return numeroVol; }
    public void setNumeroVol(String numeroVol) { this.numeroVol = numeroVol; }

    public String getAvion() { return avion; }
    public void setAvion(String avion) { this.avion = avion; }

    public String getCompagnie() { return compagnie; }
    public void setCompagnie(String compagnie) { this.compagnie = compagnie; }

    public String getDuree() { return duree; }
    public void setDuree(String duree) { this.duree = duree; }

    public int getTotalPlaces() { return totalPlaces; }
    public void setTotalPlaces(int totalPlaces) { this.totalPlaces = totalPlaces; }

    public int getPlacesBusiness() { return placesBusiness; }
    public void setPlacesBusiness(int placesBusiness) { this.placesBusiness = placesBusiness; }

    public int getPlacesExtraLeg() { return placesExtraLeg; }
    public void setPlacesExtraLeg(int placesExtraLeg) { this.placesExtraLeg = placesExtraLeg; }

    public int getPlacesEco() { return placesEco; }
    public void setPlacesEco(int placesEco) { this.placesEco = placesEco; }

    public int getPlacesMineur() { return placesMineur; }
    public void setPlacesMineur(int placesMineur) { this.placesMineur = placesMineur; }

    public double getPrixBase() { return prixBase; }
    public void setPrixBase(double prixBase) { this.prixBase = prixBase; }

    @Override
    public boolean equals(Object autreObjet) {
        if (this == autreObjet) return true;
        if (!(autreObjet instanceof Vol)) return false;
        Vol autreVol = (Vol) autreObjet;
        return Objects.equals(this.numeroVol, autreVol.numeroVol);
    }

    @Override
    public int hashCode() {
        return numeroVol == null ? 0 : numeroVol.hashCode();
    }

    @Override
    public String toString() {
        return numeroVol + " : " + depVille + " (" + depAeroport + ") -> " + arrVille + " (" + arrAeroport + ") "
                + heureDepart + "-" + heureArrivee + ", " + prixBase + " EUR";
    }
}
