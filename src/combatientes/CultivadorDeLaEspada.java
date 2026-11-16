package combatientes;

public final class CultivadorDeLaEspada extends Combatiente {
    private static final double FACTOR_POTENCIACION = 1.30;

    public CultivadorDeLaEspada(int id, String nombre, Escuela escuela) {
        super(id, nombre, escuela);
    }

    @Override
    public String tipo() {
        return "Cultivador de la Espada";
    }

    @Override
    public int calcularDanioAtaque(int danioBase) {
        return (int) Math.round(danioBase * FACTOR_POTENCIACION) + bonoDanioEquipamiento();
    }
}
