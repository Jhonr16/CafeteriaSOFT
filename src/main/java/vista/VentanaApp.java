package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import modelo.ComboItem;
import modelo.EstadoPedido;
import modelo.ItemMenu;
import modelo.MedioPago;
import modelo.Pedido;
import modelo.ResumenTurno;
import patron.HappyHourStrategy;
import patron.PromoFinDeSemanaStrategy;
import patron.SinPromocionStrategy;
import servicio.CafeteriaNucleo;

public class VentanaApp extends JFrame {
    private final CafeteriaNucleo nucleo;
    private final JTextArea areaSalida;
    private Pedido pedidoActual;

    public VentanaApp(CafeteriaNucleo nucleo) {
        this.nucleo = nucleo;
        this.areaSalida = new JTextArea(14, 70);
        this.areaSalida.setEditable(false);

        setTitle("cafeteriaSOFT - Sistema de Gestión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);

        construirInterfaz();
        refrescarCatalogoEnLog();
    }

    private void construirInterfaz() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Catálogo y Combos", crearPanelCatalogoYCombos());
        tabs.addTab("Pedidos, Estados y Pagos", crearPanelPedidos());
        tabs.addTab("Turno y Notificaciones", crearPanelTurno());

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
        add(new JScrollPane(areaSalida), BorderLayout.SOUTH);
    }

