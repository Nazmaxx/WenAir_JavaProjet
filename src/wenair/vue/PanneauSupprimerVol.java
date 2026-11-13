package wenair.vue;

import wenair.modele.Vol;
import wenair.utilitaires.Validation;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/**
 * Ecran admin pour supprimer un vol : recherche par numero de vol,
 * apercu du vol trouve, puis suppression apres confirmation.
 */
public class PanneauSupprimerVol extends JPanel {

    private final FenetrePrincipale fenetre;

    private final JTextField champRecherche = ComposantsUi.creerChampTexte(10);
    private final JLabel labelErreur = new JLabel(" ");

    private final JPanel carteVol = new JPanel(new GridLayout(6, 1, 0, 4));
    private final JLabel labelVol = new JLabel();
    private final JLabel labelTrajet = new JLabel();
    private final JLabel labelHoraires = new JLabel();
    private final JLabel labelAvion = new JLabel();
    private final JLabel labelPrix = new JLabel();
    private final JButton boutonSupprimer = ComposantsUi.creerBouton("Supprimer ce vol");

    private Vol volTrouve;

    public PanneauSupprimerVol(FenetrePrincipale fenetre) {
        this.fenetre = fenetre;

        setLayout(new BorderLayout());
        setBackground(Couleurs.ROSE_FOND);
        setBorder(BorderFactory.createEmptyBorder(40, 70, 30, 70));

        add(ComposantsUi.creerEnTete("Supprimer un vol", fenetre, FenetrePrincipale.ADMIN_MENU), BorderLayout.NORTH);

        JPanel formulaire = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formulaire.setBackground(Couleurs.ROSE_PANNEAU);
        formulaire.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Couleurs.BORDURE_CARTE, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        formulaire.add(ComposantsUi.creerLabelChamp("Numéro du vol à supprimer :"));
        formulaire.add(champRecherche);
        JButton boutonRechercher = ComposantsUi.creerBouton("Rechercher");
        boutonRechercher.addActionListener(e -> rechercherVol());
        formulaire.add(boutonRechercher);

        JPanel enTeteFormulaire = new JPanel(new BorderLayout());
        enTeteFormulaire.setBackground(Couleurs.ROSE_FOND);
        enTeteFormulaire.add(formulaire, BorderLayout.NORTH);
        labelErreur.setFont(Couleurs.POLICE_PETITE);
        labelErreur.setForeground(Couleurs.ROUGE_INDISPONIBLE);
        enTeteFormulaire.add(labelErreur, BorderLayout.SOUTH);

        carteVol.setBackground(Couleurs.ROSE_PANNEAU);
        carteVol.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Couleurs.BORDURE_CARTE, 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));
        labelVol.setFont(Couleurs.POLICE_SOUS_TITRE);
        labelVol.setForeground(Couleurs.ROSE_FONCE);
        for (JLabel l : new JLabel[] { labelTrajet, labelHoraires, labelAvion, labelPrix }) {
            l.setFont(Couleurs.POLICE_NORMALE);
            l.setForeground(Couleurs.TEXTE);
        }
        carteVol.add(labelVol);
        carteVol.add(labelTrajet);
        carteVol.add(labelHoraires);
        carteVol.add(labelAvion);
        carteVol.add(labelPrix);

        boutonSupprimer.addActionListener(e -> supprimerVol());
        JPanel panneauBouton = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panneauBouton.setBackground(Couleurs.ROSE_PANNEAU);
        panneauBouton.add(boutonSupprimer);
        carteVol.add(panneauBouton);

        carteVol.setVisible(false);

        JPanel centre = new JPanel();
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));
        centre.setBackground(Couleurs.ROSE_FOND);
        centre.add(enTeteFormulaire);
        centre.add(javax.swing.Box.createVerticalStrut(20));
        centre.add(carteVol);

        add(centre, BorderLayout.CENTER);
    }

    /** Reinitialise l'ecran (masque la carte de resultat) a chaque entree. */
    public void rafraichir() {
        champRecherche.setText("");
        labelErreur.setText(" ");
        carteVol.setVisible(false);
        volTrouve = null;
    }

    private void rechercherVol() {
        String numeroVol = champRecherche.getText().trim().toUpperCase();
        if (!Validation.estRempli(numeroVol)) {
            labelErreur.setText("Veuillez saisir un numéro de vol.");
            carteVol.setVisible(false);
            return;
        }

        Vol vol = fenetre.getVolService().trouverParNumero(numeroVol);
        if (vol == null) {
            labelErreur.setText("Vol " + numeroVol + " introuvable.");
            carteVol.setVisible(false);
            return;
        }

        labelErreur.setText(" ");
        volTrouve = vol;

        labelVol.setText("Vol : " + vol.getNumeroVol());
        labelTrajet.setText("Trajet : " + vol.getDepVille() + " -> " + vol.getArrVille());
        labelHoraires.setText("Horaires : " + vol.getHeureDepart() + " -> " + vol.getHeureArrivee());
        labelAvion.setText("Avion : " + vol.getAvion());
        labelPrix.setText("Prix de base : " + String.format("%.2f", vol.getPrixBase()) + " EUR");

        carteVol.setVisible(true);
    }

    private void supprimerVol() {
        if (volTrouve == null) {
            return;
        }
        boolean confirme = ComposantsUi.demanderConfirmation(this,
                "Attention : cette action est irréversible.\n"
                        + "Confirmer la suppression du vol " + volTrouve.getNumeroVol() + " ?");
        if (!confirme) {
            return;
        }

        fenetre.getVolService().supprimerVol(volTrouve.getNumeroVol());
        ComposantsUi.afficherInfo(this, "Vol " + volTrouve.getNumeroVol() + " supprimé avec succès.");
        rafraichir();
    }
}
