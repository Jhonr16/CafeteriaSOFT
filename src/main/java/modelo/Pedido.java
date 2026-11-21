package modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pedido {
    private String id;
    private String cliente;
    private List<ItemMenu> items;
    private EstadoPedido estado;
    private LocalDateTime fechaCreacion;
    private double subtotal;
    private double descuentoAplicado;
    private double totalFinal;
    private boolean pagado;
    private MedioPago medioPago;
    private LocalDateTime fechaPago;

    public Pedido(String id, String cliente) {
        this.id = id;
        this.cliente = cliente;
        this.items = new ArrayList<>();
        this.estado = EstadoPedido.CREADO;
        this.fechaCreacion = LocalDateTime.now();
        this.pagado = false;
    }

    public void agregarItem(ItemMenu item) {
        this.items.add(item);
        recalcularSubtotal();
    }

    public void recalcularSubtotal() {
        this.subtotal = items.stream()
                .mapToDouble(ItemMenu::calcularPrecio)
                .sum();
        if (!pagado) {
            this.totalFinal = Math.max(0.0, this.subtotal - this.descuentoAplicado);
        }
    }
}