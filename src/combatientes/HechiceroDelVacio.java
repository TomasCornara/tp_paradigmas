package combatientes;

import tecnicas.Tecnica;

public final class HechiceroDelVacio extends Combatiente {
    private static final int REDUCCION_COSTO_ENERGIA = 8;

    public HechiceroDelVacio(int id, String nombre, Escuela escuela) {
        super(id, nombre, escuela);
    }

    @Override
    public String tipo() {
        return "Hechicero del Vacío";
    }

    @Override
    public int calcularCostoEnergia(Tecnica tecnica) {
        return Math.max(1, super.calcularCostoEnergia(tecnica) - REDUCCION_COSTO_ENERGIA);
    }
}
