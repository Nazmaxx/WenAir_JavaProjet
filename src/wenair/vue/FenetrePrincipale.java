package wenair.vue;

import wenair.donnees.DonneesReservation;
import wenair.donnees.DonneesSiege;
import wenair.donnees.DonneesVol;
import wenair.service.AuthService;
import wenair.service.PlanCabineService;
import wenair.service.ReservationService;
import wenair.service.SiegeService;
import wenair.service.TarifService;
import wenair.service.VolService;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.nio.file.Path;

/**
 * Fenêtre principale de l'application Wen Air : une seule fenêtre, dont le
 * contenu change grâce à un CardLayout selon l'écran courant (accueil,
 * recherche, sélection de sièges, etc). Crée aussi les services une seule
 * fois et les partage avec tous les écrans.
 */
public class FenetrePrincipale extends JFrame {

    public static final String ACCUEIL = "ACCUEIL";
    public static final String ESPACE_CLIENT = "ESPACE_CLIENT";
    public static final String RECHERCHE = "RECHERCHE";
    public static final String RESULTATS = "RESULTATS";
    public static final String DETAILS_VOL = "DETAILS_VOL";
    public static final String INFOS_PASSAGERS = "INFOS_PASSAGERS";
    public static final String SURCLASSEMENT_INITIAL = "SURCLASSEMENT_INITIAL";
    public static final String SIEGES = "SIEGES";
    public static final String RECAPITULATIF = "RECAPITULATIF";
    public static final String CONFIRMATION = "CONFIRMATION";
    public static final String CONSULTATION = "CONSULTATION";
    public static final String BAGAGE = "BAGAGE";
    public static final String CARTE_EMBARQUEMENT = "CARTE_EMBARQUEMENT";
    public static final String SURCLASSEMENT = "SURCLASSEMENT";
    public static final String REPAS_SPECIAL = "REPAS_SPECIAL";
    public static final String OPTIONS_BILLET = "OPTIONS_BILLET";
    public static final String ADMIN_LOGIN = "ADMIN_LOGIN";
    public static final String ADMIN_MENU = "ADMIN_MENU";
    public static final String ADMIN_AJOUTER_VOL = "ADMIN_AJOUTER_VOL";
    public static final String ADMIN_MODIFIER_VOL = "ADMIN_MODIFIER_VOL";
    public static final String ADMIN_SUPPRIMER_VOL = "ADMIN_SUPPRIMER_VOL";
    public static final String ADMIN_LISTE_VOLS = "ADMIN_LISTE_VOLS";
    public static final String ADMIN_MANIFESTE = "ADMIN_MANIFESTE";
    public static final String ADMIN_VOLS_COMPLETS = "ADMIN_VOLS_COMPLETS";
    public static final String ADMIN_CHIFFRE_AFFAIRES = "ADMIN_CHIFFRE_AFFAIRES";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel panneauCentral = new JPanel(cardLayout);

    private final VolService volService;
    private final SiegeService siegeService;
    private final TarifService tarifService;
    private final ReservationService reservationService;
    private final AuthService authService;
    private final PlanCabineService planCabineService;

    private final EtatReservation etat = new EtatReservation();

    private PanneauResultats panneauResultats;
    private PanneauDetailsVol panneauDetailsVol;
    private PanneauInfosPassagers panneauInfosPassagers;
    private PanneauSurclassementInitial panneauSurclassementInitial;
    private PanneauSieges panneauSieges;
    private PanneauRecapitulatif panneauRecapitulatif;
    private PanneauConfirmation panneauConfirmation;
    private PanneauConsultation panneauConsultation;
    private PanneauAjoutBagage panneauAjoutBagage;
    private PanneauCarteEmbarquement panneauCarteEmbarquement;
    private PanneauSurclassement panneauSurclassement;
    private PanneauRepasSpecial panneauRepasSpecial;
    private PanneauOptionsBillet panneauOptionsBillet;
    private PanneauAdminLogin panneauAdminLogin;
    private PanneauAjouterVol panneauAjouterVol;
    private PanneauModifierVol panneauModifierVol;
    private PanneauSupprimerVol panneauSupprimerVol;
    private PanneauListeVols panneauListeVols;
    private PanneauManifesteVol panneauManifesteVol;
    private PanneauVolsComplets panneauVolsComplets;
    private PanneauChiffreAffaires panneauChiffreAffaires;

    private String refBilletSelectionne;
    private String refCommandeSelectionnee;

