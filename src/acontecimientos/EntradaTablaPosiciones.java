package acontecimientos;

import torneo.Pareja;

public class EntradaTablaPosiciones implements Comparable<EntradaTablaPosiciones> {
    private final Pareja pareja;
    private final int ultimaRonda;
    private final int vidaTotalFinal;

    public EntradaTablaPosiciones(Pareja pareja, int ultimaRonda, int vidaTotalFinal) {
        this.pareja = pareja;
        this.ultimaRonda = ultimaRonda;
        this.vidaTotalFinal = vidaTotalFinal;
    }

    public Pareja pareja() {
        return pareja;
    }

    public int ultimaRonda() {
        return ultimaRonda;
    }

    public int vidaTotalFinal() {
        return vidaTotalFinal;
    }

    @Override
    public int compareTo(EntradaTablaPosiciones otra) {
        int cmpRonda = Integer.compare(otra.ultimaRonda, this.ultimaRonda);
        if (cmpRonda != 0) {
            return cmpRonda;
        }
        int cmpVida = Integer.compare(otra.vidaTotalFinal, this.vidaTotalFinal);
        if (cmpVida != 0) {
            return cmpVida;
        }
        return Integer.compare(this.pareja.id(), otra.pareja.id());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntradaTablaPosiciones)) return false;
        EntradaTablaPosiciones that = (EntradaTablaPosiciones) o;
        if (ultimaRonda != that.ultimaRonda) return false;
        if (vidaTotalFinal != that.vidaTotalFinal) return false;
        return pareja != null ? pareja.equals(that.pareja) : that.pareja == null;
    }

    @Override
    public int hashCode() {
        int result = pareja != null ? pareja.hashCode() : 0;
        result = 31 * result + ultimaRonda;
        result = 31 * result + vidaTotalFinal;
        return result;
    }

    @Override
    public String toString() {
        return "EntradaTablaPosiciones[pareja=" + pareja + ", ultimaRonda=" + ultimaRonda + ", vidaTotalFinal=" + vidaTotalFinal + "]";
    }
}
