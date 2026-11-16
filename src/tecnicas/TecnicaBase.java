package tecnicas;

import java.util.Objects;

public abstract class TecnicaBase implements Tecnica {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tecnica)) return false;
        Tecnica other = (Tecnica) o;
        return Objects.equals(nombre(), other.nombre());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nombre());
    }

    @Override
    public String toString() {
        return nombre() + " (Costo: " + costoEnergia() + ", Tipo: " + tipo() + ")";
    }
}
