package wenair.service;

import wenair.donnees.DonneesReservation;
import wenair.modele.Billet;
import wenair.modele.LigneChiffreAffaires;
import wenair.modele.Reservation;
import wenair.modele.Vol;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Gère les réservations : création, consultation, annulation. Utilise
 * DonneesReservation pour enregistrer les billets et SiegeService pour
 * gérer les sièges.
 */
public class ReservationService {

    private static final String LETTRES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final DonneesReservation donneesReservation;
    private final SiegeService siegeService;
    private final Random alea = new Random();

    private final VolService volService;
    private final TarifService tarifService;

    public ReservationService(DonneesReservation donneesReservation, SiegeService siegeService,
                               VolService volService, TarifService tarifService) {
        this.donneesReservation = donneesReservation;
        this.siegeService = siegeService;
        this.volService = volService;
        this.tarifService = tarifService;
    }

    /** Authentifie un client par référence de commande et nom. */
    public Reservation authentifierClient(String refCommande, String nom) {
        return donneesReservation.trouverParRefEtNom(refCommande, nom);
    }

    public Reservation consulter(String refCommande) {
        return donneesReservation.trouverParRef(refCommande);
    }

    /**
     * Enregistre une nouvelle réservation : sauvegarde chaque billet, puis
     * marque comme pris le siège de chaque billet qui en a un (les bébés
     * n'ont pas de siège dédié). Tous les billets doivent déjà porter la
     * même référence de commande.
     */
    public void enregistrerReservation(List<Billet> billets) {
        for (Billet b : billets) {
            donneesReservation.ajouterBillet(b);
            if (b.getSiege() != null) {
                siegeService.marquerPris(b.getNumeroVol(), b.getDateVol(), b.getSiege(), b.getClasse(),
                        b.getRefCommande(), b.getRefBillet());
            }
        }
    }

    /** Renvoie le billet portant cette reference, ou {@code null} s'il n'existe pas. */
    public Billet trouverBillet(String refBillet) {
        return donneesReservation.trouverBilletParRef(refBillet);
    }

    /**
     * Renvoie l'email de contact du dossier auquel appartient ce billet :
     * celui du billet lui-meme s'il en a un, sinon le premier email trouve
     * parmi les autres billets du meme dossier (utile pour les billets sans
     * email propre comme les bebes ou mineurs). Renvoie {@code null} si
     * aucun billet du dossier n'a d'email renseigne.
     */
    public String trouverEmailContact(Billet billet) {
        if (billet == null) {
            return null;
        }
        if (billet.getEmail() != null) {
            return billet.getEmail();
        }
        Reservation reservation = consulter(billet.getRefCommande());
        if (reservation == null) {
            return null;
        }
        for (Billet autre : reservation.getBillets()) {
            if (autre.getEmail() != null) {
                return autre.getEmail();
            }
        }
        return null;
    }

    /**
     * Ajoute du bagage supplementaire a un billet : le poids choisi
     * s'ajoute au poids supplementaire deja enregistre. Renvoie le billet
     * mis a jour, ou {@code null} si le billet n'existe pas.
     */
    public Billet ajouterBagage(String refBillet, int poidsAjoute) {
        Billet billet = donneesReservation.trouverBilletParRef(refBillet);
        if (billet == null) {
            return null;
        }
        billet.setPoidsBagageSupp(billet.getPoidsBagageSupp() + poidsAjoute);
        donneesReservation.mettreAJourBillet(billet);
        return billet;
    }

    /**
     * Surclasse un billet vers une classe superieure. Le siege actuel
     * appartient a l'ancienne classe, donc il est libere : le passager
     * devra en choisir un nouveau. Renvoie le billet mis a jour, ou
     * {@code null} si le billet n'existe pas.
     */
    public Billet surclasserBillet(String refBillet, String nouvelleClasse) {
        Billet billet = donneesReservation.trouverBilletParRef(refBillet);
        if (billet == null) {
            return null;
        }
        if (billet.getSiege() != null) {
            siegeService.libererParBillet(refBillet);
        }
        billet.setClasse(nouvelleClasse);
        billet.setSiege(null);
        donneesReservation.mettreAJourBillet(billet);
        return billet;
    }

