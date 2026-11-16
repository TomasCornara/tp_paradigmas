package tecnicas;

public final class MeditacionDelInmortal extends TecnicaBase {
    private static final int COSTO_ENERGIA = 20;
    private static final int CURACION = 25;

    @Override
    public String nombre() {
        return "Meditación del Inmortal";
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
        contexto.afectado().curar(CURACION);
    }
}
