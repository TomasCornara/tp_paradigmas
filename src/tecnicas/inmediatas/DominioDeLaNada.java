package tecnicas.inmediatas;

import tecnicas.ContextoEjecucion;
import tecnicas.Tecnica;
import tecnicas.TipoTecnica;

public final class DominioDeLaNada implements Tecnica {
    private static final int COSTO_ENERGIA = 25;
    private static final int DANIO_BASE = 18;
    private static final int ENERGIA_DRENADA = 15;

    @Override
    public String nombre() {
        return "Dominio de la Nada";
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
        contexto.afectado().reducirEnergia(ENERGIA_DRENADA);
    }
}
