package patron;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HappyHourStrategy implements EstrategiaPromocion {
    private double porcentajeDescuento;
    private int horaInicio;
    private int horaFin;
    private boolean forzarSimulacion;

    public HappyHourStrategy() {
        this(0.20, 15, 18, false);
    }

    public HappyHourStrategy(double porcentajeDescuento, int horaInicio, int horaFin, boolean forzarSimulacion) {
        this.porcentajeDescuento = porcentajeDescuento;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.forzarSimulacion = forzarSimulacion;
    }

    @Override
    public String getNombrePromocion() {
        return "Happy Hour (" + (int) (porcentajeDescuento * 100) + "% OFF)";
    }

    @Override
    public boolean esAplicable(LocalDateTime momento) {
        if (forzarSimulacion) {
            return true;
        }
        int hora = momento.getHour();
        return hora >= horaInicio && hora < horaFin;
    }

    @Override
    public double calcularDescuento(double subtotal, LocalDateTime momento) {
        if (!esAplicable(momento)) {
            return 0.0;
        }
        return subtotal * porcentajeDescuento;
    }
}