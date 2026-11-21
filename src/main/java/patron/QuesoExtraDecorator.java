package patron;

import modelo.ItemMenu;

public class QuesoExtraDecorator extends AdicionDecorator {
    public QuesoExtraDecorator(ItemMenu itemDecorado) {
        super(itemDecorado, "Queso Extra", 3500.0);
    }
}