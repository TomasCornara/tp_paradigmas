package torneo;

import combatientes.Combatiente;
import combatientes.CreadorCombatientes;
import combatientes.Escuela;
import combatientes.TipoCombatiente;
import equipamiento.AnilloEspiritual;
import equipamiento.ArmaduraDorada;
import equipamiento.Equipamiento;
import equipamiento.EspadaCelestial;
import equipamiento.ReglaEquipamientoException;
import equipamiento.SelloDelFenix;
import tecnicas.CorteDeLosMilAmaneceres;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EscuelaTest {

    @Test
    @DisplayName("Regla: la escuela rechaza un equipamiento inicial de más de tres objetos")
    void laEscuelaRechazaUnEquipamientoInicialDeMasDeTresObjetos() {
        List<Equipamiento> kitSobrecargado = List.of(
                new EspadaCelestial(), new ArmaduraDorada(), new AnilloEspiritual(), new SelloDelFenix());

        assertThrows(ReglaEquipamientoException.class,
                () -> new Escuela("Escuela Sobrecargada", List.of(), kitSobrecargado));
    }

    @Test
    @DisplayName("Regla: la escuela rechaza un equipamiento inicial con dos objetos de la misma categoría")
    void laEscuelaRechazaUnEquipamientoInicialConCategoriaRepetida() {
        List<Equipamiento> kitDuplicado = List.of(new EspadaCelestial(), new EspadaCelestial());

        assertThrows(ReglaEquipamientoException.class,
                () -> new Escuela("Escuela Duplicada", List.of(), kitDuplicado));
    }

    @Test
    @DisplayName("Regla: el equipamiento inicial de la escuela queda equipado en el combatiente al crearlo")
    void elEquipamientoInicialDeLaEscuelaQuedaEquipadoEnElCombatiente() {
        EspadaCelestial espada = new EspadaCelestial();
        Escuela escuela = new Escuela("Escuela de la Espada Celestial",
                List.of(new CorteDeLosMilAmaneceres()), List.of(espada));

        Combatiente cultivador = new CreadorCombatientes(1)
                .crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Aurelio", escuela);

        assertEquals(1, cultivador.equipamiento().size(), "El combatiente debe llevar el equipamiento inicial");
        assertTrue(cultivador.equipamiento().contains(espada), "El combatiente conoce los objetos que lleva");
        assertEquals(43, cultivador.calcularDanioAtaque(25),
                "El arma inicial ya modifica el daño: 33 del Cultivador + 10 de la espada");
    }
}
