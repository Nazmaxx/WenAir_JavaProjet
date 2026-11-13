package wenair.vue;

import wenair.modele.Billet;
import wenair.modele.OptionBagage;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;

/**
 * Ajoute un bagage supplementaire sur un billet existant, apres la
 * reservation, accessible depuis "Consulter ma réservation". Les tarifs
 * dependent de la classe du billet et les bebes n'ont pas d'option de bagage.
 */
public class PanneauAjoutBagage extends JPanel {

    private final FenetrePrincipale fenetre;
    private final JPanel contenu = new JPanel();

    public PanneauAjoutBagage(FenetrePrincipale fenetre) {
        this.fenetre = fenetre;

        setLayout(new BorderLayout());
        setBackground(Couleurs.ROSE_FOND);
        setBorder(BorderFactory.createEmptyBorder(40, 70, 30, 70));

        contenu.setLayout(new BoxLayout(contenu, BoxLayout.Y_AXIS));
        contenu.setBackground(Couleurs.ROSE_FOND);
        contenu.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(ComposantsUi.creerEnTete("Ajouter un bagage", fenetre, () -> fenetre.revenirVersConsultation()), BorderLayout.NORTH);
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

        if (billet.estBebe()) {
            contenu.add(creerMessage("Billet : " + billet.getRefBillet() + "  -  " + billet.getNom() + " " + billet.getPrenom()));
            contenu.add(Box.createVerticalStrut(10));
            contenu.add(creerMessage("Les bebes n'ont pas d'option de bagage supplementaire."));
            contenu.revalidate();
            contenu.repaint();
            return;
        }

        int bagageInclus = fenetre.getTarifService().bagageInclusKg(billet.getClasse());

        contenu.add(creerMessage("Billet : " + billet.getRefBillet()
                + "     Passager : " + billet.getNom() + " " + billet.getPrenom()
                + "     Classe : " + billet.getClasse()));
        contenu.add(Box.createVerticalStrut(6));

        String texteActuel = "Bagage inclus : " + bagageInclus + "kg";
        if (billet.getPoidsBagageSupp() > 0) {
            texteActuel += "   +   " + billet.getPoidsBagageSupp() + "kg déjà ajoutés";
        }
        contenu.add(creerMessage(texteActuel));
        contenu.add(Box.createVerticalStrut(24));

        JLabel labelOptions = ComposantsUi.creerLabelChamp("Options disponibles :");
        labelOptions.setFont(Couleurs.POLICE_SOUS_TITRE);
        labelOptions.setForeground(Couleurs.ROSE_FONCE);
        labelOptions.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenu.add(labelOptions);
        contenu.add(Box.createVerticalStrut(10));

        for (OptionBagage option : fenetre.getTarifService().optionsBagageSupplementaire(billet.getClasse())) {
            JButton bouton = ComposantsUi.creerBouton(option.toString());
            bouton.setAlignmentX(Component.LEFT_ALIGNMENT);
            bouton.addActionListener(e -> confirmerAjout(billet, option));
            contenu.add(bouton);
            contenu.add(Box.createVerticalStrut(8));
        }

        contenu.revalidate();
        contenu.repaint();
    }

    private JLabel creerMessage(String texte) {
        JLabel label = ComposantsUi.creerLabelChamp(texte);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void confirmerAjout(Billet billet, OptionBagage option) {
        String message = "Ajouter " + option.getPoidsKg() + "kg pour "
                + String.format("%.2f", option.getPrix()) + " EUR ?";
        if (!ComposantsUi.demanderConfirmation(this, message)) {
            return;
        }
        Billet miseAJour = fenetre.getReservationService().ajouterBagage(billet.getRefBillet(), option.getPoidsKg());
        ComposantsUi.afficherInfo(this, "Bagage ajouté avec succès !\n\n"
                + "Nouveau quota supplementaire : " + miseAJour.getPoidsBagageSupp() + "kg\n"
                + "Montant à régler : " + String.format("%.2f", option.getPrix()) + " EUR\n"
                + "Debit sur la carte enregistree, sous 24-48h.");
        fenetre.revenirVersConsultation();
    }
}
