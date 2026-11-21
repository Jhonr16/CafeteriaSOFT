package patron;

import modelo.ItemMenu;

public class ExtraShotEspressoDecorator extends AdicionDecorator {
    public ExtraShotEspressoDecorator(ItemMenu itemDecorado) {
        super(itemDecorado, "Extra Shot Espresso", 3000.0);
    }
}