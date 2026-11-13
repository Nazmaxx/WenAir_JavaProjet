package wenair.vue;

import wenair.modele.Billet;
import wenair.modele.Vol;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Offre de surclassement groupée, proposée après la saisie des passagers
 * et avant le choix des sièges. Comme la réservation n'est pas encore payée,
 * surclasser remplace simplement le tarif du billet par celui de la
 * nouvelle classe : tout est inclus dans le paiement final.
 *
 * Pas de surclassement pour un mineur non accompagné ni pour un bébé
 * directement (un bébé voyageant avec un adulte surclassé profite de la
 * même cabine gratuitement). Écran sauté automatiquement si aucun billet
 * n'est éligible.
 */
public class PanneauSurclassementInitial extends JPanel {

    private final FenetrePrincipale fenetre;
    private final JPanel contenu = new JPanel();

    public PanneauSurclassementInitial(FenetrePrincipale fenetre) {
        this.fenetre = fenetre;

        setLayout(new BorderLayout());
        setBackground(Couleurs.ROSE_FOND);
        setBorder(BorderFactory.createEmptyBorder(40, 70, 30, 70));

        contenu.setLayout(new BoxLayout(contenu, BoxLayout.Y_AXIS));
        contenu.setBackground(Couleurs.ROSE_FOND);
        contenu.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(ComposantsUi.creerEnTete("Offre de surclassement", fenetre, FenetrePrincipale.INFOS_PASSAGERS), BorderLayout.NORTH);

        JScrollPane defilement = new JScrollPane(contenu);
        defilement.setBorder(BorderFactory.createEmptyBorder());
        defilement.getViewport().setBackground(Couleurs.ROSE_FOND);
        add(defilement, BorderLayout.CENTER);

        JButton boutonContinuer = ComposantsUi.creerBouton("Continuer vers le choix des sièges");
        boutonContinuer.addActionListener(e -> fenetre.afficherPanneau(FenetrePrincipale.SIEGES));
        JPanel conteneurBouton = new JPanel();
        conteneurBouton.setBackground(Couleurs.ROSE_FOND);
        conteneurBouton.add(boutonContinuer);
        add(conteneurBouton, BorderLayout.SOUTH);
    }

    public void rafraichir() {
        contenu.removeAll();

        EtatReservation etat = fenetre.getEtat();
        Vol vol = etat.getResultatChoisi().getVol();

        List<Billet> eligibles = new ArrayList<Billet>();
        for (Billet billet : etat.getBillets()) {
            if (estEligible(billet, vol)) {
                eligibles.add(billet);
            }
        }

        if (eligibles.isEmpty()) {
            // Rien à proposer : on passe directement à l'étape suivante
            // plutôt que de faire attendre le client sur un écran vide.
            fenetre.afficherPanneau(FenetrePrincipale.SIEGES);
            return;
        }

        contenu.add(ligne("Des places sont disponibles dans une classe supérieure sur ce vol."));
        JLabel labelIntroDetail = ligne("Le surclassement est inclus dans le paiement final, sans frais séparé à régler plus tard.");
        labelIntroDetail.setFont(Couleurs.POLICE_PETITE);
        labelIntroDetail.setForeground(Couleurs.TEXTE_SECONDAIRE);
        contenu.add(labelIntroDetail);
        contenu.add(Box.createVerticalStrut(16));

        for (Billet billet : eligibles) {
            contenu.add(construireLigne(billet, vol));
            contenu.add(Box.createVerticalStrut(10));
        }

        if (etat.getNbBebes() > 0) {
            JLabel labelBebe = ligne("[i] Un bébé voyageant avec un adulte surclassé profite de la même cabine, gratuitement.");
            labelBebe.setFont(Couleurs.POLICE_PETITE);
            labelBebe.setForeground(Couleurs.TEXTE_SECONDAIRE);
            contenu.add(labelBebe);
        }

        contenu.revalidate();
        contenu.repaint();
    }

    private boolean estEligible(Billet billet, Vol vol) {
        if (billet.estBebe() || billet.estMineurNonAccompagne()) {
            return false;
        }
        if ("BUSINESS".equalsIgnoreCase(billet.getClasse())) {
            return false;
        }
        return !classesCibles(billet, vol).isEmpty();
    }

    private List<String> classesCibles(Billet billet, Vol vol) {
        List<String> classes = new ArrayList<String>();
        if ("ECONOMY".equalsIgnoreCase(billet.getClasse())) {
            if (vol.getPlacesExtraLeg() > 0) {
                classes.add("EXTRA LEG");
            }
            if (vol.getPlacesBusiness() > 0) {
                classes.add("BUSINESS");
            }
        } else if ("EXTRA LEG".equalsIgnoreCase(billet.getClasse())) {
            if (vol.getPlacesBusiness() > 0) {
                classes.add("BUSINESS");
            }
        }
        return classes;
    }

    private JPanel construireLigne(Billet billet, Vol vol) {
        JPanel ligne = new JPanel();
        ligne.setLayout(new BoxLayout(ligne, BoxLayout.Y_AXIS));
        ligne.setBackground(Couleurs.ROSE_PANNEAU);
        ligne.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Couleurs.BORDURE_CARTE, 1),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        ligne.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelPassager = new JLabel(billet.getNom() + " " + billet.getPrenom()
                + "     Classe actuelle : " + billet.getClasse()
                + "     Tarif actuel : " + String.format("%.2f", billet.getPrixBillet()) + " EUR");
        labelPassager.setFont(Couleurs.POLICE_PETITE);
        labelPassager.setForeground(Couleurs.TEXTE);
        labelPassager.setAlignmentX(Component.LEFT_ALIGNMENT);
        ligne.add(labelPassager);
        ligne.add(Box.createVerticalStrut(6));

        JPanel boutons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        boutons.setBackground(Couleurs.ROSE_PANNEAU);
        boutons.setAlignmentX(Component.LEFT_ALIGNMENT);

        double prixBase = vol.getPrixBase();

        for (String classeCible : classesCibles(billet, vol)) {
            double nouveauTarif = fenetre.getTarifService().tarifCibleSurclassement(prixBase, classeCible);
            JButton bouton = ComposantsUi.creerBoutonSecondaire(
                    "Passer en " + classeCible + "  (" + String.format("%.2f", nouveauTarif) + " EUR)");
            bouton.addActionListener(e -> {
                billet.setClasse(classeCible);
                billet.setPrixBillet(nouveauTarif);
                rafraichir();
            });
            boutons.add(bouton);
        }

        ligne.add(boutons);
        ligne.setMaximumSize(new Dimension(Integer.MAX_VALUE, ligne.getPreferredSize().height));
        return ligne;
    }

    private JLabel ligne(String texte) {
        JLabel label = ComposantsUi.creerLabelChamp(texte);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
}
