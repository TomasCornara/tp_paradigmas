package tecnicas;

import combatientes.Combatiente;

public final class Regeneracion extends EfectoTemporal {
    private final int curacionPorTurno;

    public Regeneracion(int turnos, int curacionPorTurno) {
        super(turnos);
        this.curacionPorTurno = curacionPorTurno;
    }

    @Override
    public String nombre() {
        return "Regeneración";
    }

    @Override
    protected void aplicarEfecto(Combatiente combatiente) {
        combatiente.curar(curacionPorTurno);
    }
}
