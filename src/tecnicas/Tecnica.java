package tecnicas;

public interface Tecnica {
    String nombre();
    int costoEnergia();
    TipoTecnica tipo();
    void ejecutar(ContextoEjecucion contexto);
}
