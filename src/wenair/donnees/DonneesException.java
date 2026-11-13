package wenair.donnees;

/**
 * Erreur de lecture/écriture d'un fichier de données. Non vérifiée pour
 * garder les couches service/IHM simples à écrire ; le message explique
 * toujours ce qui a échoué et pourquoi.
 */
public class DonneesException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DonneesException(String message, Throwable cause) {
        super(message, cause);
    }

    public DonneesException(String message) {
        super(message);
    }
}
