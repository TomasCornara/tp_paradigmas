package tecnicas.inmediatas;

import tecnicas.ContextoEjecucion;
import tecnicas.Tecnica;
import tecnicas.TipoTecnica;

public final class RugidoDeLaBestiaPrimordial implements Tecnica {
    private static final int COSTO_ENERGIA = 30;
    private static final int DANIO_BASE = 35;

    @Override
    public String nombre() {
        return "Rugido de la Bestia Primordial";
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
    }
}
