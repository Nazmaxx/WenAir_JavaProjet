package wenair.vue;

import wenair.modele.Billet;
import wenair.modele.CaseSiege;
import wenair.modele.RangeesCabine;
import wenair.modele.Vol;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

/**
 * Écran de sélection des sièges : carte visuelle et cliquable de la
 * cabine, passager par passager. Les bébés n'ont pas de siège dédié
 * et sont ignorés sur cet écran.
 */
public class PanneauSieges extends JPanel {

    private final FenetrePrincipale fenetre;

    private final JLabel labelPassagerCourant = new JLabel();
    private final JLabel labelConsigne = new JLabel();
    private final JPanel panneauCarte = new JPanel();
    private final JLabel labelInfoSiege = new JLabel(" ");
    private final JLabel labelNoteBebe = new JLabel(" ");
    private final JButton boutonPrecedent = ComposantsUi.creerBoutonSecondaire("< Passager précédent");
    private final JButton boutonAucunSiege = ComposantsUi.creerBoutonSecondaire("Pas de siège (check-in)");
    private final JButton boutonConfirmer = ComposantsUi.creerBouton("Confirmer et continuer");

    private List<Billet> billetsAvecSiege = new ArrayList<Billet>();
    private int indexCourant;
    private String siegeSelectionneCourant;
    private double prixSiegeSelectionneCourant;

    public PanneauSieges(FenetrePrincipale fenetre) {
        this.fenetre = fenetre;

        setLayout(new BorderLayout());
        setBackground(Couleurs.ROSE_FOND);
        setBorder(BorderFactory.createEmptyBorder(30, 50, 20, 50));

        add(ComposantsUi.creerEnTete("Choix des sièges", fenetre, FenetrePrincipale.INFOS_PASSAGERS), BorderLayout.NORTH);

        JPanel centre = new JPanel(new BorderLayout());
        centre.setBackground(Couleurs.ROSE_FOND);

        labelPassagerCourant.setFont(Couleurs.POLICE_SOUS_TITRE);
        labelPassagerCourant.setForeground(Couleurs.ROSE_FONCE);
        labelPassagerCourant.setHorizontalAlignment(SwingConstants.CENTER);

        labelConsigne.setFont(Couleurs.POLICE_PETITE);
        labelConsigne.setForeground(Couleurs.TEXTE_SECONDAIRE);
        labelConsigne.setHorizontalAlignment(SwingConstants.CENTER);
        labelConsigne.setText("Vert = disponible   |   Rose foncé = sélectionné   |   Rouge = indisponible");

        labelNoteBebe.setFont(Couleurs.POLICE_PETITE);
        labelNoteBebe.setForeground(Couleurs.ROSE_FONCE);
        labelNoteBebe.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel enTeteCarte = new JPanel(new GridLayoutUneColonne());
        enTeteCarte.setBackground(Couleurs.ROSE_FOND);
        enTeteCarte.add(labelPassagerCourant);
        enTeteCarte.add(labelConsigne);
        enTeteCarte.add(labelNoteBebe);
        centre.add(enTeteCarte, BorderLayout.NORTH);

        panneauCarte.setLayout(new BoxLayout(panneauCarte, BoxLayout.Y_AXIS));
        panneauCarte.setBackground(Couleurs.ROSE_FOND);
        JScrollPane defilement = new JScrollPane(panneauCarte);
        defilement.setBorder(BorderFactory.createEmptyBorder());
        defilement.getViewport().setBackground(Couleurs.ROSE_FOND);
        defilement.getVerticalScrollBar().setUnitIncrement(16);
        centre.add(defilement, BorderLayout.CENTER);

        add(centre, BorderLayout.CENTER);

        JPanel panneauBas = new JPanel(new BorderLayout());
        panneauBas.setBackground(Couleurs.ROSE_FOND);

        labelInfoSiege.setFont(Couleurs.POLICE_NORMALE);
        labelInfoSiege.setForeground(Couleurs.TEXTE);
        labelInfoSiege.setHorizontalAlignment(SwingConstants.CENTER);
        panneauBas.add(labelInfoSiege, BorderLayout.NORTH);

        JPanel ligneBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        ligneBoutons.setBackground(Couleurs.ROSE_FOND);
        boutonPrecedent.addActionListener(e -> allerAuPrecedent());
        boutonAucunSiege.addActionListener(e -> confirmerPassager(null, 0));
        boutonConfirmer.addActionListener(e -> confirmerPassager(siegeSelectionneCourant, prixSiegeSelectionneCourant));
        ligneBoutons.add(boutonPrecedent);
        ligneBoutons.add(boutonAucunSiege);
        ligneBoutons.add(boutonConfirmer);
        panneauBas.add(ligneBoutons, BorderLayout.SOUTH);

        add(panneauBas, BorderLayout.SOUTH);
    }

