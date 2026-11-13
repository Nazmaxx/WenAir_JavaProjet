package wenair.vue;

import java.awt.Color;
import java.awt.Font;

/**
 * Couleurs et polices utilisées dans toute l'application Wen Air : fond
 * blanc, et le rose réservé aux accents (boutons, titres, bordures de
 * carte, éléments actifs).
 */
public final class Couleurs {

    private Couleurs() {
    }

    /** Rose principal (bordure des champs de saisie). */
    public static final Color ROSE_PRINCIPAL = new Color(240, 140, 170);
    /** Rose soutenu, pour les boutons, titres et éléments actifs. */
    public static final Color ROSE_FONCE = new Color(220, 100, 140);
    /** Fond des écrans : blanc pur, pour un rendu épuré. */
    public static final Color ROSE_FOND = new Color(255, 255, 255);
    /** Fond des panneaux/cartes : blanc à peine teinté de rose, différencié du fond grâce à BORDURE_CARTE. */
    public static final Color ROSE_PANNEAU = new Color(255, 248, 250);
    /** Bordure fine rose pâle, pour délimiter une carte/un panneau sur fond blanc. */
    public static final Color BORDURE_CARTE = new Color(240, 210, 222);
    /** Blanc pur, pour les zones de saisie. */
    public static final Color BLANC_SAISIE = new Color(255, 255, 255);
    /** Vert doux, pour "disponible" / succès. */
    public static final Color VERT_DISPONIBLE = new Color(160, 210, 160);
    /** Rouge/rose foncé doux, pour "indisponible" / erreur. */
    public static final Color ROUGE_INDISPONIBLE = new Color(220, 120, 130);
    /** Texte principal (gris très foncé, pas noir pur). */
    public static final Color TEXTE = new Color(70, 45, 55);
    /** Texte secondaire, plus discret. */
    public static final Color TEXTE_SECONDAIRE = new Color(140, 100, 115);
    /** Blanc pour le texte sur fond rose foncé. */
    public static final Color BLANC = Color.WHITE;

    public static final Font POLICE_TITRE = new Font("SansSerif", Font.BOLD, 22);
    public static final Font POLICE_SOUS_TITRE = new Font("SansSerif", Font.BOLD, 16);
    public static final Font POLICE_NORMALE = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font POLICE_PETITE = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font POLICE_BOUTON = new Font("SansSerif", Font.BOLD, 14);
    public static final Font POLICE_SIEGE = new Font("SansSerif", Font.BOLD, 11);
}
