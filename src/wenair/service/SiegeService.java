package wenair.service;

import wenair.donnees.DonneesSiege;
import wenair.modele.Siege;

import java.util.List;

/**
 * Logique liée à l'occupation des sièges (au-dessus de DonneesSiege qui ne
 * fait que lire/écrire le fichier).
 */
public class SiegeService {

    private final DonneesSiege donneesSiege;

    public SiegeService(DonneesSiege donneesSiege) {
        this.donneesSiege = donneesSiege;
    }

    public boolean estPris(String numeroVol, String date, String numeroSiege) {
        return donneesSiege.estPris(numeroVol, date, numeroSiege);
    }

    public List<Siege> pourVolEtDate(String numeroVol, String date) {
        return donneesSiege.pourVolEtDate(numeroVol, date);
    }

    /**
     * Nombre de sièges déjà occupés pour un vol, une date et une classe
     * donnés (comparaison insensible à la casse).
     */
    public int compterSiegesPris(String numeroVol, String date, String classe) {
        List<Siege> sieges = donneesSiege.pourVolEtDate(numeroVol, date);
        int compteur = 0;
        for (Siege s : sieges) {
            if (s.getClasse() != null && classe != null && s.getClasse().equalsIgnoreCase(classe)) {
                compteur++;
            }
        }
        return compteur;
    }

    /** Marque un siège comme pris pour un billet donné. */
    public void marquerPris(String numeroVol, String date, String numeroSiege, String classe,
                             String refCommande, String refBillet) {
        donneesSiege.marquerPris(new Siege(numeroVol, date, numeroSiege, classe, refCommande, refBillet));
    }

    /** Libère un siège précis (ex : changement de siège). */
    public void libererParCoordonnees(String numeroVol, String date, String numeroSiege) {
        donneesSiege.libererParCoordonnees(numeroVol, date, numeroSiege);
    }

    /** Libère le(s) siège(s) occupé(s) par un billet (utilisé lors d'une annulation). */
    public void libererParBillet(String refBillet) {
        donneesSiege.libererParBillet(refBillet);
    }
}
