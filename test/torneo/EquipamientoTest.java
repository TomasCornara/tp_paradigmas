package torneo;

import combatientes.Combatiente;
import combatientes.CreadorCombatientes;
import combatientes.Escuela;
import combatientes.TipoCombatiente;
import equipamiento.AnilloEspiritual;
import equipamiento.ArmaduraDorada;
import equipamiento.CategoriaEquipamiento;
import equipamiento.EspadaCelestial;
import equipamiento.ReglaEquipamientoException;
import equipamiento.SelloDelFenix;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EquipamientoTest {
    private Combatiente combatiente;

    @BeforeEach
    void setUp() {
        Escuela escuelaVacia = new Escuela("Escuela Neutra", List.of(), List.of());
        CreadorCombatientes creador = new CreadorCombatientes(1);
        combatiente = creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Test", escuelaVacia);
    }

    @Test
    @DisplayName("Regla: equipar una categoría repetida se rechaza sin cambios")
    void equiparCategoriaRepetidaSeRechazaSinCambios() {
        EspadaCelestial espada1 = new EspadaCelestial();
        EspadaCelestial espada2 = new EspadaCelestial();

        combatiente.equipar(espada1);
        assertEquals(1, combatiente.equipamiento().size());

        assertThrows(ReglaEquipamientoException.class, () -> combatiente.equipar(espada2));
        assertEquals(1, combatiente.equipamiento().size(), "La lista de equipamientos no debe haber cambiado");
    }

    @Test
    @DisplayName("Regla: equipar un cuarto objeto se rechaza sin cambios")
    void equiparCuartoObjetoSeRechazaSinCambios() {
        combatiente.equipar(new EspadaCelestial());
        combatiente.equipar(new ArmaduraDorada());
        combatiente.equipar(new AnilloEspiritual());
        assertEquals(3, combatiente.equipamiento().size());

        assertThrows(ReglaEquipamientoException.class, () -> combatiente.equipar(new SelloDelFenix()));
        assertEquals(3, combatiente.equipamiento().size(), "No debe admitir un cuarto objeto");
    }

    @Test
    @DisplayName("Regla: al quitar un equipamiento que sube la vida máxima, la vida actual se recorta")
    void alQuitarEquipamientoQueSubeVidaMaximaLaVidaActualSeRecorta() {
        SelloDelFenix sello = new SelloDelFenix();
        combatiente.equipar(sello);

        assertEquals(140, combatiente.vidaMaxima());
        combatiente.curar(140);
        assertEquals(140, combatiente.vidaActual());

        combatiente.desequipar(CategoriaEquipamiento.SELLO);
        assertEquals(100, combatiente.vidaMaxima());
        assertEquals(100, combatiente.vidaActual(), "La vida actual debe haberse recortado a la nueva máxima");
    }

    @Test
    @DisplayName("Regla: equipar un objeto que sube un máximo no aumenta el valor actual")
    void equiparObjetoQueSubeMaximoNoAumentaElActual() {
        combatiente.recibirDanioDirecto(50);
        assertEquals(50, combatiente.vidaActual());

        combatiente.equipar(new SelloDelFenix());
        assertEquals(140, combatiente.vidaMaxima());
        assertEquals(50, combatiente.vidaActual(), "Sube el máximo pero no la vida actual");

        combatiente.consumirEnergia(30);
        assertEquals(70, combatiente.energiaActual());
        combatiente.equipar(new AnilloEspiritual());
        assertEquals(130, combatiente.energiaMaxima());
        assertEquals(70, combatiente.energiaActual(), "Sube el máximo pero no la energía actual");
    }

    @Test
    @DisplayName("Regla: se puede equipar y quitar entre combates")
    void sePuedeEquiparYQuitarEntreCombates() {
        EspadaCelestial espada = new EspadaCelestial();
        combatiente.equipar(espada);
        assertEquals(43, combatiente.calcularDanioAtaque(25), "Con la espada, el daño sube 10");

        combatiente.restablecerCombate();
        combatiente.desequipar(CategoriaEquipamiento.ARMA);
        assertEquals(0, combatiente.equipamiento().size());
        assertEquals(33, combatiente.calcularDanioAtaque(25), "Sin la espada, el daño vuelve al base");

        SelloDelFenix sello = new SelloDelFenix();
        combatiente.equipar(sello);
        assertEquals(140, combatiente.vidaMaxima(), "El nuevo equipamiento se aplica al combate siguiente");

        combatiente.restablecerCombate();
        assertEquals(140, combatiente.vidaActual(), "En el combate siguiente arranca con la vida del nuevo equipamiento");
    }

    @Test
    @DisplayName("Regla: los efectos de equipamientos distintos se acumulan y el combatiente puede consultarlos")
    void efectosSeAcumulanYCombatienteConoceObjetos() {
        EspadaCelestial espada = new EspadaCelestial();
        ArmaduraDorada armadura = new ArmaduraDorada();
        AnilloEspiritual anillo = new AnilloEspiritual();

        combatiente.equipar(espada);
        combatiente.equipar(armadura);
        combatiente.equipar(anillo);

        assertEquals(3, combatiente.equipamiento().size());
        assertTrue(combatiente.equipamiento().contains(espada));
        assertTrue(combatiente.equipamiento().contains(armadura));
        assertTrue(combatiente.equipamiento().contains(anillo));

        assertEquals(130, combatiente.energiaMaxima(), "Anillo suma +30 a la energía máxima");
    }

    @Test
    @DisplayName("Regla: los efectos de dos equipamientos distintos están activos al mismo tiempo y se acumulan")
    void losEfectosDeDosEquipamientosDistintosEstanActivosAlMismoTiempo() {
        Combatiente conArmaYArmadura = crearCombatienteCon();
        Combatiente soloConArma = crearCombatienteCon();

        conArmaYArmadura.equipar(new EspadaCelestial());
        conArmaYArmadura.equipar(new ArmaduraDorada());
        soloConArma.equipar(new EspadaCelestial());

        assertEquals(43, conArmaYArmadura.calcularDanioAtaque(25), "33 del Cultivador + 10 de la espada");
        assertEquals(43, soloConArma.calcularDanioAtaque(25));

        conArmaYArmadura.recibirDanio(50);
        soloConArma.recibirDanio(50);

        assertEquals(58, conArmaYArmadura.vidaActual(), "La armadura absorbe 8 de los 50 de daño");
        assertEquals(50, soloConArma.vidaActual(), "Sin armadura recibe el daño completo");
    }

    @Test
    @DisplayName("Regla: al quitar un equipamiento que sube la energía máxima, la energía actual se recorta")
    void alQuitarEquipamientoQueSubeEnergiaMaximaLaEnergiaActualSeRecorta() {
        combatiente.equipar(new AnilloEspiritual());
        assertEquals(130, combatiente.energiaMaxima());

        combatiente.recuperarEnergia(200);
        assertEquals(130, combatiente.energiaActual(), "Con el anillo se puede llegar al máximo de 130");

        combatiente.desequipar(CategoriaEquipamiento.ANILLO);
        assertEquals(100, combatiente.energiaMaxima());
        assertEquals(100, combatiente.energiaActual(), "La energía actual debe haberse recortado a la nueva máxima");
    }

    @Test
    @DisplayName("Regla: quitar una categoría que no se lleva se rechaza con excepción")
    void quitarUnaCategoriaQueNoSeLlevaSeRechaza() {
        assertThrows(ReglaEquipamientoException.class, () -> combatiente.desequipar(CategoriaEquipamiento.ARMADURA));
        assertEquals(0, combatiente.equipamiento().size(), "No debe haber cambiado el equipamiento");
    }

    private Combatiente crearCombatienteCon() {
        Escuela escuelaVacia = new Escuela("Escuela Neutra", List.of(), List.of());
        return new CreadorCombatientes(1).crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Test", escuelaVacia);
    }
}