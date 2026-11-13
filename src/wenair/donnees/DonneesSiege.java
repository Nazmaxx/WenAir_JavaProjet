package wenair.donnees;

import wenair.modele.Siege;
import wenair.utilitaires.Champs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Accès au fichier d'occupation des sièges :
 * numeroVol;date;siege;classe;refCommande;refBillet.
 *
 * Une place est jugée occupée s'il existe une ligne pour le même triplet
 * (numéro de vol, date, numéro de siège). La date est stockée en texte
 * "jj/mm/aaaa".
 */
public class DonneesSiege {

    private static final String SEPARATEUR = ";";
    private static final int NB_CHAMPS = 6;

    private final Path fichier;

    public DonneesSiege(Path fichier) {
        this.fichier = fichier;
    }

    public List<Siege> chargerTous() {
        List<Siege> sieges = new ArrayList<Siege>();
        if (!Files.exists(fichier)) {
            return sieges;
        }
        try {
            List<String> lignes = Files.readAllLines(fichier, StandardCharsets.UTF_8);
            for (int i = 0; i < lignes.size(); i++) {
                String ligne = lignes.get(i);
                if (ligne.isBlank()) {
                    continue;
                }
                try {
                    sieges.add(parserLigne(ligne));
                } catch (RuntimeException e) {
                    System.err.println("[DonneesSiege] Ligne " + (i + 1) + " ignorée : " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new DonneesException("Impossible de lire le fichier des sièges : " + fichier, e);
        }
        return sieges;
    }

    public void sauvegarderTous(List<Siege> sieges) {
        List<String> lignes = new ArrayList<String>();
        for (Siege s : sieges) {
            lignes.add(formaterLigne(s));
        }
        try {
            if (fichier.toAbsolutePath().getParent() != null) {
                Files.createDirectories(fichier.toAbsolutePath().getParent());
            }
            Files.write(fichier, lignes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DonneesException("Impossible d'écrire le fichier des sièges : " + fichier, e);
        }
    }

    public boolean estPris(String numeroVol, String date, String numeroSiege) {
        List<Siege> sieges = chargerTous();
        for (Siege s : sieges) {
            if (s.getNumeroVol().equalsIgnoreCase(numeroVol)
                    && s.getDate().equals(date)
                    && s.getNumeroSiege().equalsIgnoreCase(numeroSiege)) {
                return true;
            }
        }
        return false;
    }

    /** Toutes les places occupées pour un vol à une date donnée (pour dessiner le plan de cabine). */
    public List<Siege> pourVolEtDate(String numeroVol, String date) {
        List<Siege> resultat = new ArrayList<Siege>();
        for (Siege s : chargerTous()) {
            if (s.getNumeroVol().equalsIgnoreCase(numeroVol) && s.getDate().equals(date)) {
                resultat.add(s);
            }
        }
        return resultat;
    }

    /** Marque un siège comme pris (ajout + sauvegarde immédiate du fichier). */
    public void marquerPris(Siege siege) {
        List<Siege> sieges = chargerTous();
        sieges.add(siege);
        sauvegarderTous(sieges);
    }

    /** Libère un siège précis (ex : changement de siège). */
    public void libererParCoordonnees(String numeroVol, String date, String numeroSiege) {
        List<Siege> sieges = chargerTous();
        List<Siege> conservees = new ArrayList<Siege>();
        for (Siege s : sieges) {
            boolean correspond = s.getNumeroVol().equalsIgnoreCase(numeroVol)
                    && s.getDate().equals(date)
                    && s.getNumeroSiege().equalsIgnoreCase(numeroSiege);
            if (!correspond) {
                conservees.add(s);
            }
        }
        sauvegarderTous(conservees);
    }

    /** Libère le(s) siège(s) occupé(s) par un billet précis (utilisé lors d'une annulation). */
    public void libererParBillet(String refBillet) {
        if (refBillet == null) {
            return;
        }
        List<Siege> sieges = chargerTous();
        List<Siege> conservees = new ArrayList<Siege>();
        for (Siege s : sieges) {
            if (!refBillet.equalsIgnoreCase(s.getRefBillet())) {
                conservees.add(s);
            }
        }
        sauvegarderTous(conservees);
    }

    private Siege parserLigne(String ligne) {
        String[] c = ligne.split(SEPARATEUR, -1);
        if (c.length < NB_CHAMPS) {
            throw new DonneesException("Ligne incomplète (" + c.length + "/" + NB_CHAMPS + " champs) : " + ligne);
        }
        Siege s = new Siege();
        s.setNumeroVol(Champs.texte(c[0]));
        s.setDate(Champs.texte(c[1]));
        s.setNumeroSiege(Champs.texte(c[2]));
        s.setClasse(Champs.texte(c[3]));
        s.setRefCommande(Champs.texte(c[4]));
        s.setRefBillet(Champs.texte(c[5]));
        return s;
    }

    private String formaterLigne(Siege s) {
        return Champs.versFichier(s.getNumeroVol()) + SEPARATEUR
                + Champs.versFichier(s.getDate()) + SEPARATEUR
                + Champs.versFichier(s.getNumeroSiege()) + SEPARATEUR
                + Champs.versFichier(s.getClasse()) + SEPARATEUR
                + Champs.versFichier(s.getRefCommande()) + SEPARATEUR
                + Champs.versFichier(s.getRefBillet());
    }
}
