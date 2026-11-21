package modelo;

public class ProductoBase extends ItemMenu {

    public ProductoBase(String codigo, String nombre, double precioBase, TipoItem tipo, boolean disponible) {
        super(codigo, nombre, precioBase, tipo, disponible);
    }

    @Override
    public String getDescripcionDetallada() {
        return getNombre() + (isDisponible() ? "" : " [NO DISPONIBLE]");
    }

    @Override
    public double calcularPrecio() {
        return getPrecioBase();
    }
}