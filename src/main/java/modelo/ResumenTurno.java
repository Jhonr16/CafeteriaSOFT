package modelo;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResumenTurno {
    private List<Pedido> pedidosPagados;
    private double totalRecaudado;
    private Map<MedioPago, Double> totalesPorMedioPago;

    public ResumenTurno(List<Pedido> pedidosPagados) {
        this.pedidosPagados = pedidosPagados;
        this.totalesPorMedioPago = new EnumMap<>(MedioPago.class);
        for (MedioPago medio : MedioPago.values()) {
            this.totalesPorMedioPago.put(medio, 0.0);
        }
        double acumulado = 0.0;
        for (Pedido p : pedidosPagados) {
            acumulado += p.getTotalFinal();
            MedioPago medio = p.getMedioPago() != null ? p.getMedioPago() : MedioPago.EFECTIVO;
            this.totalesPorMedioPago.put(medio, this.totalesPorMedioPago.get(medio) + p.getTotalFinal());
        }
        this.totalRecaudado = acumulado;
    }
}