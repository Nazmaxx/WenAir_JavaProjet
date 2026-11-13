package wenair.donnees;

import wenair.modele.Vol;
import wenair.utilitaires.Champs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Accès au fichier des vols (une ligne par vol, champs séparés par ';').
 *
 * Ordre des champs : aéroport dép., terminal dép., ville dép., aéroport
 * arr., terminal arr., ville arr., heure dép., heure arr., numéro de vol,
 * appareil, compagnie, durée, places totales, places business, places
 * extra leg, places economy, places mineur, prix de base.
 */
public class DonneesVol {

    private static final String SEPARATEUR = ";";
    private static final int NB_CHAMPS = 18;

    private final Path fichier;

    public DonneesVol(Path fichier) {
        this.fichier = fichier;
    }

    /** Charge tous les vols du fichier. Les lignes illisibles sont ignorées (et signalées). */
    public List<Vol> chargerTous() {
        List<Vol> vols = new ArrayList<Vol>();
        if (!Files.exists(fichier)) {
            return vols;
        }
        try {
            List<String> lignes = Files.readAllLines(fichier, StandardCharsets.UTF_8);
            for (int i = 0; i < lignes.size(); i++) {
                String ligne = lignes.get(i);
                if (ligne.isBlank()) {
                    continue;
                }
                try {
                    vols.add(parserLigne(ligne));
                } catch (RuntimeException e) {
                    System.err.println("[DonneesVol] Ligne " + (i + 1) + " ignorée (" + fichier.getFileName() + ") : " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new DonneesException("Impossible de lire le fichier des vols : " + fichier, e);
        }
        return vols;
    }

    /** Réécrit entièrement le fichier avec la liste de vols donnée. */
    public void sauvegarderTous(List<Vol> vols) {
        List<String> lignes = new ArrayList<String>();
        for (Vol v : vols) {
            lignes.add(formaterLigne(v));
        }
        try {
            if (fichier.toAbsolutePath().getParent() != null) {
                Files.createDirectories(fichier.toAbsolutePath().getParent());
            }
            Files.write(fichier, lignes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DonneesException("Impossible d'écrire le fichier des vols : " + fichier, e);
        }
    }

    /** Renvoie le vol de ce numéro, ou {@code null} s'il n'existe pas. */
    public Vol trouverParNumero(String numeroVol) {
        if (numeroVol == null) {
            return null;
        }
        List<Vol> vols = chargerTous();
        for (Vol v : vols) {
            if (numeroVol.equalsIgnoreCase(v.getNumeroVol())) {
                return v;
            }
        }
        return null;
    }

    /** Ajoute un nouveau vol (utilisé par l'espace admin). */
    public void ajouter(Vol vol) {
        List<Vol> vols = chargerTous();
        vols.add(vol);
        sauvegarderTous(vols);
    }

    /** Remplace le vol de même numéro par la version modifiée (utilisé par l'espace admin). */
    public void mettreAJour(Vol volModifie) {
        List<Vol> vols = chargerTous();
        for (int i = 0; i < vols.size(); i++) {
            if (vols.get(i).getNumeroVol().equalsIgnoreCase(volModifie.getNumeroVol())) {
                vols.set(i, volModifie);
                sauvegarderTous(vols);
                return;
            }
        }
        throw new DonneesException("Vol introuvable : " + volModifie.getNumeroVol());
    }

    /** Supprime le vol de ce numéro. Renvoie {@code true} s'il a bien été trouvé et supprimé. */
    public boolean supprimer(String numeroVol) {
        List<Vol> vols = chargerTous();
        boolean retire = false;
        for (int i = vols.size() - 1; i >= 0; i--) {
            if (vols.get(i).getNumeroVol().equalsIgnoreCase(numeroVol)) {
                vols.remove(i);
                retire = true;
            }
        }
        if (retire) {
            sauvegarderTous(vols);
        }
        return retire;
    }

    private Vol parserLigne(String ligne) {
        String[] c = ligne.split(SEPARATEUR, -1);
        if (c.length < NB_CHAMPS) {
            throw new DonneesException("Ligne incomplète (" + c.length + "/" + NB_CHAMPS + " champs) : " + ligne);
        }
        Vol v = new Vol();
        v.setDepAeroport(Champs.texte(c[0]));
        v.setDepTerminal(Champs.texte(c[1]));
        v.setDepVille(Champs.texte(c[2]));
        v.setArrAeroport(Champs.texte(c[3]));
        v.setArrTerminal(Champs.texte(c[4]));
        v.setArrVille(Champs.texte(c[5]));
        v.setHeureDepart(Champs.texte(c[6]));
        v.setHeureArrivee(Champs.texte(c[7]));
        v.setNumeroVol(Champs.texte(c[8]));
        v.setAvion(Champs.texte(c[9]));
        v.setCompagnie(Champs.texte(c[10]));
        v.setDuree(Champs.texte(c[11]));
        v.setTotalPlaces(Champs.entier(c[12]));
        v.setPlacesBusiness(Champs.entier(c[13]));
        v.setPlacesExtraLeg(Champs.entier(c[14]));
        v.setPlacesEco(Champs.entier(c[15]));
        v.setPlacesMineur(Champs.entier(c[16]));
        v.setPrixBase(Double.parseDouble(c[17].trim()));
        return v;
    }

    private String formaterLigne(Vol v) {
        return Champs.versFichier(v.getDepAeroport()) + SEPARATEUR
                + Champs.versFichier(v.getDepTerminal()) + SEPARATEUR
                + Champs.versFichier(v.getDepVille()) + SEPARATEUR
                + Champs.versFichier(v.getArrAeroport()) + SEPARATEUR
                + Champs.versFichier(v.getArrTerminal()) + SEPARATEUR
                + Champs.versFichier(v.getArrVille()) + SEPARATEUR
                + Champs.versFichier(v.getHeureDepart()) + SEPARATEUR
                + Champs.versFichier(v.getHeureArrivee()) + SEPARATEUR
                + Champs.versFichier(v.getNumeroVol()) + SEPARATEUR
                + Champs.versFichier(v.getAvion()) + SEPARATEUR
                + Champs.versFichier(v.getCompagnie()) + SEPARATEUR
                + Champs.versFichier(v.getDuree()) + SEPARATEUR
                + v.getTotalPlaces() + SEPARATEUR
                + v.getPlacesBusiness() + SEPARATEUR
                + v.getPlacesExtraLeg() + SEPARATEUR
                + v.getPlacesEco() + SEPARATEUR
                + v.getPlacesMineur() + SEPARATEUR
                + String.format("%.2f", v.getPrixBase());
    }
}
