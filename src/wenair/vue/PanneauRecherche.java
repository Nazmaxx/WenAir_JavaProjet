package wenair.vue;

import wenair.utilitaires.Validation;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Écran de recherche de vol : villes de départ/arrivée, date, classe et
 * nombre de passagers. La date est validée au fur et à mesure de la
 * saisie (format jj/mm/aaaa).
 */
public class PanneauRecherche extends JPanel {

    private final JTextField champVilleDepart = ComposantsUi.creerChampTexte(15);
    private final JTextField champVilleArrivee = ComposantsUi.creerChampTexte(15);
    private final JTextField champDate = ComposantsUi.creerChampTexte(15);
    private final JLabel labelAideDate = ComposantsUi.creerLabelAide();
    private final JComboBox<String> comboClasse = new JComboBox<String>(new String[] { "ECONOMY", "BUSINESS", "EXTRA LEG" });
    private final JSpinner spinnerAdultes = new JSpinner(new SpinnerNumberModel(1, 0, 9, 1));
    private final JSpinner spinnerEnfants = new JSpinner(new SpinnerNumberModel(0, 0, 9, 1));
    private final JSpinner spinnerBebes = new JSpinner(new SpinnerNumberModel(0, 0, 9, 1));

    public PanneauRecherche(FenetrePrincipale fenetre) {
        setLayout(new BorderLayout());
        setBackground(Couleurs.ROSE_FOND);
        setBorder(BorderFactory.createEmptyBorder(50, 80, 50, 80));

        add(ComposantsUi.creerEnTete("Rechercher un vol", fenetre, FenetrePrincipale.ESPACE_CLIENT), BorderLayout.NORTH);

        JPanel formulaire = new JPanel(new GridBagLayout());
        formulaire.setBackground(Couleurs.ROSE_PANNEAU);
        formulaire.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Couleurs.BORDURE_CARTE, 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;

        int ligne = 0;

        c.gridy = ligne++;
        formulaire.add(ComposantsUi.creerLabelChamp("Ville de départ :"), c);
        c.gridx = 1;
        formulaire.add(champVilleDepart, c);
        c.gridx = 0;

        c.gridy = ligne++;
        formulaire.add(ComposantsUi.creerLabelChamp("Ville d'arrivée :"), c);
        c.gridx = 1;
        formulaire.add(champVilleArrivee, c);
        c.gridx = 0;

        c.gridy = ligne++;
        formulaire.add(ComposantsUi.creerLabelChamp("Date du vol (jj/mm/aaaa) :"), c);
        c.gridx = 1;
        formulaire.add(champDate, c);
        c.gridx = 0;

        c.gridy = ligne++;
        c.gridx = 1;
        formulaire.add(labelAideDate, c);
        c.gridx = 0;

        c.gridy = ligne++;
        formulaire.add(ComposantsUi.creerLabelChamp("Classe :"), c);
        c.gridx = 1;
        formulaire.add(comboClasse, c);
        c.gridx = 0;

        c.gridy = ligne++;
        formulaire.add(ComposantsUi.creerLabelChamp("Adultes (12 ans et +) :"), c);
        c.gridx = 1;
        formulaire.add(spinnerAdultes, c);
        c.gridx = 0;

        c.gridy = ligne++;
        formulaire.add(ComposantsUi.creerLabelChamp("Enfants (2 à 11 ans) :"), c);
        c.gridx = 1;
        formulaire.add(spinnerEnfants, c);
        c.gridx = 0;

        c.gridy = ligne++;
        formulaire.add(ComposantsUi.creerLabelChamp("Bébés (moins de 2 ans) :"), c);
        c.gridx = 1;
        formulaire.add(spinnerBebes, c);

        champDate.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { validerDate(); }
            @Override
            public void removeUpdate(DocumentEvent e) { validerDate(); }
            @Override
            public void changedUpdate(DocumentEvent e) { validerDate(); }

            private void validerDate() {
                String texte = champDate.getText().trim();
                if (texte.isEmpty()) {
                    labelAideDate.setText(" ");
                } else if (Validation.estDateValide(texte)) {
                    labelAideDate.setText("Date correcte");
                    labelAideDate.setForeground(Couleurs.VERT_DISPONIBLE.darker());
                } else {
                    labelAideDate.setText("Format attendu : jj/mm/aaaa");
                    labelAideDate.setForeground(Couleurs.ROUGE_INDISPONIBLE);
                }
            }
        });

        JPanel conteneurFormulaire = new JPanel(new BorderLayout());
        conteneurFormulaire.setBackground(Couleurs.ROSE_FOND);
        conteneurFormulaire.add(formulaire, BorderLayout.NORTH);
        add(conteneurFormulaire, BorderLayout.CENTER);

        JButton boutonRechercher = ComposantsUi.creerBouton("Rechercher");
        boutonRechercher.addActionListener(e -> lancerRecherche(fenetre));

        JPanel panneauBas = new JPanel();
        panneauBas.setBackground(Couleurs.ROSE_FOND);
        panneauBas.add(boutonRechercher);
        add(panneauBas, BorderLayout.SOUTH);
    }

    private void lancerRecherche(FenetrePrincipale fenetre) {
        String villeDepart = champVilleDepart.getText().trim();
        String villeArrivee = champVilleArrivee.getText().trim();
        String date = champDate.getText().trim();
        String classe = (String) comboClasse.getSelectedItem();
        int nbAdultes = (Integer) spinnerAdultes.getValue();
        int nbEnfants = (Integer) spinnerEnfants.getValue();
        int nbBebes = (Integer) spinnerBebes.getValue();

        if (!Validation.estRempli(villeDepart) || !Validation.estRempli(villeArrivee)) {
            ComposantsUi.afficherErreur(this, "Veuillez saisir une ville de départ et une ville d'arrivée.");
            return;
        }
        if (!Validation.estDateValide(date)) {
            ComposantsUi.afficherErreur(this, "La date du vol doit être au format jj/mm/aaaa.");
            return;
        }
        if (!Validation.estDateNonPassee(date)) {
            ComposantsUi.afficherErreur(this, "La date du vol ne peut pas être antérieure à aujourd'hui.");
            return;
        }
        if (nbAdultes + nbEnfants == 0) {
            ComposantsUi.afficherErreur(this, "Il faut au moins un passager adulte ou enfant.");
            return;
        }
        if (nbBebes > nbAdultes) {
            ComposantsUi.afficherErreur(this, "Un bébé doit être accompagné d'un adulte : au maximum "
                    + nbAdultes + " bébé(s) pour cette réservation.");
            return;
        }

        EtatReservation etat = fenetre.getEtat();
        etat.reinitialiser();
        etat.setVilleDepart(villeDepart);
        etat.setVilleArrivee(villeArrivee);
        etat.setDateVol(date);
        etat.setClasseRecherche(classe);
        etat.setNbAdultes(nbAdultes);
        etat.setNbEnfants(nbEnfants);
        etat.setNbBebes(nbBebes);

        fenetre.afficherPanneau(FenetrePrincipale.RESULTATS);
    }
}
