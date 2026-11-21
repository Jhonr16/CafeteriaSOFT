package patron;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import modelo.EstadoPedido;
import modelo.Pedido;

@Getter
public class EstacionMesero implements ObservadorPedido {
    private final List<String> notificacionesRecibidas = new ArrayList<>();

    @Override
    public String getNombreEstacion() {
        return "Mesero";
    }

    @Override
    public void actualizar(Pedido pedido, EstadoPedido estadoAnterior, EstadoPedido nuevoEstado) {
        String msg = "[MESERO] Pedido #" + pedido.getId() + " para '" + pedido.getCliente()
                + "' ahora está en estado " + nuevoEstado + ".";
        notificacionesRecibidas.add(msg);
    }
}