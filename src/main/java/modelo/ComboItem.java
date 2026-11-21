package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComboItem extends ItemMenu {
    private ItemMenu bebida;
    private ItemMenu plato;
    private ItemMenu acompanamiento;
    private ItemMenu postreOpcional;
    private double precioEspecialCombo;

    public ComboItem(String codigo, String nombreCombo, ItemMenu bebida, ItemMenu plato,
                     ItemMenu acompanamiento, ItemMenu postreOpcional, double precioEspecialCombo) {
        super(codigo, nombreCombo, precioEspecialCombo, TipoItem.PLATO,
                validarDisponibilidad(bebida, plato, acompanamiento, postreOpcional));
        this.bebida = bebida;
        this.plato = plato;
        this.acompanamiento = acompanamiento;
        this.postreOpcional = postreOpcional;
        this.precioEspecialCombo = precioEspecialCombo;
    }

    private static boolean validarDisponibilidad(ItemMenu bebida, ItemMenu plato,
                                                 ItemMenu acompanamiento, ItemMenu postreOpcional) {
        List<ItemMenu> partes = new ArrayList<>();
        if (bebida != null) partes.add(bebida);
        if (plato != null) partes.add(plato);
        if (acompanamiento != null) partes.add(acompanamiento);
        if (postreOpcional != null) partes.add(postreOpcional);
        return partes.stream().allMatch(ItemMenu::isDisponible);
    }

    @Override
    public String getDescripcionDetallada() {
        List<String> componentes = new ArrayList<>();
        if (bebida != null) componentes.add("Bebida: " + bebida.getDescripcionDetallada());
        if (plato != null) componentes.add("Plato: " + plato.getDescripcionDetallada());
        if (acompanamiento != null) componentes.add("Acompañamiento: " + acompanamiento.getDescripcionDetallada());
        if (postreOpcional != null) componentes.add("Postre: " + postreOpcional.getDescripcionDetallada());
        return getNombre() + " [" + componentes.stream().collect(Collectors.joining(" | ")) + "]";
    }

    @Override
    public double calcularPrecio() {
        return precioEspecialCombo;
    }
}