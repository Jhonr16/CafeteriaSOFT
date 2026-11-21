package patron;

import java.time.LocalDateTime;

public class SinPromocionStrategy implements EstrategiaPromocion {
    @Override
    public String getNombrePromocion() {
        return "Sin Promoción";
    }

    @Override
    public boolean esAplicable(LocalDateTime momento) {
        return true;
    }

    @Override
    public double calcularDescuento(double subtotal, LocalDateTime momento) {
        return 0.0;
    }
}