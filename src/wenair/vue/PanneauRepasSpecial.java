package wenair.vue;

import wenair.modele.Billet;
import wenair.modele.OptionRepas;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import java.awt.BorderLayout;
import java.awt.Component;

/**
 * Demande de repas special a posteriori sur un billet existant, accessible
 * depuis "Consulter ma réservation". Service gratuit, a demander au moins
 * 24h avant le vol. Rien n'est persiste : une simple reference de demande
 * ({@code RS-<ref billet>-<numero repas>}) est generee et affichee a l'ecran.
 */
public class PanneauRepasSpecial extends JPanel {

    private final FenetrePrincipale fenetre;
    private final JPanel contenu = new JPanel();

    public PanneauRepasSpecial(FenetrePrincipale fenetre) {
        this.fenetre = fenetre;

        setLayout(new BorderLayout());
        setBackground(Couleurs.ROSE_FOND);
        setBorder(BorderFactory.createEmptyBorder(40, 70, 30, 70));

        contenu.setLayout(new BoxLayout(contenu, BoxLayout.Y_AXIS));
        contenu.setBackground(Couleurs.ROSE_FOND);
        contenu.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(ComposantsUi.creerEnTete("Repas spécial", fenetre, () -> fenetre.revenirVersConsultation()), BorderLayout.NORTH);

        JScrollPane defilement = new JScrollPane(contenu);
        defilement.setBorder(BorderFactory.createEmptyBorder());
        defilement.getViewport().setBackground(Couleurs.ROSE_FOND);
        defilement.getVerticalScrollBar().setUnitIncrement(16);
        add(defilement, BorderLayout.CENTER);
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
        contenu.add(Box.createVerticalStrut(6));
        contenu.add(creerMessage("Service gratuit - à demander au moins 24h avant le vol."));
        contenu.add(Box.createVerticalStrut(24));

        JLabel labelOptions = ComposantsUi.creerLabelChamp("Types de repas disponibles :");
        labelOptions.setFont(Couleurs.POLICE_SOUS_TITRE);
        labelOptions.setForeground(Couleurs.ROSE_FONCE);
        labelOptions.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenu.add(labelOptions);
        contenu.add(Box.createVerticalStrut(10));

        for (int i = 0; i < OptionRepas.TOUS.length; i++) {
            OptionRepas option = OptionRepas.TOUS[i];

            // Separation visuelle avant la derniere option ("Autre")
            if (i == OptionRepas.TOUS.length - 1) {
                contenu.add(Box.createVerticalStrut(8));
                JSeparator separateur = new JSeparator();
                separateur.setAlignmentX(Component.LEFT_ALIGNMENT);
                separateur.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 2));
                contenu.add(separateur);
                contenu.add(Box.createVerticalStrut(14));
            }

            JButton bouton = ComposantsUi.creerBouton(option.toString());
            bouton.setAlignmentX(Component.LEFT_ALIGNMENT);
            bouton.addActionListener(e -> confirmerDemande(billet, option));
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

    private void confirmerDemande(Billet billet, OptionRepas option) {
        // Pour un bebe ou un mineur sans email propre, on utilise l'email
        // de contact du dossier.
        String email = fenetre.getReservationService().trouverEmailContact(billet);
        if (email == null) {
            email = "N/A";
        }
        String message = "Repas demandé : " + option + "\n"
                + "Service : GRATUIT\n\n"
                + "Important : la demande doit être effectuée au moins 24h avant le vol.\n"
                + "Une notification sera envoyée à : " + email + "\n\n"
                + "Confirmer la demande ?";
        if (!ComposantsUi.demanderConfirmation(this, message)) {
            return;
        }
        String reference = "RS-" + billet.getRefBillet() + "-" + option.getNumero();
        ComposantsUi.afficherInfo(this, "Demande enregistrée avec succès !\n\n"
                + "Type : " + option + "\n"
                + "Référence : " + reference + "\n\n"
                + "Un email de confirmation vous sera envoyé sous 48h.");
        fenetre.revenirVersConsultation();
    }
}