    public void rafraichir() {
        EtatReservation etat = fenetre.getEtat();

        // Rappel : le bébé n'a pas de siège attribué, il voyage sur les genoux d'un adulte.
        labelNoteBebe.setText(etat.getNbBebes() > 0
                ? "[i] Le(s) bébé(s) de cette réservation voyagent sur les genoux d'un adulte : aucun siège ne leur est attribué."
                : " ");

        billetsAvecSiege = new ArrayList<Billet>();
        for (Billet billet : etat.getBillets()) {
            if (!billet.estBebe()) {
                billetsAvecSiege.add(billet);
            } else {
                billet.setSiege(null);
                billet.setCoutSiege(0);
            }
        }

        indexCourant = 0;
        afficherPassagerCourant();
    }

    private void afficherPassagerCourant() {
        if (billetsAvecSiege.isEmpty()) {
            labelPassagerCourant.setText("Aucun siège à attribuer pour cette réservation.");
            panneauCarte.removeAll();
            panneauCarte.revalidate();
            panneauCarte.repaint();
            boutonConfirmer.setText("Continuer");
            boutonConfirmer.setEnabled(true);
            boutonAucunSiege.setEnabled(false);
            boutonPrecedent.setEnabled(false);
            return;
        }

        Billet billet = billetsAvecSiege.get(indexCourant);
        EtatReservation etat = fenetre.getEtat();
        Vol vol = etat.getResultatChoisi().getVol();
        String classeReelle = billet.getClasse();

        RangeesCabine rangees = fenetre.getPlanCabineService().calculerRangees(vol);
        Bornes bornes = determinerBornes(classeReelle, vol, rangees);

        List<CaseSiege> cases = fenetre.getPlanCabineService().genererSieges(vol.getNumeroVol(), etat.getDateVol(),
                classeReelle, bornes.debut, bornes.fin, rangees.getRangeeDebutEco(), rangees.getRangeeFinEco(), bornes.total);

        siegeSelectionneCourant = billet.getSiege();
        prixSiegeSelectionneCourant = billet.getCoutSiege();

        labelPassagerCourant.setText("Passager " + (indexCourant + 1) + " / " + billetsAvecSiege.size()
                + "  -  " + billet.getNom() + " " + billet.getPrenom() + "  (" + classeReelle + ")");

        construireCarte(cases, siegesReservesParAutresPassagers(indexCourant));

        boutonPrecedent.setEnabled(indexCourant > 0);
        boutonConfirmer.setText(indexCourant == billetsAvecSiege.size() - 1 ? "Confirmer et voir le récapitulatif" : "Confirmer et continuer");
        mettreAJourLabelInfo();
    }

    private void construireCarte(List<CaseSiege> cases, List<String> siegesReservesParAutresPassagers) {
        panneauCarte.removeAll();

        int i = 0;
        while (i < cases.size()) {
            int rangeeCourante = cases.get(i).getRangee();

            JPanel ligne = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 6));
            ligne.setBackground(Couleurs.ROSE_FOND);

            JLabel labelRangee = new JLabel(String.valueOf(rangeeCourante));
            labelRangee.setFont(Couleurs.POLICE_PETITE);
            labelRangee.setForeground(Couleurs.TEXTE_SECONDAIRE);
            labelRangee.setPreferredSize(new Dimension(20, 40));
            ligne.add(labelRangee);

            boolean alleeAjoutee = false;
            while (i < cases.size() && cases.get(i).getRangee() == rangeeCourante) {
                CaseSiege caseSiege = cases.get(i);

                if (!caseSiege.estColonneGauche() && !alleeAjoutee) {
                    ligne.add(Box.createHorizontalStrut(24));
                    alleeAjoutee = true;
                }

                boolean reserveParAutre = siegesReservesParAutresPassagers.contains(caseSiege.getNumeroSiege());
                ligne.add(construireBoutonSiege(caseSiege, reserveParAutre));
                i++;
            }