    public FenetrePrincipale() {
        super("Wen Air");

        Path dossierData = Path.of("data");
        DonneesVol donneesVol = new DonneesVol(dossierData.resolve("vols_bd.txt"));
        DonneesSiege donneesSiege = new DonneesSiege(dossierData.resolve("sieges_bd.txt"));
        DonneesReservation donneesReservation = new DonneesReservation(dossierData.resolve("reservations.txt"));

        this.tarifService = new TarifService();
        this.siegeService = new SiegeService(donneesSiege);
        this.volService = new VolService(donneesVol, siegeService, tarifService);
        this.reservationService = new ReservationService(donneesReservation, siegeService, volService, tarifService);
        this.authService = new AuthService();
        this.planCabineService = new PlanCabineService(siegeService, tarifService);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 740);
        setMinimumSize(new Dimension(950, 650));
        setLocationRelativeTo(null);

        panneauCentral.setBackground(Couleurs.ROSE_FOND);
        getContentPane().setBackground(Couleurs.ROSE_FOND);

        construirePanneaux();
        add(panneauCentral);

        afficherPanneau(ACCUEIL);
    }

    private void construirePanneaux() {
        panneauCentral.add(new PanneauAccueil(this), ACCUEIL);
        panneauCentral.add(new PanneauEspaceClient(this), ESPACE_CLIENT);
        panneauCentral.add(new PanneauRecherche(this), RECHERCHE);

        panneauResultats = new PanneauResultats(this);
        panneauCentral.add(panneauResultats, RESULTATS);

        panneauDetailsVol = new PanneauDetailsVol(this);
        panneauCentral.add(panneauDetailsVol, DETAILS_VOL);

        panneauInfosPassagers = new PanneauInfosPassagers(this);
        panneauCentral.add(panneauInfosPassagers, INFOS_PASSAGERS);

        panneauSurclassementInitial = new PanneauSurclassementInitial(this);
        panneauCentral.add(panneauSurclassementInitial, SURCLASSEMENT_INITIAL);

        panneauSieges = new PanneauSieges(this);
        panneauCentral.add(panneauSieges, SIEGES);

        panneauRecapitulatif = new PanneauRecapitulatif(this);
        panneauCentral.add(panneauRecapitulatif, RECAPITULATIF);

        panneauConfirmation = new PanneauConfirmation(this);
        panneauCentral.add(panneauConfirmation, CONFIRMATION);

        panneauConsultation = new PanneauConsultation(this);
        panneauCentral.add(panneauConsultation, CONSULTATION);

        panneauAjoutBagage = new PanneauAjoutBagage(this);
        panneauCentral.add(panneauAjoutBagage, BAGAGE);

        panneauCarteEmbarquement = new PanneauCarteEmbarquement(this);
        panneauCentral.add(panneauCarteEmbarquement, CARTE_EMBARQUEMENT);

        panneauSurclassement = new PanneauSurclassement(this);
        panneauCentral.add(panneauSurclassement, SURCLASSEMENT);

        panneauRepasSpecial = new PanneauRepasSpecial(this);
        panneauCentral.add(panneauRepasSpecial, REPAS_SPECIAL);

        panneauOptionsBillet = new PanneauOptionsBillet(this);
        panneauCentral.add(panneauOptionsBillet, OPTIONS_BILLET);

        panneauAdminLogin = new PanneauAdminLogin(this);
        panneauCentral.add(panneauAdminLogin, ADMIN_LOGIN);

        panneauCentral.add(new PanneauAdminMenu(this), ADMIN_MENU);

        panneauAjouterVol = new PanneauAjouterVol(this);
        panneauCentral.add(panneauAjouterVol, ADMIN_AJOUTER_VOL);

        panneauModifierVol = new PanneauModifierVol(this);
        panneauCentral.add(panneauModifierVol, ADMIN_MODIFIER_VOL);

        panneauSupprimerVol = new PanneauSupprimerVol(this);
        panneauCentral.add(panneauSupprimerVol, ADMIN_SUPPRIMER_VOL);

        panneauListeVols = new PanneauListeVols(this);
        panneauCentral.add(panneauListeVols, ADMIN_LISTE_VOLS);

        panneauManifesteVol = new PanneauManifesteVol(this);
        panneauCentral.add(panneauManifesteVol, ADMIN_MANIFESTE);

        panneauVolsComplets = new PanneauVolsComplets(this);
        panneauCentral.add(panneauVolsComplets, ADMIN_VOLS_COMPLETS);

        panneauChiffreAffaires = new PanneauChiffreAffaires(this);
        panneauCentral.add(panneauChiffreAffaires, ADMIN_CHIFFRE_AFFAIRES);
    }

    /** Affiche l'écran demandé, en rafraîchissant d'abord son contenu si besoin. */
    public void afficherPanneau(String nomEcran) {
        if (RESULTATS.equals(nomEcran)) {
            panneauResultats.rafraichir();
        } else if (DETAILS_VOL.equals(nomEcran)) {
            panneauDetailsVol.rafraichir();
        } else if (INFOS_PASSAGERS.equals(nomEcran)) {
            panneauInfosPassagers.rafraichir();
        } else if (SURCLASSEMENT_INITIAL.equals(nomEcran)) {
            panneauSurclassementInitial.rafraichir();
        } else if (SIEGES.equals(nomEcran)) {
            panneauSieges.rafraichir();
        } else if (RECAPITULATIF.equals(nomEcran)) {
            panneauRecapitulatif.rafraichir();
        } else if (CONFIRMATION.equals(nomEcran)) {
            panneauConfirmation.rafraichir();
        } else if (CONSULTATION.equals(nomEcran)) {
            panneauConsultation.rafraichir();
        } else if (BAGAGE.equals(nomEcran)) {
            panneauAjoutBagage.rafraichir();
        } else if (CARTE_EMBARQUEMENT.equals(nomEcran)) {
            panneauCarteEmbarquement.rafraichir();
        } else if (SURCLASSEMENT.equals(nomEcran)) {
            panneauSurclassement.rafraichir();
        } else if (REPAS_SPECIAL.equals(nomEcran)) {
            panneauRepasSpecial.rafraichir();
        } else if (OPTIONS_BILLET.equals(nomEcran)) {
            panneauOptionsBillet.rafraichir();
        } else if (ADMIN_LOGIN.equals(nomEcran)) {
            panneauAdminLogin.rafraichir();
        } else if (ADMIN_AJOUTER_VOL.equals(nomEcran)) {
            panneauAjouterVol.rafraichir();
        } else if (ADMIN_MODIFIER_VOL.equals(nomEcran)) {
            panneauModifierVol.rafraichir();
        } else if (ADMIN_SUPPRIMER_VOL.equals(nomEcran)) {
            panneauSupprimerVol.rafraichir();
        } else if (ADMIN_LISTE_VOLS.equals(nomEcran)) {
            panneauListeVols.rafraichir();
        } else if (ADMIN_MANIFESTE.equals(nomEcran)) {
            panneauManifesteVol.rafraichir();
        } else if (ADMIN_VOLS_COMPLETS.equals(nomEcran)) {
            panneauVolsComplets.rafraichir();
        } else if (ADMIN_CHIFFRE_AFFAIRES.equals(nomEcran)) {
            panneauChiffreAffaires.rafraichir();
        }
        cardLayout.show(panneauCentral, nomEcran);
    }

    /**
     * Ouvre un ecran d'action post-reservation (ajout de bagage, carte
     * d'embarquement, surclassement) pour le billet designe, en memorisant
     * sa reference et celle du dossier pour que l'ecran cible puisse les
     * relire dans rafraichir().
     */
    public void ouvrirActionSurBillet(String nomEcran, String refBillet, String refCommande) {
        this.refBilletSelectionne = refBillet;
        this.refCommandeSelectionnee = refCommande;
        afficherPanneau(nomEcran);
    }

    /**
     * Revient a l'ecran de consultation depuis un ecran d'action, en
     * rafraichissant le dossier affiche (donnees potentiellement
     * modifiees) plutot qu'en reinitialisant tout le formulaire de
     * recherche comme le ferait afficherPanneau(CONSULTATION).
     */
    public void revenirVersConsultation() {
        panneauConsultation.rafraichirApresAction();
        cardLayout.show(panneauCentral, CONSULTATION);
    }

    public String getRefBilletSelectionne() { return refBilletSelectionne; }
    public String getRefCommandeSelectionnee() { return refCommandeSelectionnee; }

    public VolService getVolService() { return volService; }
    public SiegeService getSiegeService() { return siegeService; }
    public TarifService getTarifService() { return tarifService; }
    public ReservationService getReservationService() { return reservationService; }
    public AuthService getAuthService() { return authService; }
    public PlanCabineService getPlanCabineService() { return planCabineService; }
    public EtatReservation getEtat() { return etat; }
}
