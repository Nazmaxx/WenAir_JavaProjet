package wenair.vue;

import wenair.modele.Billet;
import wenair.modele.ResponsableLegal;
import wenair.utilitaires.Validation;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Écran de saisie de l'identité et de la date de naissance de chaque
 * passager. L'âge est calculé à partir de la date de naissance et doit
 * correspondre à la catégorie choisie à la recherche (Adulte, Enfant,
 * Bébé). Règles du service Mineur Non Accompagné (UM) :
 * - sans aucun passager majeur (18 ans ou +) dans la réservation, les
 *   enfants de 5 à 11 ans voyagent obligatoirement en UM, les 2-4 ans ne
 *   peuvent pas voyager du tout, et les 12-17 ans peuvent activer le
 *   service UM (au choix, pour tous) ;
 * - au maximum 3 mineurs non accompagnés par réservation ;
 * - un bébé doit toujours être accompagné d'un passager majeur.
 */
public class PanneauInfosPassagers extends JPanel {

    private final FenetrePrincipale fenetre;

    private final JPanel conteneurFormulaires = new JPanel();
    private final List<FormulairePassager> formulaires = new ArrayList<FormulairePassager>();

    private final JCheckBox caseServiceUm = new JCheckBox(
            "Activer le service Mineur Non Accompagné pour les passagers de 12 à 17 ans (supplément tarifaire)");
    private final JPanel panneauUm = new JPanel(new FlowLayout(FlowLayout.LEFT));

    private final JTextField champNomDeposant = ComposantsUi.creerChampTexte(15);
    private final JTextField champTelDeposant = ComposantsUi.creerChampTexte(12);
    private final JTextField champNomReceveur = ComposantsUi.creerChampTexte(15);
    private final JTextField champTelReceveur = ComposantsUi.creerChampTexte(12);
    private final JPanel panneauGuardian = new JPanel();

    private final JLabel labelErreur = new JLabel(" ");

    public PanneauInfosPassagers(FenetrePrincipale fenetre) {
        this.fenetre = fenetre;

        setLayout(new BorderLayout());
        setBackground(Couleurs.ROSE_FOND);
        setBorder(BorderFactory.createEmptyBorder(40, 60, 30, 60));

        add(ComposantsUi.creerEnTete("Informations des passagers", fenetre, FenetrePrincipale.RESULTATS), BorderLayout.NORTH);

        conteneurFormulaires.setLayout(new BoxLayout(conteneurFormulaires, BoxLayout.Y_AXIS));
        conteneurFormulaires.setBackground(Couleurs.ROSE_FOND);

        panneauUm.setBackground(Couleurs.ROSE_PANNEAU);
        panneauUm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Couleurs.BORDURE_CARTE, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panneauUm.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        caseServiceUm.setBackground(Couleurs.ROSE_PANNEAU);
        caseServiceUm.setFont(Couleurs.POLICE_NORMALE);
        caseServiceUm.addChangeListener(e -> recalculerAffichage());
        panneauUm.add(caseServiceUm);

        panneauGuardian.setLayout(new BoxLayout(panneauGuardian, BoxLayout.Y_AXIS));
        panneauGuardian.setBackground(Couleurs.ROSE_PANNEAU);
        panneauGuardian.setBorder(BorderFactory.createTitledBorder("Responsable légal"));
        panneauGuardian.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        JLabel labelGuardianAide = ComposantsUi.creerLabelAide();
        labelGuardianAide.setText("À fournir pour le dépôt et la récupération du mineur non accompagné.");
        labelGuardianAide.setForeground(Couleurs.TEXTE_SECONDAIRE);

        JPanel ligneDeposant = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ligneDeposant.setBackground(Couleurs.ROSE_PANNEAU);
        ligneDeposant.add(ComposantsUi.creerLabelChamp("Déposant - nom :"));
        ligneDeposant.add(champNomDeposant);
        ligneDeposant.add(ComposantsUi.creerLabelChamp("téléphone :"));
        ligneDeposant.add(champTelDeposant);

        JPanel ligneReceveur = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ligneReceveur.setBackground(Couleurs.ROSE_PANNEAU);
        ligneReceveur.add(ComposantsUi.creerLabelChamp("Receveur - nom :"));
        ligneReceveur.add(champNomReceveur);
        ligneReceveur.add(ComposantsUi.creerLabelChamp("téléphone :"));
        ligneReceveur.add(champTelReceveur);

        panneauGuardian.add(labelGuardianAide);
        panneauGuardian.add(ligneDeposant);
        panneauGuardian.add(ligneReceveur);

        JPanel conteneurEnsemble = new JPanel();
        conteneurEnsemble.setLayout(new BoxLayout(conteneurEnsemble, BoxLayout.Y_AXIS));
        conteneurEnsemble.setBackground(Couleurs.ROSE_FOND);
        conteneurEnsemble.add(conteneurFormulaires);
        conteneurEnsemble.add(panneauUm);
        conteneurEnsemble.add(panneauGuardian);

        JScrollPane defilement = new JScrollPane(conteneurEnsemble);
        defilement.setBorder(BorderFactory.createEmptyBorder());
        defilement.getViewport().setBackground(Couleurs.ROSE_FOND);
        defilement.getVerticalScrollBar().setUnitIncrement(16);
        add(defilement, BorderLayout.CENTER);

        JPanel panneauBas = new JPanel(new BorderLayout());
        panneauBas.setBackground(Couleurs.ROSE_FOND);
        labelErreur.setFont(Couleurs.POLICE_PETITE);
        labelErreur.setForeground(Couleurs.ROUGE_INDISPONIBLE);
        panneauBas.add(labelErreur, BorderLayout.NORTH);

        JButton boutonSuivant = ComposantsUi.creerBouton("Suivant : choix des sièges");
        boutonSuivant.addActionListener(e -> validerEtContinuer());
        JPanel conteneurBouton = new JPanel();
        conteneurBouton.setBackground(Couleurs.ROSE_FOND);
        conteneurBouton.add(boutonSuivant);
        panneauBas.add(conteneurBouton, BorderLayout.SOUTH);

        add(panneauBas, BorderLayout.SOUTH);
    }

