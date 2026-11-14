package tecnicas.temporales;

import combatientes.Combatiente;

public abstract class EfectoTemporal {
    private int turnosRestantes;

    protected EfectoTemporal(int turnos) {
        this.turnosRestantes = turnos;
    }

    public abstract String nombre();

    public int turnosRestantes() {
        return turnosRestantes;
    }

    public final void aplicarAlInicioTurno(Combatiente combatiente) {
        if (turnosRestantes > 0 && combatiente.estaEnPie()) {
            aplicarEfecto(combatiente);
            turnosRestantes--;
        }
    }

    protected abstract void aplicarEfecto(Combatiente combatiente);

    public boolean haTerminado() {
        return turnosRestantes <= 0;
    }
}