    /**
     * Annule un billet individuel : supprime la ligne du billet et libère
     * son siège. Renvoie le billet annulé, ou {@code null} s'il
     * n'existait pas.
     */
    public Billet annulerBillet(String refBillet) {
        Billet supprime = donneesReservation.supprimerBillet(refBillet);
        if (supprime != null) {
            siegeService.libererParBillet(refBillet);
        }
        return supprime;
    }

    /**
     * Annule une réservation complète : supprime tous ses billets et
     * libère tous les sièges correspondants. Renvoie les billets annulés
     * (liste vide si la réservation n'existait pas).
     */
    public List<Billet> annulerReservation(String refCommande) {
        List<Billet> supprimes = donneesReservation.supprimerReservation(refCommande);
        for (Billet b : supprimes) {
            siegeService.libererParBillet(b.getRefBillet());
        }
        return supprimes;
    }

    /**
     * Génère une référence de commande unique : 6 lettres majuscules
     * aléatoires, en réessayant si la référence est déjà utilisée.
     */
    public String genererRefCommande() {
        String ref;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                sb.append(LETTRES.charAt(alea.nextInt(LETTRES.length())));
            }
            ref = sb.toString();
        } while (donneesReservation.trouverParRef(ref) != null);
        return ref;
    }

    /**
     * Génère une référence de billet unique : "WN" + 6 chiffres, en
     * réessayant en cas de collision.
     */
    public String genererRefBillet() {
        String ref;
        boolean dejaUtilisee;
        do {
            int nombre = alea.nextInt(1000000);
            ref = String.format("WN%06d", nombre);
            dejaUtilisee = false;
            for (Billet b : donneesReservation.chargerTousLesBillets()) {
                if (ref.equalsIgnoreCase(b.getRefBillet())) {
                    dejaUtilisee = true;
                    break;
                }
            }
        } while (dejaUtilisee);
        return ref;
    }

    /** Liste les billets d'un vol a une date donnee (espace admin). */
    public List<Billet> manifestePourVolEtDate(String numeroVol, String date) {
        List<Billet> resultat = new ArrayList<Billet>();
        if (numeroVol == null || date == null) {
            return resultat;
        }
        for (Billet b : donneesReservation.chargerTousLesBillets()) {
            if (numeroVol.equalsIgnoreCase(b.getNumeroVol()) && date.equals(b.getDateVol())) {
                resultat.add(b);
            }
        }
        return resultat;
    }

    /**
     * Calcule le chiffre d'affaires par vol pour une annee donnee (espace
     * admin) : un billet compte pour l'annee extraite de sa date de vol
     * (JJ/MM/AAAA). Le montant facture est estime par
     * TarifService.montantFactureVol().
     */
    public List<LigneChiffreAffaires> chiffreAffairesParAnnee(int annee) {
        List<LigneChiffreAffaires> lignes = new ArrayList<LigneChiffreAffaires>();

        for (Billet b : donneesReservation.chargerTousLesBillets()) {
            int anneeVol = anneeDepuisDate(b.getDateVol());
            if (anneeVol != annee) {
                continue;
            }

            Vol vol = volService.trouverParNumero(b.getNumeroVol());
            double prixBase = (vol == null) ? 0.0 : vol.getPrixBase();
            double montant = tarifService.montantFactureVol(prixBase, b.getClasse(), b.getPoidsBagageSupp());

            LigneChiffreAffaires ligne = null;
            for (LigneChiffreAffaires candidate : lignes) {
                if (candidate.getNumeroVol().equalsIgnoreCase(b.getNumeroVol())) {
                    ligne = candidate;
                    break;
                }
            }
            if (ligne == null) {
                ligne = new LigneChiffreAffaires(b.getNumeroVol());
                lignes.add(ligne);
            }
            ligne.ajouterPassager(montant);
        }

        return lignes;
    }

    /** Extrait l'annee d'une date au format JJ/MM/AAAA (0 si le format n'est pas reconnu). */
    private int anneeDepuisDate(String date) {
        if (date == null || date.length() != 10 || date.charAt(2) != '/' || date.charAt(5) != '/') {
            return 0;
        }
        try {
            return Integer.parseInt(date.substring(6, 10));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
