package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import modelo.ComboItem;
import modelo.EstadoPedido;
import modelo.ItemMenu;
import modelo.MedioPago;
import modelo.Pedido;
import modelo.ProductoBase;
import modelo.ResumenTurno;
import modelo.TipoItem;
import patron.HappyHourStrategy;
import patron.PromoFinDeSemanaStrategy;
import patron.SinPromocionStrategy;
import servicio.CafeteriaNucleo;

public class VentanaApp extends JFrame {
    private final CafeteriaNucleo nucleo;
    private final JLabel lblPedidoActivo;
    private final JTextArea areaSalida;
    private final DefaultTableModel modeloCatalogo;

    private final JComboBox<OpcionCatalogo> cbProductoPersonalizar;
    private final JComboBox<OpcionCatalogo> cbComboBebida;
    private final JComboBox<OpcionCatalogo> cbComboPlato;
    private final JComboBox<OpcionCatalogo> cbComboAcomp;
    private final JComboBox<OpcionCatalogo> cbComboPostre;

    private final JPanel filaAdiciones;
    private final Map<JCheckBox, ItemMenu> checkboxesAdicionesDinamicas;

    private final JCheckBox cbAlmendra;
    private final JCheckBox cbShot;
    private final JCheckBox cbCaramelo;
    private final JCheckBox cbQueso;

    private Pedido pedidoActual;

    private record OpcionCatalogo(String codigo, String etiqueta) {
        @Override
        public String toString() {
            return etiqueta;
        }
    }

