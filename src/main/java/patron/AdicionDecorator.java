package patron;

import lombok.Getter;
import lombok.Setter;
import modelo.ItemMenu;

@Getter
@Setter
public abstract class AdicionDecorator extends ItemMenu {
    protected final ItemMenu itemDecorado;
    private final double precioAdicion;
    private final String nombreAdicion;

    protected AdicionDecorator(ItemMenu itemDecorado, String nombreAdicion, double precioAdicion) {
        super(
                itemDecorado.getCodigo(),
                itemDecorado.getNombre(),
                itemDecorado.getPrecioBase(),
                itemDecorado.getTipo(),
                itemDecorado.isDisponible()
        );
        this.itemDecorado = itemDecorado;
        this.nombreAdicion = nombreAdicion;
        this.precioAdicion = precioAdicion;
    }

    @Override
    public String getDescripcionDetallada() {
        return itemDecorado.getDescripcionDetallada() + " + " + nombreAdicion;
    }

    @Override
    public double calcularPrecio() {
        return itemDecorado.calcularPrecio() + precioAdicion;
    }
}
