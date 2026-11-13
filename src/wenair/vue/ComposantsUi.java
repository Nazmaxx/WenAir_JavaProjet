package wenair.vue;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/**
 * Méthodes pour créer les composants Swing utilisés dans tous les écrans,
 * pour garder le même style (couleurs, polices, bordures) sans le
 * recopier dans chaque panneau.
 */
public final class ComposantsUi {

    private ComposantsUi() {
    }

    /** Bouton principal : fond rose soutenu, texte blanc, contour visible. */
    public static JButton creerBouton(String texte) {
        JButton bouton = new JButton(texte);
        bouton.setFont(Couleurs.POLICE_BOUTON);
        bouton.setBackground(Couleurs.ROSE_FONCE);
        bouton.setForeground(Couleurs.BLANC);
        bouton.setFocusPainted(false);
        // setOpaque/setContentAreaFilled : pour que la couleur de fond
        // s'affiche bien peu importe le theme graphique du systeme.
        bouton.setOpaque(true);
        bouton.setContentAreaFilled(true);
        bouton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Couleurs.ROSE_FONCE.darker(), 1),
                BorderFactory.createEmptyBorder(10, 18, 10, 18)));
        return bouton;
    }

    /** Bouton secondaire : fond clair, texte rose fonce, contour visible, pour les actions moins prioritaires. */
    public static JButton creerBoutonSecondaire(String texte) {
        JButton bouton = new JButton(texte);
        bouton.setFont(Couleurs.POLICE_BOUTON);
        bouton.setBackground(Couleurs.ROSE_PANNEAU);
        bouton.setForeground(Couleurs.ROSE_FONCE);
        bouton.setFocusPainted(false);
        bouton.setOpaque(true);
        bouton.setContentAreaFilled(true);
        bouton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Couleurs.ROSE_FONCE, 1),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        return bouton;
    }

    /** Champ de saisie texte avec un style cohérent. */
    public static JTextField creerChampTexte(int colonnes) {
        JTextField champ = new JTextField(colonnes);
        champ.setFont(Couleurs.POLICE_NORMALE);
        champ.setBackground(Couleurs.BLANC_SAISIE);
        champ.setForeground(Couleurs.TEXTE);
        champ.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Couleurs.ROSE_PRINCIPAL, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        return champ;
    }

    /** Champ de saisie de mot de passe (texte masqué), même style que creerChampTexte. */
    public static JPasswordField creerChampMotDePasse(int colonnes) {
        JPasswordField champ = new JPasswordField(colonnes);
        champ.setFont(Couleurs.POLICE_NORMALE);
        champ.setBackground(Couleurs.BLANC_SAISIE);
        champ.setForeground(Couleurs.TEXTE);
        champ.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Couleurs.ROSE_PRINCIPAL, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        return champ;
    }

    /** Libellé de champ de formulaire. */
    public static JLabel creerLabelChamp(String texte) {
        JLabel label = new JLabel(texte);
        label.setFont(Couleurs.POLICE_NORMALE);
        label.setForeground(Couleurs.TEXTE);
        return label;
    }

    /** Petit texte d'aide ou de message d'erreur de validation, sous un champ. */
    public static JLabel creerLabelAide() {
        JLabel label = new JLabel(" ");
        label.setFont(Couleurs.POLICE_PETITE);
        label.setForeground(Couleurs.ROUGE_INDISPONIBLE);
        return label;
    }

    /** Titre d'écran avec un bouton "Retour" vers l'écran indiqué. */
    public static JPanel creerEnTete(String titre, FenetrePrincipale fenetre, String panneauRetour) {
        JPanel enTete = new JPanel(new BorderLayout(20, 0));
        enTete.setBackground(Couleurs.ROSE_FOND);

        JLabel labelTitre = new JLabel(titre);
        labelTitre.setFont(Couleurs.POLICE_TITRE);
        labelTitre.setForeground(Couleurs.ROSE_FONCE);
        enTete.add(labelTitre, BorderLayout.CENTER);

        if (panneauRetour != null) {
            JButton boutonRetour = creerBoutonSecondaire("< Retour");
            boutonRetour.addActionListener(e -> fenetre.afficherPanneau(panneauRetour));
            JPanel panneauRetourConteneur = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            panneauRetourConteneur.setBackground(Couleurs.ROSE_FOND);
            panneauRetourConteneur.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
            panneauRetourConteneur.add(boutonRetour);
            enTete.add(panneauRetourConteneur, BorderLayout.WEST);
        }

        enTete.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        return enTete;
    }

    /** Titre d'ecran avec un bouton "Retour" qui execute une action personnalisee (ex : rafraichir un ecran parent sans le reinitialiser completement). */
    public static JPanel creerEnTete(String titre, FenetrePrincipale fenetre, Runnable actionRetour) {
        JPanel enTete = new JPanel(new BorderLayout(20, 0));
        enTete.setBackground(Couleurs.ROSE_FOND);

        JLabel labelTitre = new JLabel(titre);
        labelTitre.setFont(Couleurs.POLICE_TITRE);
        labelTitre.setForeground(Couleurs.ROSE_FONCE);
        enTete.add(labelTitre, BorderLayout.CENTER);

        if (actionRetour != null) {
            JButton boutonRetour = creerBoutonSecondaire("< Retour");
            boutonRetour.addActionListener(e -> actionRetour.run());
            JPanel panneauRetourConteneur = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            panneauRetourConteneur.setBackground(Couleurs.ROSE_FOND);
            panneauRetourConteneur.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
            panneauRetourConteneur.add(boutonRetour);
            enTete.add(panneauRetourConteneur, BorderLayout.WEST);
        }

        enTete.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        return enTete;
    }

    public static void afficherInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void afficherErreur(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean demanderConfirmation(Component parent, String message) {
        int reponse = JOptionPane.showConfirmDialog(parent, message, "Confirmation", JOptionPane.YES_NO_OPTION);
        return reponse == JOptionPane.YES_OPTION;
    }
}
