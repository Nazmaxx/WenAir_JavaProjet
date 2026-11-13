package wenair.vue;

import wenair.modele.Billet;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

/**
 * Écran de confirmation affiché une fois le paiement effectué et la
 * réservation enregistrée : message de validation, mémo aéroport,
 * référence de dossier et référence de chaque billet.
 */
public class PanneauConfirmation extends JPanel {

    private static final String MESSAGE_VALIDATION =
            "Félicitations ! Votre billet a été validé avec succès. Vous êtes officiellement enregistré(e) pour votre vol.\n\n"
            + "Merci d'avoir choisi notre compagnie pour votre voyage. Nous mettons tout en œuvre pour vous garantir une "
            + "expérience agréable, du décollage jusqu'à l'atterrissage. Notre équipe reste à votre disposition pour "
            + "toute question avant votre départ.\n\n"
            + "Nous vous souhaitons un excellent voyage et espérons vous revoir très bientôt à bord de l'un de nos vols !\n\n"
            + "Petit mémo : à faire à l'aéroport\n"
            + "Rappel avant votre vol :\n"
            + "1. Arrivez au moins 2h avant le décollage\n"
            + "2. Présentez votre pièce d'identité + billet à l'enregistrement\n"
            + "3. Déposez vos bagages en soute si nécessaire\n"
            + "4. Passez le contrôle de sécurité\n"
            + "5. Vérifiez votre porte d'embarquement sur les écrans d'affichage\n"
            + "6. Présentez-vous à la porte au moins 30 min avant le départ";

    private final FenetrePrincipale fenetre;
    private final JLabel labelMerci = new JLabel();
    private final JLabel labelRef = new JLabel();
    private final JTextArea zoneMessage = new JTextArea();
    private final PanneauListeVerticale listeBillets = new PanneauListeVerticale();

    public PanneauConfirmation(FenetrePrincipale fenetre) {
        this.fenetre = fenetre;

        setLayout(new BorderLayout());
        setBackground(Couleurs.ROSE_FOND);
        setBorder(BorderFactory.createEmptyBorder(60, 80, 40, 80));

        labelMerci.setFont(Couleurs.POLICE_TITRE);
        labelMerci.setForeground(Couleurs.ROSE_FONCE);
        labelMerci.setHorizontalAlignment(SwingConstants.CENTER);
        labelMerci.setText("VALIDATION RÉUSSIE");

        labelRef.setFont(Couleurs.POLICE_SOUS_TITRE);
        labelRef.setForeground(Couleurs.TEXTE);
        labelRef.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel enTete = new JPanel(new java.awt.GridLayout(2, 1, 0, 12));
        enTete.setBackground(Couleurs.ROSE_FOND);
        enTete.add(labelMerci);
        enTete.add(labelRef);
        add(enTete, BorderLayout.NORTH);

        zoneMessage.setText(MESSAGE_VALIDATION);
        zoneMessage.setEditable(false);
        zoneMessage.setFocusable(false);
        zoneMessage.setLineWrap(true);
        zoneMessage.setWrapStyleWord(true);
        zoneMessage.setFont(Couleurs.POLICE_NORMALE);
        zoneMessage.setForeground(Couleurs.TEXTE);
        zoneMessage.setBackground(Couleurs.ROSE_PANNEAU);
        zoneMessage.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Couleurs.BORDURE_CARTE, 1),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)));
        zoneMessage.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelVosBillets = ComposantsUi.creerLabelChamp("Vos billets :");
        labelVosBillets.setFont(Couleurs.POLICE_SOUS_TITRE);
        labelVosBillets.setForeground(Couleurs.ROSE_FONCE);
        labelVosBillets.setAlignmentX(Component.LEFT_ALIGNMENT);
        labelVosBillets.setBorder(BorderFactory.createEmptyBorder(20, 0, 8, 0));

        listeBillets.setLayout(new BoxLayout(listeBillets, BoxLayout.Y_AXIS));
        listeBillets.setBackground(Couleurs.ROSE_FOND);
        listeBillets.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel conteneurCentre = new JPanel();
        conteneurCentre.setLayout(new BoxLayout(conteneurCentre, BoxLayout.Y_AXIS));
        conteneurCentre.setBackground(Couleurs.ROSE_FOND);
        conteneurCentre.add(zoneMessage);
        conteneurCentre.add(labelVosBillets);
        conteneurCentre.add(listeBillets);

        JScrollPane defilement = new JScrollPane(conteneurCentre);
        defilement.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        defilement.setBackground(Couleurs.ROSE_FOND);
        defilement.getViewport().setBackground(Couleurs.ROSE_FOND);
        defilement.getVerticalScrollBar().setUnitIncrement(16);
        add(defilement, BorderLayout.CENTER);

        JButton boutonAccueil = ComposantsUi.creerBouton("Retour à l'accueil");
        boutonAccueil.addActionListener(e -> {
            fenetre.getEtat().reinitialiser();
            fenetre.afficherPanneau(FenetrePrincipale.ACCUEIL);
        });

        JPanel panneauBas = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panneauBas.setBackground(Couleurs.ROSE_FOND);
        panneauBas.add(boutonAccueil);
        add(panneauBas, BorderLayout.SOUTH);
    }

    public void rafraichir() {
        EtatReservation etat = fenetre.getEtat();
        labelRef.setText("Référence de votre dossier : " + etat.getRefCommandeConfirmee()
                + "   -   Total payé : " + String.format("%.2f", etat.getMontantTotalConfirme()) + " EUR");

        listeBillets.removeAll();
        for (Billet billet : etat.getBillets()) {
            JPanel ligne = new JPanel(new BorderLayout());
            ligne.setBackground(Couleurs.ROSE_PANNEAU);
            ligne.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Couleurs.BORDURE_CARTE, 1),
                    BorderFactory.createEmptyBorder(6, 14, 6, 14)));
            ligne.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 34));

            String siegeTexte = billet.estBebe() ? "N/A"
                    : (billet.getSiege() == null ? "check-in aéroport" : billet.getSiege());
            JLabel labelBillet = new JLabel("N° billet : " + billet.getRefBillet()
                    + "     Passager : " + billet.getNom() + " " + billet.getPrenom()
                    + "     Siège : " + siegeTexte);
            labelBillet.setFont(Couleurs.POLICE_PETITE);
            labelBillet.setForeground(Couleurs.TEXTE);

            ligne.add(labelBillet, BorderLayout.CENTER);
            listeBillets.add(ligne);
            listeBillets.add(javax.swing.Box.createVerticalStrut(4));
        }

        listeBillets.revalidate();
        listeBillets.repaint();
    }
}
