package acontecimientos;

import torneo.Pareja;

public class InicioCombateAcontecimiento implements Acontecimiento {
    private final int ronda;
    private final Pareja pareja1;
    private final Pareja pareja2;

    public InicioCombateAcontecimiento(int ronda, Pareja pareja1, Pareja pareja2) {
        this.ronda = ronda;
        this.pareja1 = pareja1;
        this.pareja2 = pareja2;
    }

    public int ronda() {
        return ronda;
    }

    public Pareja pareja1() {
        return pareja1;
    }

    public Pareja pareja2() {
        return pareja2;
    }

    @Override
    public String descripcion() {
        return "Ronda " + ronda + " - Inicio de combate entre [" + pareja1.nombreCompuesto() +
                "] y [" + pareja2.nombreCompuesto() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InicioCombateAcontecimiento)) return false;
        InicioCombateAcontecimiento that = (InicioCombateAcontecimiento) o;
        if (ronda != that.ronda) return false;
        if (pareja1 != null ? !pareja1.equals(that.pareja1) : that.pareja1 != null) return false;
        return pareja2 != null ? pareja2.equals(that.pareja2) : that.pareja2 == null;
    }

    @Override
    public int hashCode() {
        int result = ronda;
        result = 31 * result + (pareja1 != null ? pareja1.hashCode() : 0);
        result = 31 * result + (pareja2 != null ? pareja2.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return descripcion();
    }
}
