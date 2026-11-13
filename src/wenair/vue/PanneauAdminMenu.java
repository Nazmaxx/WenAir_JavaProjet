package wenair.vue;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.GridLayout;

/**
 * Menu principal de l'espace administrateur : gestion des vols
 * (ajouter/modifier/supprimer) et consultation (liste des vols,
 * manifeste, vols complets, chiffre d'affaires).
 */
public class PanneauAdminMenu extends JPanel {

    public PanneauAdminMenu(FenetrePrincipale fenetre) {
        setLayout(new BorderLayout());
        setBackground(Couleurs.ROSE_FOND);
        setBorder(BorderFactory.createEmptyBorder(50, 80, 50, 80));

        add(ComposantsUi.creerEnTete("Espace administrateur", fenetre, FenetrePrincipale.ACCUEIL), BorderLayout.NORTH);

        JPanel centre = new JPanel(new GridLayout(2, 1, 0, 30));
        centre.setBackground(Couleurs.ROSE_FOND);

        centre.add(creerSection("Gestion des vols",
                creerBouton("Ajouter un vol", () -> fenetre.afficherPanneau(FenetrePrincipale.ADMIN_AJOUTER_VOL)),
                creerBouton("Modifier un vol", () -> fenetre.afficherPanneau(FenetrePrincipale.ADMIN_MODIFIER_VOL)),
                creerBouton("Supprimer un vol", () -> fenetre.afficherPanneau(FenetrePrincipale.ADMIN_SUPPRIMER_VOL))));

        centre.add(creerSection("Consultation",
                creerBoutonSecondaire("Afficher tous les vols", () -> fenetre.afficherPanneau(FenetrePrincipale.ADMIN_LISTE_VOLS)),
                creerBoutonSecondaire("Manifeste d'un vol", () -> fenetre.afficherPanneau(FenetrePrincipale.ADMIN_MANIFESTE)),
                creerBoutonSecondaire("Vols complets", () -> fenetre.afficherPanneau(FenetrePrincipale.ADMIN_VOLS_COMPLETS)),
                creerBoutonSecondaire("Chiffre d'affaires", () -> fenetre.afficherPanneau(FenetrePrincipale.ADMIN_CHIFFRE_AFFAIRES))));

        add(centre, BorderLayout.CENTER);
    }

    private JPanel creerSection(String titre, JButton... boutons) {
        JPanel section = new JPanel(new BorderLayout(0, 15));
        section.setBackground(Couleurs.ROSE_FOND);

        JLabel labelTitre = new JLabel(titre, SwingConstants.LEFT);
        labelTitre.setFont(Couleurs.POLICE_SOUS_TITRE);
        labelTitre.setForeground(Couleurs.ROSE_FONCE);
        section.add(labelTitre, BorderLayout.NORTH);

        JPanel ligneBoutons = new JPanel(new GridLayout(1, boutons.length, 20, 0));
        ligneBoutons.setBackground(Couleurs.ROSE_FOND);
        for (JButton bouton : boutons) {
            ligneBoutons.add(bouton);
        }
        section.add(ligneBoutons, BorderLayout.CENTER);

        return section;
    }

    private JButton creerBouton(String texte, Runnable action) {
        JButton bouton = ComposantsUi.creerBouton(texte);
        bouton.addActionListener(e -> action.run());
        return bouton;
    }

    private JButton creerBoutonSecondaire(String texte, Runnable action) {
        JButton bouton = ComposantsUi.creerBoutonSecondaire(texte);
        bouton.addActionListener(e -> action.run());
        return bouton;
    }
}
