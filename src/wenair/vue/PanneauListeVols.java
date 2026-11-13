package wenair.vue;

import wenair.modele.Vol;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

/**
 * Ecran admin "Afficher tous les vols" : liste en lecture seule de tous
 * les vols enregistres.
 */
public class PanneauListeVols extends JPanel {

    private final FenetrePrincipale fenetre;
    private final PanneauListeVerticale liste = new PanneauListeVerticale();
    private final JLabel labelTotal = new JLabel();
    private final JLabel labelAucunVol = new JLabel("Aucun vol enregistre.", SwingConstants.CENTER);

    public PanneauListeVols(FenetrePrincipale fenetre) {
        this.fenetre = fenetre;

        setLayout(new BorderLayout());
        setBackground(Couleurs.ROSE_FOND);
        setBorder(BorderFactory.createEmptyBorder(40, 70, 30, 70));

        add(ComposantsUi.creerEnTete("Tous les vols", fenetre, FenetrePrincipale.ADMIN_MENU), BorderLayout.NORTH);

        labelTotal.setFont(Couleurs.POLICE_NORMALE);
        labelTotal.setForeground(Couleurs.TEXTE_SECONDAIRE);
        labelAucunVol.setFont(Couleurs.POLICE_SOUS_TITRE);
        labelAucunVol.setForeground(Couleurs.TEXTE_SECONDAIRE);

        liste.setBackground(Couleurs.ROSE_FOND);
        JScrollPane defilement = new JScrollPane(liste);
        defilement.setBorder(BorderFactory.createEmptyBorder());
        defilement.getViewport().setBackground(Couleurs.ROSE_FOND);
        defilement.getVerticalScrollBar().setUnitIncrement(16);

        JPanel centre = new JPanel(new BorderLayout());
        centre.setBackground(Couleurs.ROSE_FOND);
        centre.add(labelTotal, BorderLayout.NORTH);
        centre.add(defilement, BorderLayout.CENTER);
        add(centre, BorderLayout.CENTER);
    }

    public void rafraichir() {
        List<Vol> vols = fenetre.getVolService().listerTous();
        liste.removeAll();

        if (vols.isEmpty()) {
            liste.add(labelAucunVol);
            labelTotal.setText(" ");
        } else {
            for (Vol vol : vols) {
                liste.add(construireCarte(vol));
                liste.add(javax.swing.Box.createVerticalStrut(12));
            }
            labelTotal.setText("Total : " + vols.size() + " vol(s) enregistre(s)");
        }

        liste.revalidate();
        liste.repaint();
    }

    private JPanel construireCarte(Vol vol) {
        JPanel carte = new JPanel(new BorderLayout(20, 0));
        carte.setBackground(Couleurs.ROSE_PANNEAU);
        carte.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Couleurs.BORDURE_CARTE, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));
        carte.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        JPanel infos = new JPanel(new GridLayout(4, 1));
        infos.setBackground(Couleurs.ROSE_PANNEAU);

        JLabel labelVol = new JLabel(vol.getNumeroVol() + "  -  " + vol.getDepVille() + " -> " + vol.getArrVille());
        labelVol.setFont(Couleurs.POLICE_SOUS_TITRE);
        labelVol.setForeground(Couleurs.TEXTE);

        JLabel labelAeroports = new JLabel("Aéroports : " + vol.getDepAeroport() + " (" + vol.getDepTerminal() + ") -> "
                + vol.getArrAeroport() + " (" + vol.getArrTerminal() + ")");
        JLabel labelHoraires = new JLabel("Horaires : " + vol.getHeureDepart() + " -> " + vol.getHeureArrivee()
                + "  (" + vol.getDuree() + ")  -  " + vol.getAvion());
        JLabel labelPlaces = new JLabel("Places : " + vol.getTotalPlaces() + " total (" + vol.getPlacesBusiness()
                + " Business / " + vol.getPlacesExtraLeg() + " Extra Leg / " + vol.getPlacesEco()
                + " Economy / " + vol.getPlacesMineur() + " Mineur)");

        for (JLabel l : new JLabel[] { labelAeroports, labelHoraires, labelPlaces }) {
            l.setFont(Couleurs.POLICE_PETITE);
            l.setForeground(Couleurs.TEXTE_SECONDAIRE);
        }

        infos.add(labelVol);
        infos.add(labelAeroports);
        infos.add(labelHoraires);
        infos.add(labelPlaces);

        JLabel labelPrix = new JLabel(String.format("%.2f EUR", vol.getPrixBase()), SwingConstants.CENTER);
        labelPrix.setFont(Couleurs.POLICE_SOUS_TITRE);
        labelPrix.setForeground(Couleurs.ROSE_FONCE);

        carte.add(infos, BorderLayout.CENTER);
        carte.add(labelPrix, BorderLayout.EAST);

        return carte;
    }
}
