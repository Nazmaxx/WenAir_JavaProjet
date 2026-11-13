package wenair.vue;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Ecran de connexion a l'espace administrateur : mot de passe unique,
 * 3 tentatives maximum. Au bout de 3 echecs, retour automatique a l'accueil.
 */
public class PanneauAdminLogin extends JPanel {

    private final FenetrePrincipale fenetre;
    private final JPasswordField champMdp = ComposantsUi.creerChampMotDePasse(15);
    private final JLabel labelTentatives = new JLabel(" ", SwingConstants.CENTER);
    private final JLabel labelErreur = new JLabel(" ", SwingConstants.CENTER);

    private int tentativesRestantes;

    public PanneauAdminLogin(FenetrePrincipale fenetre) {
        this.fenetre = fenetre;

        setLayout(new BorderLayout());
        setBackground(Couleurs.ROSE_FOND);
        setBorder(BorderFactory.createEmptyBorder(50, 80, 50, 80));

        add(ComposantsUi.creerEnTete("Acces administrateur", fenetre, FenetrePrincipale.ACCUEIL), BorderLayout.NORTH);

        JPanel formulaire = new JPanel(new GridBagLayout());
        formulaire.setBackground(Couleurs.ROSE_PANNEAU);
        formulaire.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Couleurs.BORDURE_CARTE, 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0;
        c.gridy = 0;
        formulaire.add(ComposantsUi.creerLabelChamp("Mot de passe administrateur :"), c);
        c.gridx = 1;
        formulaire.add(champMdp, c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        labelTentatives.setFont(Couleurs.POLICE_PETITE);
        labelTentatives.setForeground(Couleurs.TEXTE_SECONDAIRE);
        formulaire.add(labelTentatives, c);

        c.gridy = 2;
        labelErreur.setFont(Couleurs.POLICE_PETITE);
        labelErreur.setForeground(Couleurs.ROUGE_INDISPONIBLE);
        formulaire.add(labelErreur, c);

        JButton boutonConnexion = ComposantsUi.creerBouton("Se connecter");
        boutonConnexion.addActionListener(e -> tenterConnexion());
        champMdp.addActionListener(e -> tenterConnexion());

        c.gridy = 3;
        formulaire.add(boutonConnexion, c);

        JPanel conteneur = new JPanel(new BorderLayout());
        conteneur.setBackground(Couleurs.ROSE_FOND);
        conteneur.add(formulaire, BorderLayout.NORTH);
        add(conteneur, BorderLayout.CENTER);
    }

    /** Reinitialise le formulaire et le compteur de tentatives a chaque entree sur l'ecran. */
    public void rafraichir() {
        champMdp.setText("");
        labelErreur.setText(" ");
        tentativesRestantes = wenair.service.AuthService.TENTATIVES_MAX;
        labelTentatives.setText("Tentatives restantes : " + tentativesRestantes);
        champMdp.requestFocusInWindow();
    }

    private void tenterConnexion() {
        String saisie = new String(champMdp.getPassword());

        if (fenetre.getAuthService().verifierMotDePasseAdmin(saisie)) {
            fenetre.afficherPanneau(FenetrePrincipale.ADMIN_MENU);
            return;
        }

        tentativesRestantes--;
        champMdp.setText("");

        if (tentativesRestantes > 0) {
            labelErreur.setText("Mot de passe incorrect.");
            labelTentatives.setText("Tentatives restantes : " + tentativesRestantes);
        } else {
            ComposantsUi.afficherErreur(this, "Accès bloqué : trop de tentatives échouées. Retour à l'accueil.");
            fenetre.afficherPanneau(FenetrePrincipale.ACCUEIL);
        }
    }
}
