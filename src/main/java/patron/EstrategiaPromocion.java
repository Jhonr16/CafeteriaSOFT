package patron;

import java.time.LocalDateTime;

public interface EstrategiaPromocion {
    String getNombrePromocion();

    boolean esAplicable(LocalDateTime momento);

    double calcularDescuento(double subtotal, LocalDateTime momento);
}