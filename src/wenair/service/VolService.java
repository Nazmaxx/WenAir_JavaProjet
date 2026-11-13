package wenair.service;

import wenair.donnees.DonneesVol;
import wenair.modele.PrixCalcule;
import wenair.modele.ResultatRecherche;
import wenair.modele.Vol;

import java.util.ArrayList;
import java.util.List;

/**
 * Recherche de vols et gestion des vols côté admin : vérifie la
 * correspondance ville/aéroport, qu'il reste de la place dans la classe
 * demandée, calcule les prix, et trie du moins cher au plus cher.
 */
public class VolService {

    private final DonneesVol donneesVol;
    private final SiegeService siegeService;
    private final TarifService tarifService;

    public VolService(DonneesVol donneesVol, SiegeService siegeService, TarifService tarifService) {
        this.donneesVol = donneesVol;
        this.siegeService = siegeService;
        this.tarifService = tarifService;
    }

    public List<Vol> listerTous() {
        return donneesVol.chargerTous();
    }

    public Vol trouverParNumero(String numeroVol) {
        return donneesVol.trouverParNumero(numeroVol);
    }

    /**
     * Recherche les vols entre deux villes, pour une classe et une date
     * données, avec assez de places disponibles. Le résultat est trié du
     * prix adulte le moins cher au plus cher.
     *
     * Si nbMineursUm ou nbAdultesUm est supérieur à 0, les prix pour un
     * mineur non accompagné (et l'adulte qui l'accompagne) sont aussi
     * calculés.
     */
    public List<ResultatRecherche> rechercherVols(String villeDep, String villeArr, String classe, String date,
                                                   int nbMineursUm, int nbAdultesUm) {
        List<ResultatRecherche> resultats = new ArrayList<ResultatRecherche>();

        for (Vol vol : donneesVol.chargerTous()) {
            if (villeDep == null || villeArr == null) {
                continue;
            }
            if (!villeDep.equalsIgnoreCase(vol.getDepVille()) || !villeArr.equalsIgnoreCase(vol.getArrVille())) {
                continue;
            }

            if (classe.equalsIgnoreCase("EXTRA LEG") && vol.getPlacesExtraLeg() == 0) {
                continue;
            }
            if (classe.equalsIgnoreCase("BUSINESS") && vol.getPlacesBusiness() == 0) {
                continue;
            }

            String classeSiege;
            if (classe.equalsIgnoreCase("BUSINESS")) {
                classeSiege = "BUSINESS";
            } else if (classe.equalsIgnoreCase("EXTRA LEG")) {
                classeSiege = "EXTRA LEG";
            } else {
                classeSiege = "ECONOMY";
            }

            int placesTotalesClasse = vol.placesPourClasse(classeSiege);
            int placesPrises = siegeService.compterSiegesPris(vol.getNumeroVol(), date, classeSiege);
            int placesRestantes = placesTotalesClasse - placesPrises;

            if (placesRestantes <= 0) {
                continue;
            }

            PrixCalcule prix = tarifService.calculerPrix(vol, classe);
            double prixMineur = 0;
            double prixMineurAdulte = 0;
            if (nbMineursUm > 0 || nbAdultesUm > 0) {
                PrixCalcule prixUm = tarifService.calculerPrix(vol, "MINEUR UM");
                prixMineur = prixUm.getPrixEnfant();
                prixMineurAdulte = prixUm.getPrixMineurAdulte();
            }

            resultats.add(new ResultatRecherche(vol, prix.getPrixAdulte(), prix.getPrixEnfant(),
                    prixMineur, prixMineurAdulte, placesRestantes));
        }

        trierParPrixCroissant(resultats);
        return resultats;
    }

    /** Tri par sélection, du prix adulte le moins cher au plus cher. */
    private void trierParPrixCroissant(List<ResultatRecherche> resultats) {
        for (int i = 0; i < resultats.size() - 1; i++) {
            int indexMin = i;
            for (int j = i + 1; j < resultats.size(); j++) {
                if (resultats.get(j).getPrixAdulte() < resultats.get(indexMin).getPrixAdulte()) {
                    indexMin = j;
                }
            }
            if (indexMin != i) {
                ResultatRecherche temp = resultats.get(i);
                resultats.set(i, resultats.get(indexMin));
                resultats.set(indexMin, temp);
            }
        }
    }

    // --- Espace admin ---

    /** Ajoute un nouveau vol. Refuse si un vol avec le même numéro existe déjà. */
    public void ajouterVol(Vol vol) {
        if (vol.getNumeroVol() == null || vol.getNumeroVol().isBlank()) {
            throw new IllegalArgumentException("Le numéro de vol est obligatoire.");
        }
        if (donneesVol.trouverParNumero(vol.getNumeroVol()) != null) {
            throw new IllegalArgumentException("Un vol avec le numéro " + vol.getNumeroVol() + " existe déjà.");
        }
        donneesVol.ajouter(vol);
    }

    public void modifierVol(Vol vol) {
        donneesVol.mettreAJour(vol);
    }

    public boolean supprimerVol(String numeroVol) {
        return donneesVol.supprimer(numeroVol);
    }

    /**
     * Calcule automatiquement la duree de vol a partir des heures de
     * depart et d'arrivee (format HH:MM), en supposant une arrivee le
     * lendemain si l'heure d'arrivee est plus petite que l'heure de
     * depart. Format de sortie "XhYY" (ex : 1h35).
     */
    public String calculerDuree(String heureDepart, String heureArrivee) {
        int depH = Integer.parseInt(heureDepart.substring(0, 2));
        int depM = Integer.parseInt(heureDepart.substring(3, 5));
        int arrH = Integer.parseInt(heureArrivee.substring(0, 2));
        int arrM = Integer.parseInt(heureArrivee.substring(3, 5));

        int depTotal = depH * 60 + depM;
        int arrTotal = arrH * 60 + arrM;

        if (arrTotal < depTotal) {
            arrTotal += 24 * 60;
        }

        int diff = arrTotal - depTotal;
        int heures = diff / 60;
        int minutes = diff % 60;

        if (minutes < 10) {
            return heures + "h0" + minutes;
        }
        return heures + "h" + minutes;
    }

    /**
     * Liste les vols complets a une date donnee (espace admin) : un vol
     * est complet quand ses places Business, Extra Leg et Economy sont
     * toutes occupees (la classe Mineur n'entre pas dans ce calcul).
     */
    public List<Vol> volsCompletsPourDate(String date) {
        List<Vol> complets = new ArrayList<Vol>();
        for (Vol vol : donneesVol.chargerTous()) {
            int prisBusiness = siegeService.compterSiegesPris(vol.getNumeroVol(), date, "BUSINESS");
            int prisExtraLeg = siegeService.compterSiegesPris(vol.getNumeroVol(), date, "EXTRA LEG");
            int prisEco = siegeService.compterSiegesPris(vol.getNumeroVol(), date, "ECONOMY");

            int restantsBusiness = vol.getPlacesBusiness() - prisBusiness;
            int restantsExtraLeg = vol.getPlacesExtraLeg() - prisExtraLeg;
            int restantsEco = vol.getPlacesEco() - prisEco;

            if (restantsBusiness <= 0 && restantsExtraLeg <= 0 && restantsEco <= 0) {
                complets.add(vol);
            }
        }
        return complets;
    }
}
