package wenair.vue;

import wenair.modele.Billet;
import wenair.modele.Reservation;
import wenair.utilitaires.Validation;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/**
 * Consultation et gestion d'une réservation existante : authentification
 * par référence de dossier + nom, puis annulation d'un billet ou de tout
 * le dossier.
 */
public class PanneauConsultation extends JPanel {

    private final FenetrePrincipale fenetre;

    private final JTextField champRef = ComposantsUi.creerChampTexte(10);
    private final JTextField champNom = ComposantsUi.creerChampTexte(15);
    private final JLabel labelErreur = new JLabel(" ");
    private final PanneauListeVerticale panneauResultat = new PanneauListeVerticale();

    private String refCommandeCourante;

    public PanneauConsultation(FenetrePrincipale fenetre) {
        this.fenetre = fenetre;

        setLayout(new BorderLayout());
        setBackground(Couleurs.ROSE_FOND);
        setBorder(BorderFactory.createEmptyBorder(40, 70, 30, 70));

        JPanel formulaire = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formulaire.setBackground(Couleurs.ROSE_PANNEAU);
        formulaire.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Couleurs.BORDURE_CARTE, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        formulaire.add(ComposantsUi.creerLabelChamp("Référence de dossier :"));
        formulaire.add(champRef);
        formulaire.add(ComposantsUi.creerLabelChamp("Nom :"));
        formulaire.add(champNom);
        JButton boutonRechercher = ComposantsUi.creerBouton("Consulter");
        boutonRechercher.addActionListener(e -> rechercherReservation());
        formulaire.add(boutonRechercher);

        labelErreur.setFont(Couleurs.POLICE_PETITE);
        labelErreur.setForeground(Couleurs.ROUGE_INDISPONIBLE);

        JPanel enTeteFormulaire = new JPanel(new BorderLayout());
        enTeteFormulaire.setBackground(Couleurs.ROSE_FOND);
        enTeteFormulaire.add(formulaire, BorderLayout.NORTH);
        enTeteFormulaire.add(labelErreur, BorderLayout.SOUTH);

        panneauResultat.setLayout(new BoxLayout(panneauResultat, BoxLayout.Y_AXIS));
        panneauResultat.setBackground(Couleurs.ROSE_FOND);
        JScrollPane defilement = new JScrollPane(panneauResultat);
        defilement.setBorder(BorderFactory.createEmptyBorder());
        defilement.getViewport().setBackground(Couleurs.ROSE_FOND);

        JPanel centre = new JPanel(new BorderLayout());
        centre.setBackground(Couleurs.ROSE_FOND);
        centre.add(enTeteFormulaire, BorderLayout.NORTH);
        centre.add(defilement, BorderLayout.CENTER);

        add(ComposantsUi.creerEnTete("Consulter ma réservation", fenetre, FenetrePrincipale.ESPACE_CLIENT), BorderLayout.NORTH);
        add(centre, BorderLayout.CENTER);
    }

    public void rafraichir() {
        champRef.setText("");
        champNom.setText("");
        labelErreur.setText(" ");
        panneauResultat.removeAll();
        refCommandeCourante = null;
        panneauResultat.revalidate();
        panneauResultat.repaint();
    }

    private void rechercherReservation() {
        String ref = champRef.getText().trim().toUpperCase();
        String nom = champNom.getText().trim();

        if (!Validation.estRempli(ref) || !Validation.estRempli(nom)) {
            labelErreur.setText("Merci de renseigner la référence de dossier et le nom.");
            return;
        }

        Reservation reservation = fenetre.getReservationService().authentifierClient(ref, nom);
        if (reservation == null) {
            labelErreur.setText("Aucune réservation trouvée pour cette référence et ce nom.");
            panneauResultat.removeAll();
            panneauResultat.revalidate();
            panneauResultat.repaint();
            return;
        }

        labelErreur.setText(" ");
        refCommandeCourante = ref;
        afficherReservation(reservation);
    }

