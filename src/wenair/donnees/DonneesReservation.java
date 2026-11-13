package wenair.donnees;

import wenair.modele.Billet;
import wenair.modele.Reservation;
import wenair.modele.ResponsableLegal;
import wenair.utilitaires.Champs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Accès au fichier des billets/réservations :
 *
 * numeroVol;classe;civilite;nom;prenom;telephone;email;refCommande;
 * refBillet;siege;poidsBagageSupp;numeroCarte;expirationCarte;cvv;dateVol
 * puis 4 champs responsable légal ("N/A" x4 si pas un mineur non
 * accompagné).
 *
 * Les billets partageant la même référence de commande sont regroupés. Annuler un billet ou une réservation
 * supprime directement la ou les lignes concernées.
 */
public class DonneesReservation {

    private static final String SEPARATEUR = ";";
    private static final int NB_CHAMPS = 19;

    private final Path fichier;

    public DonneesReservation(Path fichier) {
        this.fichier = fichier;
    }

    public List<Billet> chargerTousLesBillets() {
        List<Billet> billets = new ArrayList<Billet>();
        if (!Files.exists(fichier)) {
            return billets;
        }
        try {
            List<String> lignes = Files.readAllLines(fichier, StandardCharsets.UTF_8);
            for (int i = 0; i < lignes.size(); i++) {
                String ligne = lignes.get(i);
                if (ligne.isBlank()) {
                    continue;
                }
                try {
                    billets.add(parserLigne(ligne));
                } catch (RuntimeException e) {
                    System.err.println("[DonneesReservation] Ligne " + (i + 1) + " ignorée : " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new DonneesException("Impossible de lire le fichier des réservations : " + fichier, e);
        }
        return billets;
    }

    public void sauvegarderTousLesBillets(List<Billet> billets) {
        List<String> lignes = new ArrayList<String>();
        for (Billet b : billets) {
            lignes.add(formaterLigne(b));
        }
        try {
            if (fichier.toAbsolutePath().getParent() != null) {
                Files.createDirectories(fichier.toAbsolutePath().getParent());
            }
            Files.write(fichier, lignes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DonneesException("Impossible d'écrire le fichier des réservations : " + fichier, e);
        }
    }

    /** Regroupe tous les billets par référence de commande. */
    public List<Reservation> chargerToutesLesReservations() {
        List<String> refsVues = new ArrayList<String>();
        List<List<Billet>> groupes = new ArrayList<List<Billet>>();

        for (Billet b : chargerTousLesBillets()) {
            int index = -1;
            for (int i = 0; i < refsVues.size(); i++) {
                if (refsVues.get(i).equalsIgnoreCase(b.getRefCommande())) {
                    index = i;
                    break;
                }
            }
            if (index == -1) {
                refsVues.add(b.getRefCommande());
                List<Billet> nouveauGroupe = new ArrayList<Billet>();
                nouveauGroupe.add(b);
                groupes.add(nouveauGroupe);
            } else {
                groupes.get(index).add(b);
            }
        }

        List<Reservation> resultats = new ArrayList<Reservation>();
        for (int i = 0; i < refsVues.size(); i++) {
            resultats.add(new Reservation(refsVues.get(i), groupes.get(i)));
        }
        return resultats;
    }

    /** Renvoie la réservation correspondant à cette référence, ou {@code null} si elle n'existe pas. */
    public Reservation trouverParRef(String refCommande) {
        if (refCommande == null) {
            return null;
        }
        List<Billet> billetsDuGroupe = new ArrayList<Billet>();
        for (Billet b : chargerTousLesBillets()) {
            if (refCommande.equalsIgnoreCase(b.getRefCommande())) {
                billetsDuGroupe.add(b);
            }
        }
        if (billetsDuGroupe.isEmpty()) {
            return null;
        }
        return new Reservation(refCommande, billetsDuGroupe);
    }

    /**
     * Authentifie un client : la référence de commande doit exister ET au
     * moins un passager du groupe doit porter le nom donné.
     */
    public Reservation trouverParRefEtNom(String refCommande, String nom) {
        if (nom == null) {
            return null;
        }
        Reservation res = trouverParRef(refCommande);
        if (res == null) {
            return null;
        }
        for (Billet b : res.getBillets()) {
            if (nom.equalsIgnoreCase(b.getNom())) {
                return res;
            }
        }
        return null;
    }

    /** Renvoie le billet portant cette référence, ou {@code null} s'il n'existe pas. */
    public Billet trouverBilletParRef(String refBillet) {
        if (refBillet == null) {
            return null;
        }
        for (Billet b : chargerTousLesBillets()) {
            if (refBillet.equalsIgnoreCase(b.getRefBillet())) {
                return b;
            }
        }
        return null;
    }

    /** Ajoute un billet à la fin du fichier (une nouvelle réservation en ajoute un par passager). */
    public void ajouterBillet(Billet billet) {
        List<Billet> billets = chargerTousLesBillets();
        billets.add(billet);
        sauvegarderTousLesBillets(billets);
    }

    /** Remplace le billet ayant la même référence de billet, s'il existe. */
    public void mettreAJourBillet(Billet billetModifie) {
        List<Billet> billets = chargerTousLesBillets();
        for (int i = 0; i < billets.size(); i++) {
            if (billets.get(i).getRefBillet().equalsIgnoreCase(billetModifie.getRefBillet())) {
                billets.set(i, billetModifie);
                sauvegarderTousLesBillets(billets);
                return;
            }
        }
        throw new DonneesException("Billet introuvable : " + billetModifie.getRefBillet());
    }

    /**
     * Supprime un billet précis (annulation individuelle). Ne touche pas
     * au fichier des sièges : c'est au service appelant de libérer le
     * siège via DonneesSiege.
     */
    public Billet supprimerBillet(String refBillet) {
        if (refBillet == null) {
            return null;
        }
        List<Billet> billets = chargerTousLesBillets();
        Billet trouve = null;
        for (Billet b : billets) {
            if (refBillet.equalsIgnoreCase(b.getRefBillet())) {
                trouve = b;
                break;
            }
        }
        if (trouve == null) {
            return null;
        }
        billets.remove(trouve);
        sauvegarderTousLesBillets(billets);
        return trouve;
    }

    /** Supprime tous les billets d'une réservation (annulation complète). */
    public List<Billet> supprimerReservation(String refCommande) {
        List<Billet> supprimes = new ArrayList<Billet>();
        if (refCommande == null) {
            return supprimes;
        }
        List<Billet> billets = chargerTousLesBillets();
        List<Billet> conserves = new ArrayList<Billet>();
        for (Billet b : billets) {
            if (refCommande.equalsIgnoreCase(b.getRefCommande())) {
                supprimes.add(b);
            } else {
                conserves.add(b);
            }
        }
        if (!supprimes.isEmpty()) {
            sauvegarderTousLesBillets(conserves);
        }
        return supprimes;
    }

    private Billet parserLigne(String ligne) {
        String[] c = ligne.split(SEPARATEUR, -1);
        if (c.length < NB_CHAMPS) {
            throw new DonneesException("Ligne incomplète (" + c.length + "/" + NB_CHAMPS + " champs) : " + ligne);
        }
        Billet b = new Billet();
        b.setNumeroVol(Champs.texte(c[0]));
        b.setClasse(Champs.texte(c[1]));
        b.setCivilite(Champs.texte(c[2]));
        b.setNom(Champs.texte(c[3]));
        b.setPrenom(Champs.texte(c[4]));
        b.setTelephone(Champs.texte(c[5]));
        b.setEmail(Champs.texte(c[6]));
        b.setRefCommande(Champs.texte(c[7]));
        b.setRefBillet(Champs.texte(c[8]));
        b.setSiege(Champs.texte(c[9]));
        b.setPoidsBagageSupp(Champs.entier(c[10]));
        b.setNumeroCarte(Champs.texte(c[11]));
        b.setExpirationCarte(Champs.texte(c[12]));
        b.setCvv(Champs.texte(c[13]));
        b.setDateVol(Champs.texte(c[14]));

        ResponsableLegal resp = new ResponsableLegal(
                Champs.texte(c[15]), Champs.texte(c[16]), Champs.texte(c[17]), Champs.texte(c[18]));
        b.setResponsableLegal(resp.estVide() ? null : resp);

        return b;
    }

    private String formaterLigne(Billet b) {
        ResponsableLegal r = b.getResponsableLegal();
        String nomDeposant = (r == null) ? Champs.VIDE : Champs.versFichier(r.getNomDeposant());
        String telDeposant = (r == null) ? Champs.VIDE : Champs.versFichier(r.getTelDeposant());
        String nomReceveur = (r == null) ? Champs.VIDE : Champs.versFichier(r.getNomReceveur());
        String telReceveur = (r == null) ? Champs.VIDE : Champs.versFichier(r.getTelReceveur());

        return Champs.versFichier(b.getNumeroVol()) + SEPARATEUR
                + Champs.versFichier(b.getClasse()) + SEPARATEUR
                + Champs.versFichier(b.getCivilite()) + SEPARATEUR
                + Champs.versFichier(b.getNom()) + SEPARATEUR
                + Champs.versFichier(b.getPrenom()) + SEPARATEUR
                + Champs.versFichier(b.getTelephone()) + SEPARATEUR
                + Champs.versFichier(b.getEmail()) + SEPARATEUR
                + Champs.versFichier(b.getRefCommande()) + SEPARATEUR
                + Champs.versFichier(b.getRefBillet()) + SEPARATEUR
                + Champs.versFichier(b.getSiege()) + SEPARATEUR
                + b.getPoidsBagageSupp() + SEPARATEUR
                + Champs.versFichier(b.getNumeroCarte()) + SEPARATEUR
                + Champs.versFichier(b.getExpirationCarte()) + SEPARATEUR
                + Champs.versFichier(b.getCvv()) + SEPARATEUR
                + Champs.versFichier(b.getDateVol()) + SEPARATEUR
                + nomDeposant + SEPARATEUR
                + telDeposant + SEPARATEUR
                + nomReceveur + SEPARATEUR
                + telReceveur;
    }
}
