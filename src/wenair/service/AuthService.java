package wenair.service;

/**
 * Authentification administrateur : mot de passe unique en dur dans le
 * code. Simplification volontaire pour un projet scolaire, pas une
 * vraie sécurité.
 */
public class AuthService {

    private static final String MOT_DE_PASSE_ADMIN = "wenair2026";

    /** Nombre de tentatives autorisées avant blocage. */
    public static final int TENTATIVES_MAX = 3;

    public boolean verifierMotDePasseAdmin(String saisie) {
        return MOT_DE_PASSE_ADMIN.equals(saisie);
    }
}
