package servicio;

import java.util.List;
import modelo.ItemMenu;
import patron.AdicionDecorator;
import patron.ExtraShotEspressoDecorator;
import patron.LecheAlmendraDecorator;
import patron.QuesoExtraDecorator;
import patron.SalsaCarameloDecorator;

public class PersonalizacionServicio {

    public void validarDisponibilidad(ItemMenu item) {
        if (item == null || !item.isDisponible()) {
            String nombre = item != null ? item.getNombre() : "Desconocido";
            throw new DisponibilidadException("El ítem '" + nombre + "' no está disponible en el menú.");
        }
    }

    public ItemMenu aplicarAdiciones(ItemMenu base, List<String> codigosAdicion) {
        validarDisponibilidad(base);
        ItemMenu actual = base;
        for (String codigo : codigosAdicion) {
            actual = switch (codigo.toUpperCase()) {
                case "ADD_ALMENDRA" -> new LecheAlmendraDecorator(actual);
                case "ADD_SHOT" -> new ExtraShotEspressoDecorator(actual);
                case "ADD_CARAMELO" -> new SalsaCarameloDecorator(actual);
                case "ADD_QUESO" -> new QuesoExtraDecorator(actual);
                default -> throw new IllegalArgumentException("Código de adición no válido: " + codigo);
            };
        }
        return actual;
    }

    public ItemMenu aplicarAdicionesConItems(ItemMenu base, List<ItemMenu> adicionesSeleccionadas) {
        validarDisponibilidad(base);
        ItemMenu actual = base;
        for (ItemMenu adicion : adicionesSeleccionadas) {
            validarDisponibilidad(adicion);
            actual = new AdicionDecorator(actual, adicion.getNombre(), adicion.calcularPrecio()) { };
        }
        return actual;
    }
}