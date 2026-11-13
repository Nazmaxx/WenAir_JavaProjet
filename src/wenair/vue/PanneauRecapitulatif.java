package wenair.vue;

import wenair.modele.Billet;
import wenair.modele.Vol;
import wenair.utilitaires.Validation;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/**
 * Récapitulatif de la réservation (passagers, sièges, prix) et paiement
 * simulé. Numéro de carte à 10 chiffres, expiration MM/AA (mois 1-12,
 * année entre 26 et 45), CVV à 3 chiffres.
 */
public class PanneauRecapitulatif extends JPanel {

    private final FenetrePrincipale fenetre;

    private final PanneauListeVerticale listeBillets = new PanneauListeVerticale();
    private final JLabel labelTotal = new JLabel();

    private final JTextField champCarte = ComposantsUi.creerChampTexte(14);
    private final JTextField champExpiration = ComposantsUi.creerChampTexte(6);
    private final JTextField champCvv = ComposantsUi.creerChampTexte(5);
    private final JLabel labelErreur = new JLabel(" ");

    public PanneauRecapitulatif(FenetrePrincipale fenetre) {
        this.fenetre = fenetre;

        setLayout(new BorderLayout());
        setBackground(Couleurs.ROSE_FOND);
        setBorder(BorderFactory.createEmptyBorder(30, 60, 20, 60));

        add(ComposantsUi.creerEnTete("Récapitulatif et paiement", fenetre, FenetrePrincipale.SIEGES), BorderLayout.NORTH);

        listeBillets.setLayout(new BoxLayout(listeBillets, BoxLayout.Y_AXIS));
        listeBillets.setBackground(Couleurs.ROSE_FOND);

        JScrollPane defilement = new JScrollPane(listeBillets);
        defilement.setBorder(BorderFactory.createEmptyBorder());
        defilement.getViewport().setBackground(Couleurs.ROSE_FOND);
        defilement.getVerticalScrollBar().setUnitIncrement(16);

        JPanel centre = new JPanel(new BorderLayout());
        centre.setBackground(Couleurs.ROSE_FOND);
        centre.add(defilement, BorderLayout.CENTER);

        labelTotal.setFont(Couleurs.POLICE_SOUS_TITRE);
        labelTotal.setForeground(Couleurs.ROSE_FONCE);
        labelTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        centre.add(labelTotal, BorderLayout.SOUTH);

        add(centre, BorderLayout.CENTER);

        JPanel panneauPaiement = new JPanel(new GridLayout(4, 1, 0, 8));
        panneauPaiement.setBackground(Couleurs.ROSE_PANNEAU);
        panneauPaiement.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Paiement (simulé)"),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        JPanel ligneCarte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ligneCarte.setBackground(Couleurs.ROSE_PANNEAU);
        ligneCarte.add(ComposantsUi.creerLabelChamp("Numéro de carte (10 chiffres) :"));
        ligneCarte.add(champCarte);

        JPanel ligneExpiration = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ligneExpiration.setBackground(Couleurs.ROSE_PANNEAU);
        ligneExpiration.add(ComposantsUi.creerLabelChamp("Expiration (MM/AA) :"));
        ligneExpiration.add(champExpiration);

        JPanel ligneCvv = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ligneCvv.setBackground(Couleurs.ROSE_PANNEAU);
        ligneCvv.add(ComposantsUi.creerLabelChamp("CVV (3 chiffres) :"));
        ligneCvv.add(champCvv);

        labelErreur.setFont(Couleurs.POLICE_PETITE);
        labelErreur.setForeground(Couleurs.ROUGE_INDISPONIBLE);

        panneauPaiement.add(ligneCarte);
        panneauPaiement.add(ligneExpiration);
        panneauPaiement.add(ligneCvv);
        panneauPaiement.add(labelErreur);

        JButton boutonPayer = ComposantsUi.creerBouton("Payer et confirmer la réservation");
        boutonPayer.addActionListener(e -> payerEtConfirmer());

        JPanel panneauBas = new JPanel(new BorderLayout());
        panneauBas.setBackground(Couleurs.ROSE_FOND);
        panneauBas.add(panneauPaiement, BorderLayout.CENTER);
        JPanel conteneurBouton = new JPanel();
        conteneurBouton.setBackground(Couleurs.ROSE_FOND);
        conteneurBouton.add(boutonPayer);
        panneauBas.add(conteneurBouton, BorderLayout.SOUTH);

        add(panneauBas, BorderLayout.SOUTH);
    }

