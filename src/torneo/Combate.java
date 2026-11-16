package torneo;

import acontecimientos.*;
import combatientes.Combatiente;
import tecnicas.ContextoEjecucion;
import tecnicas.Tecnica;
import tecnicas.TipoTecnica;

import java.util.Comparator;
import java.util.List;

public final class Combate {
    public static final int ENERGIA_POR_ASALTO = 10;
    public static final int DANIO_ATAQUE_BASICO = 10;
    public static final int LIMITE_ASALTOS_DEFECTO = 30;

    private final Pareja pareja1;
    private final Pareja pareja2;
    private final int ronda;
    private final int limiteAsaltos;
    private final EmisorAcontecimientos emisor;

    public Combate(Pareja pareja1, Pareja pareja2, int ronda, EmisorAcontecimientos emisor) {
        this(pareja1, pareja2, ronda, LIMITE_ASALTOS_DEFECTO, emisor);
    }

    public Combate(Pareja pareja1, Pareja pareja2, int ronda, int limiteAsaltos, EmisorAcontecimientos emisor) {
        this.pareja1 = pareja1;
        this.pareja2 = pareja2;
        this.ronda = ronda;
        this.limiteAsaltos = limiteAsaltos;
        if (emisor == null) {
            this.emisor = new EmisorAcontecimientos();
        } else {
            this.emisor = emisor;
        }
    }

    public Pareja resolver() {
        pareja1.restablecerParaNuevoCombate();
        pareja2.restablecerParaNuevoCombate();

        emisor.notificar(new InicioCombateAcontecimiento(ronda, pareja1, pareja2));

        Pareja primera = pareja1.id() <= pareja2.id() ? pareja1 : pareja2;
        Pareja segunda = primera == pareja1 ? pareja2 : pareja1;

        int asaltoActual = 1;
        while (primera.sigueEnPie() && segunda.sigueEnPie() && asaltoActual <= limiteAsaltos) {
            recuperarEnergiaInicioAsalto(primera);
            recuperarEnergiaInicioAsalto(segunda);

            ejecutarTurnoPareja(primera, segunda);
            if (!segunda.sigueEnPie()) {
                break;
            }

            ejecutarTurnoPareja(segunda, primera);
            if (!primera.sigueEnPie()) {
                break;
            }

            asaltoActual++;
        }

        Pareja ganadora = determinarGanadora();
        Pareja perdedora = ganadora.equals(pareja1) ? pareja2 : pareja1;

        emisor.notificar(new ResultadoCombateAcontecimiento(
                ronda,
                ganadora,
                perdedora,
                ganadora.vidaTotal(),
                perdedora.vidaTotal()
        ));

        return ganadora;
    }

    private void recuperarEnergiaInicioAsalto(Pareja pareja) {
        for (Combatiente c : pareja.combatientes()) {
            if (c.estaEnPie()) {
                c.recuperarEnergia(ENERGIA_POR_ASALTO);
            }
        }
    }

    private void ejecutarTurnoPareja(Pareja parejaTurno, Pareja parejaRival) {
        List<Combatiente> integrantesEnPie = parejaTurno.combatientes().stream()
                .filter(Combatiente::estaEnPie)
                .sorted(Comparator.comparingInt(Combatiente::id))
                .toList();

        for (Combatiente combatiente : integrantesEnPie) {
            if (!combatiente.estaEnPie() || !parejaRival.sigueEnPie()) {
                break;
            }

            combatiente.procesarEfectosAlInicioTurno(emisor);
            if (!combatiente.estaEnPie() || !parejaRival.sigueEnPie()) {
                break;
            }

            realizarAccion(combatiente, parejaTurno, parejaRival);

            if (!parejaRival.sigueEnPie()) {
                break;
            }
        }
    }

    private void realizarAccion(Combatiente ejecutor, Pareja parejaTurno, Pareja parejaRival) {
        Tecnica tecnica = ejecutor.seleccionarTecnica(parejaTurno);

        if (tecnica != null) {
            int costo = ejecutor.calcularCostoEnergia(tecnica);
            ejecutor.consumirEnergia(costo);
            ejecutor.descontarUso(tecnica);
            parejaTurno.registrarUsoTecnica(ejecutor, tecnica);

            ContextoEjecucion contexto = tecnica.tipo() == TipoTecnica.OFENSIVA
                    ? ContextoEjecucion.contra(ejecutor, parejaRival.combatientes())
                    : ContextoEjecucion.sobreSiMismo(ejecutor);

            Combatiente afectado = contexto.afectado();
            int vidaAntes = afectado.vidaActual();
            tecnica.ejecutar(contexto);
            int deltaVida = Math.abs(afectado.vidaActual() - vidaAntes);

            String detalle = "Costo: " + costo + " ener, Usos restantes: " + ejecutor.usosRestantes(tecnica);
            int danioProvocado = tecnica.tipo() == TipoTecnica.OFENSIVA ? deltaVida : 0;
            int curacionProvocada = tecnica.tipo() == TipoTecnica.RECUPERACION ? deltaVida : 0;
            if (deltaVida > 0) {
                detalle += (tecnica.tipo() == TipoTecnica.OFENSIVA ? ", Daño: " : ", Curación: ") + deltaVida;
            }
            emisor.notificar(new AccionAcontecimiento(ejecutor, tecnica.nombre(), afectado, detalle,
                    danioProvocado, curacionProvocada, costo));

            if (!afectado.estaEnPie()) {
                emisor.notificar(new EliminacionAcontecimiento(afectado));
            }
        } else {
            Combatiente objetivo = ContextoEjecucion.elegirObjetivo(parejaRival.combatientes());
            if (objetivo != null) {
                int danio = ejecutor.calcularDanioAtaque(DANIO_ATAQUE_BASICO);
                int vidaAntes = objetivo.vidaActual();
                objetivo.recibirDanio(danio);
                int danioReal = vidaAntes - objetivo.vidaActual();

                emisor.notificar(new AccionAcontecimiento(
                        ejecutor,
                        "Ataque básico",
                        objetivo,
                        "Daño: " + danioReal,
                        danioReal,
                        0,
                        0
                ));

                if (!objetivo.estaEnPie()) {
                    emisor.notificar(new EliminacionAcontecimiento(objetivo));
                }
            }
        }
    }

    private Pareja determinarGanadora() {
        boolean p1EnPie = pareja1.sigueEnPie();
        boolean p2EnPie = pareja2.sigueEnPie();

        if (p1EnPie && !p2EnPie) {
            return pareja1;
        }
        if (!p1EnPie && p2EnPie) {
            return pareja2;
        }

        int vida1 = pareja1.vidaTotal();
        int vida2 = pareja2.vidaTotal();

        if (vida1 > vida2) {
            return pareja1;
        } else if (vida2 > vida1) {
            return pareja2;
        } else {
            return pareja1.id() < pareja2.id() ? pareja1 : pareja2;
        }
    }
}
