package tecnicas;

import combatientes.Combatiente;

import java.util.List;

public class ContextoEjecucion {
    private final Combatiente usuario;
    private final Combatiente afectado;

    public ContextoEjecucion(Combatiente usuario, Combatiente afectado) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (afectado == null) {
            throw new IllegalArgumentException("El afectado no puede ser nulo");
        }
        this.usuario = usuario;
        this.afectado = afectado;
    }

    public Combatiente usuario() {
        return usuario;
    }

    public Combatiente afectado() {
        return afectado;
    }

    public static ContextoEjecucion contra(Combatiente usuario, List<Combatiente> rivales) {
        Combatiente objetivo = elegirObjetivo(rivales);
        if (objetivo == null) {
            throw new IllegalStateException("No hay rivales en pie para atacar");
        }
        return new ContextoEjecucion(usuario, objetivo);
    }

    public static ContextoEjecucion sobreSiMismo(Combatiente usuario) {
        return new ContextoEjecucion(usuario, usuario);
    }

    public static Combatiente elegirObjetivo(List<Combatiente> rivales) {
        Combatiente objetivo = null;
        for (Combatiente c : rivales) {
            if (c.estaEnPie()) {
                if (objetivo == null) {
                    objetivo = c;
                } else if (c.vidaActual() < objetivo.vidaActual()) {
                    objetivo = c;
                } else if (c.vidaActual() == objetivo.vidaActual() && c.id() < objetivo.id()) {
                    objetivo = c;
                }
            }
        }
        return objetivo;
    }
}