    /** Reconstruit les formulaires de saisie d'après le nombre de passagers choisi à la recherche. */
    public void rafraichir() {
        EtatReservation etat = fenetre.getEtat();

        conteneurFormulaires.removeAll();
        formulaires.clear();
        labelErreur.setText(" ");
        caseServiceUm.setSelected(false);
        champNomDeposant.setText("");
        champTelDeposant.setText("");
        champNomReceveur.setText("");
        champTelReceveur.setText("");

        boolean contactAssigne = false;

        for (int i = 0; i < etat.getNbAdultes(); i++) {
            boolean estContact = !contactAssigne;
            contactAssigne = true;
            FormulairePassager fp = creerFormulaire("Adulte " + (i + 1) + " (12 ans et +)", true, false, estContact);
            formulaires.add(fp);
            conteneurFormulaires.add(fp.panneau);
        }
        for (int i = 0; i < etat.getNbEnfants(); i++) {
            boolean estContact = !contactAssigne;
            contactAssigne = true;
            FormulairePassager fp = creerFormulaire("Enfant " + (i + 1) + " (2 à 11 ans)", false, false, estContact);
            formulaires.add(fp);
            conteneurFormulaires.add(fp.panneau);
        }
        for (int i = 0; i < etat.getNbBebes(); i++) {
            FormulairePassager fp = creerFormulaire("Bébé " + (i + 1) + " (moins de 2 ans)", false, true, false);
            formulaires.add(fp);
            conteneurFormulaires.add(fp.panneau);
        }

        recalculerAffichage();
        conteneurFormulaires.revalidate();
        conteneurFormulaires.repaint();
    }

