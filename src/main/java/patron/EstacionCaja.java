package patron;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import modelo.EstadoPedido;
import modelo.Pedido;

@Getter
public class EstacionCaja implements ObservadorPedido {
    private final List<String> notificacionesRecibidas = new ArrayList<>();

    @Override
    public String getNombreEstacion() {
        return "Caja";
    }

    @Override
    public void actualizar(Pedido pedido, EstadoPedido estadoAnterior, EstadoPedido nuevoEstado) {
        String msg = "[CAJA] Pedido #" + pedido.getId() + " en estado " + nuevoEstado
                + " | Total a cobrar: $" + String.format("%.0f", pedido.getTotalFinal())
                + " | Pagado: " + (pedido.isPagado() ? "SÍ" : "NO");
        notificacionesRecibidas.add(msg);
    }
}