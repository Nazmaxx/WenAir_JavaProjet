package wenair.vue;

import wenair.modele.Billet;
import wenair.modele.ResultatRecherche;

import java.util.ArrayList;
import java.util.List;

/**
 * État de la réservation que le client est en train de faire, partagé
 * entre les écrans de l'assistant de réservation (recherche, passagers,
 * sièges, récapitulatif). Il n'est jamais sauvegardé tel quel : à la
 * confirmation, les billets sont enregistrés via ReservationService puis
 * cet état est remis à zéro pour une nouvelle réservation.
 */
public class EtatReservation {

    private String villeDepart;
    private String villeArrivee;
    private String dateVol;
    private String classeRecherche;
    private ResultatRecherche resultatChoisi;

    private int nbAdultes;
    private int nbEnfants;
    private int nbBebes;

    private List<Billet> billets = new ArrayList<Billet>();

    private String refCommandeConfirmee;
    private double montantTotalConfirme;

    /** Remet à zéro la réservation en cours pour repartir d'une nouvelle recherche. */
    public void reinitialiser() {
        villeDepart = null;
        villeArrivee = null;
        dateVol = null;
        classeRecherche = null;
        resultatChoisi = null;
        nbAdultes = 0;
        nbEnfants = 0;
        nbBebes = 0;
        billets = new ArrayList<Billet>();
        refCommandeConfirmee = null;
        montantTotalConfirme = 0;
    }

    /** Nombre total de passagers (adultes + enfants + bébés) de la réservation en cours. */
    public int getNombreTotalPassagers() {
        return nbAdultes + nbEnfants + nbBebes;
    }

    public String getVilleDepart() { return villeDepart; }
    public void setVilleDepart(String villeDepart) { this.villeDepart = villeDepart; }

    public String getVilleArrivee() { return villeArrivee; }
    public void setVilleArrivee(String villeArrivee) { this.villeArrivee = villeArrivee; }

    public String getDateVol() { return dateVol; }
    public void setDateVol(String dateVol) { this.dateVol = dateVol; }

    public String getClasseRecherche() { return classeRecherche; }
    public void setClasseRecherche(String classeRecherche) { this.classeRecherche = classeRecherche; }

    public ResultatRecherche getResultatChoisi() { return resultatChoisi; }
    public void setResultatChoisi(ResultatRecherche resultatChoisi) { this.resultatChoisi = resultatChoisi; }

    public int getNbAdultes() { return nbAdultes; }
    public void setNbAdultes(int nbAdultes) { this.nbAdultes = nbAdultes; }

    public int getNbEnfants() { return nbEnfants; }
    public void setNbEnfants(int nbEnfants) { this.nbEnfants = nbEnfants; }

    public int getNbBebes() { return nbBebes; }
    public void setNbBebes(int nbBebes) { this.nbBebes = nbBebes; }

    public List<Billet> getBillets() { return billets; }
    public void setBillets(List<Billet> billets) { this.billets = billets; }

    public String getRefCommandeConfirmee() { return refCommandeConfirmee; }
    public void setRefCommandeConfirmee(String refCommandeConfirmee) { this.refCommandeConfirmee = refCommandeConfirmee; }

    public double getMontantTotalConfirme() { return montantTotalConfirme; }
    public void setMontantTotalConfirme(double montantTotalConfirme) { this.montantTotalConfirme = montantTotalConfirme; }
}
