package railway;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                RailwayService service = new RailwayService();
                new MainFrame(service).setVisible(true);
            } catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "Could not start the application:\n" + e.getMessage(),
                        "Startup Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}
