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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Ecran admin "Ajouter un vol" : saisie d'un nouveau vol, avec validation
 * en direct des champs sensibles (horaires, numero de vol) et
 * recapitulatif de confirmation avant enregistrement.
 */
public class PanneauAjouterVol extends JPanel {

    private final FenetrePrincipale fenetre;

    private final JTextField champVilleDepart = ComposantsUi.creerChampTexte(15);
    private final JTextField champAeroportDepart = ComposantsUi.creerChampTexte(6);
    private final JTextField champTerminalDepart = ComposantsUi.creerChampTexte(10);
    private final JTextField champVilleArrivee = ComposantsUi.creerChampTexte(15);
    private final JTextField champAeroportArrivee = ComposantsUi.creerChampTexte(6);
    private final JTextField champTerminalArrivee = ComposantsUi.creerChampTexte(10);
    private final JTextField champHeureDepart = ComposantsUi.creerChampTexte(6);
    private final JTextField champHeureArrivee = ComposantsUi.creerChampTexte(6);
    private final JLabel labelDuree = ComposantsUi.creerLabelAide();
    private final JTextField champNumeroVol = ComposantsUi.creerChampTexte(10);
    private final JLabel labelAideNumeroVol = ComposantsUi.creerLabelAide();
    private final JComboBox<TypeAvion> comboAvion = new JComboBox<TypeAvion>(TypeAvion.TOUS);
    private final JLabel labelDetailAvion = ComposantsUi.creerLabelAide();
    private final JTextField champPrixBase = ComposantsUi.creerChampTexte(8);
    private final JLabel labelMessage = new JLabel(" ");

