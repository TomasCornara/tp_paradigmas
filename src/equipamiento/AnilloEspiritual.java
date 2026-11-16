package equipamiento;

public final class AnilloEspiritual implements Equipamiento {
    @Override
    public String nombre() {
        return "Anillo Espiritual";
    }

    @Override
    public CategoriaEquipamiento categoria() {
        return CategoriaEquipamiento.ANILLO;
    }

    @Override
    public int modificadorEnergiaMaxima() {
        return 30;
    }
}
