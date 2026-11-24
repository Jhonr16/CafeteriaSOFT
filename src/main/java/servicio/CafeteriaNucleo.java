package servicio;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import modelo.ComboItem;
import modelo.EstadoPedido;
import modelo.ItemMenu;
import modelo.MedioPago;
import modelo.Pedido;
import modelo.ResumenTurno;
import modelo.TipoItem;
import patron.EstacionCaja;
import patron.EstacionCocina;
import patron.EstacionMesero;
import patron.EstrategiaPromocion;
import repositorio.CatalogoRepositorio;
import repositorio.PedidoRepositorio;

@Getter
public class CafeteriaNucleo {
    private final CatalogoRepositorio catalogoRepositorio;
    private final PedidoRepositorio pedidoRepositorio;
    private final PersonalizacionServicio personalizacionServicio;
    private final ComboServicio comboServicio;
    private final NotificacionServicio notificacionServicio;
    private final PedidoServicio pedidoServicio;
    private final EstacionCocina estacionCocina;
    private final EstacionCaja estacionCaja;
    private final EstacionMesero estacionMesero;

    public CafeteriaNucleo() {
        this.catalogoRepositorio = new CatalogoRepositorio();
        this.pedidoRepositorio = new PedidoRepositorio();
        this.personalizacionServicio = new PersonalizacionServicio();
        this.comboServicio = new ComboServicio(personalizacionServicio);
        this.notificacionServicio = new NotificacionServicio();
        this.pedidoServicio = new PedidoServicio(pedidoRepositorio, personalizacionServicio, notificacionServicio);

        this.estacionCocina = new EstacionCocina();
        this.estacionCaja = new EstacionCaja();
        this.estacionMesero = new EstacionMesero();

        this.notificacionServicio.suscribir(estacionCocina);
        this.notificacionServicio.suscribir(estacionCaja);
        this.notificacionServicio.suscribir(estacionMesero);
    }

    public List<ItemMenu> listarCatalogo() {
        return catalogoRepositorio.getItems();
    }

    public List<ItemMenu> listarPorTipo(TipoItem tipo) {
        return catalogoRepositorio.listarPorTipo(tipo);
    }

    public ItemMenu personalizarProducto(String codigoBase, List<String> codigosAdicion) {
        ItemMenu base = catalogoRepositorio.buscarPorCodigo(codigoBase)
                .orElseThrow(() -> new IllegalArgumentException("Producto no existe: " + codigoBase));
        return personalizacionServicio.aplicarAdiciones(base, codigosAdicion);
    }

    public ComboItem crearYRegistrarCombo(String codigoCombo, String nombreCombo,
                                          String codBebida, String codPlato,
                                          String codAcompanamiento, String codPostreOpcional,
                                          double precioEspecial) {
        ItemMenu bebida = buscarItem(codBebida);
        ItemMenu plato = buscarItem(codPlato);
        ItemMenu acomp = buscarItem(codAcompanamiento);
        ItemMenu postre = (codPostreOpcional != null && !codPostreOpcional.isBlank())
                ? buscarItem(codPostreOpcional)
                : null;

        ComboItem combo = comboServicio.crearComboPasoAPaso(
                codigoCombo, nombreCombo, bebida, plato, acomp, postre, precioEspecial
        );
        catalogoRepositorio.guardar(combo);
        return combo;
    }

    public Pedido iniciarPedido(String cliente) {
        return pedidoServicio.crearPedido(cliente);
    }

    public void agregarItem(String idPedido, ItemMenu item) {
        pedidoServicio.agregarItemAPedido(idPedido, item);
    }

    public void cambiarEstado(String idPedido, EstadoPedido nuevoEstado) {
        pedidoServicio.cambiarEstadoPedido(idPedido, nuevoEstado);
    }

    public void configurarPromocion(EstrategiaPromocion estrategia) {
        pedidoServicio.setEstrategiaPromocion(estrategia);
        for (Pedido p : pedidoRepositorio.listarTodos()) {
            pedidoServicio.aplicarPromocion(p, LocalDateTime.now());
        }
    }

    public Pedido pagarPedido(String idPedido, MedioPago medioPago) {
        return pedidoServicio.registrarPago(idPedido, medioPago);
    }

    public List<Pedido> historialPedidos() {
        return pedidoServicio.obtenerHistorialPedidos();
    }

    public ResumenTurno cuadreTurno() {
        return pedidoServicio.generarCuadreTurno();
    }

    private ItemMenu buscarItem(String codigo) {
        return catalogoRepositorio.buscarPorCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Código no encontrado en catálogo: " + codigo));
    }
}