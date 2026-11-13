package wenair.modele;

/**
 * Un billet pour un passager sur un vol donné, rattaché à une réservation
 * (référence commande).
 *
 * classe vaut "ECONOMY", "BUSINESS", "EXTRA LEG", "MINEUR UM" ou "BEBE".
 * civilite vaut "MONSIEUR" ou "MADAME".
 */
public class Billet {

    private String numeroVol;
    private String classe;
    private String civilite;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String refCommande;
    private String refBillet;
    private String siege;
    private int poidsBagageSupp;
    private String numeroCarte;
    private String expirationCarte;
    private String cvv;
    private String dateVol;
    private ResponsableLegal responsableLegal;

    // Champs utilisés uniquement dans l'IHM pour calculer le total, pas sauvegardés.
    private double prixBillet;
    private double coutSiege;

    public Billet() {
    }

    /** Vrai si ce billet est un mineur non accompagné (UM). */
    public boolean estMineurNonAccompagne() {
        return classe != null && classe.equalsIgnoreCase("MINEUR UM");
    }

    /** Vrai si ce billet correspond à un bébé (pas de siège dédié). */
    public boolean estBebe() {
        return classe != null && classe.equalsIgnoreCase("BEBE");
    }

    public String getNumeroVol() { return numeroVol; }
    public void setNumeroVol(String numeroVol) { this.numeroVol = numeroVol; }

    public String getClasse() { return classe; }
    public void setClasse(String classe) { this.classe = classe; }

    public String getCivilite() { return civilite; }
    public void setCivilite(String civilite) { this.civilite = civilite; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRefCommande() { return refCommande; }
    public void setRefCommande(String refCommande) { this.refCommande = refCommande; }

    public String getRefBillet() { return refBillet; }
    public void setRefBillet(String refBillet) { this.refBillet = refBillet; }

    public String getSiege() { return siege; }
    public void setSiege(String siege) { this.siege = siege; }

    public int getPoidsBagageSupp() { return poidsBagageSupp; }
    public void setPoidsBagageSupp(int poidsBagageSupp) { this.poidsBagageSupp = poidsBagageSupp; }

    public String getNumeroCarte() { return numeroCarte; }
    public void setNumeroCarte(String numeroCarte) { this.numeroCarte = numeroCarte; }

    public String getExpirationCarte() { return expirationCarte; }
    public void setExpirationCarte(String expirationCarte) { this.expirationCarte = expirationCarte; }

    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }

    public String getDateVol() { return dateVol; }
    public void setDateVol(String dateVol) { this.dateVol = dateVol; }

    public ResponsableLegal getResponsableLegal() { return responsableLegal; }
    public void setResponsableLegal(ResponsableLegal responsableLegal) { this.responsableLegal = responsableLegal; }

    public double getPrixBillet() { return prixBillet; }
    public void setPrixBillet(double prixBillet) { this.prixBillet = prixBillet; }

    public double getCoutSiege() { return coutSiege; }
    public void setCoutSiege(double coutSiege) { this.coutSiege = coutSiege; }

    /** Prix total de ce billet : tarif du billet + supplément de siège éventuel. */
    public double getPrixTotal() {
        return prixBillet + coutSiege;
    }

    @Override
    public boolean equals(Object autreObjet) {
        if (this == autreObjet) return true;
        if (!(autreObjet instanceof Billet)) return false;
        Billet autreBillet = (Billet) autreObjet;
        return this.refBillet != null && this.refBillet.equals(autreBillet.refBillet);
    }

    @Override
    public int hashCode() {
        return refBillet == null ? 0 : refBillet.hashCode();
    }

    @Override
    public String toString() {
        return refBillet + " : " + nom + " " + prenom + " - vol " + numeroVol + " - siège " + siege;
    }
}
