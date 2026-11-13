package wenair.vue;

import wenair.modele.ResultatRecherche;
import wenair.modele.Vol;

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
import java.awt.Dimension;

/**
 * Recapitulatif detaille du vol choisi, affiche avant la saisie des
 * passagers : identite et horaires du vol, bagage inclus, tarif par
 * categorie de passager, et ce qui est inclus (repas) ou payant (bagage
 * supplementaire, siege hors Business) sur cette classe.
 */
public class PanneauDetailsVol extends JPanel {

    private final FenetrePrincipale fenetre;
    private final JPanel contenu = new JPanel();

    public PanneauDetailsVol(FenetrePrincipale fenetre) {
        this.fenetre = fenetre;

        setLayout(new BorderLayout());
        setBackground(Couleurs.ROSE_FOND);
        setBorder(BorderFactory.createEmptyBorder(40, 70, 30, 70));

        contenu.setLayout(new BoxLayout(contenu, BoxLayout.Y_AXIS));
        contenu.setBackground(Couleurs.ROSE_PANNEAU);
        contenu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Couleurs.BORDURE_CARTE, 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)));
        contenu.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(ComposantsUi.creerEnTete("Détails du vol", fenetre, FenetrePrincipale.RESULTATS), BorderLayout.NORTH);

        JPanel conteneurCentre = new JPanel(new BorderLayout());
        conteneurCentre.setBackground(Couleurs.ROSE_FOND);
        conteneurCentre.add(contenu, BorderLayout.NORTH);
        JScrollPane defilement = new JScrollPane(conteneurCentre);
        defilement.setBorder(BorderFactory.createEmptyBorder());
        defilement.getViewport().setBackground(Couleurs.ROSE_FOND);
        add(defilement, BorderLayout.CENTER);

        JButton boutonContinuer = ComposantsUi.creerBouton("Continuer vers la saisie des passagers");
        boutonContinuer.addActionListener(e -> fenetre.afficherPanneau(FenetrePrincipale.INFOS_PASSAGERS));
        JPanel conteneurBouton = new JPanel();
        conteneurBouton.setBackground(Couleurs.ROSE_FOND);
        conteneurBouton.add(boutonContinuer);
        add(conteneurBouton, BorderLayout.SOUTH);
    }

    public void rafraichir() {
        contenu.removeAll();

        EtatReservation etat = fenetre.getEtat();
        ResultatRecherche resultat = etat.getResultatChoisi();
        Vol vol = resultat.getVol();
        String classe = etat.getClasseRecherche();

        contenu.add(ligneTitre(vol.getNumeroVol() + "  -  " + vol.getCompagnie() + "  (" + vol.getAvion() + ")     [" + classe + "]"));
        contenu.add(Box.createVerticalStrut(10));

        contenu.add(ligne(vol.getDepVille() + " (" + vol.getDepAeroport() + ", term. " + vol.getDepTerminal() + ")"
                + "   ->   " + vol.getArrVille() + " (" + vol.getArrAeroport() + ", term. " + vol.getArrTerminal() + ")"));
        contenu.add(ligne("Date : " + etat.getDateVol() + "     Départ : " + vol.getHeureDepart()
                + "     Arrivée : " + vol.getHeureArrivee() + "     Durée : " + vol.getDuree()));
        contenu.add(Box.createVerticalStrut(12));
        contenu.add(separateur());
        contenu.add(Box.createVerticalStrut(12));

        int bagageInclus = fenetre.getTarifService().bagageInclusKg(classe);
        contenu.add(ligne("Bagage inclus : " + bagageInclus + " kg"));
        contenu.add(Box.createVerticalStrut(12));
        contenu.add(separateur());
        contenu.add(Box.createVerticalStrut(12));

        contenu.add(ligneTitre("Tarifs"));
        contenu.add(Box.createVerticalStrut(6));
        if (etat.getNbAdultes() > 0) {
            contenu.add(ligne("Adulte : " + String.format("%.2f", resultat.getPrixAdulte()) + " EUR"));
        }
        if (etat.getNbEnfants() > 0) {
            contenu.add(ligne("Enfant : " + String.format("%.2f", resultat.getPrixEnfant()) + " EUR"));
        }
        if (etat.getNbBebes() > 0) {
            JLabel labelBebe = ligne("Bébé : GRATUIT");
            labelBebe.setForeground(Couleurs.VERT_DISPONIBLE.darker());
            contenu.add(labelBebe);
        }
        if (etat.getNbEnfants() > 0 || etat.getNbAdultes() > 0) {
            JLabel labelUm = ligne("[i] Un tarif Mineur Non Accompagné (UM) pourra s'appliquer si nécessaire, déterminé à l'étape suivante.");
            labelUm.setFont(Couleurs.POLICE_PETITE);
            labelUm.setForeground(Couleurs.TEXTE_SECONDAIRE);
            contenu.add(labelUm);
        }
        contenu.add(Box.createVerticalStrut(12));
        contenu.add(separateur());
        contenu.add(Box.createVerticalStrut(12));

        contenu.add(ligne("Repas : INCLUS"));
        contenu.add(ligne("Bagage supplémentaire : PAYANT (ajoutable après la réservation)"));
        if ("BUSINESS".equalsIgnoreCase(classe)) {
            contenu.add(ligne("Siège : INCLUS"));
        } else {
            contenu.add(ligne("Siège : payant selon la rangée (gratuit sur certaines rangées Economy)"));
            boolean surclassementPossible = vol.getPlacesExtraLeg() > 0 || vol.getPlacesBusiness() > 0;
            if (surclassementPossible) {
                JLabel labelSurclassement = ligne("Surclassement : possible, proposé après la saisie des passagers.");
                labelSurclassement.setForeground(Couleurs.ROSE_FONCE);
                contenu.add(labelSurclassement);
            }
        }

        contenu.revalidate();
        contenu.repaint();
    }

    private JLabel ligneTitre(String texte) {
        JLabel label = ComposantsUi.creerLabelChamp(texte);
        label.setFont(Couleurs.POLICE_SOUS_TITRE);
        label.setForeground(Couleurs.ROSE_FONCE);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel ligne(String texte) {
        JLabel label = ComposantsUi.creerLabelChamp(texte);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JSeparator separateur() {
        JSeparator separateur = new JSeparator();
        separateur.setAlignmentX(Component.LEFT_ALIGNMENT);
        separateur.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return separateur;
    }
}
