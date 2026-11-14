package equipamiento.items;

import equipamiento.CategoriaEquipamiento;
import equipamiento.Equipamiento;

public final class ArmaduraDorada implements Equipamiento {
    @Override
    public String nombre() {
        return "Armadura Dorada";
    }

    @Override
    public CategoriaEquipamiento categoria() {
        return CategoriaEquipamiento.ARMADURA;
    }

    @Override
    public int modificadorDefensa() {
        return 8;
    }
}