    public VentanaApp(CafeteriaNucleo nucleo) {
        this.nucleo = nucleo;
        this.lblPedidoActivo = new JLabel("Pedido activo: Ninguno");
        this.lblPedidoActivo.setFont(new Font("SansSerif", Font.BOLD, 13));

        this.areaSalida = new JTextArea(8, 70);
        this.areaSalida.setEditable(false);
        this.areaSalida.setFont(new Font("Monospaced", Font.PLAIN, 12));

        this.modeloCatalogo = new DefaultTableModel(
                new Object[]{"Código", "Tipo", "Descripción", "Precio", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        this.cbProductoPersonalizar = new JComboBox<>();
        this.cbComboBebida = new JComboBox<>();
        this.cbComboPlato = new JComboBox<>();
        this.cbComboAcomp = new JComboBox<>();
        this.cbComboPostre = new JComboBox<>();

        this.filaAdiciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        this.checkboxesAdicionesDinamicas = new LinkedHashMap<>();

        this.cbAlmendra = new JCheckBox("Leche Almendra (+2500)");
        this.cbShot = new JCheckBox("Extra Shot (+3000)");
        this.cbCaramelo = new JCheckBox("Caramelo (+2000)");
        this.cbQueso = new JCheckBox("Queso Extra (+3500)");

        setTitle("cafeteriaSOFT");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1020, 760);
        setLocationRelativeTo(null);

        construirInterfaz();
        refrescarCatalogoYCombos();
    }

    private void construirInterfaz() {
        JPanel barraSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        barraSuperior.setBorder(BorderFactory.createEtchedBorder());
        barraSuperior.add(lblPedidoActivo);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Catálogo y Productos", crearPanelCatalogoYProductos());
        tabs.addTab("Personalizar y Combos", crearPanelPersonalizarYCombos());
        tabs.addTab("Pedidos, Promos y Pago", crearPanelPedidosPromosYPago());
        tabs.addTab("Historial, Cuadre y Estaciones", crearPanelHistorialYTurno());

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBorder(BorderFactory.createTitledBorder("Registro de actividad"));
        panelInferior.add(new JScrollPane(areaSalida), BorderLayout.CENTER);

        setLayout(new BorderLayout(8, 8));
        add(barraSuperior, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }

    private JPanel crearPanelCatalogoYProductos() {
        JPanel contenedor = new JPanel(new BorderLayout(10, 10));
        contenedor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelFormularios = new JPanel();
        panelFormularios.setLayout(new BoxLayout(panelFormularios, BoxLayout.Y_AXIS));

        JPanel formNuevo = new JPanel(new GridLayout(2, 1, 6, 6));
        formNuevo.setBorder(BorderFactory.createTitledBorder("Crear nuevo producto"));

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JTextField txtCodigo = new JTextField(8);
        JTextField txtNombre = new JTextField(18);
        JTextField txtPrecio = new JTextField(8);
        fila1.add(new JLabel("Código:"));
        fila1.add(txtCodigo);
        fila1.add(new JLabel("Nombre:"));
        fila1.add(txtNombre);
        fila1.add(new JLabel("Precio:"));
        fila1.add(txtPrecio);

        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JComboBox<TipoItem> cbTipo = new JComboBox<>(TipoItem.values());
        JCheckBox chkDisponible = new JCheckBox("Disponible", true);
        JButton btnGuardarProducto = new JButton("Agregar Producto");
        JButton btnActualizarTabla = new JButton("Refrescar Catálogo");

        btnGuardarProducto.addActionListener(e -> {
            try {
                double precio = Double.parseDouble(txtPrecio.getText().trim());
                ProductoBase creado = nucleo.crearProducto(
                        txtCodigo.getText(),
                        txtNombre.getText(),
                        precio,
                        (TipoItem) cbTipo.getSelectedItem(),
                        chkDisponible.isSelected()
                );
                refrescarCatalogoYCombos();
                txtCodigo.setText("");
                txtNombre.setText("");
                txtPrecio.setText("");
                log("Producto creado: [" + creado.getCodigo() + "] " + creado.getNombre()
                        + " - $" + String.format("%.0f", creado.calcularPrecio()));
            } catch (Exception ex) {
                mostrarError(ex.getMessage());
            }
        });

        btnActualizarTabla.addActionListener(e -> refrescarCatalogoYCombos());

        fila2.add(new JLabel("Tipo:"));
        fila2.add(cbTipo);
        fila2.add(chkDisponible);
        fila2.add(btnGuardarProducto);
        fila2.add(btnActualizarTabla);

        formNuevo.add(fila1);
        formNuevo.add(fila2);

        JPanel formActualizar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        formActualizar.setBorder(BorderFactory.createTitledBorder(
                "Actualizar producto o disponibilidad (clic en tabla para cargar)"
        ));

        JTextField txtEditCodigo = new JTextField("BEB04", 7);
        JTextField txtEditNombre = new JTextField(14);
        JTextField txtEditPrecio = new JTextField(7);
        JCheckBox chkEditDisponible = new JCheckBox("Disponible", true);
        JButton btnActualizarProducto = new JButton("Actualizar Producto");

        formActualizar.add(new JLabel("Código:"));
        formActualizar.add(txtEditCodigo);
        formActualizar.add(new JLabel("Nuevo nombre:"));
        formActualizar.add(txtEditNombre);
        formActualizar.add(new JLabel("Nuevo precio:"));
        formActualizar.add(txtEditPrecio);
        formActualizar.add(chkEditDisponible);
        formActualizar.add(btnActualizarProducto);

        btnActualizarProducto.addActionListener(e -> {
            try {
                String cod = txtEditCodigo.getText().trim();
                String nuevoNombre = txtEditNombre.getText().trim();
                Double nuevoPrecio = null;
                if (!txtEditPrecio.getText().trim().isEmpty()) {
                    nuevoPrecio = Double.parseDouble(txtEditPrecio.getText().trim());
                }

                ItemMenu actualizado = nucleo.actualizarProducto(
                        cod,
                        nuevoNombre.isEmpty() ? null : nuevoNombre,
                        nuevoPrecio,
                        chkEditDisponible.isSelected()
                );
                refrescarCatalogoYCombos();
                log("Producto actualizado: [" + actualizado.getCodigo() + "] "
                        + actualizado.getDescripcionDetallada()
                        + " - $" + String.format("%.0f", actualizado.calcularPrecio())
                        + " | Estado: " + (actualizado.isDisponible() ? "ACTIVO" : "AGOTADO"));
            } catch (Exception ex) {
                mostrarError(ex.getMessage());
            }
        });

        panelFormularios.add(formNuevo);
        panelFormularios.add(formActualizar);

        JTable tabla = new JTable(modeloCatalogo);
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tabla.getSelectedRow();
                if (fila >= 0) {
                    String codigo = String.valueOf(modeloCatalogo.getValueAt(fila, 0));
                    nucleo.getCatalogoRepositorio().buscarPorCodigo(codigo).ifPresent(item -> {
                        txtEditCodigo.setText(item.getCodigo());
                        txtEditNombre.setText(item.getNombre());
                        txtEditPrecio.setText(String.format(java.util.Locale.US, "%.0f", item.getPrecioBase()));
                        chkEditDisponible.setSelected(item.isDisponible());
                    });
                }
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Catálogo actual"));

        contenedor.add(panelFormularios, BorderLayout.NORTH);
        contenedor.add(scrollTabla, BorderLayout.CENTER);
        return contenedor;
    }

    private JPanel crearPanelPersonalizarYCombos() {
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelPersonalizar = new JPanel(new GridLayout(2, 1, 6, 6));
        panelPersonalizar.setBorder(BorderFactory.createTitledBorder("Personalizar producto con adiciones"));

        JPanel filaBase = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton btnAgregarPersonalizado = new JButton("Agregar al Pedido Actual");
        filaBase.add(new JLabel("Producto:"));
        filaBase.add(cbProductoPersonalizar);
        filaBase.add(btnAgregarPersonalizado);

        filaAdiciones.add(cbAlmendra);
        filaAdiciones.add(cbShot);
        filaAdiciones.add(cbCaramelo);
        filaAdiciones.add(cbQueso);

        btnAgregarPersonalizado.addActionListener(e -> {
            try {
                OpcionCatalogo seleccionada = (OpcionCatalogo) cbProductoPersonalizar.getSelectedItem();
                if (seleccionada == null) {
                    throw new IllegalArgumentException("Selecciona un producto del desplegable.");
                }
                asegurarPedidoActual();

                List<String> codigosFijos = new ArrayList<>();
                if (cbAlmendra.isSelected()) codigosFijos.add("ADD_ALMENDRA");
                if (cbShot.isSelected()) codigosFijos.add("ADD_SHOT");
                if (cbCaramelo.isSelected()) codigosFijos.add("ADD_CARAMELO");
                if (cbQueso.isSelected()) codigosFijos.add("ADD_QUESO");

                ItemMenu item = nucleo.personalizarProducto(seleccionada.codigo(), codigosFijos);

                List<ItemMenu> extrasCreados = new ArrayList<>();
                for (Map.Entry<JCheckBox, ItemMenu> entry : checkboxesAdicionesDinamicas.entrySet()) {
                    if (entry.getKey().isSelected()) {
                        extrasCreados.add(entry.getValue());
                    }
                }
                if (!extrasCreados.isEmpty()) {
                    item = nucleo.getPersonalizacionServicio().aplicarAdicionesConItems(item, extrasCreados);
                }

                nucleo.agregarItem(pedidoActual.getId(), item);
                actualizarEtiquetaPedido();
                log("Agregado a " + pedidoActual.getId() + ": " + item.getDescripcionDetallada()
                        + " ($" + String.format("%.0f", item.calcularPrecio()) + ")");
            } catch (Exception ex) {
                mostrarError(ex.getMessage());
            }
        });

        panelPersonalizar.add(filaBase);
        panelPersonalizar.add(new JScrollPane(filaAdiciones));

        JPanel panelCombo = new JPanel(new GridLayout(3, 1, 6, 6));
        panelCombo.setBorder(BorderFactory.createTitledBorder("Armar combo paso a paso"));

        JPanel filaCombo1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JTextField txtNombreCombo = new JTextField("Combo Especial", 14);
        filaCombo1.add(new JLabel("Nombre combo:"));
        filaCombo1.add(txtNombreCombo);
        filaCombo1.add(new JLabel("Bebida:"));
        filaCombo1.add(cbComboBebida);
        filaCombo1.add(new JLabel("Plato:"));
        filaCombo1.add(cbComboPlato);

        JPanel filaCombo2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filaCombo2.add(new JLabel("Acompañamiento:"));
        filaCombo2.add(cbComboAcomp);
        filaCombo2.add(new JLabel("Postre opcional:"));
        filaCombo2.add(cbComboPostre);

        JPanel filaCombo3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JTextField txtPrecioCombo = new JTextField("22000", 8);
        JButton btnCrearCombo = new JButton("Crear Combo y Agregar");

        btnCrearCombo.addActionListener(e -> {
            try {
                OpcionCatalogo beb = (OpcionCatalogo) cbComboBebida.getSelectedItem();
                OpcionCatalogo pla = (OpcionCatalogo) cbComboPlato.getSelectedItem();
                OpcionCatalogo aco = (OpcionCatalogo) cbComboAcomp.getSelectedItem();
                OpcionCatalogo pos = (OpcionCatalogo) cbComboPostre.getSelectedItem();

                if (beb == null || pla == null || aco == null) {
                    throw new IllegalArgumentException("Selecciona bebida, plato y acompañamiento.");
                }

                asegurarPedidoActual();
                ComboItem combo = nucleo.crearYRegistrarCombo(
                        "CMB-" + (System.currentTimeMillis() % 1000),
                        txtNombreCombo.getText().trim(),
                        beb.codigo(),
                        pla.codigo(),
                        aco.codigo(),
                        (pos != null && !pos.codigo().isEmpty()) ? pos.codigo() : "",
                        Double.parseDouble(txtPrecioCombo.getText().trim())
                );
                nucleo.agregarItem(pedidoActual.getId(), combo);
                refrescarCatalogoYCombos();
                actualizarEtiquetaPedido();
                log("Combo agregado: " + combo.getDescripcionDetallada()
                        + " ($" + String.format("%.0f", combo.calcularPrecio()) + ")");
            } catch (Exception ex) {
                mostrarError(ex.getMessage());
            }
        });

        filaCombo3.add(new JLabel("Precio especial:"));
        filaCombo3.add(txtPrecioCombo);
        filaCombo3.add(btnCrearCombo);

        panelCombo.add(filaCombo1);
        panelCombo.add(filaCombo2);
        panelCombo.add(filaCombo3);

        contenedor.add(panelPersonalizar);
        contenedor.add(panelCombo);
        return contenedor;
    }

    private JPanel crearPanelPedidosPromosYPago() {
        JPanel contenedor = new JPanel(new GridLayout(3, 1, 8, 8));
        contenedor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filaCrear = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        filaCrear.setBorder(BorderFactory.createTitledBorder("1. Crear pedido"));
        JTextField txtCliente = new JTextField("Cliente Mostrador", 18);
        JButton btnNuevoPedido = new JButton("Crear Pedido");
        btnNuevoPedido.addActionListener(e -> {
            pedidoActual = nucleo.iniciarPedido(txtCliente.getText().trim());
            actualizarEtiquetaPedido();
            log("Nuevo pedido creado: " + pedidoActual.getId() + " para " + pedidoActual.getCliente());
        });
        filaCrear.add(new JLabel("Cliente / Mesa:"));
        filaCrear.add(txtCliente);
        filaCrear.add(btnNuevoPedido);

        JPanel filaEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        filaEstado.setBorder(BorderFactory.createTitledBorder("2. Control de estado"));
        JComboBox<EstadoPedido> cbEstado = new JComboBox<>(EstadoPedido.values());
        JButton btnCambiarEstado = new JButton("Actualizar Estado");
        btnCambiarEstado.addActionListener(e -> {
            try {
                asegurarPedidoActual();
                EstadoPedido estado = (EstadoPedido) cbEstado.getSelectedItem();
                nucleo.cambiarEstado(pedidoActual.getId(), estado);
                actualizarEtiquetaPedido();
                log("Pedido " + pedidoActual.getId() + " -> " + estado);
            } catch (Exception ex) {
                mostrarError(ex.getMessage());
            }
        });
        filaEstado.add(new JLabel("Estado:"));
        filaEstado.add(cbEstado);
        filaEstado.add(btnCambiarEstado);

        JPanel filaPromoPago = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        filaPromoPago.setBorder(BorderFactory.createTitledBorder("3. Promociones y pago"));
        JComboBox<String> cbPromo = new JComboBox<>(new String[]{
                "Sin Promoción", "Happy Hour (20% OFF)", "Fin de Semana (15% OFF)"
        });
        JButton btnAplicarPromo = new JButton("Aplicar Promoción");
        btnAplicarPromo.addActionListener(e -> {
            int idx = cbPromo.getSelectedIndex();
            if (idx == 1) nucleo.configurarPromocion(new HappyHourStrategy(0.20, 15, 18, true));
            else if (idx == 2) nucleo.configurarPromocion(new PromoFinDeSemanaStrategy(0.15, true));
            else nucleo.configurarPromocion(new SinPromocionStrategy());
            actualizarEtiquetaPedido();
            log("Promoción activa: " + cbPromo.getSelectedItem());
        });

        JComboBox<MedioPago> cbMedioPago = new JComboBox<>(MedioPago.values());
        JButton btnPagar = new JButton("Registrar Pago");
        btnPagar.addActionListener(e -> {
            try {
                asegurarPedidoActual();
                MedioPago medio = (MedioPago) cbMedioPago.getSelectedItem();
                Pedido pagado = nucleo.pagarPedido(pedidoActual.getId(), medio);
                actualizarEtiquetaPedido();
                log("Pedido " + pagado.getId() + " pagado con " + medio
                        + " | Subtotal: $" + String.format("%.0f", pagado.getSubtotal())
                        + " | Desc: $" + String.format("%.0f", pagado.getDescuentoAplicado())
                        + " | Total: $" + String.format("%.0f", pagado.getTotalFinal()));
            } catch (Exception ex) {
                mostrarError(ex.getMessage());
            }
        });

        filaPromoPago.add(cbPromo);
        filaPromoPago.add(btnAplicarPromo);
        filaPromoPago.add(new JLabel("Medio:"));
        filaPromoPago.add(cbMedioPago);
        filaPromoPago.add(btnPagar);

        contenedor.add(filaCrear);
        contenedor.add(filaEstado);
        contenedor.add(filaPromoPago);
        return contenedor;
    }

    private JPanel crearPanelHistorialYTurno() {
        JPanel contenedor = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        contenedor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnHistorial = new JButton("Ver Historial de Pedidos");
        JButton btnCuadre = new JButton("Ver Cuadre de Turno");
        JButton btnNotif = new JButton("Ver Notificaciones a Estaciones");

        btnHistorial.addActionListener(e -> {
            log("=== HISTORIAL DE PEDIDOS ===");
            for (Pedido p : nucleo.historialPedidos()) {
                log(p.getId() + " | " + p.getCliente() + " | " + p.getEstado()
                        + " | Total: $" + String.format("%.0f", p.getTotalFinal())
                        + " | Pagado: " + (p.isPagado() ? "SÍ (" + p.getMedioPago() + ")" : "NO"));
            }
        });

        btnCuadre.addActionListener(e -> {
            ResumenTurno r = nucleo.cuadreTurno();
            log("=== CUADRE DE TURNO === Pedidos pagados: " + r.getPedidosPagados().size()
                    + " | Total recaudado: $" + String.format("%.0f", r.getTotalRecaudado()));
        });

        btnNotif.addActionListener(e -> {
            log("=== NOTIFICACIONES A ESTACIONES ===");
            for (String n : nucleo.getNotificacionServicio().getLogGlobalNotificaciones()) {
                log(n);
            }
        });

        contenedor.add(btnHistorial);
        contenedor.add(btnCuadre);
        contenedor.add(btnNotif);
        return contenedor;
    }

    private void refrescarCatalogoYCombos() {
        modeloCatalogo.setRowCount(0);
        cbProductoPersonalizar.removeAllItems();
        cbComboBebida.removeAllItems();
        cbComboPlato.removeAllItems();
        cbComboAcomp.removeAllItems();
        cbComboPostre.removeAllItems();

        for (JCheckBox cbDinamico : checkboxesAdicionesDinamicas.keySet()) {
            filaAdiciones.remove(cbDinamico);
        }
        checkboxesAdicionesDinamicas.clear();

        cbComboPostre.addItem(new OpcionCatalogo("", "-- Sin postre --"));

        for (ItemMenu item : nucleo.listarCatalogo()) {
            String estadoTexto = item.isDisponible() ? "ACTIVO" : "AGOTADO";
            modeloCatalogo.addRow(new Object[]{
                    item.getCodigo(),
                    item.getTipo().name(),
                    item.getDescripcionDetallada(),
                    "$" + String.format("%.0f", item.calcularPrecio()),
                    estadoTexto
            });

            String etiqueta = item.getCodigo() + " - " + item.getNombre()
                    + " ($" + String.format("%.0f", item.calcularPrecio()) + ")"
                    + (item.isDisponible() ? "" : " [AGOTADO]");
            OpcionCatalogo opcion = new OpcionCatalogo(item.getCodigo(), etiqueta);
            cbProductoPersonalizar.addItem(opcion);

            if (item.getTipo() == TipoItem.BEBIDA) cbComboBebida.addItem(opcion);
            else if (item.getTipo() == TipoItem.PLATO) cbComboPlato.addItem(opcion);
            else if (item.getTipo() == TipoItem.ACOMPANAMIENTO) cbComboAcomp.addItem(opcion);
            else if (item.getTipo() == TipoItem.POSTRE) cbComboPostre.addItem(opcion);
            else if (item.getTipo() == TipoItem.ADICION) {
                String labelAdicion = item.getNombre() + " (+" + String.format("%.0f", item.calcularPrecio()) + ")"
                        + (item.isDisponible() ? "" : " [AGOTADO]");
                JCheckBox chk = new JCheckBox(labelAdicion);
                checkboxesAdicionesDinamicas.put(chk, item);
                filaAdiciones.add(chk);
            }
        }

        filaAdiciones.revalidate();
        filaAdiciones.repaint();
    }

    private void asegurarPedidoActual() {
        if (pedidoActual == null) {
            pedidoActual = nucleo.iniciarPedido("Cliente General");
            actualizarEtiquetaPedido();
            log("Creado automáticamente pedido " + pedidoActual.getId());
        }
    }

    private void actualizarEtiquetaPedido() {
        if (pedidoActual == null) {
            lblPedidoActivo.setText("Pedido activo: Ninguno");
        } else {
            lblPedidoActivo.setText("Pedido activo: " + pedidoActual.getId()
                    + " (" + pedidoActual.getCliente() + ")"
                    + " | Estado: " + pedidoActual.getEstado()
                    + " | Total: $" + String.format("%.0f", pedidoActual.getTotalFinal())
                    + (pedidoActual.isPagado() ? " [PAGADO]" : ""));
        }
    }

    private void log(String mensaje) {
        areaSalida.append(mensaje + "\n");
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}