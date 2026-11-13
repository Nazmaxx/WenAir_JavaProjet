package wenair;

import wenair.vue.FenetrePrincipale;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Point d'entrée de l'application Wen Air. Lance la fenêtre principale
 * via Swing.
 */
public class WenAir {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception exception) {
         
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                FenetrePrincipale fenetre = new FenetrePrincipale();
                fenetre.setVisible(true);
            }
        });
    }
}
