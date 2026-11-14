package tecnicas.inmediatas;

import tecnicas.ContextoEjecucion;
import tecnicas.Tecnica;
import tecnicas.TipoTecnica;
import tecnicas.temporales.Regeneracion;

public final class RegeneracionVital implements Tecnica {
    private static final int COSTO_ENERGIA = 20;
    private static final int TURNOS_REGENERACION = 3;
    private static final int CURACION_POR_TURNO = 8;

    @Override
    public String nombre() {
        return "Regeneración Vital";
    }

    @Override
    public int costoEnergia() {
        return COSTO_ENERGIA;
    }

    @Override
    public TipoTecnica tipo() {
        return TipoTecnica.RECUPERACION;
    }

    @Override
    public void ejecutar(ContextoEjecucion contexto) {
        contexto.afectado().agregarEfectoTemporal(
                new Regeneracion(TURNOS_REGENERACION, CURACION_POR_TURNO));
    }
}
