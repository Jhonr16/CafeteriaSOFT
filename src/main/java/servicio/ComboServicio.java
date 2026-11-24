package servicio;

import modelo.ComboItem;
import modelo.ItemMenu;

public class ComboServicio {
    private final PersonalizacionServicio personalizacionServicio;

    public ComboServicio(PersonalizacionServicio personalizacionServicio) {
        this.personalizacionServicio = personalizacionServicio;
    }

    public ComboItem crearComboPasoAPaso(String codigoCombo, String nombreCombo,
                                         ItemMenu bebida, ItemMenu plato,
                                         ItemMenu acompanamiento, ItemMenu postreOpcional,
                                         double precioEspecialCombo) {
        personalizacionServicio.validarDisponibilidad(bebida);
        personalizacionServicio.validarDisponibilidad(plato);
        personalizacionServicio.validarDisponibilidad(acompanamiento);
        if (postreOpcional != null) {
            personalizacionServicio.validarDisponibilidad(postreOpcional);
        }
        return new ComboItem(
                codigoCombo,
                nombreCombo,
                bebida,
                plato,
                acompanamiento,
                postreOpcional,
                precioEspecialCombo
        );
    }
}
