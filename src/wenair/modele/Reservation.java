package wenair.modele;

import java.util.Collections;
import java.util.List;

/** Regroupe tous les billets partageant la même référence de commande (PNR). */
public class Reservation {

    private final String refCommande;
    private final List<Billet> billets;

    public Reservation(String refCommande, List<Billet> billets) {
        this.refCommande = refCommande;
        this.billets = Collections.unmodifiableList(billets);
    }

    public String getRefCommande() {
        return refCommande;
    }

    public List<Billet> getBillets() {
        return billets;
    }

    public int getNombrePassagers() {
        return billets.size();
    }

    /** Premier billet du groupe : sert de "contact" (nom / email) pour la réservation. */
    public Billet getBilletContact() {
        if (billets.isEmpty()) {
            return null;
        }
        return billets.get(0);
    }

    /** Numéro du vol commun à tous les billets de la réservation. */
    public String getNumeroVol() {
        Billet contact = getBilletContact();
        return contact == null ? null : contact.getNumeroVol();
    }

    /** Date du vol commune à tous les billets de la réservation. */
    public String getDateVol() {
        Billet contact = getBilletContact();
        return contact == null ? null : contact.getDateVol();
    }
}