    private JPanel crearPanelCatalogoYCombos() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));

        JPanel filaPersonalizar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtCodigoItem = new JTextField("BEB01", 8);
        JCheckBox cbAlmendra = new JCheckBox("Leche Almendra (+2500)");
        JCheckBox cbShot = new JCheckBox("Extra Shot (+3000)");
        JCheckBox cbCaramelo = new JCheckBox("Caramelo (+2000)");
        JCheckBox cbQueso = new JCheckBox("Queso Extra (+3500)");
        JButton btnAgregarPersonalizado = new JButton("Agregar al Pedido Actual");

        btnAgregarPersonalizado.addActionListener(e -> {
            try {
                asegurarPedidoActual();
                List<String> adds = new ArrayList<>();
                if (cbAlmendra.isSelected()) adds.add("ADD_ALMENDRA");
                if (cbShot.isSelected()) adds.add("ADD_SHOT");
                if (cbCaramelo.isSelected()) adds.add("ADD_CARAMELO");
                if (cbQueso.isSelected()) adds.add("ADD_QUESO");

                ItemMenu item = nucleo.personalizarProducto(txtCodigoItem.getText().trim(), adds);
                nucleo.agregarItem(pedidoActual.getId(), item);
                log("Agregado a " + pedidoActual.getId() + ": " + item.getDescripcionDetallada()
                        + " ($" + String.format("%.0f", item.calcularPrecio()) + ")");
            } catch (Exception ex) {
                mostrarError(ex.getMessage());
            }
        });

        filaPersonalizar.add(new JLabel("Código:"));
        filaPersonalizar.add(txtCodigoItem);
        filaPersonalizar.add(cbAlmendra);
        filaPersonalizar.add(cbShot);
        filaPersonalizar.add(cbCaramelo);
        filaPersonalizar.add(cbQueso);
        filaPersonalizar.add(btnAgregarPersonalizado);

        JPanel filaCombo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtBeb = new JTextField("BEB02", 6);
        JTextField txtPla = new JTextField("PLA01", 6);
        JTextField txtAco = new JTextField("ACO01", 6);
        JTextField txtPos = new JTextField("POS01", 6);
        JTextField txtPrecioCombo = new JTextField("22000", 8);
        JButton btnCrearCombo = new JButton("Crear Combo y Agregar");

        btnCrearCombo.addActionListener(e -> {
            try {
                asegurarPedidoActual();
                ComboItem combo = nucleo.crearYRegistrarCombo(
                        "CMB-" + System.currentTimeMillis() % 1000,
                        "Combo Especial",
                        txtBeb.getText().trim(),
                        txtPla.getText().trim(),
                        txtAco.getText().trim(),
                        txtPos.getText().trim(),
                        Double.parseDouble(txtPrecioCombo.getText().trim())
                );
                nucleo.agregarItem(pedidoActual.getId(), combo);
                log("Combo agregado: " + combo.getDescripcionDetallada()
                        + " ($" + String.format("%.0f", combo.calcularPrecio()) + ")");
            } catch (Exception ex) {
                mostrarError(ex.getMessage());
            }
        });

        filaCombo.add(new JLabel("Bebida:"));
        filaCombo.add(txtBeb);
        filaCombo.add(new JLabel("Plato:"));
        filaCombo.add(txtPla);
        filaCombo.add(new JLabel("Acomp:"));
        filaCombo.add(txtAco);
        filaCombo.add(new JLabel("Postre:"));
        filaCombo.add(txtPos);
        filaCombo.add(new JLabel("Precio:"));
        filaCombo.add(txtPrecioCombo);
        filaCombo.add(btnCrearCombo);

        JButton btnVerCatalogo = new JButton("Ver Catálogo Completo");
        btnVerCatalogo.addActionListener(e -> refrescarCatalogoEnLog());

        panel.add(filaPersonalizar);
        panel.add(filaCombo);
        panel.add(btnVerCatalogo);
        return panel;
    }

    private JPanel crearPanelPedidos() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));

        JPanel filaCrear = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtCliente = new JTextField("Cliente Mostrador", 15);
        JButton btnNuevoPedido = new JButton("Crear Pedido");
        btnNuevoPedido.addActionListener(e -> {
            pedidoActual = nucleo.iniciarPedido(txtCliente.getText().trim());
            log("Nuevo pedido creado: " + pedidoActual.getId() + " para " + pedidoActual.getCliente());
        });
        filaCrear.add(new JLabel("Cliente:"));
        filaCrear.add(txtCliente);
        filaCrear.add(btnNuevoPedido);

        JPanel filaEstado = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<EstadoPedido> cbEstado = new JComboBox<>(EstadoPedido.values());
        JButton btnCambiarEstado = new JButton("Cambiar Estado");
        btnCambiarEstado.addActionListener(e -> {
            try {
                asegurarPedidoActual();
                EstadoPedido estado = (EstadoPedido) cbEstado.getSelectedItem();
                nucleo.cambiarEstado(pedidoActual.getId(), estado);
                log("Pedido " + pedidoActual.getId() + " -> " + estado);
            } catch (Exception ex) {
                mostrarError(ex.getMessage());
            }
        });
        filaEstado.add(new JLabel("Estado:"));
        filaEstado.add(cbEstado);
        filaEstado.add(btnCambiarEstado);

        JPanel filaPromoPago = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> cbPromo = new JComboBox<>(new String[]{
                "Sin Promoción", "Happy Hour (20% OFF)", "Fin de Semana (15% OFF)"
        });
        JButton btnAplicarPromo = new JButton("Aplicar Promo");
        btnAplicarPromo.addActionListener(e -> {
            int idx = cbPromo.getSelectedIndex();
            if (idx == 1) nucleo.configurarPromocion(new HappyHourStrategy(0.20, 15, 18, true));
            else if (idx == 2) nucleo.configurarPromocion(new PromoFinDeSemanaStrategy(0.15, true));
            else nucleo.configurarPromocion(new SinPromocionStrategy());
            log("Promoción seleccionada: " + cbPromo.getSelectedItem());
        });

        JComboBox<MedioPago> cbMedioPago = new JComboBox<>(MedioPago.values());
        JButton btnPagar = new JButton("Simular Pago");
        btnPagar.addActionListener(e -> {
            try {
                asegurarPedidoActual();
                MedioPago medio = (MedioPago) cbMedioPago.getSelectedItem();
                Pedido pagado = nucleo.pagarPedido(pedidoActual.getId(), medio);
                log("Pedido " + pagado.getId() + " pagado por " + medio
                        + " | Subtotal: $" + String.format("%.0f", pagado.getSubtotal())
                        + " | Desc: $" + String.format("%.0f", pagado.getDescuentoAplicado())
                        + " | Total: $" + String.format("%.0f", pagado.getTotalFinal()));
            } catch (Exception ex) {
                mostrarError(ex.getMessage());
            }
        });

        filaPromoPago.add(cbPromo);
        filaPromoPago.add(btnAplicarPromo);
        filaPromoPago.add(new JLabel("Pago:"));
        filaPromoPago.add(cbMedioPago);
        filaPromoPago.add(btnPagar);

        panel.add(filaCrear);
        panel.add(filaEstado);
        panel.add(filaPromoPago);
        return panel;
    }

    private JPanel crearPanelTurno() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnHistorial = new JButton("Ver Historial Pedidos");
        JButton btnCuadre = new JButton("Ver Cuadre de Turno");
        JButton btnNotif = new JButton("Ver Notificaciones Estaciones");

        btnHistorial.addActionListener(e -> {
            log("=== HISTORIAL ===");
            for (Pedido p : nucleo.historialPedidos()) {
                log(p.getId() + " | " + p.getCliente() + " | " + p.getEstado()
                        + " | Total: $" + String.format("%.0f", p.getTotalFinal())
                        + " | Pagado: " + p.isPagado());
            }
        });

        btnCuadre.addActionListener(e -> {
            ResumenTurno r = nucleo.cuadreTurno();
            log("=== CUADRE TURNO === Pedidos pagados: " + r.getPedidosPagados().size()
                    + " | Total recaudado: $" + String.format("%.0f", r.getTotalRecaudado()));
        });

        btnNotif.addActionListener(e -> {
            log("=== NOTIFICACIONES ===");
            for (String n : nucleo.getNotificacionServicio().getLogGlobalNotificaciones()) {
                log(n);
            }
        });

        panel.add(btnHistorial);
        panel.add(btnCuadre);
        panel.add(btnNotif);
        return panel;
    }

    private void asegurarPedidoActual() {
        if (pedidoActual == null) {
            pedidoActual = nucleo.iniciarPedido("Cliente General");
            log("Creado automáticamente pedido " + pedidoActual.getId());
        }
    }

    private void refrescarCatalogoEnLog() {
        log("=== CATÁLOGO DISPONIBLE ===");
        for (ItemMenu item : nucleo.listarCatalogo()) {
            log("[" + item.getCodigo() + "] " + item.getDescripcionDetallada()
                    + " - $" + String.format("%.0f", item.calcularPrecio())
                    + (item.isDisponible() ? " (Disponible)" : " (No disponible)"));
        }
    }

    private void log(String mensaje) {
        areaSalida.append(mensaje + "\n");
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}