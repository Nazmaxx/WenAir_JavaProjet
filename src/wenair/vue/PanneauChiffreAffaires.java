package wenair.vue;

import wenair.modele.LigneChiffreAffaires;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

/**
 * Ecran admin "Chiffre d'affaires par vol et par année" : chiffre
 * d'affaires cumule et nombre de passagers par vol pour une annee donnee,
 * plus le total general.
 */
public class PanneauChiffreAffaires extends JPanel {

    private final FenetrePrincipale fenetre;

    private final JSpinner spinnerAnnee = new JSpinner(new SpinnerNumberModel(2026, 2000, 2100, 1));
    private final JLabel labelErreur = new JLabel(" ");
    private final JLabel labelTotal = new JLabel(" ", SwingConstants.CENTER);
    private final PanneauListeVerticale liste = new PanneauListeVerticale();

    public PanneauChiffreAffaires(FenetrePrincipale fenetre) {
        this.fenetre = fenetre;

        // Editeur sans separateur de milliers (une annee ne doit pas
        // s'afficher "2 026" ou "2,026", selon la locale de la machine).
        JSpinner.NumberEditor editeurAnnee = new JSpinner.NumberEditor(spinnerAnnee, "#");
        spinnerAnnee.setEditor(editeurAnnee);

        setLayout(new BorderLayout());
        setBackground(Couleurs.ROSE_FOND);
        setBorder(BorderFactory.createEmptyBorder(40, 70, 30, 70));

        add(ComposantsUi.creerEnTete("Chiffre d'affaires", fenetre, FenetrePrincipale.ADMIN_MENU), BorderLayout.NORTH);

        JPanel formulaire = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formulaire.setBackground(Couleurs.ROSE_PANNEAU);
        formulaire.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Couleurs.BORDURE_CARTE, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        formulaire.add(ComposantsUi.creerLabelChamp("Année :"));
        formulaire.add(spinnerAnnee);
        JButton boutonAfficher = ComposantsUi.creerBouton("Calculer");
        boutonAfficher.addActionListener(e -> calculerChiffreAffaires());
        formulaire.add(boutonAfficher);

        labelErreur.setFont(Couleurs.POLICE_PETITE);
        labelErreur.setForeground(Couleurs.ROUGE_INDISPONIBLE);
        labelTotal.setFont(Couleurs.POLICE_TITRE.deriveFont(20f));
        labelTotal.setForeground(Couleurs.ROSE_FONCE);

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
        conteneurListe.add(defilement, BorderLayout.CENTER);

        JPanel pied = new JPanel();
        pied.setBackground(Couleurs.ROSE_PANNEAU);
        pied.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Couleurs.BORDURE_CARTE, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        pied.add(labelTotal);
        conteneurListe.add(pied, BorderLayout.SOUTH);

        JPanel centre = new JPanel(new BorderLayout());
        centre.setBackground(Couleurs.ROSE_FOND);
        centre.add(enTeteFormulaire, BorderLayout.NORTH);
        centre.add(conteneurListe, BorderLayout.CENTER);
        add(centre, BorderLayout.CENTER);
    }

    /** Reinitialise le formulaire et la liste a chaque entree sur l'ecran. */
    public void rafraichir() {
        spinnerAnnee.setValue(2026);
        labelErreur.setText(" ");
        labelTotal.setText(" ");
        liste.removeAll();
        liste.revalidate();
        liste.repaint();
    }

    private void calculerChiffreAffaires() {
        int annee = (Integer) spinnerAnnee.getValue();
        labelErreur.setText(" ");

        List<LigneChiffreAffaires> lignes = fenetre.getReservationService().chiffreAffairesParAnnee(annee);
        liste.removeAll();

        if (lignes.isEmpty()) {
            JLabel labelVide = new JLabel("Aucune réservation trouvée pour l'année " + annee + ".");
            labelVide.setFont(Couleurs.POLICE_SOUS_TITRE);
            labelVide.setForeground(Couleurs.TEXTE_SECONDAIRE);
            liste.add(labelVide);
            labelTotal.setText(" ");
        } else {
            double caTotal = 0;
            for (LigneChiffreAffaires ligne : lignes) {
                liste.add(construireCarte(ligne));
                liste.add(javax.swing.Box.createVerticalStrut(10));
                caTotal += ligne.getChiffreAffaires();
            }
            labelTotal.setText("CA TOTAL " + annee + " : " + String.format("%.2f", caTotal) + " EUR");
        }

        liste.revalidate();
        liste.repaint();
    }

    private JPanel construireCarte(LigneChiffreAffaires ligne) {
        JPanel carte = new JPanel(new GridLayout(3, 1, 0, 3));
        carte.setBackground(Couleurs.ROSE_PANNEAU);
        carte.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Couleurs.BORDURE_CARTE, 1),
                BorderFactory.createEmptyBorder(12, 18, 12, 18)));

        JLabel labelVol = new JLabel("Vol : " + ligne.getNumeroVol());
        labelVol.setFont(Couleurs.POLICE_SOUS_TITRE);
        labelVol.setForeground(Couleurs.TEXTE);

        JLabel labelPassagers = new JLabel("Passagers : " + ligne.getNbPassagers());
        labelPassagers.setFont(Couleurs.POLICE_PETITE);
        labelPassagers.setForeground(Couleurs.TEXTE_SECONDAIRE);

        JLabel labelCa = new JLabel("CA total : " + String.format("%.2f", ligne.getChiffreAffaires()) + " EUR");
        labelCa.setFont(Couleurs.POLICE_NORMALE);
        labelCa.setForeground(Couleurs.ROSE_FONCE);

        carte.add(labelVol);
        carte.add(labelPassagers);
        carte.add(labelCa);

        return carte;
    }
}
