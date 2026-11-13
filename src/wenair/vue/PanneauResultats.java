package wenair.vue;

import wenair.modele.ResultatRecherche;
import wenair.modele.Vol;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

/**
 * Liste des vols trouvés pour la recherche en cours, triés par prix
 * croissant (VolService s'en charge). Chaque résultat est présenté sous
 * forme de carte avec un bouton "Sélectionner".
 */
public class PanneauResultats extends JPanel {

    private final FenetrePrincipale fenetre;
    private final PanneauListeVerticale listeResultats = new PanneauListeVerticale();
    private final JLabel labelEnTeteRecherche = new JLabel();
    private final JLabel labelAucunResultat = new JLabel("Aucun vol ne correspond à cette recherche.", SwingConstants.CENTER);

    public PanneauResultats(FenetrePrincipale fenetre) {
        this.fenetre = fenetre;

        setLayout(new BorderLayout());
        setBackground(Couleurs.ROSE_FOND);
        setBorder(BorderFactory.createEmptyBorder(50, 80, 50, 80));

        JPanel enTete = ComposantsUi.creerEnTete("Résultats de recherche", fenetre, FenetrePrincipale.RECHERCHE);
        add(enTete, BorderLayout.NORTH);

        labelEnTeteRecherche.setFont(Couleurs.POLICE_NORMALE);
        labelEnTeteRecherche.setForeground(Couleurs.TEXTE_SECONDAIRE);

        labelAucunResultat.setFont(Couleurs.POLICE_SOUS_TITRE);
        labelAucunResultat.setForeground(Couleurs.TEXTE_SECONDAIRE);

        listeResultats.setLayout(new BoxLayout(listeResultats, BoxLayout.Y_AXIS));
        listeResultats.setBackground(Couleurs.ROSE_FOND);

        JScrollPane defilement = new JScrollPane(listeResultats);
        defilement.setBorder(BorderFactory.createEmptyBorder());
        defilement.getViewport().setBackground(Couleurs.ROSE_FOND);
        defilement.getVerticalScrollBar().setUnitIncrement(16);

        JPanel conteneurCentre = new JPanel(new BorderLayout());
        conteneurCentre.setBackground(Couleurs.ROSE_FOND);
        conteneurCentre.add(labelEnTeteRecherche, BorderLayout.NORTH);
        conteneurCentre.add(defilement, BorderLayout.CENTER);
        add(conteneurCentre, BorderLayout.CENTER);
    }

    /** Relance la recherche à partir de l'état courant et reconstruit la liste des cartes. */
    public void rafraichir() {
        EtatReservation etat = fenetre.getEtat();

        labelEnTeteRecherche.setText(etat.getVilleDepart() + " -> " + etat.getVilleArrivee()
                + "  |  " + etat.getDateVol() + "  |  " + etat.getClasseRecherche()
                + "  |  " + etat.getNombreTotalPassagers() + " passager(s)");

        List<ResultatRecherche> resultats = fenetre.getVolService().rechercherVols(
                etat.getVilleDepart(), etat.getVilleArrivee(), etat.getClasseRecherche(), etat.getDateVol(), 1, 1);

        listeResultats.removeAll();

        if (resultats.isEmpty()) {
            listeResultats.add(labelAucunResultat);
        } else {
            for (ResultatRecherche resultat : resultats) {
                listeResultats.add(construireCarte(resultat));
                listeResultats.add(javax.swing.Box.createVerticalStrut(12));
            }
        }

        listeResultats.revalidate();
        listeResultats.repaint();
    }

    private JPanel construireCarte(ResultatRecherche resultat) {
        Vol vol = resultat.getVol();

        JPanel carte = new JPanel(new BorderLayout(20, 0));
        carte.setBackground(Couleurs.ROSE_PANNEAU);
        carte.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Couleurs.BORDURE_CARTE, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));
        carte.setMaximumSize(new Dimension(Integer.MAX_VALUE, 118));

        JPanel infosVol = new JPanel(new GridLayout(4, 1));
        infosVol.setBackground(Couleurs.ROSE_PANNEAU);

        JLabel labelVol = new JLabel(vol.getNumeroVol() + "  -  " + vol.getCompagnie() + "  (" + vol.getAvion() + ")");
        labelVol.setFont(Couleurs.POLICE_SOUS_TITRE);
        labelVol.setForeground(Couleurs.TEXTE);

        JLabel labelTrajet = new JLabel(vol.getDepVille() + " (" + vol.getDepAeroport() + ", " + vol.getHeureDepart() + ")"
                + "   ->   " + vol.getArrVille() + " (" + vol.getArrAeroport() + ", " + vol.getHeureArrivee() + ")"
                + "   -   " + vol.getDuree());
        labelTrajet.setFont(Couleurs.POLICE_NORMALE);
        labelTrajet.setForeground(Couleurs.TEXTE);

        JLabel labelPlaces = new JLabel(resultat.getPlacesRestantes() + " place(s) restante(s) dans cette classe");
        labelPlaces.setFont(Couleurs.POLICE_PETITE);
        labelPlaces.setForeground(Couleurs.TEXTE_SECONDAIRE);

        // Bagage inclus affiché directement dans les résultats de recherche.
        int bagageInclus = fenetre.getTarifService().bagageInclusKg(fenetre.getEtat().getClasseRecherche());
        JLabel labelBagage = new JLabel("Bagage inclus : " + bagageInclus + "kg");
        labelBagage.setFont(Couleurs.POLICE_PETITE);
        labelBagage.setForeground(Couleurs.TEXTE_SECONDAIRE);

        infosVol.add(labelVol);
        infosVol.add(labelTrajet);
        infosVol.add(labelPlaces);
        infosVol.add(labelBagage);

        JPanel panneauPrixEtBouton = new JPanel(new GridLayout(3, 1, 0, 4));
        panneauPrixEtBouton.setBackground(Couleurs.ROSE_PANNEAU);

        JLabel labelPrix = new JLabel(String.format("%.2f EUR / adulte", resultat.getPrixAdulte()), SwingConstants.CENTER);
        labelPrix.setFont(Couleurs.POLICE_SOUS_TITRE);
        labelPrix.setForeground(Couleurs.ROSE_FONCE);

        JLabel labelPrixEnfant = new JLabel(String.format("%.2f EUR / enfant", resultat.getPrixEnfant()), SwingConstants.CENTER);
        labelPrixEnfant.setFont(Couleurs.POLICE_PETITE);
        labelPrixEnfant.setForeground(Couleurs.TEXTE_SECONDAIRE);

        JButton boutonSelectionner = ComposantsUi.creerBouton("Sélectionner");
        boutonSelectionner.addActionListener(e -> {
            fenetre.getEtat().setResultatChoisi(resultat);
            fenetre.afficherPanneau(FenetrePrincipale.DETAILS_VOL);
        });

        panneauPrixEtBouton.add(labelPrix);
        panneauPrixEtBouton.add(labelPrixEnfant);
        panneauPrixEtBouton.add(boutonSelectionner);

        carte.add(infosVol, BorderLayout.CENTER);
        carte.add(panneauPrixEtBouton, BorderLayout.EAST);

        return carte;
    }
}
