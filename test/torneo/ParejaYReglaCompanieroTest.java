package torneo;

import combatientes.Combatiente;
import combatientes.CreadorCombatientes;
import combatientes.Escuela;
import combatientes.tipos.TipoCombatiente;
import torneo.*;
import tecnicas.inmediatas.CorteDeLosMilAmaneceres;
import tecnicas.Tecnica;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParejaYReglaCompanieroTest {
    private CreadorCombatientes creador;
    private Escuela escuela;
    private Combatiente c1;
    private Combatiente c2;
    private Pareja pareja;
    private Tecnica corte;

    @BeforeEach
    void setUp() {
        corte = new CorteDeLosMilAmaneceres();
        escuela = new Escuela("Escuela Espada", List.of(corte), List.of());
        creador = new CreadorCombatientes(1);
        c1 = creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Aurelio", escuela);
        c2 = creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Darian", escuela);
        pareja = new Pareja(10, c1, c2);
    }

    @Test
    @DisplayName("Regla: una pareja no puede tener dos veces el mismo combatiente ni combatientes nulos")
    void parejaRequiereCombatientesDistintos() {
        assertThrows(IllegalArgumentException.class, () -> new Pareja(1, c1, c1));
        assertThrows(IllegalArgumentException.class, () -> new Pareja(1, c1, null));
    }

    @Test
    @DisplayName("Regla: cada pareja recibe un identificador único al crearse")
    void cadaParejaRecibeIdentificadorUnico() {
        Torneo torneo = new Torneo();
        Pareja primera = torneo.inscribir(c1, c2);
        Pareja segunda = torneo.inscribir(
                creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Guardián", escuela),
                creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Aliado", escuela));

        assertNotEquals(primera.id(), segunda.id(), "El identificador se asigna solo y no se repite");
        assertEquals(1, primera.id(), "El identificador se asigna en orden de inscripción");
        assertEquals(2, segunda.id(), "El identificador se asigna en orden de inscripción");

        Pareja explicita = new Pareja(10, c1, c2);
        assertEquals(10, explicita.id(), "Permite indicar el identificador al crearse");
    }

    @Test
    @DisplayName("Regla: el compañero no puede usar una técnica mientras el primero conserve usos y siga en pie")
    void companieroNoPuedeUsarTecnicaMientrasPrimeroConserveUsosYEsteEnPie() {
        assertTrue(pareja.puedeUsarPorReglaCompaniero(c1, corte));
        assertTrue(pareja.puedeUsarPorReglaCompaniero(c2, corte));

        pareja.registrarUsoTecnica(c1, corte);
        c1.descontarUso(corte);

        assertTrue(pareja.puedeUsarPorReglaCompaniero(c1, corte));

        assertFalse(pareja.puedeUsarPorReglaCompaniero(c2, corte),
                "El compañero no debe poder usarla mientras c1 conserve usos y siga en pie");
    }

    @Test
    @DisplayName("Regla: el compañero puede usar la técnica si el primero agota los usos o queda fuera de combate")
    void companieroPuedeUsarTecnicaSiPrimeroAgotaUsosOCae() {
        pareja.registrarUsoTecnica(c1, corte);
        c1.descontarUso(corte);
        assertFalse(pareja.puedeUsarPorReglaCompaniero(c2, corte));

        while (c1.usosRestantes(corte) > 0) {
            c1.descontarUso(corte);
        }
        assertEquals(0, c1.usosRestantes(corte));
        assertTrue(pareja.puedeUsarPorReglaCompaniero(c2, corte),
                "c2 debe poder usarla porque c1 agotó sus usos");

        c1.recuperarEnergia(100);
        Combatiente c3 = creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Guardián", escuela);
        Combatiente c4 = creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Aliado", escuela);
        Pareja pareja2 = new Pareja(20, c3, c4);
        pareja2.registrarUsoTecnica(c3, corte);
        c3.descontarUso(corte);
        assertEquals(4, c3.usosRestantes(corte));

        assertFalse(pareja2.puedeUsarPorReglaCompaniero(c4, corte));

        c3.recibirDanioDirecto(200);
        assertFalse(c3.estaEnPie());

        assertTrue(pareja2.puedeUsarPorReglaCompaniero(c4, corte),
                "c4 debe poder usarla porque c3 quedó fuera de combate");
    }

    @Test
    @DisplayName("Regla: la restricción de la regla del compañero termina con el combate")
    void restriccionTerminaConElCombate() {
        pareja.registrarUsoTecnica(c1, corte);
        c1.descontarUso(corte);
        assertFalse(pareja.puedeUsarPorReglaCompaniero(c2, corte));

        pareja.restablecerParaNuevoCombate();

        assertTrue(pareja.puedeUsarPorReglaCompaniero(c2, corte));
    }
}