    private FormulairePassager creerFormulaire(String titre, boolean estAdulte, boolean estBebe, boolean estContact) {
        FormulairePassager fp = new FormulairePassager();
        fp.estAdulte = estAdulte;
        fp.estBebe = estBebe;
        fp.estContact = estContact;

        JPanel panneau = new JPanel();
        panneau.setLayout(new BoxLayout(panneau, BoxLayout.Y_AXIS));
        panneau.setBackground(Couleurs.ROSE_PANNEAU);
        panneau.setBorder(BorderFactory.createTitledBorder(titre));

        JPanel ligneIdentite = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        ligneIdentite.setBackground(Couleurs.ROSE_PANNEAU);

        fp.comboCivilite = new JComboBox<String>(new String[] { "MONSIEUR", "MADAME" });
        fp.champNom = ComposantsUi.creerChampTexte(10);
        fp.champPrenom = ComposantsUi.creerChampTexte(10);

        ligneIdentite.add(ComposantsUi.creerLabelChamp("Civilité :"));
        ligneIdentite.add(fp.comboCivilite);
        ligneIdentite.add(ComposantsUi.creerLabelChamp("Nom :"));
        ligneIdentite.add(fp.champNom);
        ligneIdentite.add(ComposantsUi.creerLabelChamp("Prénom :"));
        ligneIdentite.add(fp.champPrenom);

        panneau.add(ligneIdentite);

        if (!estBebe) {
            JPanel ligneDetails = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
            ligneDetails.setBackground(Couleurs.ROSE_PANNEAU);

            fp.champDateNaissance = ComposantsUi.creerChampTexte(10);
            ligneDetails.add(ComposantsUi.creerLabelChamp("Date de naissance (jj/mm/aaaa) :"));
            ligneDetails.add(fp.champDateNaissance);

            fp.spinnerBagage = new JSpinner(new SpinnerNumberModel(0, 0, 50, 1));
            ligneDetails.add(ComposantsUi.creerLabelChamp("Bagage suppl. (kg) :"));
            ligneDetails.add(fp.spinnerBagage);

            panneau.add(ligneDetails);

            JPanel ligneAge = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
            ligneAge.setBackground(Couleurs.ROSE_PANNEAU);
            fp.labelAideAge = ComposantsUi.creerLabelAide();
            ligneAge.add(fp.labelAideAge);
            panneau.add(ligneAge);

            final FormulairePassager fpFinal = fp;
            fp.champDateNaissance.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) { miseAJour(); }
                @Override
                public void removeUpdate(DocumentEvent e) { miseAJour(); }
                @Override
                public void changedUpdate(DocumentEvent e) { miseAJour(); }

                private void miseAJour() {
                    mettreAJourAideAge(fpFinal);
                    recalculerAffichage();
                }
            });
        }

        if (estContact) {
            JPanel ligneContact = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
            ligneContact.setBackground(Couleurs.ROSE_PANNEAU);
            fp.champTelephone = ComposantsUi.creerChampTexte(10);
            fp.champEmail = ComposantsUi.creerChampTexte(14);
            ligneContact.add(ComposantsUi.creerLabelChamp("Téléphone (contact) :"));
            ligneContact.add(fp.champTelephone);
            ligneContact.add(ComposantsUi.creerLabelChamp("Email (contact) :"));
            ligneContact.add(fp.champEmail);
            panneau.add(ligneContact);
        }

        panneau.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        panneau.setMaximumSize(new Dimension(Integer.MAX_VALUE, panneau.getPreferredSize().height));

        fp.panneau = panneau;
        return fp;
    }

    /** Calcule l'âge d'un passager depuis son champ de date de naissance (-1 si non renseigné ou invalide). */
    private int ageDe(FormulairePassager fp) {
        if (fp.champDateNaissance == null) {
            return -1;
        }
        return Validation.calculerAge(fp.champDateNaissance.getText().trim());
    }

    /** Met à jour, en direct, le texte d'aide affichant l'âge calculé et sa cohérence avec la catégorie du passager. */
    private void mettreAJourAideAge(FormulairePassager fp) {
        String texte = fp.champDateNaissance.getText().trim();
        if (texte.isEmpty()) {
            fp.labelAideAge.setText(" ");
            return;
        }
        if (!Validation.estDateValide(texte)) {
            fp.labelAideAge.setText("Format attendu : jj/mm/aaaa");
            fp.labelAideAge.setForeground(Couleurs.ROUGE_INDISPONIBLE);
            return;
        }
        int age = Validation.calculerAge(texte);
        if (age < 0) {
            fp.labelAideAge.setText("Cette date de naissance n'est pas valide.");
            fp.labelAideAge.setForeground(Couleurs.ROUGE_INDISPONIBLE);
            return;
        }
        boolean correspond = fp.estAdulte ? age >= 12 : (age >= 2 && age <= 11);
        if (correspond) {
            fp.labelAideAge.setText("Âge calculé : " + age + " an(s)");
            fp.labelAideAge.setForeground(Couleurs.VERT_DISPONIBLE.darker());
        } else if (fp.estAdulte) {
            fp.labelAideAge.setText("Âge calculé : " + age + " an(s) - incompatible avec 'Adulte' (12 ans et +)");
            fp.labelAideAge.setForeground(Couleurs.ROUGE_INDISPONIBLE);
        } else {
            fp.labelAideAge.setText("Âge calculé : " + age + " an(s) - incompatible avec 'Enfant' (2 à 11 ans)");
            fp.labelAideAge.setForeground(Couleurs.ROUGE_INDISPONIBLE);
        }
    }

    /** Recalcule, en direct, la visibilité de la case UM et du bloc responsable légal. */
    private void recalculerAffichage() {
        int nbMajeurs = 0;
        int nbEligiblesUmAdo = 0;
        boolean auMoinsUnEnfantUmObligatoire = false;

        for (FormulairePassager fp : formulaires) {
            if (fp.estBebe) {
                continue;
            }
            int age = ageDe(fp);
            if (fp.estAdulte && age >= 18) {
                nbMajeurs++;
            }
        }

        for (FormulairePassager fp : formulaires) {
            if (fp.estBebe) {
                continue;
            }
            int age = ageDe(fp);
            if (nbMajeurs == 0 && fp.estAdulte && age >= 12 && age <= 17) {
                nbEligiblesUmAdo++;
            }
            if (nbMajeurs == 0 && !fp.estAdulte && age >= 5 && age <= 11) {
                auMoinsUnEnfantUmObligatoire = true;
            }
        }

        boolean afficherCaseUm = nbEligiblesUmAdo > 0;
        panneauUm.setVisible(afficherCaseUm);

        boolean afficherGuardian = auMoinsUnEnfantUmObligatoire || (afficherCaseUm && caseServiceUm.isSelected());
        panneauGuardian.setVisible(afficherGuardian);

        if (conteneurFormulaires.getParent() != null) {
            conteneurFormulaires.getParent().revalidate();
            conteneurFormulaires.getParent().repaint();
        }
    }

    private void validerEtContinuer() {
        labelErreur.setText(" ");

        // Validation des champs de base
        for (FormulairePassager fp : formulaires) {
            if (!Validation.estRempli(fp.champNom.getText()) || !Validation.estRempli(fp.champPrenom.getText())) {
                afficherErreur("Merci de renseigner le nom et le prénom de chaque passager.");
                return;
            }
            if (!Validation.sansPointVirgule(fp.champNom.getText()) || !Validation.sansPointVirgule(fp.champPrenom.getText())) {
                afficherErreur("Le nom et le prénom ne doivent pas contenir de point-virgule.");
                return;
            }
            if (!fp.estBebe) {
                String dateNaissance = fp.champDateNaissance.getText().trim();
                if (!Validation.estDateValide(dateNaissance)) {
                    afficherErreur("La date de naissance de chaque passager doit être au format jj/mm/aaaa.");
                    return;
                }
                int age = Validation.calculerAge(dateNaissance);
                if (age < 0) {
                    afficherErreur("La date de naissance saisie n'est pas valide.");
                    return;
                }
                if (fp.estAdulte && age < 12) {
                    afficherErreur("La date de naissance indiquée correspond à un âge de " + age
                            + " an(s), incompatible avec la catégorie 'Adulte' (12 ans et +).");
                    return;
                }
                if (!fp.estAdulte && (age < 2 || age > 11)) {
                    afficherErreur("La date de naissance indiquée correspond à un âge de " + age
                            + " an(s), incompatible avec la catégorie 'Enfant' (2 à 11 ans).");
                    return;
                }
            }
            if (fp.estContact) {
                if (!Validation.estRempli(fp.champTelephone.getText())) {
                    afficherErreur("Merci de renseigner un numéro de téléphone de contact.");
                    return;
                }
                if (!Validation.estEmailValide(fp.champEmail.getText())) {
                    afficherErreur("L'adresse email de contact n'est pas valide.");
                    return;
                }
            }
        }

        // Calcul du nombre de majeurs et des règles UM
        int nbMajeurs = 0;
        for (FormulairePassager fp : formulaires) {
            if (!fp.estBebe && fp.estAdulte && ageDe(fp) >= 18) {
                nbMajeurs++;
            }
        }

        if (nbMajeurs == 0) {
            for (FormulairePassager fp : formulaires) {
                if (!fp.estBebe && !fp.estAdulte) {
                    int age = ageDe(fp);
                    if (age >= 2 && age <= 4) {
                        afficherErreur("Les enfants de 2 à 4 ans ne peuvent pas voyager seuls : "
                                + "ajoutez un passager majeur (18 ans ou +) ou retirez cet enfant depuis la recherche.");
                        return;
                    }
                }
            }

            int totalMineurs = fenetre.getEtat().getNbAdultes() + fenetre.getEtat().getNbEnfants();
            if (totalMineurs > 3) {
                afficherErreur("Maximum 3 mineurs non accompagnés par réservation : "
                        + "réduisez le nombre de passagers depuis la recherche.");
                return;
            }

            if (fenetre.getEtat().getNbBebes() > 0) {
                afficherErreur("Un bébé doit être accompagné d'un passager majeur (18 ans ou plus).");
                return;
            }
        } else {
            if (fenetre.getEtat().getNbBebes() > nbMajeurs) {
                afficherErreur("Un bébé maximum par passager majeur : ajustez le nombre de bébés depuis la recherche.");
                return;
            }
        }

        // Détermination du statut UM final
        for (FormulairePassager fp : formulaires) {
            if (fp.estBebe) {
                fp.estUmFinal = false;
                continue;
            }
            int age = ageDe(fp);
            if (fp.estAdulte) {
                fp.estUmFinal = nbMajeurs == 0 && age >= 12 && age <= 17 && caseServiceUm.isSelected();
            } else {
                fp.estUmFinal = nbMajeurs == 0 && age >= 5 && age <= 11;
            }
        }

        boolean auMoinsUnUm = false;
        for (FormulairePassager fp : formulaires) {
            if (fp.estUmFinal) {
                auMoinsUnUm = true;
                break;
            }
        }

        ResponsableLegal responsableLegal = null;
        if (auMoinsUnUm) {
            if (!Validation.estRempli(champNomDeposant.getText()) || !Validation.estRempli(champTelDeposant.getText())
                    || !Validation.estRempli(champNomReceveur.getText()) || !Validation.estRempli(champTelReceveur.getText())) {
                afficherErreur("Merci de renseigner les informations complètes du responsable légal (dépôt et récupération).");
                return;
            }
            responsableLegal = new ResponsableLegal(champNomDeposant.getText().trim(), champTelDeposant.getText().trim(),
                    champNomReceveur.getText().trim(), champTelReceveur.getText().trim());
        }

        // Construction des billets (sans référence ni siège pour l'instant)
        EtatReservation etat = fenetre.getEtat();
        String classeRecherche = etat.getClasseRecherche();
        double prixAdulte = etat.getResultatChoisi().getPrixAdulte();
        double prixEnfant = etat.getResultatChoisi().getPrixEnfant();
        double prixMineurEnfant = etat.getResultatChoisi().getPrixMineur();
        double prixMineurAdulte = etat.getResultatChoisi().getPrixMineurAdulte();

        List<Billet> billets = new ArrayList<Billet>();
        Billet contact = null;

        for (FormulairePassager fp : formulaires) {
            Billet billet = new Billet();
            billet.setCivilite((String) fp.comboCivilite.getSelectedItem());
            billet.setNom(fp.champNom.getText().trim());
            billet.setPrenom(fp.champPrenom.getText().trim());

            if (fp.estContact) {
                billet.setTelephone(fp.champTelephone.getText().trim());
                billet.setEmail(fp.champEmail.getText().trim());
            }

            if (fp.estBebe) {
                billet.setClasse("BEBE");
                billet.setPoidsBagageSupp(0);
                billet.setPrixBillet(0);
            } else if (fp.estUmFinal) {
                billet.setClasse("MINEUR UM");
                billet.setPoidsBagageSupp((Integer) fp.spinnerBagage.getValue());
                billet.setResponsableLegal(responsableLegal);
                billet.setPrixBillet(fp.estAdulte ? prixMineurAdulte : prixMineurEnfant);
            } else {
                billet.setClasse(classeRecherche);
                billet.setPoidsBagageSupp((Integer) fp.spinnerBagage.getValue());
                billet.setPrixBillet(fp.estAdulte ? prixAdulte : prixEnfant);
            }

            billets.add(billet);
            if (fp.estContact) {
                contact = billet;
            }
        }

        if (contact != null && billets.indexOf(contact) != 0) {
            billets.remove(contact);
            billets.add(0, contact);
        }

        etat.setBillets(billets);
        fenetre.afficherPanneau(FenetrePrincipale.SURCLASSEMENT_INITIAL);
    }

    private void afficherErreur(String message) {
        labelErreur.setText(message);
    }

    /** Regroupe les composants Swing d'un passager pendant la saisie. */
    private static class FormulairePassager {
        boolean estAdulte;
        boolean estBebe;
        boolean estContact;
        boolean estUmFinal;
        JPanel panneau;
        JComboBox<String> comboCivilite;
        JTextField champNom;
        JTextField champPrenom;
        JTextField champDateNaissance;
        JLabel labelAideAge;
        JSpinner spinnerBagage;
        JTextField champTelephone;
        JTextField champEmail;
    }
}
