package wenair.vue;

import wenair.modele.Billet;
import wenair.modele.Vol;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;

/**
 * Carte d'embarquement : affichage en lecture seule des informations du
 * billet (passager, trajet, vol, classe/siege/bagage inclus, references),
 * accessible depuis "Consulter ma réservation".
 */
public class PanneauCarteEmbarquement extends JPanel {

    private final FenetrePrincipale fenetre;
    private final JPanel carte = new JPanel();

    public PanneauCarteEmbarquement(FenetrePrincipale fenetre) {
        this.fenetre = fenetre;

        setLayout(new BorderLayout());
        setBackground(Couleurs.ROSE_FOND);
        setBorder(BorderFactory.createEmptyBorder(40, 70, 30, 70));

        carte.setLayout(new BoxLayout(carte, BoxLayout.Y_AXIS));
        carte.setBackground(Couleurs.ROSE_PANNEAU);
        carte.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Couleurs.BORDURE_CARTE, 2),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)));
        carte.setAlignmentX(Component.LEFT_ALIGNMENT);
        carte.setMaximumSize(new java.awt.Dimension(620, 420));

        JPanel enveloppe = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        enveloppe.setBackground(Couleurs.ROSE_FOND);
        enveloppe.add(carte);

        add(ComposantsUi.creerEnTete("Carte d'embarquement", fenetre, () -> fenetre.revenirVersConsultation()), BorderLayout.NORTH);
        add(enveloppe, BorderLayout.CENTER);
    }

    public void rafraichir() {
        carte.removeAll();

        String refBillet = fenetre.getRefBilletSelectionne();
        Billet billet = fenetre.getReservationService().trouverBillet(refBillet);

        if (billet == null) {
            carte.add(creerLigne("Billet introuvable."));
            carte.revalidate();
            carte.repaint();
            return;
        }

        Vol vol = fenetre.getVolService().trouverParNumero(billet.getNumeroVol());
        int bagageInclus = fenetre.getTarifService().bagageInclusKg(billet.getClasse());
        String siegeAffiche = billet.getSiege() == null ? "CHECK-IN" : billet.getSiege();

        JLabel titre = new JLabel("WENAIR  -  BOARDING PASS");
        titre.setFont(Couleurs.POLICE_TITRE);
        titre.setForeground(Couleurs.ROSE_FONCE);
        titre.setAlignmentX(Component.LEFT_ALIGNMENT);
        carte.add(titre);
        carte.add(Box.createVerticalStrut(16));
        carte.add(creerSeparateur());
        carte.add(Box.createVerticalStrut(12));

        carte.add(creerLegende("PASSAGER"));
        carte.add(creerValeur(billet.getNom() + " " + billet.getPrenom()));
        carte.add(Box.createVerticalStrut(14));
        carte.add(creerSeparateur());
        carte.add(Box.createVerticalStrut(14));

        if (vol != null) {
            JPanel trajet = new JPanel(new GridLayout(1, 2, 20, 0));
            trajet.setBackground(Couleurs.ROSE_PANNEAU);
            trajet.setAlignmentX(Component.LEFT_ALIGNMENT);
            trajet.add(creerBloc("DÉPART", vol.getDepVille() + " (" + vol.getDepAeroport() + ")"));
            trajet.add(creerBloc("DESTINATION", vol.getArrVille() + " (" + vol.getArrAeroport() + ")"));
            carte.add(trajet);
            carte.add(Box.createVerticalStrut(14));

            JPanel volPanel = new JPanel(new GridLayout(1, 4, 12, 0));
            volPanel.setBackground(Couleurs.ROSE_PANNEAU);
            volPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            volPanel.add(creerBloc("VOL", vol.getNumeroVol()));
            volPanel.add(creerBloc("DATE", billet.getDateVol() == null ? "N/A" : billet.getDateVol()));
            volPanel.add(creerBloc("HEURE", vol.getHeureDepart()));
            volPanel.add(creerBloc("DURÉE", vol.getDuree()));
            carte.add(volPanel);
        } else {
            carte.add(creerLigne("Vol : " + billet.getNumeroVol() + "   Date : "
                    + (billet.getDateVol() == null ? "N/A" : billet.getDateVol())));
        }
        carte.add(Box.createVerticalStrut(14));
        carte.add(creerSeparateur());
        carte.add(Box.createVerticalStrut(14));

        JPanel infosPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        infosPanel.setBackground(Couleurs.ROSE_PANNEAU);
        infosPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infosPanel.add(creerBloc("CLASSE", billet.getClasse()));
        infosPanel.add(creerBloc("SIÈGE", siegeAffiche));
        infosPanel.add(creerBloc("BAGAGE INCLUS", bagageInclus + " kg"));
        carte.add(infosPanel);
        carte.add(Box.createVerticalStrut(14));
        carte.add(creerSeparateur());
        carte.add(Box.createVerticalStrut(14));

        carte.add(creerLigne("Ref. commande : " + billet.getRefCommande() + "     Billet N° : " + billet.getRefBillet()));
        carte.add(Box.createVerticalStrut(16));
        carte.add(creerSeparateur());
        carte.add(Box.createVerticalStrut(10));

        JLabel rappel1 = creerRappel("Presentez ce document au moment de l'embarquement.");
        JLabel rappel2 = creerRappel("Présentez-vous à la porte d'embarquement 30 min avant le départ.");
        carte.add(rappel1);
        carte.add(rappel2);

        carte.revalidate();
        carte.repaint();
    }

    private JPanel creerBloc(String legende, String valeur) {
        JPanel bloc = new JPanel();
        bloc.setLayout(new BoxLayout(bloc, BoxLayout.Y_AXIS));
        bloc.setBackground(Couleurs.ROSE_PANNEAU);
        JLabel labelLegende = new JLabel(legende);
        labelLegende.setFont(Couleurs.POLICE_PETITE);
        labelLegende.setForeground(Couleurs.TEXTE_SECONDAIRE);
        JLabel labelValeur = new JLabel(valeur == null ? "N/A" : valeur);
        labelValeur.setFont(Couleurs.POLICE_NORMALE.deriveFont(Font.BOLD));
        labelValeur.setForeground(Couleurs.TEXTE);
        bloc.add(labelLegende);
        bloc.add(labelValeur);
        return bloc;
    }

    private JLabel creerLegende(String texte) {
        JLabel label = new JLabel(texte);
        label.setFont(Couleurs.POLICE_PETITE);
        label.setForeground(Couleurs.TEXTE_SECONDAIRE);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel creerValeur(String texte) {
        JLabel label = new JLabel(texte);
        label.setFont(Couleurs.POLICE_SOUS_TITRE);
        label.setForeground(Couleurs.TEXTE);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel creerLigne(String texte) {
        JLabel label = new JLabel(texte);
        label.setFont(Couleurs.POLICE_NORMALE);
        label.setForeground(Couleurs.TEXTE);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel creerRappel(String texte) {
        JLabel label = new JLabel("[i] " + texte);
        label.setFont(Couleurs.POLICE_PETITE);
        label.setForeground(Couleurs.TEXTE_SECONDAIRE);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel creerSeparateur() {
        JPanel separateur = new JPanel();
        separateur.setBackground(Couleurs.ROSE_FONCE);
        separateur.setAlignmentX(Component.LEFT_ALIGNMENT);
        separateur.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 1));
        separateur.setPreferredSize(new java.awt.Dimension(560, 1));
        return separateur;
    }
}
