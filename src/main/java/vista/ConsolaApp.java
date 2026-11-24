package vista;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import modelo.ComboItem;
import modelo.EstadoPedido;
import modelo.ItemMenu;
import modelo.MedioPago;
import modelo.Pedido;
import modelo.ResumenTurno;
import modelo.TipoItem;
import patron.HappyHourStrategy;
import patron.PromoFinDeSemanaStrategy;
import patron.SinPromocionStrategy;
import servicio.CafeteriaNucleo;
import servicio.DisponibilidadException;

public class ConsolaApp {
    private final CafeteriaNucleo nucleo;
    private final Scanner scanner;

    public ConsolaApp(CafeteriaNucleo nucleo) {
        this.nucleo = nucleo;
        this.scanner = new Scanner(System.in);
    }

    public void ejecutar() {
        boolean salir = false;
        while (!salir) {
            imprimirMenu();
            String opcion = scanner.nextLine().trim();
            try {
                switch (opcion) {
                    case "1" -> mostrarCatalogo();
                    case "2" -> personalizarProducto();
                    case "3" -> armarComboPasoAPaso();
                    case "4" -> crearPedido();
                    case "5" -> agregarItemPedido();
                    case "6" -> cambiarEstadoPedido();
                    case "7" -> configurarPromocion();
                    case "8" -> registrarPago();
                    case "9" -> mostrarHistorial();
                    case "10" -> mostrarCuadreTurno();
                    case "11" -> mostrarNotificaciones();
                    case "0" -> salir = true;
                    default -> System.out.println("Opción no válida.");
                }
            } catch (DisponibilidadException | IllegalArgumentException e) {
                System.out.println("Error de validación: " + e.getMessage());
            }
        }
        System.out.println("¡Hasta pronto en cafeteriaSOFT!");
    }

    private void imprimirMenu() {
        System.out.println("\n=== cafeteriaSOFT ===");
        System.out.println("1. Ver catálogo de productos y disponibilidad ");
        System.out.println("2. Personalizar producto con adiciones");
        System.out.println("3. Armar combo paso a paso");
        System.out.println("4. Crear nuevo pedido");
        System.out.println("5. Agregar ítem a pedido existente ");
        System.out.println("6. Cambiar estado de pedido [CREADO / EN_PREPARACION / LISTO] ");
        System.out.println("7. Configurar promoción dinámica [Happy Hour / Fin de semana / Ninguna] ");
        System.out.println("8. Simular pago de pedido [EFECTIVO / TARJETA / TRANSFERENCIA]");
        System.out.println("9. Ver historial de pedidos en memoria");
        System.out.println("10. Ver cuadre de ventas y turno");
        System.out.println("11. Ver notificaciones a estaciones [Cocina, Caja, Mesero]");
        System.out.println("0. Salir");
        System.out.print("Selecciona una opción: ");
    }

    private void mostrarCatalogo() {
        System.out.println("\n--- CATALOGO ---");
        for (ItemMenu item : nucleo.listarCatalogo()) {
            System.out.printf("[%s] (%s) %s - $%.0f - %s%n",
                    item.getCodigo(),
                    item.getTipo(),
                    item.getDescripcionDetallada(),
                    item.calcularPrecio(),
                    item.isDisponible() ? "ACTIVO" : "AGOTADO");
        }
    }

    private void personalizarProducto() {
        System.out.print("Código del producto base (ej. BEB01, PLA01): ");
        String codBase = scanner.nextLine().trim();
        List<String> adiciones = new ArrayList<>();
        System.out.println("Adiciones disponibles: ADD_ALMENDRA ($2500), ADD_SHOT ($3000), ADD_CARAMELO ($2000), ADD_QUESO ($3500)");
        while (true) {
            System.out.print("Ingresa código de adición o ENTER para terminar: ");
            String add = scanner.nextLine().trim();
            if (add.isEmpty()) break;
            adiciones.add(add);
        }
        ItemMenu personalizado = nucleo.personalizarProducto(codBase, adiciones);
        System.out.println("Resultado: " + personalizado.getDescripcionDetallada()
                + " | Total: $" + String.format("%.0f", personalizado.calcularPrecio()));
    }

    private void armarComboPasoAPaso() {
        System.out.println("\n--- ARMAR COMBO PASO A PASO ---");
        System.out.print("Código nuevo combo (ej. CMB01): ");
        String codCombo = scanner.nextLine().trim();
        System.out.print("Nombre del combo: ");
        String nombreCombo = scanner.nextLine().trim();

        System.out.println("Bebidas: " + formatoLista(TipoItem.BEBIDA));
        System.out.print("Código bebida: ");
        String codBeb = scanner.nextLine().trim();

        System.out.println("Platos: " + formatoLista(TipoItem.PLATO));
        System.out.print("Código plato: ");
        String codPla = scanner.nextLine().trim();

        System.out.println("Acompañamientos: " + formatoLista(TipoItem.ACOMPANAMIENTO));
        System.out.print("Código acompañamiento: ");
        String codAco = scanner.nextLine().trim();

        System.out.println("Postres opcionales: " + formatoLista(TipoItem.POSTRE));
        System.out.print("Código postre opcional (o ENTER para omitir): ");
        String codPos = scanner.nextLine().trim();

        System.out.print("Precio especial del combo: ");
        double precio = Double.parseDouble(scanner.nextLine().trim());

        ComboItem combo = nucleo.crearYRegistrarCombo(codCombo, nombreCombo, codBeb, codPla, codAco, codPos, precio);
        System.out.println("Combo creado: " + combo.getDescripcionDetallada() + " -> $" + String.format("%.0f", combo.calcularPrecio()));
    }

