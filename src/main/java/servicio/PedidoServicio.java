package servicio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.Setter;
import modelo.EstadoPedido;
import modelo.ItemMenu;
import modelo.MedioPago;
import modelo.Pedido;
import modelo.ResumenTurno;
import patron.EstrategiaPromocion;
import patron.SinPromocionStrategy;
import repositorio.PedidoRepositorio;

@Getter
@Setter
public class PedidoServicio {
    private final PedidoRepositorio pedidoRepositorio;
    private final PersonalizacionServicio personalizacionServicio;
    private final NotificacionServicio notificacionServicio;
    private EstrategiaPromocion estrategiaPromocion;
    private final AtomicInteger secuenciaId = new AtomicInteger(100);

    public PedidoServicio(PedidoRepositorio pedidoRepositorio,
                          PersonalizacionServicio personalizacionServicio,
                          NotificacionServicio notificacionServicio) {
        this.pedidoRepositorio = pedidoRepositorio;
        this.personalizacionServicio = personalizacionServicio;
        this.notificacionServicio = notificacionServicio;
        this.estrategiaPromocion = new SinPromocionStrategy();
    }

    public Pedido crearPedido(String cliente) {
        String id = "PED-" + secuenciaId.incrementAndGet();
        Pedido pedido = new Pedido(id, cliente);
        aplicarPromocion(pedido, LocalDateTime.now());
        pedidoRepositorio.guardar(pedido);
        notificacionServicio.notificarCambioEstado(pedido, null, EstadoPedido.CREADO);
        return pedido;
    }

    public void agregarItemAPedido(String idPedido, ItemMenu item) {
        personalizacionServicio.validarDisponibilidad(item);
        Pedido pedido = buscarPedidoObligatorio(idPedido);
        pedido.agregarItem(item);
        aplicarPromocion(pedido, LocalDateTime.now());
    }

    public void cambiarEstadoPedido(String idPedido, EstadoPedido nuevoEstado) {
        Pedido pedido = buscarPedidoObligatorio(idPedido);
        EstadoPedido anterior = pedido.getEstado();
        pedido.setEstado(nuevoEstado);
        notificacionServicio.notificarCambioEstado(pedido, anterior, nuevoEstado);
    }

    public void aplicarPromocion(Pedido pedido, LocalDateTime momento) {
        pedido.recalcularSubtotal();
        double descuento = estrategiaPromocion != null
                ? estrategiaPromocion.calcularDescuento(pedido.getSubtotal(), momento)
                : 0.0;
        pedido.setDescuentoAplicado(descuento);
        pedido.setTotalFinal(Math.max(0.0, pedido.getSubtotal() - descuento));
    }

    public Pedido registrarPago(String idPedido, MedioPago medioPago) {
        Pedido pedido = buscarPedidoObligatorio(idPedido);
        aplicarPromocion(pedido, LocalDateTime.now());
        pedido.setMedioPago(medioPago);
        pedido.setPagado(true);
        pedido.setFechaPago(LocalDateTime.now());
        return pedido;
    }

    public List<Pedido> obtenerHistorialPedidos() {
        return pedidoRepositorio.listarTodos();
    }

    public ResumenTurno generarCuadreTurno() {
        return new ResumenTurno(pedidoRepositorio.listarPagados());
    }

    private Pedido buscarPedidoObligatorio(String idPedido) {
        return pedidoRepositorio.buscarPorId(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + idPedido));
    }
}