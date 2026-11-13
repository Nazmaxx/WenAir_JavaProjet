package wenair.vue;

import wenair.modele.Billet;
import wenair.modele.Vol;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;

/**
 * Surclassement d'un billet existant, accessible depuis "Consulter ma
 * reservation". Pas de surclassement pour un mineur non accompagne ni
 * pour un bebe directement. EXTRA LEG ne peut viser que BUSINESS,
 * ECONOMY peut viser EXTRA LEG et/ou BUSINESS selon les places du vol.
 * Le siege actuel est libere : le passager devra en choisir un nouveau.
 */
public class PanneauSurclassement extends JPanel {

    private final FenetrePrincipale fenetre;
    private final JPanel contenu = new JPanel();

    public PanneauSurclassement(FenetrePrincipale fenetre) {
        this.fenetre = fenetre;

        setLayout(new BorderLayout());
        setBackground(Couleurs.ROSE_FOND);
        setBorder(BorderFactory.createEmptyBorder(40, 70, 30, 70));

        contenu.setLayout(new BoxLayout(contenu, BoxLayout.Y_AXIS));
        contenu.setBackground(Couleurs.ROSE_FOND);
        contenu.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(ComposantsUi.creerEnTete("Surclasser ce billet", fenetre, () -> fenetre.revenirVersConsultation()), BorderLayout.NORTH);
        add(contenu, BorderLayout.CENTER);
    }

    public void rafraichir() {
        contenu.removeAll();

        String refBillet = fenetre.getRefBilletSelectionne();
        Billet billet = fenetre.getReservationService().trouverBillet(refBillet);

        if (billet == null) {
            contenu.add(creerMessage("Billet introuvable."));
            terminer();
            return;
        }

        contenu.add(creerMessage("Billet : " + billet.getRefBillet()
                + "     Passager : " + billet.getNom() + " " + billet.getPrenom()
                + "     Classe actuelle : " + billet.getClasse()));
        contenu.add(Box.createVerticalStrut(20));

        if (billet.estMineurNonAccompagne()) {
            contenu.add(creerMessage("Le surclassement n'est pas possible pour les mineurs non accompagnes (service UM)."));
            terminer();
            return;
        }
        if (billet.estBebe()) {
            contenu.add(creerMessage("Un bebe est surclasse automatiquement avec son adulte responsable : cette action n'est pas disponible directement sur son billet."));
            terminer();
            return;
        }
        if ("BUSINESS".equalsIgnoreCase(billet.getClasse())) {
            contenu.add(creerMessage("Ce billet est déjà en classe BUSINESS, la classe la plus élevée."));
            terminer();
            return;
        }

        Vol vol = fenetre.getVolService().trouverParNumero(billet.getNumeroVol());
        if (vol == null) {
            contenu.add(creerMessage("Vol introuvable : le surclassement n'est pas disponible."));
            terminer();
            return;
        }

        java.util.List<String> classesCibles = new java.util.ArrayList<String>();
        if ("ECONOMY".equalsIgnoreCase(billet.getClasse())) {
            if (vol.getPlacesExtraLeg() > 0) {
                classesCibles.add("EXTRA LEG");
            }
            if (vol.getPlacesBusiness() > 0) {
                classesCibles.add("BUSINESS");
            }
        } else if ("EXTRA LEG".equalsIgnoreCase(billet.getClasse())) {
            if (vol.getPlacesBusiness() > 0) {
                classesCibles.add("BUSINESS");
            }
        }

        if (classesCibles.isEmpty()) {
            contenu.add(creerMessage("Aucun surclassement n'est disponible sur ce vol."));
            terminer();
            return;
        }

        JLabel labelOptions = ComposantsUi.creerLabelChamp("Offres de surclassement disponibles :");
        labelOptions.setFont(Couleurs.POLICE_SOUS_TITRE);
        labelOptions.setForeground(Couleurs.ROSE_FONCE);
        labelOptions.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenu.add(labelOptions);
        contenu.add(Box.createVerticalStrut(10));

        double prixBase = vol.getPrixBase();
        double tarifActuel = fenetre.getTarifService().tarifActuelEstime(prixBase, billet.getClasse());

        for (String classeCible : classesCibles) {
            double tarifCible = fenetre.getTarifService().tarifCibleSurclassement(prixBase, classeCible);
            double supplement = Math.max(0.0, tarifCible - tarifActuel);

            JButton bouton = ComposantsUi.creerBouton("Passer en " + classeCible
                    + "   (+" + String.format("%.2f", supplement) + " EUR)");
            bouton.setAlignmentX(Component.LEFT_ALIGNMENT);
            bouton.addActionListener(e -> confirmerSurclassement(billet, classeCible, tarifActuel, tarifCible, supplement));
            contenu.add(bouton);
            contenu.add(Box.createVerticalStrut(8));
        }

        contenu.add(Box.createVerticalStrut(16));
        contenu.add(creerMessage("[i] Le siège actuel sera libéré : un nouveau siège devra être choisi après le surclassement."));

        terminer();
    }

    private void terminer() {
        contenu.revalidate();
        contenu.repaint();
    }

    private JLabel creerMessage(String texte) {
        JLabel label = ComposantsUi.creerLabelChamp(texte);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void confirmerSurclassement(Billet billet, String classeCible, double tarifActuel, double tarifCible, double supplement) {
        String message = "Passager : " + billet.getNom() + " " + billet.getPrenom() + "\n"
                + "Tarif actuel (" + billet.getClasse() + ") : " + String.format("%.2f", tarifActuel) + " EUR\n"
                + "Nouveau tarif (" + classeCible + ") : " + String.format("%.2f", tarifCible) + " EUR\n"
                + "Supplément à régler : " + String.format("%.2f", supplement) + " EUR\n\n"
                + "Confirmer le surclassement ?";
        if (!ComposantsUi.demanderConfirmation(this, message)) {
            return;
        }
        fenetre.getReservationService().surclasserBillet(billet.getRefBillet(), classeCible);
        ComposantsUi.afficherInfo(this, "Surclasse en " + classeCible + " avec succès !\n\n"
                + "Montant à régler : " + String.format("%.2f", supplement) + " EUR (debit sur la carte enregistree, sous 24-48h).\n"
                + "Le siège précédent a été libéré : pensez à en choisir un nouveau.");
        fenetre.revenirVersConsultation();
    }
}
