package combatientes.tipos;

import combatientes.Combatiente;
import combatientes.Escuela;

public final class CultivadorDeLaEspada extends Combatiente {
    private static final double FACTOR_POTENCIACION = 1.30;

    public CultivadorDeLaEspada(int id, String nombre, Escuela escuela) {
        super(id, nombre, escuela);
    }

    @Override
    public TipoCombatiente tipo() {
        return TipoCombatiente.CULTIVADOR_DE_LA_ESPADA;
    }

    @Override
    public int calcularDanioAtaque(int danioBase) {
        return (int) Math.round(danioBase * FACTOR_POTENCIACION) + bonoDanioEquipamiento();
    }
}
