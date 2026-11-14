package equipamiento.items;

import equipamiento.CategoriaEquipamiento;
import equipamiento.Equipamiento;

public final class SelloDelFenix implements Equipamiento {
    @Override
    public String nombre() {
        return "Sello del Fénix";
    }

    @Override
    public CategoriaEquipamiento categoria() {
        return CategoriaEquipamiento.SELLO;
    }

    @Override
    public int modificadorVidaMaxima() {
        return 40;
    }
}
