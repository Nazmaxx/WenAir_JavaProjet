package wenair.vue;

import wenair.modele.Billet;
import wenair.modele.ResponsableLegal;
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
 * Ecran admin "Manifeste d'un vol" : liste des passagers d'un vol a une
 * date donnee, avec le total par rapport a la capacite de l'appareil.
 */
public class PanneauManifesteVol extends JPanel {

    private final FenetrePrincipale fenetre;

    private final JTextField champNumeroVol = ComposantsUi.creerChampTexte(10);
    private final JTextField champDate = ComposantsUi.creerChampTexte(10);
    private final JLabel labelErreur = new JLabel(" ");
    private final JLabel labelTotal = new JLabel(" ");
    private final PanneauListeVerticale liste = new PanneauListeVerticale();

    public PanneauManifesteVol(FenetrePrincipale fenetre) {
        this.fenetre = fenetre;

        setLayout(new BorderLayout());
        setBackground(Couleurs.ROSE_FOND);
        setBorder(BorderFactory.createEmptyBorder(40, 70, 30, 70));

        add(ComposantsUi.creerEnTete("Manifeste d'un vol", fenetre, FenetrePrincipale.ADMIN_MENU), BorderLayout.NORTH);

        JPanel formulaire = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formulaire.setBackground(Couleurs.ROSE_PANNEAU);
        formulaire.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Couleurs.BORDURE_CARTE, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        formulaire.add(ComposantsUi.creerLabelChamp("Numéro de vol :"));
        formulaire.add(champNumeroVol);
        formulaire.add(ComposantsUi.creerLabelChamp("Date (jj/mm/aaaa) :"));
        formulaire.add(champDate);
        JButton boutonAfficher = ComposantsUi.creerBouton("Afficher le manifeste");
        boutonAfficher.addActionListener(e -> afficherManifeste());
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
        champNumeroVol.setText("");
        champDate.setText("");
        labelErreur.setText(" ");
        labelTotal.setText(" ");
        liste.removeAll();
        liste.revalidate();
        liste.repaint();
    }

    private void afficherManifeste() {
        String numeroVol = champNumeroVol.getText().trim().toUpperCase();
        String date = champDate.getText().trim();

        if (!Validation.estRempli(numeroVol) || !Validation.estDateValide(date)) {
            labelErreur.setText("Veuillez saisir un numéro de vol et une date valide (jj/mm/aaaa).");
            liste.removeAll();
            liste.revalidate();
            liste.repaint();
            labelTotal.setText(" ");
            return;
        }
        labelErreur.setText(" ");

        List<Billet> passagers = fenetre.getReservationService().manifestePourVolEtDate(numeroVol, date);

        liste.removeAll();

        if (passagers.isEmpty()) {
            JLabel labelVide = new JLabel("Aucun passager trouve pour le vol " + numeroVol + " le " + date + ".");
            labelVide.setFont(Couleurs.POLICE_SOUS_TITRE);
            labelVide.setForeground(Couleurs.TEXTE_SECONDAIRE);
            liste.add(labelVide);
            labelTotal.setText(" ");
        } else {
            int numero = 0;
            for (Billet b : passagers) {
                numero++;
                liste.add(construireCarte(numero, b));
                liste.add(javax.swing.Box.createVerticalStrut(10));
            }

            Vol vol = fenetre.getVolService().trouverParNumero(numeroVol);
            if (vol != null) {
                labelTotal.setText("Total passagers : " + passagers.size() + " / " + vol.getTotalPlaces());
            } else {
                labelTotal.setText("Total passagers : " + passagers.size());
            }
        }

        liste.revalidate();
        liste.repaint();
    }

    private JPanel construireCarte(int numero, Billet b) {
        boolean estUm = b.getResponsableLegal() != null;
        int nbLignes = estUm ? 7 : 5;

        JPanel carte = new JPanel(new GridLayout(nbLignes, 1, 0, 3));
        carte.setBackground(Couleurs.ROSE_PANNEAU);
        carte.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Couleurs.BORDURE_CARTE, 1),
                BorderFactory.createEmptyBorder(12, 18, 12, 18)));

        JLabel labelPassager = new JLabel("Passager " + numero + "  -  " + b.getCivilite() + " " + b.getNom() + " " + b.getPrenom());
        labelPassager.setFont(Couleurs.POLICE_SOUS_TITRE);
        labelPassager.setForeground(Couleurs.TEXTE);
        carte.add(labelPassager);

        carte.add(creerLigne("Classe : " + b.getClasse()));
        carte.add(creerLigne("Siège : " + (b.getSiege() == null ? "N/A" : b.getSiege())));
        carte.add(creerLigne("Bagage supp. : " + b.getPoidsBagageSupp() + " kg"));
        carte.add(creerLigne("Ref. billet : " + b.getRefBillet() + "   Ref. commande : " + b.getRefCommande()));

        if (estUm) {
            ResponsableLegal r = b.getResponsableLegal();
            carte.add(creerLigne("Deposant : " + r.getNomDeposant() + " (" + r.getTelDeposant() + ")"));
            carte.add(creerLigne("Receveur : " + r.getNomReceveur() + " (" + r.getTelReceveur() + ")"));
        }

        return carte;
    }

    private JLabel creerLigne(String texte) {
        JLabel label = new JLabel(texte);
        label.setFont(Couleurs.POLICE_PETITE);
        label.setForeground(Couleurs.TEXTE_SECONDAIRE);
        return label;
    }
}
