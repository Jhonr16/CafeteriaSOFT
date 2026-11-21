package patron;

import modelo.ItemMenu;

public class LecheAlmendraDecorator extends AdicionDecorator {
    public LecheAlmendraDecorator(ItemMenu itemDecorado) {
        super(itemDecorado, "Leche de Almendra", 2500.0);
    }
}