    public void rafraichir() {
        EtatReservation etat = fenetre.getEtat();
        Vol vol = etat.getResultatChoisi().getVol();

        listeBillets.removeAll();
        champCarte.setText("");
        champExpiration.setText("");
        champCvv.setText("");
        labelErreur.setText(" ");

        double total = 0;

        for (Billet billet : etat.getBillets()) {
            JPanel ligne = new JPanel(new BorderLayout());
            ligne.setBackground(Couleurs.ROSE_PANNEAU);
            ligne.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Couleurs.BORDURE_CARTE, 1),
                    BorderFactory.createEmptyBorder(6, 14, 6, 14)));
            ligne.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 34));

            String siegeTexte = billet.estBebe() ? "N/A"
                    : (billet.getSiege() == null ? "check-in aéroport" : billet.getSiege());
            int bagageInclus = fenetre.getTarifService().bagageInclusKg(billet.getClasse());
            JLabel labelPassager = new JLabel("Passager : " + billet.getCivilite() + " " + billet.getNom() + " " + billet.getPrenom()
                    + "     Classe : " + billet.getClasse() + "     Siège : " + siegeTexte
                    + "     Bagage inclus : " + bagageInclus + "kg");
            labelPassager.setFont(Couleurs.POLICE_PETITE);
            labelPassager.setForeground(Couleurs.TEXTE);

            JLabel labelPrix = new JLabel(String.format("%.2f EUR", billet.getPrixTotal()));
            labelPrix.setFont(Couleurs.POLICE_NORMALE.deriveFont(java.awt.Font.BOLD));
            labelPrix.setForeground(Couleurs.ROSE_FONCE);

            ligne.add(labelPassager, BorderLayout.CENTER);
            ligne.add(labelPrix, BorderLayout.EAST);

            listeBillets.add(ligne);
            listeBillets.add(javax.swing.Box.createVerticalStrut(4));

            total += billet.getPrixTotal();
        }

        labelTotal.setText(String.format("Total : %.2f EUR", total));

        listeBillets.revalidate();
        listeBillets.repaint();
    }

    private void payerEtConfirmer() {
        labelErreur.setText(" ");

        String carte = champCarte.getText().trim();
        String expiration = champExpiration.getText().trim();
        String cvv = champCvv.getText().trim();

        if (!Validation.estCarteValide(carte)) {
            labelErreur.setText("Le numéro de carte doit comporter exactement 10 chiffres.");
            return;
        }
        if (!Validation.estExpirationValide(expiration)) {
            labelErreur.setText("Expiration invalide : format MM/AA attendu, année entre 26 et 45.");
            return;
        }
        if (!Validation.estCvvValide(cvv)) {
            labelErreur.setText("Le CVV doit comporter exactement 3 chiffres.");
            return;
        }

        EtatReservation etat = fenetre.getEtat();
        Vol vol = etat.getResultatChoisi().getVol();
        String refCommande = fenetre.getReservationService().genererRefCommande();

        double total = 0;
        for (Billet billet : etat.getBillets()) {
            billet.setNumeroVol(vol.getNumeroVol());
            billet.setDateVol(etat.getDateVol());
            billet.setRefCommande(refCommande);
            billet.setRefBillet(fenetre.getReservationService().genererRefBillet());
            billet.setNumeroCarte(carte);
            billet.setExpirationCarte(expiration);
            billet.setCvv(cvv);
            total += billet.getPrixTotal();
        }

        fenetre.getReservationService().enregistrerReservation(etat.getBillets());

        etat.setRefCommandeConfirmee(refCommande);
        etat.setMontantTotalConfirme(total);

        fenetre.afficherPanneau(FenetrePrincipale.CONFIRMATION);
    }
}
