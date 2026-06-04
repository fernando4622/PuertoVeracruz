package veracruz;

import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 *
 * @author ferna
 */
public class PuertoVeracruz {
    public static void main(String[] args) {
        GLProfile.initSingleton();
        SwingUtilities.invokeLater(() -> {
            VeracruzDemo panel = new VeracruzDemo();
            JFrame frame = new JFrame(
                "Puerto de Veracruz");
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.getContentPane().add(panel);
            frame.setSize(1280, 720);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            panel.requestFocusInWindow();
            FPSAnimator anim = new FPSAnimator(panel, 60, true);
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    anim.stop(); frame.dispose(); System.exit(0);
                }
            });
            anim.start();
        });
    }
}
