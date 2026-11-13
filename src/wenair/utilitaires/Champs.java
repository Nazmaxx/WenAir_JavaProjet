package wenair.utilitaires;

/**
 * Fonctions utilitaires pour lire/écrire les fichiers texte du projet
 * (champs séparés par ';', valeur absente notée "N/A").
 */
public final class Champs {

    /** Valeur utilisée dans les fichiers texte pour représenter un champ vide. */
    public static final String VIDE = "N/A";

    private Champs() {
        // Classe utilitaire : pas d'instanciation
    }

    /**
     * Convertit un champ brut lu dans un fichier en texte "propre" :
     * "N/A" (insensible à la casse) ou une chaîne vide deviennent
     * {@code null} ; les espaces de début/fin sont retirés.
     */
    public static String texte(String brut) {
        if (brut == null) {
            return null;
        }
        String v = brut.trim();
        if (v.isEmpty() || v.equalsIgnoreCase(VIDE)) {
            return null;
        }
        return v;
    }

    /** Inverse de {@link #texte(String)} : {@code null} ou vide redevient "N/A". */
    public static String versFichier(String valeur) {
        return (valeur == null || valeur.isBlank()) ? VIDE : valeur;
    }

    /** Lit un entier, en tolérant un champ vide ou invalide (retourne alors 0). */
    public static int entier(String brut) {
        String v = texte(brut);
        if (v == null) {
            return 0;
        }
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
