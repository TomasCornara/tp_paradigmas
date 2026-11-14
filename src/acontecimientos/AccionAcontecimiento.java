package acontecimientos;

import combatientes.Combatiente;

public class AccionAcontecimiento implements Acontecimiento {
    private final Combatiente ejecutor;
    private final String accion;
    private final Combatiente objetivo;
    private final String detalle;

    public AccionAcontecimiento(Combatiente ejecutor, String accion, Combatiente objetivo, String detalle) {
        this.ejecutor = ejecutor;
        this.accion = accion;
        this.objetivo = objetivo;
        this.detalle = detalle;
    }

    public Combatiente ejecutor() {
        return ejecutor;
    }

    public String accion() {
        return accion;
    }

    public Combatiente objetivo() {
        return objetivo;
    }

    public String detalle() {
        return detalle;
    }

    @Override
    public String descripcion() {
        String detalleLimpio = detalle == null || detalle.isBlank() ? "" : " (" + detalle + ")";
        if (objetivo == null || objetivo.equals(ejecutor)) {
            return ejecutor.nombre() + " ejecuta " + accion + detalleLimpio;
        }
        return ejecutor.nombre() + " ejecuta " + accion + " sobre " + objetivo.nombre() + detalleLimpio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccionAcontecimiento)) return false;
        AccionAcontecimiento that = (AccionAcontecimiento) o;
        if (ejecutor != null ? !ejecutor.equals(that.ejecutor) : that.ejecutor != null) return false;
        if (accion != null ? !accion.equals(that.accion) : that.accion != null) return false;
        if (objetivo != null ? !objetivo.equals(that.objetivo) : that.objetivo != null) return false;
        return detalle != null ? detalle.equals(that.detalle) : that.detalle == null;
    }

    @Override
    public int hashCode() {
        int result = ejecutor != null ? ejecutor.hashCode() : 0;
        result = 31 * result + (accion != null ? accion.hashCode() : 0);
        result = 31 * result + (objetivo != null ? objetivo.hashCode() : 0);
        result = 31 * result + (detalle != null ? detalle.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return descripcion();
    }
}
