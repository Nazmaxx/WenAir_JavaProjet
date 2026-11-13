package wenair.vue;

import wenair.modele.TypeAvion;
import wenair.modele.Vol;
import wenair.utilitaires.Validation;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Ecran admin "Modifier un vol" : recherche par numero de vol, puis
 * edition des champs modifiables (horaires, duree recalculee, prix,
 * numero de vol, type d'avion). Le trajet (villes/aeroports/terminaux)
 * n'est pas modifiable.
 */
public class PanneauModifierVol extends JPanel {

    private final FenetrePrincipale fenetre;

    private final JTextField champRecherche = ComposantsUi.creerChampTexte(10);
    private final JLabel labelErreurRecherche = new JLabel(" ");

    private final JPanel panneauEdition = new JPanel(new GridBagLayout());
    private final JLabel labelTrajet = new JLabel(" ");
    private final JTextField champHeureDepart = ComposantsUi.creerChampTexte(6);
    private final JTextField champHeureArrivee = ComposantsUi.creerChampTexte(6);
    private final JLabel labelDuree = ComposantsUi.creerLabelAide();
    private final JTextField champNumeroVol = ComposantsUi.creerChampTexte(10);
    private final JComboBox<TypeAvion> comboAvion = new JComboBox<TypeAvion>(TypeAvion.TOUS);
    private final JLabel labelDetailAvion = ComposantsUi.creerLabelAide();
    private final JTextField champPrixBase = ComposantsUi.creerChampTexte(8);
    private final JLabel labelMessage = new JLabel(" ");

    private Vol volEnCoursDEdition;

    public PanneauModifierVol(FenetrePrincipale fenetre) {
        this.fenetre = fenetre;

        setLayout(new BorderLayout());
        setBackground(Couleurs.ROSE_FOND);
        setBorder(BorderFactory.createEmptyBorder(40, 70, 30, 70));

        add(ComposantsUi.creerEnTete("Modifier un vol", fenetre, FenetrePrincipale.ADMIN_MENU), BorderLayout.NORTH);

        JPanel formuleRecherche = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formuleRecherche.setBackground(Couleurs.ROSE_PANNEAU);
        formuleRecherche.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Couleurs.BORDURE_CARTE, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        formuleRecherche.add(ComposantsUi.creerLabelChamp("Numéro du vol à modifier :"));
        formuleRecherche.add(champRecherche);
        JButton boutonRechercher = ComposantsUi.creerBouton("Rechercher");
        boutonRechercher.addActionListener(e -> rechercherVol());
        formuleRecherche.add(boutonRechercher);

        JPanel enTeteRecherche = new JPanel(new BorderLayout());
        enTeteRecherche.setBackground(Couleurs.ROSE_FOND);
        enTeteRecherche.add(formuleRecherche, BorderLayout.NORTH);
        labelErreurRecherche.setFont(Couleurs.POLICE_PETITE);
        labelErreurRecherche.setForeground(Couleurs.ROUGE_INDISPONIBLE);
        enTeteRecherche.add(labelErreurRecherche, BorderLayout.SOUTH);

        construireFormulaireEdition();
        panneauEdition.setVisible(false);

        JPanel centre = new JPanel(new BorderLayout());
        centre.setBackground(Couleurs.ROSE_FOND);
        centre.add(enTeteRecherche, BorderLayout.NORTH);
        centre.add(panneauEdition, BorderLayout.CENTER);

        JScrollPane defilement = new JScrollPane(centre);
        defilement.setBorder(BorderFactory.createEmptyBorder());
        defilement.getViewport().setBackground(Couleurs.ROSE_FOND);
        defilement.getVerticalScrollBar().setUnitIncrement(16);
        add(defilement, BorderLayout.CENTER);
    }

    private void construireFormulaireEdition() {
        panneauEdition.setBackground(Couleurs.ROSE_PANNEAU);
        panneauEdition.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Couleurs.BORDURE_CARTE, 1),
                BorderFactory.createEmptyBorder(25, 35, 25, 35)));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.anchor = GridBagConstraints.WEST;

        int ligne = 0;
        c.gridx = 0;
        c.gridy = ligne;
        c.gridwidth = 2;
        labelTrajet.setFont(Couleurs.POLICE_SOUS_TITRE);
        labelTrajet.setForeground(Couleurs.TEXTE);
        panneauEdition.add(labelTrajet, c);
        ligne++;
        c.gridwidth = 1;

        ligne = ajouterLigne(c, ligne, "Heure de départ (HH:MM) :", champHeureDepart);
        ligne = ajouterLigne(c, ligne, "Heure d'arrivée (HH:MM) :", champHeureArrivee);

        c.gridx = 1;
        c.gridy = ligne++;
        panneauEdition.add(labelDuree, c);

        ligne = ajouterLigne(c, ligne, "Numéro de vol :", champNumeroVol);
        ligne = ajouterLigne(c, ligne, "Type d'avion :", comboAvion);

        c.gridx = 1;
        c.gridy = ligne++;
        labelDetailAvion.setForeground(Couleurs.TEXTE_SECONDAIRE);
        panneauEdition.add(labelDetailAvion, c);

        ligne = ajouterLigne(c, ligne, "Prix de base (EUR) :", champPrixBase);

        javax.swing.event.DocumentListener validateurDuree = new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { recalculerDuree(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { recalculerDuree(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { recalculerDuree(); }
        };
        champHeureDepart.getDocument().addDocumentListener(validateurDuree);
        champHeureArrivee.getDocument().addDocumentListener(validateurDuree);
        comboAvion.addActionListener(e -> mettreAJourDetailAvion());

        JButton boutonEnregistrer = ComposantsUi.creerBouton("Enregistrer les modifications");
        boutonEnregistrer.addActionListener(e -> validerEtEnregistrer());

        labelMessage.setFont(Couleurs.POLICE_PETITE);

        c.gridx = 0;
        c.gridy = ligne++;
        c.gridwidth = 2;
        panneauEdition.add(boutonEnregistrer, c);
        c.gridy = ligne;
        panneauEdition.add(labelMessage, c);
    }

    private int ajouterLigne(GridBagConstraints c, int ligne, String label, java.awt.Component champ) {
        c.gridx = 0;
        c.gridy = ligne;
        c.gridwidth = 1;
        panneauEdition.add(ComposantsUi.creerLabelChamp(label), c);
        c.gridx = 1;
        panneauEdition.add(champ, c);
        return ligne + 1;
    }

    private void mettreAJourDetailAvion() {
        TypeAvion avion = (TypeAvion) comboAvion.getSelectedItem();
        labelDetailAvion.setText(avion == null ? " " : avion.detailPlaces());
    }

    private void recalculerDuree() {
        String heureDepart = champHeureDepart.getText().trim();
        String heureArrivee = champHeureArrivee.getText().trim();
        if (Validation.estHeureValide(heureDepart) && Validation.estHeureValide(heureArrivee)) {
            labelDuree.setText("Durée : " + fenetre.getVolService().calculerDuree(heureDepart, heureArrivee));
            labelDuree.setForeground(Couleurs.VERT_DISPONIBLE.darker());
        } else {
            labelDuree.setText("Format attendu : HH:MM");
            labelDuree.setForeground(Couleurs.ROUGE_INDISPONIBLE);
        }
    }

    /** Reinitialise l'ecran (masque le formulaire d'edition) a chaque entree. */
    public void rafraichir() {
        champRecherche.setText("");
        labelErreurRecherche.setText(" ");
        panneauEdition.setVisible(false);
        volEnCoursDEdition = null;
    }

    private void rechercherVol() {
        String numeroVol = champRecherche.getText().trim().toUpperCase();
        if (!Validation.estRempli(numeroVol)) {
            labelErreurRecherche.setText("Veuillez saisir un numéro de vol.");
            panneauEdition.setVisible(false);
            return;
        }

        Vol vol = fenetre.getVolService().trouverParNumero(numeroVol);
        if (vol == null) {
            labelErreurRecherche.setText("Vol " + numeroVol + " introuvable.");
            panneauEdition.setVisible(false);
            return;
        }

        labelErreurRecherche.setText(" ");
        volEnCoursDEdition = vol;
        labelMessage.setText(" ");

        labelTrajet.setText("Vol " + vol.getNumeroVol() + "  |  " + vol.getDepVille() + " -> " + vol.getArrVille());
        champHeureDepart.setText(vol.getHeureDepart());
        champHeureArrivee.setText(vol.getHeureArrivee());
        champNumeroVol.setText(vol.getNumeroVol());
        champPrixBase.setText(String.format("%.2f", vol.getPrixBase()));
        selectionnerAvionCorrespondant(vol);
        mettreAJourDetailAvion();
        recalculerDuree();

        panneauEdition.setVisible(true);
    }

    private void selectionnerAvionCorrespondant(Vol vol) {
        for (int i = 0; i < TypeAvion.TOUS.length; i++) {
            TypeAvion t = TypeAvion.TOUS[i];
            if (t.getTotalPlaces() == vol.getTotalPlaces() && t.getPlacesBusiness() == vol.getPlacesBusiness()
                    && t.getPlacesExtraLeg() == vol.getPlacesExtraLeg() && t.getPlacesEco() == vol.getPlacesEco()
                    && t.getPlacesMineur() == vol.getPlacesMineur() && t.getNom().equals(vol.getAvion())) {
                comboAvion.setSelectedIndex(i);
                return;
            }
        }
        comboAvion.setSelectedIndex(0);
    }

    private void validerEtEnregistrer() {
        if (volEnCoursDEdition == null) {
            return;
        }

        String heureDepart = champHeureDepart.getText().trim();
        String heureArrivee = champHeureArrivee.getText().trim();
        String numeroVol = champNumeroVol.getText().trim().toUpperCase();
        String texteprix = champPrixBase.getText().trim();

        if (!Validation.estHeureValide(heureDepart) || !Validation.estHeureValide(heureArrivee)) {
            ComposantsUi.afficherErreur(this, "Les horaires doivent être au format HH:MM.");
            return;
        }
        if (!Validation.estNumeroVolValide(numeroVol)) {
            ComposantsUi.afficherErreur(this, "Le numéro de vol doit commencer par WN suivi de 4 chiffres (ex: WN7300).");
            return;
        }
        if (!numeroVol.equalsIgnoreCase(volEnCoursDEdition.getNumeroVol())
                && fenetre.getVolService().trouverParNumero(numeroVol) != null) {
            ComposantsUi.afficherErreur(this, "Un autre vol porte déjà ce numéro.");
            return;
        }
        double prixBase;
        try {
            prixBase = Double.parseDouble(texteprix.replace(",", "."));
        } catch (NumberFormatException e) {
            ComposantsUi.afficherErreur(this, "Le prix de base doit être un nombre (ex: 89.90).");
            return;
        }
        if (prixBase <= 0) {
            ComposantsUi.afficherErreur(this, "Le prix de base doit être supérieur à 0.");
            return;
        }

        if (!ComposantsUi.demanderConfirmation(this, "Confirmer les modifications du vol "
                + volEnCoursDEdition.getNumeroVol() + " ?")) {
            return;
        }

        TypeAvion avion = (TypeAvion) comboAvion.getSelectedItem();
        String duree = fenetre.getVolService().calculerDuree(heureDepart, heureArrivee);

        Vol volModifie = new Vol(volEnCoursDEdition.getDepAeroport(), volEnCoursDEdition.getDepTerminal(),
                volEnCoursDEdition.getDepVille(), volEnCoursDEdition.getArrAeroport(),
                volEnCoursDEdition.getArrTerminal(), volEnCoursDEdition.getArrVille(),
                heureDepart, heureArrivee, numeroVol,
                avion.getNom(), volEnCoursDEdition.getCompagnie(), duree,
                avion.getTotalPlaces(), avion.getPlacesBusiness(), avion.getPlacesExtraLeg(),
                avion.getPlacesEco(), avion.getPlacesMineur(), prixBase);

        // Le numero de vol sert de cle : s'il change, on supprime l'ancien
        // vol puis on ajoute le nouveau au lieu de le mettre a jour.
        if (!numeroVol.equalsIgnoreCase(volEnCoursDEdition.getNumeroVol())) {
            fenetre.getVolService().supprimerVol(volEnCoursDEdition.getNumeroVol());
            try {
                fenetre.getVolService().ajouterVol(volModifie);
            } catch (IllegalArgumentException e) {
                ComposantsUi.afficherErreur(this, e.getMessage());
                return;
            }
        } else {
            fenetre.getVolService().modifierVol(volModifie);
        }

        volEnCoursDEdition = volModifie;
        labelMessage.setForeground(Couleurs.VERT_DISPONIBLE.darker());
        labelMessage.setText("Vol " + volModifie.getNumeroVol() + " modifié avec succès !");
    }
}
