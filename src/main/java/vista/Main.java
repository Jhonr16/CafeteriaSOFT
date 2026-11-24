package vista;

import javax.swing.SwingUtilities;
import servicio.CafeteriaNucleo;

public class Main {
    public static void main(String[] args) {
        CafeteriaNucleo nucleo = new CafeteriaNucleo();
        boolean usarConsola = args.length > 0 && args[0].equalsIgnoreCase("--consola");

        if (usarConsola) {
            ConsolaApp consola = new ConsolaApp(nucleo);
            consola.ejecutar();
        } else {
            SwingUtilities.invokeLater(() -> {
                VentanaApp ventana = new VentanaApp(nucleo);
                ventana.setVisible(true);
            });
        }
    }
}