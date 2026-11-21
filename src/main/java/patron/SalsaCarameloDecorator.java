package patron;

import modelo.ItemMenu;

public class SalsaCarameloDecorator extends AdicionDecorator {
    public SalsaCarameloDecorator(ItemMenu itemDecorado) {
        super(itemDecorado, "Salsa Caramelo", 2000.0);
    }
}