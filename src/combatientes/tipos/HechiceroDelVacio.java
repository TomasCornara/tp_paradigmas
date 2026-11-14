package combatientes.tipos;

import combatientes.Combatiente;
import combatientes.Escuela;
import tecnicas.Tecnica;

public final class HechiceroDelVacio extends Combatiente {
    private static final int REDUCCION_COSTO_ENERGIA = 8;

    public HechiceroDelVacio(int id, String nombre, Escuela escuela) {
        super(id, nombre, escuela);
    }

    @Override
    public TipoCombatiente tipo() {
        return TipoCombatiente.HECHICERO_DEL_VACIO;
    }

    @Override
    public int calcularCostoEnergia(Tecnica tecnica) {
        return Math.max(1, super.calcularCostoEnergia(tecnica) - REDUCCION_COSTO_ENERGIA);
    }
}
