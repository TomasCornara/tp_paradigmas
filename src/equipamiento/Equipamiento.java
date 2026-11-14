package equipamiento;

public interface Equipamiento {
    int MAX_EQUIPAMIENTOS = 3;

    String nombre();
    CategoriaEquipamiento categoria();

    default int modificadorDanio() {
        return 0;
    }

    default int modificadorDefensa() {
        return 0;
    }

    default int modificadorVidaMaxima() {
        return 0;
    }

    default int modificadorEnergiaMaxima() {
        return 0;
    }
}
