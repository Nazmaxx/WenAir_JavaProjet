package wenair.modele;

/**
 * Une ligne d'occupation de siège : indique qu'un siège donné, pour un vol
 * et une date donnés, est occupé par un billet précis.
 *
 * La date est stockée en texte (format "jj/mm/aaaa").
 */
public class Siege {

    private String numeroVol;
    private String date;
    private String numeroSiege;
    private String classe;
    private String refCommande;
    private String refBillet;

    public Siege() {
    }

    public Siege(String numeroVol, String date, String numeroSiege, String classe,
                 String refCommande, String refBillet) {
        this.numeroVol = numeroVol;
        this.date = date;
        this.numeroSiege = numeroSiege;
        this.classe = classe;
        this.refCommande = refCommande;
        this.refBillet = refBillet;
    }

    public String getNumeroVol() { return numeroVol; }
    public void setNumeroVol(String numeroVol) { this.numeroVol = numeroVol; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getNumeroSiege() { return numeroSiege; }
    public void setNumeroSiege(String numeroSiege) { this.numeroSiege = numeroSiege; }

    public String getClasse() { return classe; }
    public void setClasse(String classe) { this.classe = classe; }

    public String getRefCommande() { return refCommande; }
    public void setRefCommande(String refCommande) { this.refCommande = refCommande; }

    public String getRefBillet() { return refBillet; }
    public void setRefBillet(String refBillet) { this.refBillet = refBillet; }
}
