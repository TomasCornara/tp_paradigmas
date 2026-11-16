package equipamiento;

public final class EspadaCelestial implements Equipamiento {
    @Override
    public String nombre() {
        return "Espada Celestial";
    }

    @Override
    public CategoriaEquipamiento categoria() {
        return CategoriaEquipamiento.ARMA;
    }

    @Override
    public int modificadorDanio() {
        return 10;
    }
}
