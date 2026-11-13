package wenair.vue;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

/**
 * Écran d'accueil : logo/texte de bienvenue et choix entre l'espace client
 * et l'espace administrateur.
 */
public class PanneauAccueil extends JPanel {

    public PanneauAccueil(FenetrePrincipale fenetre) {
        setLayout(new BorderLayout());
        setBackground(Couleurs.ROSE_FOND);
        setBorder(BorderFactory.createEmptyBorder(60, 80, 60, 80));

        JLabel titre = new JLabel("Bienvenue chez Wen Air", SwingConstants.CENTER);
        titre.setFont(Couleurs.POLICE_TITRE.deriveFont(30f));
        titre.setForeground(Couleurs.ROSE_FONCE);

        JLabel sousTitre = new JLabel("Le monde est à vous, dès maintenant !", SwingConstants.CENTER);
        sousTitre.setFont(Couleurs.POLICE_NORMALE);
        sousTitre.setForeground(Couleurs.TEXTE_SECONDAIRE);

        JPanel panneauTextes = new JPanel(new GridLayout(2, 1, 0, 10));
        panneauTextes.setBackground(Couleurs.ROSE_FOND);
        panneauTextes.add(titre);
        panneauTextes.add(sousTitre);
        add(panneauTextes, BorderLayout.NORTH);

        JPanel panneauBoutons = new JPanel(new GridLayout(2, 1, 0, 20));
        panneauBoutons.setBackground(Couleurs.ROSE_FOND);
        panneauBoutons.setBorder(BorderFactory.createEmptyBorder(60, 200, 60, 200));

        JButton boutonClient = ComposantsUi.creerBouton("Espace Client");
        boutonClient.setPreferredSize(new Dimension(300, 60));
        boutonClient.addActionListener(e -> fenetre.afficherPanneau(FenetrePrincipale.ESPACE_CLIENT));

        JButton boutonAdmin = ComposantsUi.creerBoutonSecondaire("Espace Administrateur");
        boutonAdmin.setPreferredSize(new Dimension(300, 60));
        boutonAdmin.addActionListener(e -> fenetre.afficherPanneau(FenetrePrincipale.ADMIN_LOGIN));

        panneauBoutons.add(boutonClient);
        panneauBoutons.add(boutonAdmin);
        add(panneauBoutons, BorderLayout.CENTER);
    }
}
