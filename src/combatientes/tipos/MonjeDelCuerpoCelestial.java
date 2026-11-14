package combatientes.tipos;

import combatientes.Combatiente;
import combatientes.Escuela;

public final class MonjeDelCuerpoCelestial extends Combatiente {
    private static final double FACTOR_MITIGACION = 0.70;

    public MonjeDelCuerpoCelestial(int id, String nombre, Escuela escuela) {
        super(id, nombre, escuela);
    }

    @Override
    public TipoCombatiente tipo() {
        return TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL;
    }

    @Override
    public void recibirDanio(int danioEntrante) {
        super.recibirDanio((int) Math.round(danioEntrante * FACTOR_MITIGACION));
    }
}
