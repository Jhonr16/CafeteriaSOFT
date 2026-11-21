package patron;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromoFinDeSemanaStrategy implements EstrategiaPromocion {
    private double porcentajeDescuento;
    private boolean forzarSimulacion;

    public PromoFinDeSemanaStrategy() {
        this(0.15, false);
    }

    public PromoFinDeSemanaStrategy(double porcentajeDescuento, boolean forzarSimulacion) {
        this.porcentajeDescuento = porcentajeDescuento;
        this.forzarSimulacion = forzarSimulacion;
    }

    @Override
    public String getNombrePromocion() {
        return "Promo Fin de Semana (" + (int) (porcentajeDescuento * 100) + "% OFF)";
    }

    @Override
    public boolean esAplicable(LocalDateTime momento) {
        if (forzarSimulacion) {
            return true;
        }
        DayOfWeek dia = momento.getDayOfWeek();
        return dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY;
    }

    @Override
    public double calcularDescuento(double subtotal, LocalDateTime momento) {
        if (!esAplicable(momento)) {
            return 0.0;
        }
        return subtotal * porcentajeDescuento;
    }
}