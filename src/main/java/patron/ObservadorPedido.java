package patron;

import modelo.EstadoPedido;
import modelo.Pedido;

public interface ObservadorPedido {
    String getNombreEstacion();

    void actualizar(Pedido pedido, EstadoPedido estadoAnterior, EstadoPedido nuevoEstado);
}