    private void afficherReservation(Reservation reservation) {
        panneauResultat.removeAll();

        String dateAffichee = reservation.getDateVol() == null ? "N/A" : reservation.getDateVol();
        JLabel labelDossier = ComposantsUi.creerLabelChamp("Dossier : " + reservation.getRefCommande()
                + "   Vol : " + reservation.getNumeroVol() + "   Date : " + dateAffichee
                + "   " + reservation.getNombrePassagers() + " passager(s)");
        labelDossier.setFont(Couleurs.POLICE_PETITE.deriveFont(java.awt.Font.BOLD));
        labelDossier.setForeground(Couleurs.ROSE_FONCE);
        panneauResultat.add(labelDossier);
        panneauResultat.add(javax.swing.Box.createVerticalStrut(10));

        for (Billet billet : reservation.getBillets()) {
            JPanel ligne = new JPanel();
            ligne.setLayout(new BoxLayout(ligne, BoxLayout.Y_AXIS));
            ligne.setBackground(Couleurs.ROSE_PANNEAU);
            ligne.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Couleurs.BORDURE_CARTE, 1),
                    BorderFactory.createEmptyBorder(6, 14, 6, 14)));
            ligne.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 118));
            ligne.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

            String siegeTexte = billet.estBebe() ? "N/A"
                    : (billet.getSiege() == null ? "check-in aéroport" : billet.getSiege());
            JLabel labelBillet = new JLabel("N° billet : " + billet.getRefBillet()
                    + "     Passager : " + billet.getNom() + " " + billet.getPrenom()
                    + "     Classe : " + billet.getClasse() + "     Siège : " + siegeTexte);
            labelBillet.setFont(Couleurs.POLICE_PETITE);
            labelBillet.setForeground(Couleurs.TEXTE);
            labelBillet.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            ligne.add(labelBillet);
            ligne.add(javax.swing.Box.createVerticalStrut(6));

            // Boutons repartis sur deux rangees pour ne pas depasser la largeur du panneau.
            JPanel boutons = new JPanel();
            boutons.setLayout(new BoxLayout(boutons, BoxLayout.Y_AXIS));
            boutons.setBackground(Couleurs.ROSE_PANNEAU);
            boutons.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

            JPanel boutonsLigne1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            boutonsLigne1.setBackground(Couleurs.ROSE_PANNEAU);
            boutonsLigne1.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

            JPanel boutonsLigne2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            boutonsLigne2.setBackground(Couleurs.ROSE_PANNEAU);
            boutonsLigne2.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

            JButton boutonOptions = ComposantsUi.creerBoutonSecondaire("Options du billet");
            boutonOptions.addActionListener(e -> fenetre.ouvrirActionSurBillet(
                    FenetrePrincipale.OPTIONS_BILLET, billet.getRefBillet(), refCommandeCourante));
            boutonsLigne1.add(boutonOptions);

            JButton boutonCarte = ComposantsUi.creerBoutonSecondaire("Carte d'embarquement");
            boutonCarte.addActionListener(e -> fenetre.ouvrirActionSurBillet(
                    FenetrePrincipale.CARTE_EMBARQUEMENT, billet.getRefBillet(), refCommandeCourante));
            boutonsLigne1.add(boutonCarte);

            JButton boutonSurclasser = ComposantsUi.creerBoutonSecondaire("Surclasser");
            boutonSurclasser.addActionListener(e -> fenetre.ouvrirActionSurBillet(
                    FenetrePrincipale.SURCLASSEMENT, billet.getRefBillet(), refCommandeCourante));
            boutonsLigne2.add(boutonSurclasser);

            JButton boutonAnnulerBillet = ComposantsUi.creerBoutonSecondaire("Annuler ce billet");
            boutonAnnulerBillet.addActionListener(e -> annulerBillet(billet.getRefBillet()));
            boutonsLigne2.add(boutonAnnulerBillet);

            boutons.add(boutonsLigne1);
            boutons.add(boutonsLigne2);

            ligne.add(boutons);

            panneauResultat.add(ligne);
            panneauResultat.add(javax.swing.Box.createVerticalStrut(5));
        }

        JButton boutonAnnulerTout = ComposantsUi.creerBoutonSecondaire("Annuler toute la réservation");
        boutonAnnulerTout.addActionListener(e -> annulerReservation());
        JPanel conteneurBouton = new JPanel(new FlowLayout(FlowLayout.LEFT));
        conteneurBouton.setBackground(Couleurs.ROSE_FOND);
        conteneurBouton.add(boutonAnnulerTout);
        panneauResultat.add(conteneurBouton);

        panneauResultat.revalidate();
        panneauResultat.repaint();
    }

    private void annulerBillet(String refBillet) {
        if (!ComposantsUi.demanderConfirmation(this, "Annuler ce billet (" + refBillet + ") ?")) {
            return;
        }
        fenetre.getReservationService().annulerBillet(refBillet);
        ComposantsUi.afficherInfo(this, "Billet annulé.");
        rafraichirApresAction();
    }

    private void annulerReservation() {
        if (!ComposantsUi.demanderConfirmation(this, "Annuler toute la réservation " + refCommandeCourante + " ?")) {
            return;
        }
        fenetre.getReservationService().annulerReservation(refCommandeCourante);
        ComposantsUi.afficherInfo(this, "Réservation annulée.");
        panneauResultat.removeAll();
        panneauResultat.revalidate();
        panneauResultat.repaint();
        refCommandeCourante = null;
    }

    /** Recharge et reaffiche le dossier courant (utilise apres une annulation ou au retour d'un ecran d'action). */
    public void rafraichirApresAction() {
        Reservation reservation = fenetre.getReservationService().consulter(refCommandeCourante);
        if (reservation == null || reservation.getBillets().isEmpty()) {
            panneauResultat.removeAll();
            panneauResultat.revalidate();
            panneauResultat.repaint();
            refCommandeCourante = null;
        } else {
            afficherReservation(reservation);
        }
    }
}
