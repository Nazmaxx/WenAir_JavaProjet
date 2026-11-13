package wenair.vue;

import wenair.modele.Billet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;

/**
 * Sous-menu "Options du billet", accessible depuis "Consulter ma
 * reservation" : regroupe les services qu'on peut demander sur un billet
 * existant (repas special, bagage supplementaire).
 */
public class PanneauOptionsBillet extends JPanel {

    private final FenetrePrincipale fenetre;
    private final JPanel contenu = new JPanel();

    public PanneauOptionsBillet(FenetrePrincipale fenetre) {
        this.fenetre = fenetre;

        setLayout(new BorderLayout());
        setBackground(Couleurs.ROSE_FOND);
        setBorder(BorderFactory.createEmptyBorder(40, 70, 30, 70));

        contenu.setLayout(new BoxLayout(contenu, BoxLayout.Y_AXIS));
        contenu.setBackground(Couleurs.ROSE_FOND);
        contenu.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(ComposantsUi.creerEnTete("Options du billet", fenetre, () -> fenetre.revenirVersConsultation()), BorderLayout.NORTH);
        add(contenu, BorderLayout.CENTER);
    }

    public void rafraichir() {
        contenu.removeAll();

        String refBillet = fenetre.getRefBilletSelectionne();
        Billet billet = fenetre.getReservationService().trouverBillet(refBillet);

        if (billet == null) {
            contenu.add(creerMessage("Billet introuvable."));
            contenu.revalidate();
            contenu.repaint();
            return;
        }

        contenu.add(creerMessage("Billet : " + billet.getRefBillet()
                + "     Passager : " + billet.getNom() + " " + billet.getPrenom()
                + "     Vol : " + billet.getNumeroVol()));
        contenu.add(Box.createVerticalStrut(24));

        JLabel labelOptions = ComposantsUi.creerLabelChamp("Que souhaitez-vous demander pour ce billet ?");
        labelOptions.setFont(Couleurs.POLICE_SOUS_TITRE);
        labelOptions.setForeground(Couleurs.ROSE_FONCE);
        labelOptions.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenu.add(labelOptions);
        contenu.add(Box.createVerticalStrut(10));

        JButton boutonRepas = ComposantsUi.creerBouton("Repas spécial");
        boutonRepas.setAlignmentX(Component.LEFT_ALIGNMENT);
        boutonRepas.addActionListener(e -> fenetre.afficherPanneau(FenetrePrincipale.REPAS_SPECIAL));
        contenu.add(boutonRepas);
        contenu.add(Box.createVerticalStrut(8));

        JButton boutonBagage = ComposantsUi.creerBouton("Ajouter un bagage");
        boutonBagage.setAlignmentX(Component.LEFT_ALIGNMENT);
        boutonBagage.addActionListener(e -> fenetre.afficherPanneau(FenetrePrincipale.BAGAGE));
        contenu.add(boutonBagage);
        contenu.add(Box.createVerticalStrut(8));

        contenu.revalidate();
        contenu.repaint();
    }

    private JLabel creerMessage(String texte) {
        JLabel label = ComposantsUi.creerLabelChamp(texte);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
}
