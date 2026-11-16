package equipamiento;

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
