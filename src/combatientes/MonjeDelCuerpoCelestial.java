package combatientes;

public final class MonjeDelCuerpoCelestial extends Combatiente {
    private static final double FACTOR_MITIGACION = 0.70;

    public MonjeDelCuerpoCelestial(int id, String nombre, Escuela escuela) {
        super(id, nombre, escuela);
    }

    @Override
    public String tipo() {
        return "Monje del Cuerpo Celestial";
    }

    @Override
    public void recibirDanio(int danioEntrante) {
        super.recibirDanio((int) Math.round(danioEntrante * FACTOR_MITIGACION));
    }
}
