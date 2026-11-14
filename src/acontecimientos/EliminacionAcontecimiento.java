package acontecimientos;

import combatientes.Combatiente;

public class EliminacionAcontecimiento implements Acontecimiento {
    private final Combatiente combatiente;

    public EliminacionAcontecimiento(Combatiente combatiente) {
        this.combatiente = combatiente;
    }

    public Combatiente combatiente() {
        return combatiente;
    }

    @Override
    public String descripcion() {
        return "¡" + combatiente.nombre() + " ha quedado fuera de combate!";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EliminacionAcontecimiento)) return false;
        EliminacionAcontecimiento that = (EliminacionAcontecimiento) o;
        return combatiente != null ? combatiente.equals(that.combatiente) : that.combatiente == null;
    }

    @Override
    public int hashCode() {
        return combatiente != null ? combatiente.hashCode() : 0;
    }

    @Override
    public String toString() {
        return descripcion();
    }
}
