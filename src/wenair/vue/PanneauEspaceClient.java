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
 * Espace client : choix entre rechercher un vol (nouvelle réservation) ou
 * consulter/gérer une réservation existante.
 */
public class PanneauEspaceClient extends JPanel {

    public PanneauEspaceClient(FenetrePrincipale fenetre) {
        setLayout(new BorderLayout());
        setBackground(Couleurs.ROSE_FOND);
        setBorder(BorderFactory.createEmptyBorder(50, 80, 50, 80));

        add(ComposantsUi.creerEnTete("Espace Client", fenetre, FenetrePrincipale.ACCUEIL), BorderLayout.NORTH);

        JPanel panneauBoutons = new JPanel(new GridLayout(2, 1, 0, 20));
        panneauBoutons.setBackground(Couleurs.ROSE_FOND);
        panneauBoutons.setBorder(BorderFactory.createEmptyBorder(60, 180, 60, 180));

        JButton boutonRecherche = ComposantsUi.creerBouton("Rechercher un vol");
        boutonRecherche.setPreferredSize(new Dimension(300, 60));
        boutonRecherche.addActionListener(e -> fenetre.afficherPanneau(FenetrePrincipale.RECHERCHE));

        JButton boutonConsultation = ComposantsUi.creerBoutonSecondaire("Consulter ma réservation");
        boutonConsultation.setPreferredSize(new Dimension(300, 60));
        boutonConsultation.addActionListener(e -> fenetre.afficherPanneau(FenetrePrincipale.CONSULTATION));

        panneauBoutons.add(boutonRecherche);
        panneauBoutons.add(boutonConsultation);

        JPanel conteneur = new JPanel(new BorderLayout());
        conteneur.setBackground(Couleurs.ROSE_FOND);
        conteneur.add(panneauBoutons, BorderLayout.CENTER);
        add(conteneur, BorderLayout.CENTER);
    }
}
