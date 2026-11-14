package tecnicas.inmediatas;

import tecnicas.ContextoEjecucion;
import tecnicas.Tecnica;
import tecnicas.TipoTecnica;

public final class MuroDelCuerpoDorado implements Tecnica {
    private static final int COSTO_ENERGIA = 20;
    private static final int REDUCCION_DANIO = 10;

    @Override
    public String nombre() {
        return "Muro del Cuerpo Dorado";
    }

    @Override
    public int costoEnergia() {
        return COSTO_ENERGIA;
    }

    @Override
    public TipoTecnica tipo() {
        return TipoTecnica.DEFENSIVA;
    }

    @Override
    public void ejecutar(ContextoEjecucion contexto) {
        contexto.afectado().activarTecnica(this, REDUCCION_DANIO);
    }
}
