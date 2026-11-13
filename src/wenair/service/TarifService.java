package wenair.service;

import wenair.modele.OptionBagage;
import wenair.modele.PrixCalcule;
import wenair.modele.Vol;

import java.util.Random;

/**
 * Calcule les prix des billets et des sièges, avec une part aléatoire
 * pour certains suppléments (Extra Leg et mineurs non accompagnés).
 */
public class TarifService {

    private final Random alea = new Random();

    /**
     * Calcule le prix adulte / enfant / adulte-accompagnant-un-mineur pour
     * un vol et une classe donnés. classe attendue : "ECONOMY", "BUSINESS",
     * "EXTRA LEG" ou "MINEUR UM" (les autres valeurs sont traitées comme
     * "ECONOMY").
     */
    public PrixCalcule calculerPrix(Vol vol, String classe) {
        double base = vol.getPrixBase();

        if (classe != null && classe.equalsIgnoreCase("BUSINESS")) {
            double prixAdulte = base * 6.0;
            double prixEnfant = base * 5.0;
            return new PrixCalcule(prixAdulte, prixEnfant, prixAdulte);

        } else if (classe != null && classe.equalsIgnoreCase("EXTRA LEG")) {
            double supplement = alea.nextInt(36) + 25; // entre 25 et 60
            double reducEnfant = alea.nextInt(21) + 10; // entre 10 et 30
            double prixAdulte = base + supplement;
            double prixEnfant = prixAdulte - reducEnfant;
            return new PrixCalcule(prixAdulte, prixEnfant, prixAdulte);

        } else if (classe != null && (classe.equalsIgnoreCase("MINEUR") || classe.equalsIgnoreCase("MINEUR UM"))) {
            double supplementUmEnfant = alea.nextInt(41) + 10; // entre 10 et 50
            double supplementUmAdo = alea.nextInt(31) + 20;    // entre 20 et 50
            double prixEnfant = base + supplementUmEnfant;
            double prixMineurAdulte = base + supplementUmAdo;
            return new PrixCalcule(prixMineurAdulte, prixEnfant, prixMineurAdulte);

        } else { // ECONOMY (et toute classe non reconnue)
            double reducEnfant = alea.nextInt(21) + 10; // entre 10 et 30
            double prixAdulte = base;
            double prixEnfant = base - reducEnfant;
            return new PrixCalcule(prixAdulte, prixEnfant, prixAdulte);
        }
    }

    /**
     * Options d'achat de bagage supplementaire proposees pour une classe
     * donnee : 3 paliers poids/prix fixes. Les bebes n'ont pas d'option
     * (verifie a part, avant l'appel a cette methode).
     */
    public OptionBagage[] optionsBagageSupplementaire(String classe) {
        if (classe != null && classe.equalsIgnoreCase("BUSINESS")) {
            return new OptionBagage[] {
                    new OptionBagage(24, 40.0),
                    new OptionBagage(30, 45.0),
                    new OptionBagage(50, 60.0)
            };
        } else if (classe != null && classe.equalsIgnoreCase("EXTRA LEG")) {
            return new OptionBagage[] {
                    new OptionBagage(13, 25.0),
                    new OptionBagage(18, 35.0),
                    new OptionBagage(25, 45.0)
            };
        } else { // ECONOMY et MINEUR UM
            return new OptionBagage[] {
                    new OptionBagage(10, 23.0),
                    new OptionBagage(15, 33.0),
                    new OptionBagage(25, 40.0)
            };
        }
    }

    /**
     * Tarif "adulte" cible d'une classe pour l'offre de surclassement :
     * EXTRA LEG = prix de base x1.3, BUSINESS = prix de base x6.0. Ne
     * s'applique qu'a la classe cible ; la classe de depart n'intervient
     * pas dans ce calcul.
     */
    public double tarifCibleSurclassement(double prixBase, String classeCible) {
        if (classeCible != null && classeCible.equalsIgnoreCase("BUSINESS")) {
            return prixBase * 6.0;
        } else if (classeCible != null && classeCible.equalsIgnoreCase("EXTRA LEG")) {
            return prixBase * 1.3;
        }
        return prixBase;
    }

    /**
     * Estimation du tarif "actuel" utilisee comme reference pour calculer
     * le supplement de surclassement (le prix reellement paye n'est pas
     * conserve, on reutilise donc la meme regle de tarif de base que
     * tarifCibleSurclassement).
     */
    public double tarifActuelEstime(double prixBase, String classeActuelle) {
        return tarifCibleSurclassement(prixBase, classeActuelle);
    }

    /**
     * Montant facture estime pour un billet, utilise pour le rapport de
     * chiffre d'affaires : prix de base x un coefficient selon la classe
     * (BUSINESS x2.5, EXTRA LEG x1.3, MINEUR UM x1.2, sinon x1.0), plus
     * 15 EUR par kg de bagage supplementaire.
     */
    public double montantFactureVol(double prixBase, String classe, int poidsBagageSupp) {
        double coefficient = 1.0;
        if (classe != null && classe.equalsIgnoreCase("BUSINESS")) {
            coefficient = 2.5;
        } else if (classe != null && classe.equalsIgnoreCase("EXTRA LEG")) {
            coefficient = 1.3;
        } else if (classe != null && classe.equalsIgnoreCase("MINEUR UM")) {
            coefficient = 1.2;
        }
        double coutBagage = poidsBagageSupp * 15.0;
        return (prixBase * coefficient) + coutBagage;
    }

    /** Poids de bagage inclus (en kg) selon la classe. */
    public int bagageInclusKg(String classe) {
        if (classe != null && classe.equalsIgnoreCase("BUSINESS")) {
            return 24;
        } else if (classe != null && classe.equalsIgnoreCase("EXTRA LEG")) {
            return 15;
        }
        return 12;
    }

    /**
     * Prix du supplément pour choisir un siège précis, selon sa rangée et
     * sa classe : gratuit en Business et pour un mineur non accompagné,
     * 20€ fixe en Extra Leg, et en Economy gratuit pour les rangées 10 à
     * 15 puis un tarif dégressif (25€ / 15€ / 10€) selon la position
     * dans la classe.
     */
    public double calculerPrixSiege(int rangee, int rangeeDebutEco, int rangeeFinEco, String classeReelle) {
        if (classeReelle != null && classeReelle.equalsIgnoreCase("BUSINESS")) {
            return 0.0;
        }
        if (classeReelle != null && (classeReelle.equalsIgnoreCase("MINEUR") || classeReelle.equalsIgnoreCase("MINEUR UM"))) {
            return 0.0;
        }
        if (classeReelle != null && classeReelle.equalsIgnoreCase("EXTRA LEG")) {
            return 20.0;
        }

        // Rangées 10 à 15 : gratuites en Economy
        if (rangee >= 10 && rangee <= 15) {
            return 0.0;
        }

        // ECONOMY : prix selon la position dans la classe
        int nbRangeesEco = rangeeFinEco - rangeeDebutEco + 1;
        int tier1 = rangeeDebutEco + (nbRangeesEco / 3);
        int tier2 = rangeeDebutEco + (nbRangeesEco * 2 / 3);

        if (rangee < tier1) {
            return 25.0;
        }
        if (rangee < tier2) {
            return 15.0;
        }
        return 10.0;
    }
}
