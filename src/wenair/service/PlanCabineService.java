package wenair.service;

import wenair.modele.CaseSiege;
import wenair.modele.RangeesCabine;
import wenair.modele.Vol;

import java.util.ArrayList;
import java.util.List;

/**
 * Construit le plan de la cabine (rangées par classe, cases de sièges
 * avec disponibilité et prix) pour l'écran de sélection des sièges.
 */
public class PlanCabineService {

    private final SiegeService siegeService;
    private final TarifService tarifService;

    public PlanCabineService(SiegeService siegeService, TarifService tarifService) {
        this.siegeService = siegeService;
        this.tarifService = tarifService;
    }

    /** Calcule les bornes de rangées de chaque classe pour un vol donné. */
    public RangeesCabine calculerRangees(Vol vol) {
        int nbRangeesBusiness = (vol.getPlacesBusiness() + 3) / 4;
        int nbRangeesExtraLeg = (vol.getPlacesExtraLeg() + 5) / 6;
        int nbRangeesEco = (vol.getPlacesEco() + 5) / 6;
        int nbRangeesMineur = (vol.getPlacesMineur() + 5) / 6;

        int rangeeFinBusiness = nbRangeesBusiness;
        int rangeeDebutExtraLeg = rangeeFinBusiness + 1;
        int rangeeFinExtraLeg = rangeeDebutExtraLeg + nbRangeesExtraLeg - 1;
        int rangeeDebutEco = rangeeFinExtraLeg + 1;
        int rangeeFinEco = rangeeDebutEco + nbRangeesEco - 1;
        int rangeeDebutMineur = rangeeFinEco + 1;
        int rangeeFinMineur = rangeeDebutMineur + nbRangeesMineur - 1;

        return new RangeesCabine(rangeeFinBusiness, rangeeDebutExtraLeg, rangeeFinExtraLeg,
                rangeeDebutEco, rangeeFinEco, rangeeDebutMineur, rangeeFinMineur);
    }

    /**
     * Génère la liste des cases de sièges pour une classe donnée, dans
     * l'ordre d'affichage (rangée par rangée, gauche puis droite), avec
     * pour chacune sa disponibilité et son supplément.
     */
    public List<CaseSiege> genererSieges(String numeroVol, String date, String classeReelle,
                                          int rangeeDebut, int rangeeFin, int rangeeDebutEco, int rangeeFinEco,
                                          int nbSiegesClasse) {
        List<CaseSiege> sieges = new ArrayList<CaseSiege>();

        char[] colsGauche;
        char[] colsDroite;
        int colsParRangee;

        if (classeReelle.equalsIgnoreCase("BUSINESS")) {
            colsGauche = new char[] { 'A', 'C' };
            colsDroite = new char[] { 'D', 'F' };
            colsParRangee = 4;
        } else {
            colsGauche = new char[] { 'A', 'B', 'C' };
            colsDroite = new char[] { 'D', 'E', 'F' };
            colsParRangee = 6;
        }

        int siegesAffiches = 0;

        for (int rangee = rangeeDebut; rangee <= rangeeFin; rangee++) {
            int siegesRestants = nbSiegesClasse - siegesAffiches;
            if (siegesRestants <= 0) {
                break;
            }

            int colsCetteRangee = (siegesRestants >= colsParRangee) ? colsParRangee : siegesRestants;

            int nbGauche;
            if (classeReelle.equalsIgnoreCase("BUSINESS")) {
                nbGauche = (colsCetteRangee >= 2) ? 2 : colsCetteRangee;
            } else {
                nbGauche = (colsCetteRangee >= 3) ? 3 : colsCetteRangee;
            }
            int nbDroite = colsCetteRangee - nbGauche;

            for (int c = 0; c < nbGauche; c++) {
                sieges.add(construireCase(numeroVol, date, rangee, colsGauche[c], rangeeDebutEco, rangeeFinEco, classeReelle));
            }
            for (int c = 0; c < nbDroite; c++) {
                sieges.add(construireCase(numeroVol, date, rangee, colsDroite[c], rangeeDebutEco, rangeeFinEco, classeReelle));
            }

            siegesAffiches += colsCetteRangee;
        }

        return sieges;
    }

    private CaseSiege construireCase(String numeroVol, String date, int rangee, char colonne,
                                      int rangeeDebutEco, int rangeeFinEco, String classeReelle) {
        String numeroSiege = rangee + String.valueOf(colonne);
        boolean pris = siegeService.estPris(numeroVol, date, numeroSiege);
        double prix = tarifService.calculerPrixSiege(rangee, rangeeDebutEco, rangeeFinEco, classeReelle);
        return new CaseSiege(rangee, colonne, numeroSiege, !pris, prix);
    }
}
