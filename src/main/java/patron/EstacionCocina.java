package patron;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import modelo.EstadoPedido;
import modelo.Pedido;

@Getter
public class EstacionCocina implements ObservadorPedido {
    private final List<String> notificacionesRecibidas = new ArrayList<>();

    @Override
    public String getNombreEstacion() {
        return "Cocina";
    }

    @Override
    public void actualizar(Pedido pedido, EstadoPedido estadoAnterior, EstadoPedido nuevoEstado) {
        String msg = "[COCINA] Pedido #" + pedido.getId() + " (" + pedido.getCliente() + ") cambió de "
                + estadoAnterior + " -> " + nuevoEstado + " | Ítems: " + pedido.getItems().size();
        notificacionesRecibidas.add(msg);
    }
}