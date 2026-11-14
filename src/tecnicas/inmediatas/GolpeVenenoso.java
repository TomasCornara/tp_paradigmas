package tecnicas.inmediatas;

import tecnicas.ContextoEjecucion;
import tecnicas.Tecnica;
import tecnicas.TipoTecnica;
import tecnicas.temporales.Veneno;

public final class GolpeVenenoso implements Tecnica {
    private static final int COSTO_ENERGIA = 20;
    private static final int DANIO_BASE = 15;
    private static final int TURNOS_VENENO = 3;
    private static final int DANIO_POR_TURNO = 6;

    @Override
    public String nombre() {
        return "Golpe Venenoso";
    }

    @Override
    public int costoEnergia() {
        return COSTO_ENERGIA;
    }

    @Override
    public TipoTecnica tipo() {
        return TipoTecnica.OFENSIVA;
    }

    @Override
    public void ejecutar(ContextoEjecucion contexto) {
        int danio = contexto.usuario().calcularDanioAtaque(DANIO_BASE);
        contexto.afectado().recibirDanio(danio);
        contexto.afectado().agregarEfectoTemporal(new Veneno(TURNOS_VENENO, DANIO_POR_TURNO));
    }
}