    public PanneauAjouterVol(FenetrePrincipale fenetre) {
        this.fenetre = fenetre;

        setLayout(new BorderLayout());
        setBackground(Couleurs.ROSE_FOND);
        setBorder(BorderFactory.createEmptyBorder(40, 70, 30, 70));

        add(ComposantsUi.creerEnTete("Ajouter un vol", fenetre, FenetrePrincipale.ADMIN_MENU), BorderLayout.NORTH);

        JPanel formulaire = new JPanel(new GridBagLayout());
        formulaire.setBackground(Couleurs.ROSE_PANNEAU);
        formulaire.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Couleurs.BORDURE_CARTE, 1),
                BorderFactory.createEmptyBorder(25, 35, 25, 35)));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.anchor = GridBagConstraints.WEST;

        int ligne = 0;
        ligne = ajouterLigne(formulaire, c, ligne, "Ville de départ :", champVilleDepart);
        ligne = ajouterLigne(formulaire, c, ligne, "Aéroport de départ (ex: CDG) :", champAeroportDepart);
        ligne = ajouterLigne(formulaire, c, ligne, "Terminal de départ :", champTerminalDepart);
        ligne = ajouterLigne(formulaire, c, ligne, "Ville d'arrivée :", champVilleArrivee);
        ligne = ajouterLigne(formulaire, c, ligne, "Aéroport d'arrivée :", champAeroportArrivee);
        ligne = ajouterLigne(formulaire, c, ligne, "Terminal d'arrivée :", champTerminalArrivee);
        ligne = ajouterLigne(formulaire, c, ligne, "Heure de départ (HH:MM) :", champHeureDepart);
        ligne = ajouterLigne(formulaire, c, ligne, "Heure d'arrivée (HH:MM) :", champHeureArrivee);

        c.gridx = 1;
        c.gridy = ligne++;
        formulaire.add(labelDuree, c);

        ligne = ajouterLigne(formulaire, c, ligne, "Numéro de vol (ex: WN7300) :", champNumeroVol);

        c.gridx = 1;
        c.gridy = ligne++;
        formulaire.add(labelAideNumeroVol, c);

        c.gridx = 0;
        c.gridy = ligne;
        formulaire.add(ComposantsUi.creerLabelChamp("Compagnie :"), c);
        c.gridx = 1;
        JLabel labelCompagnie = ComposantsUi.creerLabelChamp("Wen Air (automatique)");
        labelCompagnie.setForeground(Couleurs.TEXTE_SECONDAIRE);
        formulaire.add(labelCompagnie, c);
        ligne++;

        ligne = ajouterLigne(formulaire, c, ligne, "Type d'avion :", comboAvion);

        c.gridx = 1;
        c.gridy = ligne++;
        labelDetailAvion.setForeground(Couleurs.TEXTE_SECONDAIRE);
        formulaire.add(labelDetailAvion, c);

        ligne = ajouterLigne(formulaire, c, ligne, "Prix de base (EUR) :", champPrixBase);

        labelMessage.setFont(Couleurs.POLICE_PETITE);

        JButton boutonAjouter = ComposantsUi.creerBouton("Ajouter le vol");
        boutonAjouter.addActionListener(e -> validerEtAjouter());

        c.gridx = 0;
        c.gridy = ligne++;
        c.gridwidth = 2;
        formulaire.add(boutonAjouter, c);
        c.gridy = ligne;
        formulaire.add(labelMessage, c);

        comboAvion.addActionListener(e -> mettreAJourDetailAvion());
        mettreAJourDetailAvion();

        installerValidationDirecte();

        JScrollPane defilement = new JScrollPane(formulaire);
        defilement.setBorder(BorderFactory.createEmptyBorder());
        defilement.getViewport().setBackground(Couleurs.ROSE_FOND);
        defilement.getVerticalScrollBar().setUnitIncrement(16);
        add(defilement, BorderLayout.CENTER);
    }

    private int ajouterLigne(JPanel formulaire, GridBagConstraints c, int ligne, String label, java.awt.Component champ) {
        c.gridx = 0;
        c.gridy = ligne;
        c.gridwidth = 1;
        formulaire.add(ComposantsUi.creerLabelChamp(label), c);
        c.gridx = 1;
        formulaire.add(champ, c);
        return ligne + 1;
    }

    private void installerValidationDirecte() {
        DocumentListener validateur = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { validerHorairesEtNumero(); }
            @Override
            public void removeUpdate(DocumentEvent e) { validerHorairesEtNumero(); }
            @Override
            public void changedUpdate(DocumentEvent e) { validerHorairesEtNumero(); }
        };
        champHeureDepart.getDocument().addDocumentListener(validateur);
        champHeureArrivee.getDocument().addDocumentListener(validateur);
        champNumeroVol.getDocument().addDocumentListener(validateur);
    }

    private void mettreAJourDetailAvion() {
        TypeAvion avion = (TypeAvion) comboAvion.getSelectedItem();
        labelDetailAvion.setText(avion == null ? " " : avion.detailPlaces());
    }

    private void validerHorairesEtNumero() {
        String heureDepart = champHeureDepart.getText().trim();
        String heureArrivee = champHeureArrivee.getText().trim();

        if (Validation.estHeureValide(heureDepart) && Validation.estHeureValide(heureArrivee)) {
            String duree = fenetre.getVolService().calculerDuree(heureDepart, heureArrivee);
            labelDuree.setText("Durée calculée automatiquement : " + duree);
            labelDuree.setForeground(Couleurs.VERT_DISPONIBLE.darker());
        } else if (heureDepart.isEmpty() && heureArrivee.isEmpty()) {
            labelDuree.setText(" ");
        } else {
            labelDuree.setText("Format attendu : HH:MM");
            labelDuree.setForeground(Couleurs.ROUGE_INDISPONIBLE);
        }

        String numeroVol = champNumeroVol.getText().trim().toUpperCase();
        if (numeroVol.isEmpty()) {
            labelAideNumeroVol.setText(" ");
        } else if (!Validation.estNumeroVolValide(numeroVol)) {
            labelAideNumeroVol.setText("Format attendu : WN suivi de 4 chiffres (ex: WN7300)");
            labelAideNumeroVol.setForeground(Couleurs.ROUGE_INDISPONIBLE);
        } else if (fenetre.getVolService().trouverParNumero(numeroVol) != null) {
            labelAideNumeroVol.setText("Ce numéro de vol existe déjà.");
            labelAideNumeroVol.setForeground(Couleurs.ROUGE_INDISPONIBLE);
        } else {
            labelAideNumeroVol.setText("Numéro disponible");
            labelAideNumeroVol.setForeground(Couleurs.VERT_DISPONIBLE.darker());
        }
    }

    /** Reinitialise entierement le formulaire a chaque entree sur l'ecran. */
    public void rafraichir() {
        viderChamps();
        labelMessage.setText(" ");
    }

    /** Vide les champs de saisie sans toucher au message de statut (utilise apres un ajout reussi). */
    private void viderChamps() {
        champVilleDepart.setText("");
        champAeroportDepart.setText("");
        champTerminalDepart.setText("");
        champVilleArrivee.setText("");
        champAeroportArrivee.setText("");
        champTerminalArrivee.setText("");
        champHeureDepart.setText("");
        champHeureArrivee.setText("");
        champNumeroVol.setText("");
        champPrixBase.setText("");
        comboAvion.setSelectedIndex(0);
        labelDuree.setText(" ");
        labelAideNumeroVol.setText(" ");
    }

    private void validerEtAjouter() {
        String villeDepart = champVilleDepart.getText().trim();
        String aeroportDepart = champAeroportDepart.getText().trim().toUpperCase();
        String terminalDepart = champTerminalDepart.getText().trim();
        String villeArrivee = champVilleArrivee.getText().trim();
        String aeroportArrivee = champAeroportArrivee.getText().trim().toUpperCase();
        String terminalArrivee = champTerminalArrivee.getText().trim();
        String heureDepart = champHeureDepart.getText().trim();
        String heureArrivee = champHeureArrivee.getText().trim();
        String numeroVol = champNumeroVol.getText().trim().toUpperCase();
        String texteprix = champPrixBase.getText().trim();

        if (!Validation.estRempli(villeDepart) || !Validation.estRempli(aeroportDepart)
                || !Validation.estRempli(terminalDepart) || !Validation.estRempli(villeArrivee)
                || !Validation.estRempli(aeroportArrivee) || !Validation.estRempli(terminalArrivee)) {
            ComposantsUi.afficherErreur(this, "Veuillez remplir tous les champs de trajet.");
            return;
        }
        if (!Validation.estHeureValide(heureDepart) || !Validation.estHeureValide(heureArrivee)) {
            ComposantsUi.afficherErreur(this, "Les horaires doivent être au format HH:MM.");
            return;
        }
        if (!Validation.estNumeroVolValide(numeroVol)) {
            ComposantsUi.afficherErreur(this, "Le numéro de vol doit commencer par WN suivi de 4 chiffres (ex: WN7300).");
            return;
        }
        if (fenetre.getVolService().trouverParNumero(numeroVol) != null) {
            ComposantsUi.afficherErreur(this, "Ce numéro de vol existe déjà.");
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

        TypeAvion avion = (TypeAvion) comboAvion.getSelectedItem();
        String duree = fenetre.getVolService().calculerDuree(heureDepart, heureArrivee);

        Vol vol = new Vol(aeroportDepart, terminalDepart, villeDepart.toUpperCase(),
                aeroportArrivee, terminalArrivee, villeArrivee.toUpperCase(),
                heureDepart, heureArrivee, numeroVol,
                avion.getNom(), "Wen Air", duree,
                avion.getTotalPlaces(), avion.getPlacesBusiness(), avion.getPlacesExtraLeg(),
                avion.getPlacesEco(), avion.getPlacesMineur(), prixBase);

        String recap = "Vol : " + vol.getNumeroVol() + "\n"
                + "Trajet : " + vol.getDepVille() + " -> " + vol.getArrVille() + "\n"
                + "Horaires : " + vol.getHeureDepart() + " -> " + vol.getHeureArrivee() + " (" + vol.getDuree() + ")\n"
                + "Avion : " + vol.getAvion() + "\n"
                + "Places : " + vol.getTotalPlaces() + " total\n"
                + "Prix de base : " + String.format("%.2f", vol.getPrixBase()) + " EUR\n\n"
                + "Confirmer l'ajout de ce vol ?";

        if (!ComposantsUi.demanderConfirmation(this, recap)) {
            return;
        }

        try {
            fenetre.getVolService().ajouterVol(vol);
            labelMessage.setForeground(Couleurs.VERT_DISPONIBLE.darker());
            labelMessage.setText("Vol " + vol.getNumeroVol() + " enregistré avec succès !");
            viderChamps();
        } catch (IllegalArgumentException e) {
            ComposantsUi.afficherErreur(this, e.getMessage());
        }
    }
}
