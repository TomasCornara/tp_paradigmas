package tecnicas;

import combatientes.Combatiente;

public final class Veneno extends EfectoTemporal {
    private final int danioPorTurno;

    public Veneno(int turnos, int danioPorTurno) {
        super(turnos);
        this.danioPorTurno = danioPorTurno;
    }

    @Override
    public String nombre() {
        return "Veneno";
    }

    @Override
    protected void aplicarEfecto(Combatiente combatiente) {
        combatiente.recibirDanioDirecto(danioPorTurno);
    }
}
