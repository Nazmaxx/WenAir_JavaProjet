package wenair.vue;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * Panneau qui empile ses composants verticalement (BoxLayout.Y_AXIS), fait
 * pour être mis dans un JScrollPane. Implémente Scrollable pour que sa
 * largeur suive toujours celle de la fenêtre visible : les lignes qu'il
 * contient (résultats, billets...) ne débordent donc jamais horizontalement,
 * même quand la barre de défilement verticale apparaît.
 */
public class PanneauListeVerticale extends JPanel implements Scrollable {

    public PanneauListeVerticale() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 100;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        // La largeur suit toujours celle du JScrollPane : les lignes internes
        // ne peuvent jamais devenir plus larges que la fenêtre visible.
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        // La hauteur reste celle du contenu (pour permettre le défilement vertical).
        return false;
    }
}
