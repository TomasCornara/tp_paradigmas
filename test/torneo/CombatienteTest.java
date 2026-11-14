package torneo;

import combatientes.*;
import combatientes.tipos.CultivadorDeLaEspada;
import combatientes.tipos.HechiceroDelVacio;
import combatientes.tipos.MonjeDelCuerpoCelestial;
import combatientes.tipos.TipoCombatiente;
import torneo.*;
import tecnicas.ContextoEjecucion;
import tecnicas.inmediatas.CorteDeLosMilAmaneceres;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CombatienteTest {
    private Escuela escuelaVacia;
    private CreadorCombatientes creador;

    @BeforeEach
    void setUp() {
        escuelaVacia = new Escuela("Escuela Neutra", List.of(), List.of());
        creador = new CreadorCombatientes(1);
    }

    @Test
    @DisplayName("Regla: una misma técnica daña diferente según el tipo del ejecutor")
    void mismaTecnicaDaniaDiferenteSegunTipoEjecutor() {
        Combatiente espadachin = creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Aurelio", escuelaVacia);
        Combatiente hechicero = creador.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "Nébula", escuelaVacia);
        Combatiente blanco1 = creador.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "Objetivo 1", escuelaVacia);
        Combatiente blanco2 = creador.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "Objetivo 2", escuelaVacia);

        CorteDeLosMilAmaneceres corte = new CorteDeLosMilAmaneceres();

        corte.ejecutar(ContextoEjecucion.contra(espadachin, List.of(blanco1)));
        corte.ejecutar(ContextoEjecucion.contra(hechicero, List.of(blanco2)));

        int danioCausadoPorEspadachin = blanco1.vidaMaxima() - blanco1.vidaActual();
        int danioCausadoPorHechicero = blanco2.vidaMaxima() - blanco2.vidaActual();

        assertTrue(danioCausadoPorEspadachin > danioCausadoPorHechicero,
                "El Cultivador de la Espada debe potenciar el daño más que otros combatientes");
        assertEquals(33, danioCausadoPorEspadachin, "25 base * 1.30 = 33");
        assertEquals(25, danioCausadoPorHechicero, "25 base sin potenciación");
    }

    @Test
    @DisplayName("Regla: el creador asigna los identificadores de los combatientes en orden de creación")
    void creadorAsignaIdsSecuencialesYCentralizaCreacion() {
        CreadorCombatientes nuevoCreador = new CreadorCombatientes(1);
        Combatiente c1 = nuevoCreador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "C1", escuelaVacia);
        Combatiente c2 = nuevoCreador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "C2", escuelaVacia);
        Combatiente c3 = nuevoCreador.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "C3", escuelaVacia);

        assertEquals(1, c1.id());
        assertEquals(2, c2.id());
        assertEquals(3, c3.id());
        assertInstanceOf(CultivadorDeLaEspada.class, c1);
        assertInstanceOf(MonjeDelCuerpoCelestial.class, c2);
        assertInstanceOf(HechiceroDelVacio.class, c3);
    }
}
