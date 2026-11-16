package torneo;

import combatientes.Combatiente;
import combatientes.CreadorCombatientes;
import combatientes.Escuela;
import combatientes.TipoCombatiente;
import tecnicas.ContextoEjecucion;
import tecnicas.CorteDeLosMilAmaneceres;
import tecnicas.GolpeVenenoso;
import tecnicas.MeditacionDelInmortal;
import tecnicas.MuroDelCuerpoDorado;
import tecnicas.RegeneracionVital;
import tecnicas.RugidoDeLaBestiaPrimordial;
import tecnicas.Tecnica;
import tecnicas.Veneno;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TecnicaYAccionTest {
    private CreadorCombatientes creador;
    private Escuela escuelaCompleta;
    private Escuela escuelaVacia;

    @BeforeEach
    void setUp() {
        creador = new CreadorCombatientes(1);
        escuelaVacia = new Escuela("Escuela Neutra", List.of(), List.of());
        escuelaCompleta = new Escuela(
                "Escuela Mixta",
                List.of(
                        new CorteDeLosMilAmaneceres(),
                        new RugidoDeLaBestiaPrimordial(),
                        new MuroDelCuerpoDorado(),
                        new MeditacionDelInmortal()
                ),
                List.of()
        );
    }

    @Test
    @DisplayName("Regla: no se ejecuta una técnica sin energía ni sin usos restantes")
    void noSeEjecutaTecnicaSinEnergiaOSinUsosRestantes() {
        Combatiente c1 = creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Test", escuelaCompleta);
        Combatiente c2 = creador.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "Compañero", escuelaCompleta);
        Pareja pareja = new Pareja(1, c1, c2);

        Tecnica corte = c1.escuela().tecnicas().get(0);

        assertTrue(c1.puedeEjecutarTecnica(corte, pareja));

        c1.consumirEnergia(c1.energiaActual());
        assertFalse(c1.puedeEjecutarTecnica(corte, pareja), "Sin energía suficiente no debe poder ejecutar técnica");

        c1.recuperarEnergia(100);
        for (int i = 0; i < 5; i++) {
            c1.descontarUso(corte);
        }
        assertEquals(0, c1.usosRestantes(corte));
        assertFalse(c1.puedeEjecutarTecnica(corte, pareja), "Sin usos restantes no debe poder ejecutar técnica");

        for (Tecnica t : c1.escuela().tecnicas()) {
            while (c1.usosRestantes(t) > 0) {
                c1.descontarUso(t);
            }
        }
        Tecnica elegida = c1.seleccionarTecnica(pareja);
        assertNull(elegida, "Sin técnicas ejecutables debe recurrir al ataque básico (null)");
    }

    @Test
    @DisplayName("Regla: la acción se elige por recuperación, luego por defensiva y luego por la ofensiva con más usos")
    void criterioSeleccionAccionOrdenEstricto() {
        Combatiente c1 = creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Test", escuelaCompleta);
        Combatiente c2 = creador.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "Compañero", escuelaCompleta);
        Pareja pareja = new Pareja(1, c1, c2);

        Tecnica elegida = c1.seleccionarTecnica(pareja);
        assertNotNull(elegida);
        assertEquals("Muro del Cuerpo Dorado", elegida.nombre());

        new MuroDelCuerpoDorado().ejecutar(ContextoEjecucion.sobreSiMismo(c1));
        assertTrue(c1.tieneDefensaActiva());

        elegida = c1.seleccionarTecnica(pareja);
        assertNotNull(elegida);
        assertEquals("Corte de los Mil Amaneceres", elegida.nombre(), "En empate de usos, la primera en la escuela");

        c1.recibirDanioDirecto(30);
        assertTrue(c1.vidaActual() < c1.vidaMaxima());
        elegida = c1.seleccionarTecnica(pareja);
        assertNotNull(elegida);
        assertEquals("Meditación del Inmortal", elegida.nombre(), "Si le falta vida, prioriza recuperación");
    }

    @Test
    @DisplayName("Regla: el objetivo es el rival en pie con menos vida y, en empate, el de menor identificador")
    void seleccionObjetivoRivalConMenorVidaOEmpatePorMenorId() {
        Combatiente atacante = creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Atacante", escuelaCompleta);
        Combatiente rival1 = creador.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "Rival 1", escuelaCompleta);
        Combatiente rival2 = creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "Rival 2", escuelaCompleta);
        Pareja parejaRival = new Pareja(2, rival1, rival2);

        Combatiente objetivo = ContextoEjecucion.contra(atacante, parejaRival.combatientes()).afectado();
        assertEquals(rival1, objetivo);

        rival2.recibirDanioDirecto(20);
        objetivo = ContextoEjecucion.contra(atacante, parejaRival.combatientes()).afectado();
        assertEquals(rival2, objetivo);

        rival2.recibirDanioDirecto(100);
        assertFalse(rival2.estaEnPie());
        objetivo = ContextoEjecucion.contra(atacante, parejaRival.combatientes()).afectado();
        assertEquals(rival1, objetivo);
    }

    @Test
    @DisplayName("Bonus Sección 9: Efecto temporal (Veneno) inflige daño por turno y expira tras su duración")
    void efectoTemporalVenenoAplicaDanioPorTurnoYTermina() {
        Combatiente c = creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "Afectado", escuelaCompleta);
        Veneno veneno = new Veneno(3, 10);
        c.agregarEfectoTemporal(veneno);

        assertEquals(100, c.vidaActual());

        c.procesarEfectosAlInicioTurno(null);
        assertEquals(90, c.vidaActual());
        assertFalse(veneno.haTerminado());
        assertEquals(2, veneno.turnosRestantes());

        c.procesarEfectosAlInicioTurno(null);
        assertEquals(80, c.vidaActual());
        assertFalse(veneno.haTerminado());
        assertEquals(1, veneno.turnosRestantes());

        c.procesarEfectosAlInicioTurno(null);
        assertEquals(70, c.vidaActual());
        assertTrue(veneno.haTerminado());

        c.procesarEfectosAlInicioTurno(null);
        assertEquals(70, c.vidaActual());
    }

    @Test
    @DisplayName("Bonus Sección 9: Golpe Venenoso daña y deja veneno actuando sobre el objetivo")
    void golpeVenenosoDaniaYEnvenena() {
        Combatiente atacante = creador.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "Atacante", escuelaVacia);
        Combatiente victima = creador.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "Víctima", escuelaVacia);

        new GolpeVenenoso().ejecutar(ContextoEjecucion.contra(atacante, List.of(victima)));

        assertEquals(100 - 15, victima.vidaActual(), "15 de daño del golpe");

        victima.procesarEfectosAlInicioTurno(null);
        assertEquals(100 - 15 - 6, victima.vidaActual(), "El veneno resta 6 al inicio del turno");
    }

    @Test
    @DisplayName("Bonus Sección 9: Regeneración Vital cura al inicio de cada turno durante 3 turnos")
    void regeneracionVitalCuraPorTurno() {
        Combatiente c = creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Herido", escuelaVacia);
        c.recibirDanioDirecto(30);

        new RegeneracionVital().ejecutar(ContextoEjecucion.sobreSiMismo(c));
        assertEquals(70, c.vidaActual());

        c.procesarEfectosAlInicioTurno(null);
        assertEquals(78, c.vidaActual());
        c.procesarEfectosAlInicioTurno(null);
        assertEquals(86, c.vidaActual());
        c.procesarEfectosAlInicioTurno(null);
        assertEquals(94, c.vidaActual());

        c.procesarEfectosAlInicioTurno(null);
        assertEquals(94, c.vidaActual(), "Terminados los 3 turnos, el efecto ya no cura");
    }
}
