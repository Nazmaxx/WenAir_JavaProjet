package wenair.utilitaires;

/**
 * Fonctions de validation de saisie réutilisées par plusieurs écrans de
 * l'interface graphique (recherche, passagers, paiement...).
 */
public final class Validation {

    private Validation() {
    }

    /** Vrai si le texte n'est composé que de chiffres (et n'est pas vide). */
    public static boolean estNumerique(String texte) {
        if (texte == null || texte.isEmpty()) {
            return false;
        }
        for (int i = 0; i < texte.length(); i++) {
            char c = texte.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    /** Vrai si le texte respecte le format jj/mm/aaaa avec une date plausible. */
    public static boolean estDateValide(String texte) {
        if (texte == null || texte.length() != 10) {
            return false;
        }
        if (texte.charAt(2) != '/' || texte.charAt(5) != '/') {
            return false;
        }
        String jourStr = texte.substring(0, 2);
        String moisStr = texte.substring(3, 5);
        String anneeStr = texte.substring(6, 10);
        if (!estNumerique(jourStr) || !estNumerique(moisStr) || !estNumerique(anneeStr)) {
            return false;
        }
        int jour = Integer.parseInt(jourStr);
        int mois = Integer.parseInt(moisStr);
        return jour >= 1 && jour <= 31 && mois >= 1 && mois <= 12;
    }

    /** Vérification très simple d'une adresse email : présence d'un @ et d'un point après. */
    public static boolean estEmailValide(String texte) {
        if (texte == null || texte.isEmpty()) {
            return false;
        }
        int posArobase = texte.indexOf('@');
        if (posArobase <= 0 || posArobase == texte.length() - 1) {
            return false;
        }
        String apresArobase = texte.substring(posArobase + 1);
        return apresArobase.indexOf('.') > 0;
    }

    /** Vrai si le texte ne contient pas de point-virgule (protège le format de sauvegarde). */
    public static boolean sansPointVirgule(String texte) {
        return texte == null || texte.indexOf(';') < 0;
    }

    /** Vrai si le numéro de carte comporte exactement 10 chiffres. */
    public static boolean estCarteValide(String texte) {
        return texte != null && texte.length() == 10 && estNumerique(texte);
    }

    /** Vrai si l'expiration est au format MM/AA, mois 1-12, année entre 26 et 45. */
    public static boolean estExpirationValide(String texte) {
        if (texte == null || texte.length() != 5 || texte.charAt(2) != '/') {
            return false;
        }
        String moisStr = texte.substring(0, 2);
        String anneeStr = texte.substring(3, 5);
        if (!estNumerique(moisStr) || !estNumerique(anneeStr)) {
            return false;
        }
        int mois = Integer.parseInt(moisStr);
        int annee = Integer.parseInt(anneeStr);
        return mois >= 1 && mois <= 12 && annee >= 26 && annee <= 45;
    }

    /** Vrai si le CVV comporte exactement 3 chiffres. */
    public static boolean estCvvValide(String texte) {
        return texte != null && texte.length() == 3 && estNumerique(texte);
    }

    /** Vrai si le champ n'est ni nul ni vide une fois les espaces retirés. */
    public static boolean estRempli(String texte) {
        return texte != null && !texte.trim().isEmpty();
    }

    /**
     * Vrai si le texte respecte le format HH:MM avec une heure plausible
     * (0-23) et des minutes plausibles (0-59).
     */
    public static boolean estHeureValide(String texte) {
        if (texte == null || texte.length() != 5 || texte.charAt(2) != ':') {
            return false;
        }
        String heureStr = texte.substring(0, 2);
        String minuteStr = texte.substring(3, 5);
        if (!estNumerique(heureStr) || !estNumerique(minuteStr)) {
            return false;
        }
        int heure = Integer.parseInt(heureStr);
        int minute = Integer.parseInt(minuteStr);
        return heure >= 0 && heure <= 23 && minute >= 0 && minute <= 59;
    }

    /**
     * Vrai si le texte respecte le format d'un numéro de vol Wen Air :
     * "WN" suivi de 4 chiffres (ex : WN7300).
     */
    public static boolean estNumeroVolValide(String texte) {
        if (texte == null || texte.length() != 6) {
            return false;
        }
        String prefixe = texte.substring(0, 2).toUpperCase();
        String chiffres = texte.substring(2, 6);
        return prefixe.equals("WN") && estNumerique(chiffres);
    }

    /**
     * Calcule l'âge (en années complètes) à partir d'une date de naissance
     * au format jj/mm/aaaa. Retourne -1 si le format n'est pas valide.
     */
    public static int calculerAge(String dateNaissance) {
        if (!estDateValide(dateNaissance)) {
            return -1;
        }
        int jourNaissance = Integer.parseInt(dateNaissance.substring(0, 2));
        int moisNaissance = Integer.parseInt(dateNaissance.substring(3, 5));
        int anneeNaissance = Integer.parseInt(dateNaissance.substring(6, 10));

        java.util.Calendar aujourdhui = java.util.Calendar.getInstance();
        int jourAujourdhui = aujourdhui.get(java.util.Calendar.DAY_OF_MONTH);
        int moisAujourdhui = aujourdhui.get(java.util.Calendar.MONTH) + 1;
        int anneeAujourdhui = aujourdhui.get(java.util.Calendar.YEAR);

        int age = anneeAujourdhui - anneeNaissance;
        if (moisAujourdhui < moisNaissance
                || (moisAujourdhui == moisNaissance && jourAujourdhui < jourNaissance)) {
            age--;
        }
        return age;
    }

    /**
     * Vrai si la date (format jj/mm/aaaa) correspond à aujourd'hui ou à
     * une date future. Retourne false si le format n'est pas valide.
     */
    public static boolean estDateNonPassee(String texte) {
        if (!estDateValide(texte)) {
            return false;
        }
        int jour = Integer.parseInt(texte.substring(0, 2));
        int mois = Integer.parseInt(texte.substring(3, 5));
        int annee = Integer.parseInt(texte.substring(6, 10));

        java.util.Calendar aujourdhui = java.util.Calendar.getInstance();
        int jourAujourdhui = aujourdhui.get(java.util.Calendar.DAY_OF_MONTH);
        int moisAujourdhui = aujourdhui.get(java.util.Calendar.MONTH) + 1;
        int anneeAujourdhui = aujourdhui.get(java.util.Calendar.YEAR);

        int valeurDate = annee * 10000 + mois * 100 + jour;
        int valeurAujourdhui = anneeAujourdhui * 10000 + moisAujourdhui * 100 + jourAujourdhui;
        return valeurDate >= valeurAujourdhui;
    }
}
