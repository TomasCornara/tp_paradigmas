package torneo;

import combatientes.Combatiente;
import tecnicas.Tecnica;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pareja {
    private final int id;
    private final Combatiente primero;
    private final Combatiente segundo;

    private final Map<Tecnica, Combatiente> tecnicaUsadaPor = new HashMap<>();

    public Pareja(int id, Combatiente primero, Combatiente segundo) {
        if (id <= 0) {
            throw new IllegalArgumentException("El identificador de la pareja debe ser positivo");
        }
        if (primero == null || segundo == null) {
            throw new IllegalArgumentException("Una pareja necesita dos combatientes no nulos");
        }
        if (primero.equals(segundo)) {
            throw new IllegalArgumentException("Una pareja necesita dos combatientes distintos");
        }
        this.id = id;
        this.primero = primero;
        this.segundo = segundo;
    }

    public int id() {
        return id;
    }

    public List<Combatiente> combatientes() {
        List<Combatiente> lista = new ArrayList<>();
        lista.add(primero);
        lista.add(segundo);
        return Collections.unmodifiableList(lista);
    }

    public boolean sigueEnPie() {
        return primero.estaEnPie() || segundo.estaEnPie();
    }

    public int vidaTotal() {
        int v1 = primero.estaEnPie() ? primero.vidaActual() : 0;
        int v2 = segundo.estaEnPie() ? segundo.vidaActual() : 0;
        return v1 + v2;
    }

    public void registrarUsoTecnica(Combatiente usuario, Tecnica tecnica) {
        if (usuario != null && tecnica != null) {
            tecnicaUsadaPor.putIfAbsent(tecnica, usuario);
        }
    }

    public boolean puedeUsarPorReglaCompaniero(Combatiente solicitante, Tecnica tecnica) {
        Combatiente usuarioOriginal = tecnicaUsadaPor.get(tecnica);
        if (usuarioOriginal == null || usuarioOriginal.equals(solicitante)) {
            return true;
        }

        boolean primeroSigueEnPie = usuarioOriginal.estaEnPie();
        boolean primeroConservaUsos = usuarioOriginal.usosRestantes(tecnica) > 0;
        return !(primeroSigueEnPie && primeroConservaUsos);
    }

    public void reiniciarRestriccionesCombate() {
        tecnicaUsadaPor.clear();
    }

    public void restablecerParaNuevoCombate() {
        reiniciarRestriccionesCombate();
        primero.restablecerCombate();
        segundo.restablecerCombate();
    }

    public String nombreCompuesto() {
        return primero.nombre() + " y " + segundo.nombre();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pareja)) return false;
        Pareja pareja = (Pareja) o;
        return id == pareja.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return "[" + nombreCompuesto() + " (Pareja #" + id + ")]";
    }
}
