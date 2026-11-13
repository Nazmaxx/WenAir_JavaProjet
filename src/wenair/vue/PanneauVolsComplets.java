package wenair.vue;

import wenair.modele.Vol;
import wenair.utilitaires.Validation;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

/**
 * Ecran admin "Vols complets par date" : liste des vols dont les places
 * Business, Extra Leg et Economy sont toutes occupees a la date donnee.
 */
public class PanneauVolsComplets extends JPanel {

    private final FenetrePrincipale fenetre;

    private final JTextField champDate = ComposantsUi.creerChampTexte(10);
    private final JLabel labelErreur = new JLabel(" ");
    private final JLabel labelTotal = new JLabel(" ");
    private final PanneauListeVerticale liste = new PanneauListeVerticale();

    public PanneauVolsComplets(FenetrePrincipale fenetre) {
        this.fenetre = fenetre;

        setLayout(new BorderLayout());
        setBackground(Couleurs.ROSE_FOND);
        setBorder(BorderFactory.createEmptyBorder(40, 70, 30, 70));

        add(ComposantsUi.creerEnTete("Vols complets par date", fenetre, FenetrePrincipale.ADMIN_MENU), BorderLayout.NORTH);

        JPanel formulaire = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formulaire.setBackground(Couleurs.ROSE_PANNEAU);
        formulaire.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Couleurs.BORDURE_CARTE, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        formulaire.add(ComposantsUi.creerLabelChamp("Date (jj/mm/aaaa) :"));
        formulaire.add(champDate);
        JButton boutonAfficher = ComposantsUi.creerBouton("Rechercher");
        boutonAfficher.addActionListener(e -> rechercherVolsComplets());
        formulaire.add(boutonAfficher);

        labelErreur.setFont(Couleurs.POLICE_PETITE);
        labelErreur.setForeground(Couleurs.ROUGE_INDISPONIBLE);
        labelTotal.setFont(Couleurs.POLICE_NORMALE);
        labelTotal.setForeground(Couleurs.TEXTE_SECONDAIRE);

        JPanel enTeteFormulaire = new JPanel(new BorderLayout());
        enTeteFormulaire.setBackground(Couleurs.ROSE_FOND);
        enTeteFormulaire.add(formulaire, BorderLayout.NORTH);
        enTeteFormulaire.add(labelErreur, BorderLayout.SOUTH);

        liste.setBackground(Couleurs.ROSE_FOND);
        JScrollPane defilement = new JScrollPane(liste);
        defilement.setBorder(BorderFactory.createEmptyBorder());
        defilement.getViewport().setBackground(Couleurs.ROSE_FOND);
        defilement.getVerticalScrollBar().setUnitIncrement(16);

        JPanel conteneurListe = new JPanel(new BorderLayout());
        conteneurListe.setBackground(Couleurs.ROSE_FOND);
        conteneurListe.add(labelTotal, BorderLayout.NORTH);
        conteneurListe.add(defilement, BorderLayout.CENTER);

        JPanel centre = new JPanel(new BorderLayout());
        centre.setBackground(Couleurs.ROSE_FOND);
        centre.add(enTeteFormulaire, BorderLayout.NORTH);
        centre.add(conteneurListe, BorderLayout.CENTER);
        add(centre, BorderLayout.CENTER);
    }

    /** Reinitialise le formulaire et la liste a chaque entree sur l'ecran. */
    public void rafraichir() {
        champDate.setText("");
        labelErreur.setText(" ");
        labelTotal.setText(" ");
        liste.removeAll();
        liste.revalidate();
        liste.repaint();
    }

    private void rechercherVolsComplets() {
        String date = champDate.getText().trim();

        if (!Validation.estDateValide(date)) {
            labelErreur.setText("Veuillez saisir une date valide (jj/mm/aaaa).");
            liste.removeAll();
            liste.revalidate();
            liste.repaint();
            labelTotal.setText(" ");
            return;
        }
        labelErreur.setText(" ");

        List<Vol> complets = fenetre.getVolService().volsCompletsPourDate(date);
        liste.removeAll();

        if (complets.isEmpty()) {
            JLabel labelVide = new JLabel("Aucun vol complet le " + date + ".");
            labelVide.setFont(Couleurs.POLICE_SOUS_TITRE);
            labelVide.setForeground(Couleurs.TEXTE_SECONDAIRE);
            liste.add(labelVide);
            labelTotal.setText(" ");
        } else {
            for (Vol vol : complets) {
                liste.add(construireCarte(vol));
                liste.add(javax.swing.Box.createVerticalStrut(10));
            }
            labelTotal.setText("Total vols complets : " + complets.size());
        }

        liste.revalidate();
        liste.repaint();
    }

    private JPanel construireCarte(Vol vol) {
        JPanel carte = new JPanel(new GridLayout(4, 1, 0, 3));
        carte.setBackground(Couleurs.ROSE_PANNEAU);
        carte.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Couleurs.BORDURE_CARTE, 1),
                BorderFactory.createEmptyBorder(12, 18, 12, 18)));

        JLabel labelVol = new JLabel("Vol : " + vol.getNumeroVol());
        labelVol.setFont(Couleurs.POLICE_SOUS_TITRE);
        labelVol.setForeground(Couleurs.ROUGE_INDISPONIBLE);

        JLabel labelTrajet = new JLabel("Trajet : " + vol.getDepVille() + " -> " + vol.getArrVille());
        JLabel labelHoraires = new JLabel("Horaires : " + vol.getHeureDepart() + " -> " + vol.getHeureArrivee()
                + "  -  " + vol.getAvion());
        JLabel labelComplet = new JLabel("COMPLET (" + vol.getTotalPlaces() + " places)");

        for (JLabel l : new JLabel[] { labelTrajet, labelHoraires }) {
            l.setFont(Couleurs.POLICE_PETITE);
            l.setForeground(Couleurs.TEXTE_SECONDAIRE);
        }
        labelComplet.setFont(Couleurs.POLICE_PETITE);
        labelComplet.setForeground(Couleurs.ROUGE_INDISPONIBLE);

        carte.add(labelVol);
        carte.add(labelTrajet);
        carte.add(labelHoraires);
        carte.add(labelComplet);

        return carte;
    }
}
