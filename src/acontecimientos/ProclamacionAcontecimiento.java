package acontecimientos;

import torneo.Pareja;

public class ProclamacionAcontecimiento implements Acontecimiento {
    private final Pareja campeona;

    public ProclamacionAcontecimiento(Pareja campeona) {
        this.campeona = campeona;
    }

    public Pareja campeona() {
        return campeona;
    }

    @Override
    public String descripcion() {
        return "¡Proclamación de los Dos Santos Supremos: " + campeona.nombreCompuesto() + " (Pareja #" + campeona.id() + ")!";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProclamacionAcontecimiento)) return false;
        ProclamacionAcontecimiento that = (ProclamacionAcontecimiento) o;
        return campeona != null ? campeona.equals(that.campeona) : that.campeona == null;
    }

    @Override
    public int hashCode() {
        return campeona != null ? campeona.hashCode() : 0;
    }

    @Override
    public String toString() {
        return descripcion();
    }
}