            panneauCarte.add(ligne);
        }

        panneauCarte.revalidate();
        panneauCarte.repaint();
    }

    private JButton construireBoutonSiege(CaseSiege caseSiege, boolean reserveParAutre) {
        JButton bouton = new JButton("<html><center>" + caseSiege.getNumeroSiege() + "<br>"
                + (caseSiege.getPrix() > 0 ? String.format("+%.0f€", caseSiege.getPrix()) : "inclus") + "</center></html>");
        bouton.setFont(Couleurs.POLICE_SIEGE);
        bouton.setPreferredSize(new Dimension(68, 46));
        bouton.setFocusPainted(false);
        bouton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        // Force l'affichage de la couleur de fond du bouton.
        bouton.setOpaque(true);
        bouton.setContentAreaFilled(true);

        boolean estLaSelectionCourante = caseSiege.getNumeroSiege().equals(siegeSelectionneCourant);
        boolean estIndisponible = !caseSiege.isDisponible() || reserveParAutre;

        java.awt.Color fond;
        java.awt.Color texte;

        if (estLaSelectionCourante) {
            fond = Couleurs.ROSE_FONCE;
            texte = Couleurs.BLANC;
        } else if (estIndisponible) {
            fond = Couleurs.ROUGE_INDISPONIBLE;
            texte = Couleurs.BLANC;
        } else {
            fond = Couleurs.VERT_DISPONIBLE;
            texte = Couleurs.TEXTE;
        }

        bouton.setBackground(fond);
        bouton.setForeground(texte);
        bouton.setBorder(BorderFactory.createLineBorder(fond.darker(), 1));

        if (estIndisponible) {
            // Siège affiché mais non cliquable.
        } else if (!estLaSelectionCourante) {
            bouton.addActionListener(e -> {
                siegeSelectionneCourant = caseSiege.getNumeroSiege();
                prixSiegeSelectionneCourant = caseSiege.getPrix();
                construireCarteCourante();
                mettreAJourLabelInfo();
            });
        }

        return bouton;
    }

    private void construireCarteCourante() {
        Billet billet = billetsAvecSiege.get(indexCourant);
        EtatReservation etat = fenetre.getEtat();
        Vol vol = etat.getResultatChoisi().getVol();
        String classeReelle = billet.getClasse();
        RangeesCabine rangees = fenetre.getPlanCabineService().calculerRangees(vol);
        Bornes bornes = determinerBornes(classeReelle, vol, rangees);

        List<CaseSiege> cases = fenetre.getPlanCabineService().genererSieges(vol.getNumeroVol(), etat.getDateVol(),
                classeReelle, bornes.debut, bornes.fin, rangees.getRangeeDebutEco(), rangees.getRangeeFinEco(), bornes.total);

        construireCarte(cases, siegesReservesParAutresPassagers(indexCourant));
    }

    private void mettreAJourLabelInfo() {
        if (siegeSelectionneCourant == null) {
            labelInfoSiege.setText("Aucun siège sélectionné pour l'instant.");
        } else if (prixSiegeSelectionneCourant > 0) {
            labelInfoSiege.setText("Siège sélectionné : " + siegeSelectionneCourant
                    + " - supplément : " + String.format("%.2f", prixSiegeSelectionneCourant) + " EUR");
        } else {
            labelInfoSiege.setText("Siège sélectionné : " + siegeSelectionneCourant + " - inclus");
        }
    }

    private void confirmerPassager(String siege, double prix) {
        if (!billetsAvecSiege.isEmpty()) {
            Billet billet = billetsAvecSiege.get(indexCourant);
            billet.setSiege(siege);
            billet.setCoutSiege(prix);
        }

        if (indexCourant < billetsAvecSiege.size() - 1) {
            indexCourant++;
            afficherPassagerCourant();
        } else {
            fenetre.afficherPanneau(FenetrePrincipale.RECAPITULATIF);
        }
    }

    private void allerAuPrecedent() {
        if (indexCourant > 0) {
            indexCourant--;
            afficherPassagerCourant();
        }
    }

    /** Bornes de rangées et capacité totale pour la classe réelle d'un billet. */
    private static class Bornes {
        int debut;
        int fin;
        int total;
    }

    private Bornes determinerBornes(String classeReelle, Vol vol, RangeesCabine rangees) {
        Bornes b = new Bornes();
        if (classeReelle.equalsIgnoreCase("BUSINESS")) {
            b.debut = rangees.getRangeeDebutBusiness();
            b.fin = rangees.getRangeeFinBusiness();
            b.total = vol.getPlacesBusiness();
        } else if (classeReelle.equalsIgnoreCase("EXTRA LEG")) {
            b.debut = rangees.getRangeeDebutExtraLeg();
            b.fin = rangees.getRangeeFinExtraLeg();
            b.total = vol.getPlacesExtraLeg();
        } else if (classeReelle.equalsIgnoreCase("MINEUR UM")) {
            b.debut = rangees.getRangeeDebutMineur();
            b.fin = rangees.getRangeeFinMineur();
            b.total = vol.getPlacesMineur();
        } else {
            b.debut = rangees.getRangeeDebutEco();
            b.fin = rangees.getRangeeFinEco();
            b.total = vol.getPlacesEco();
        }
        return b;
    }

    private List<String> siegesReservesParAutresPassagers(int indexAIgnorer) {
        List<String> resultat = new ArrayList<String>();
        for (int i = 0; i < billetsAvecSiege.size(); i++) {
            if (i == indexAIgnorer) {
                continue;
            }
            String s = billetsAvecSiege.get(i).getSiege();
            if (s != null) {
                resultat.add(s);
            }
        }
        return resultat;
    }

    /** Layout simple pour empiler des labels sur une colonne. */
    private static class GridLayoutUneColonne extends java.awt.GridLayout {
        GridLayoutUneColonne() {
            super(0, 1, 0, 4);
        }
    }
}
