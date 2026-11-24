package repositorio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import modelo.Pedido;

public class PedidoRepositorio {
    private final List<Pedido> pedidosEnMemoria = new ArrayList<>();

    public void guardar(Pedido pedido) {
        pedidosEnMemoria.add(pedido);
    }

    public Optional<Pedido> buscarPorId(String id) {
        return pedidosEnMemoria.stream()
                .filter(p -> p.getId().equalsIgnoreCase(id))
                .findFirst();
    }

    public List<Pedido> listarTodos() {
        return Collections.unmodifiableList(pedidosEnMemoria);
    }

    public List<Pedido> listarPagados() {
        return pedidosEnMemoria.stream()
                .filter(Pedido::isPagado)
                .toList();
    }
}