    private String formatoLista(TipoItem tipo) {
        return nucleo.listarPorTipo(tipo).stream()
                .map(i -> i.getCodigo() + ":" + i.getNombre() + (i.isDisponible() ? "" : "[NO DISP]"))
                .toList().toString();
    }

    private void crearPedido() {
        System.out.print("Nombre del cliente o mesa: ");
        String cliente = scanner.nextLine().trim();
        Pedido p = nucleo.iniciarPedido(cliente);
        System.out.println("Pedido creado con ID: " + p.getId() + " | Estado: " + p.getEstado());
    }

    private void agregarItemPedido() {
        System.out.print("ID del pedido (ej. PED-101): ");
        String idPedido = scanner.nextLine().trim();
        System.out.print("Código del ítem del catálogo: ");
        String codItem = scanner.nextLine().trim();
        ItemMenu item = nucleo.personalizarProducto(codItem, List.of());
        nucleo.agregarItem(idPedido, item);
        System.out.println("Ítem agregado correctamente.");
    }

    private void cambiarEstadoPedido() {
        System.out.print("ID del pedido: ");
        String idPedido = scanner.nextLine().trim();
        System.out.print("Nuevo estado (CREADO, EN_PREPARACION, LISTO): ");
        EstadoPedido estado = EstadoPedido.valueOf(scanner.nextLine().trim().toUpperCase());
        nucleo.cambiarEstado(idPedido, estado);
        System.out.println("Estado actualizado y estaciones notificadas.");
    }

    private void configurarPromocion() {
        System.out.println("1. Sin promoción | 2. Happy Hour (simulado 20% OFF) | 3. Fin de semana (simulado 15% OFF)");
        String op = scanner.nextLine().trim();
        switch (op) {
            case "1" -> nucleo.configurarPromocion(new SinPromocionStrategy());
            case "2" -> nucleo.configurarPromocion(new HappyHourStrategy(0.20, 15, 18, true));
            case "3" -> nucleo.configurarPromocion(new PromoFinDeSemanaStrategy(0.15, true));
            default -> System.out.println("Selección inválida.");
        }
        System.out.println("Promoción aplicada a los pedidos.");
    }

    private void registrarPago() {
        System.out.print("ID del pedido a pagar: ");
        String idPedido = scanner.nextLine().trim();
        System.out.print("Medio de pago (EFECTIVO, TARJETA, TRANSFERENCIA): ");
        MedioPago medio = MedioPago.valueOf(scanner.nextLine().trim().toUpperCase());
        Pedido pagado = nucleo.pagarPedido(idPedido, medio);
        System.out.printf("Pedido %s pagado con %s. Total cobrado: $%.0f%n",
                pagado.getId(), pagado.getMedioPago(), pagado.getTotalFinal());
    }

    private void mostrarHistorial() {
        System.out.println("\n--- HISTORIAL DE PEDIDOS ---");
        for (Pedido p : nucleo.historialPedidos()) {
            System.out.printf("ID: %s | Cliente: %s | Estado: %s | Subtotal: $%.0f | Desc: $%.0f | Total: $%.0f | Pagado: %s%n",
                    p.getId(), p.getCliente(), p.getEstado(), p.getSubtotal(),
                    p.getDescuentoAplicado(), p.getTotalFinal(), p.isPagado() ? "SÍ (" + p.getMedioPago() + ")" : "NO");
        }
    }

    private void mostrarCuadreTurno() {
        ResumenTurno resumen = nucleo.cuadreTurno();
        System.out.println("\n--- CUADRE DE VENTAS Y TURNO ---");
        System.out.println("Pedidos pagados: " + resumen.getPedidosPagados().size());
        System.out.printf("Total recaudado en turno: $%.0f%n", resumen.getTotalRecaudado());
        resumen.getTotalesPorMedioPago().forEach((medio, monto) ->
                System.out.printf("  - %s: $%.0f%n", medio, monto));
    }

    private void mostrarNotificaciones() {
        System.out.println("\n--- NOTIFICACIONES A ESTACIONES ---");
        for (String n : nucleo.getNotificacionServicio().getLogGlobalNotificaciones()) {
            System.out.println(n);
        }
    }
}