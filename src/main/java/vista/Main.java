package vista;

import javax.swing.SwingUtilities;
import servicio.CafeteriaNucleo;

public class Main {
    public static void main(String[] args) {
        CafeteriaNucleo nucleo = new CafeteriaNucleo();
        boolean usarGui = args.length > 0 && args[0].equalsIgnoreCase("--gui");

        if (usarGui) {
            SwingUtilities.invokeLater(() -> {
                VentanaApp ventana = new VentanaApp(nucleo);
                ventana.setVisible(true);
            });
        } else {
            ConsolaApp consola = new ConsolaApp(nucleo);
            consola.ejecutar();
        }
    }
}