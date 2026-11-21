package modelo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ItemMenu {
    private String codigo;
    private String nombre;
    private double precioBase;
    private TipoItem tipo;
    private boolean disponible;

    protected ItemMenu(String codigo, String nombre, double precioBase, TipoItem tipo, boolean disponible) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precioBase = precioBase;
        this.tipo = tipo;
        this.disponible = disponible;
    }

    public abstract String getDescripcionDetallada();

    public abstract double calcularPrecio();
}