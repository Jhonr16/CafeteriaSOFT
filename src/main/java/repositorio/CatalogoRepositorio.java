package repositorio;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import modelo.ItemMenu;
import modelo.ProductoBase;
import modelo.TipoItem;

@Getter
public class CatalogoRepositorio {
    private final List<ItemMenu> items;

    public CatalogoRepositorio() {
        this.items = new ArrayList<>();
        cargarCatalogoInicial();
    }

    private void cargarCatalogoInicial() {
        items.add(new ProductoBase("BEB01", "Cafe Americano", 5500.0, TipoItem.BEBIDA, true));
        items.add(new ProductoBase("BEB02", "Capuchino", 7500.0, TipoItem.BEBIDA, true));
        items.add(new ProductoBase("BEB03", "Jugo de Naranja", 6500.0, TipoItem.BEBIDA, true));
        items.add(new ProductoBase("BEB04", "Matcha Latte Frío", 9500.0, TipoItem.BEBIDA, false));

        items.add(new ProductoBase("PLA01", "Croissant de Jamon y Queso", 11000.0, TipoItem.PLATO, true));
        items.add(new ProductoBase("PLA02", "Sandwich de Pollo", 15500.0, TipoItem.PLATO, true));
        items.add(new ProductoBase("PLA03", "Bowl de Yogur y Granola", 12000.0, TipoItem.PLATO, true));

        items.add(new ProductoBase("ACO01", "Papas Rústicas", 6000.0, TipoItem.ACOMPANAMIENTO, true));
        items.add(new ProductoBase("ACO02", "Ensalada Fresca", 5500.0, TipoItem.ACOMPANAMIENTO, true));

        items.add(new ProductoBase("POS01", "Brownie con Helado", 8500.0, TipoItem.POSTRE, true));
        items.add(new ProductoBase("POS02", "Torta de Zanahoria", 8000.0, TipoItem.POSTRE, true));
    }

    public void guardar(ItemMenu item) {
        items.add(item);
    }

    public Optional<ItemMenu> buscarPorCodigo(String codigo) {
        return items.stream()
                .filter(i -> i.getCodigo().equalsIgnoreCase(codigo))
                .findFirst();
    }

    public List<ItemMenu> listarPorTipo(TipoItem tipo) {
        return items.stream()
                .filter(i -> i.getTipo() == tipo)
                .toList();
    }

    public List<ItemMenu> listarDisponibles() {
        return items.stream()
                .filter(ItemMenu::isDisponible)
                .toList();
    }
}