package acontecimientos;

import torneo.Pareja;

public class ResultadoCombateAcontecimiento implements Acontecimiento {
    private final int ronda;
    private final Pareja ganadora;
    private final Pareja perdedora;
    private final int vidaTotalGanadora;
    private final int vidaTotalPerdedora;

    public ResultadoCombateAcontecimiento(
            int ronda,
            Pareja ganadora,
            Pareja perdedora,
            int vidaTotalGanadora,
            int vidaTotalPerdedora
    ) {
        this.ronda = ronda;
        this.ganadora = ganadora;
        this.perdedora = perdedora;
        this.vidaTotalGanadora = vidaTotalGanadora;
        this.vidaTotalPerdedora = vidaTotalPerdedora;
    }

    public int ronda() {
        return ronda;
    }

    public Pareja ganadora() {
        return ganadora;
    }

    public Pareja perdedora() {
        return perdedora;
    }

    public int vidaTotalGanadora() {
        return vidaTotalGanadora;
    }

    public int vidaTotalPerdedora() {
        return vidaTotalPerdedora;
    }

    @Override
    public String descripcion() {
        return "Ronda " + ronda + " - Ganadora: [" + ganadora.nombreCompuesto() + "] (Vida restante: " +
                vidaTotalGanadora + ") frente a [" + perdedora.nombreCompuesto() + "] (Vida restante: " +
                vidaTotalPerdedora + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResultadoCombateAcontecimiento)) return false;
        ResultadoCombateAcontecimiento that = (ResultadoCombateAcontecimiento) o;
        if (ronda != that.ronda) return false;
        if (vidaTotalGanadora != that.vidaTotalGanadora) return false;
        if (vidaTotalPerdedora != that.vidaTotalPerdedora) return false;
        if (ganadora != null ? !ganadora.equals(that.ganadora) : that.ganadora != null) return false;
        return perdedora != null ? perdedora.equals(that.perdedora) : that.perdedora == null;
    }

    @Override
    public int hashCode() {
        int result = ronda;
        result = 31 * result + (ganadora != null ? ganadora.hashCode() : 0);
        result = 31 * result + (perdedora != null ? perdedora.hashCode() : 0);
        result = 31 * result + vidaTotalGanadora;
        result = 31 * result + vidaTotalPerdedora;
        return result;
    }

    @Override
    public String toString() {
        return descripcion();
    }
}
