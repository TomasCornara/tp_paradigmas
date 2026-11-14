package torneo;

import acontecimientos.AccionAcontecimiento;
import acontecimientos.Acontecimiento;
import acontecimientos.CronistaDeLaSecta;
import acontecimientos.EliminacionAcontecimiento;
import acontecimientos.EmisorAcontecimientos;
import acontecimientos.EntradaTablaPosiciones;
import acontecimientos.InicioCombateAcontecimiento;
import acontecimientos.ProclamacionAcontecimiento;
import acontecimientos.ResultadoCombateAcontecimiento;
import combatientes.Combatiente;
import combatientes.CreadorCombatientes;
import combatientes.Escuela;
import combatientes.tipos.HechiceroDelVacio;
import combatientes.tipos.TipoCombatiente;
import equipamiento.CategoriaEquipamiento;
import equipamiento.items.ArmaduraDorada;
import equipamiento.items.EspadaCelestial;
import equipamiento.items.SelloDelFenix;
import torneo.*;
import tecnicas.inmediatas.CorteDeLosMilAmaneceres;
import tecnicas.inmediatas.MeditacionDelInmortal;
import tecnicas.inmediatas.MuroDelCuerpoDorado;
import tecnicas.Tecnica;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class CombateYTorneoTest {
    private Escuela escuela;
    private Escuela escuelaSinTecnicas;
    private CreadorCombatientes creador;

    @BeforeEach
    void setUp() {
        escuela = new Escuela(
                "Escuela Celestial",
                List.of(new CorteDeLosMilAmaneceres()),
                List.of(new EspadaCelestial())
        );
        escuelaSinTecnicas = new Escuela("Escuela sin Técnicas", List.of(), List.of());
        creador = new CreadorCombatientes(1);
    }

    @Test
    @DisplayName("Regla: los usos se conservan entre combates, mientras la vida y la energía se recuperan")
    void usosSeConservanEntreCombatesYVidaYEnergiaSeRecuperan() {
        Combatiente c1 = creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Aurelio", escuela);
        Combatiente c2 = creador.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "Nébula", escuela);
        Pareja p1 = new Pareja(1, c1, c2);

        Combatiente r1 = creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "Kael", escuela);
        Combatiente r2 = creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "Lin", escuela);
        Pareja p2 = new Pareja(2, r1, r2);

        Tecnica corte = c1.escuela().tecnicas().get(0);
        assertEquals(5, c1.usosRestantes(corte));

        Combate combate1 = new Combate(p1, p2, 1, 2, null);
        combate1.resolver();

        int usosRestantesTrasCombate1 = c1.usosRestantes(corte);
        assertTrue(usosRestantesTrasCombate1 < 5, "Debe haber consumido usos en el combate 1");

        p1.restablecerParaNuevoCombate();
        p2.restablecerParaNuevoCombate();

        assertEquals(c1.vidaMaxima(), c1.vidaActual(), "Vida recuperada al 100%");
        assertEquals(c1.energiaMaxima(), c1.energiaActual(), "Energía recuperada al 100%");

        assertEquals(usosRestantesTrasCombate1, c1.usosRestantes(corte),
                "Los usos de técnicas NO se reinician entre combates; se conservan durante el torneo");
    }

    @Test
    @DisplayName("Regla: el torneo rechaza la inscripción duplicada de una pareja o de un combatiente")
    void torneoRechazaInscripcionDeCombatienteOParejaDuplicada() {
        Torneo torneo = new Torneo();

        Combatiente c1 = creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "C1", escuela);
        Combatiente c2 = creador.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "C2", escuela);
        Combatiente c3 = creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "C3", escuela);
        Combatiente c4 = creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "C4", escuela);

        Pareja p1 = new Pareja(1, c1, c2);
        Pareja p2 = new Pareja(2, c3, c4);
        Pareja parejaConCombatienteRepetido = new Pareja(3, c1, c4);

        torneo.inscribir(p1);
        torneo.inscribir(p2);

        assertThrows(IllegalArgumentException.class, () -> torneo.inscribir(p1),
                "Una pareja no puede registrarse dos veces");

        assertThrows(IllegalArgumentException.class, () -> torneo.inscribir(parejaConCombatienteRepetido),
                "Un combatiente no puede inscribirse en dos parejas");
    }

    @Test
    @DisplayName("Regla: el torneo solo se puede jugar con 4 u 8 parejas")
    void torneoSoloAdmiteCuatroUOchoParejas() {
        Torneo torneo = new Torneo();
        Pareja p1 = new Pareja(1, creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "A", escuela), creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "B", escuela));
        Pareja p2 = new Pareja(2, creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "C", escuela), creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "D", escuela));

        torneo.inscribir(p1);
        torneo.inscribir(p2);

        assertThrows(IllegalStateException.class, torneo::iniciar);
    }

    @Test
    @DisplayName("Regla: la tabla de posiciones queda ordenada por última ronda jugada, vida total e identificador")
    void tablaDePosicionesQuedaOrdenadaPorUltimaRondaVidaTotalEIdentificador() {
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        Torneo torneo = new Torneo();
        torneo.agregarObservador(cronista);

        Pareja p1 = new Pareja(1, creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "A1", escuela), creador.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "A2", escuela));
        Pareja p2 = new Pareja(2, creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "B1", escuela), creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "B2", escuela));
        Pareja p3 = new Pareja(3, creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "C1", escuela), creador.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "C2", escuela));
        Pareja p4 = new Pareja(4, creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "D1", escuela), creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "D2", escuela));

        torneo.inscribir(p1);
        torneo.inscribir(p2);
        torneo.inscribir(p3);
        torneo.inscribir(p4);

        torneo.iniciar();

        List<EntradaTablaPosiciones> tabla = cronista.obtenerTablaPosiciones();
        assertEquals(4, tabla.size());

        assertEquals(2, tabla.get(0).ultimaRonda());
        assertEquals(2, tabla.get(1).ultimaRonda());

        assertEquals(1, tabla.get(2).ultimaRonda());
        assertEquals(1, tabla.get(3).ultimaRonda());

        assertTrue(tabla.get(0).vidaTotalFinal() >= tabla.get(1).vidaTotalFinal());
        assertTrue(tabla.get(2).vidaTotalFinal() >= tabla.get(3).vidaTotalFinal());
    }

    @Test
    @DisplayName("Regla: dos torneos armados igual producen el mismo campeón y el mismo historial")
    void dosTorneosArmadosIgualProducenElMismoCampeonYElMismoHistorial() {
        Torneo torneo1 = armarTorneo();
        CronistaDeLaSecta cronista1 = new CronistaDeLaSecta();
        torneo1.agregarObservador(cronista1);
        Pareja campeon1 = torneo1.iniciar();

        Torneo torneo2 = armarTorneo();
        CronistaDeLaSecta cronista2 = new CronistaDeLaSecta();
        torneo2.agregarObservador(cronista2);
        Pareja campeon2 = torneo2.iniciar();

        assertEquals(campeon1.id(), campeon2.id(), "Mismo campeón");
        assertEquals(cronista1.historial().size(), cronista2.historial().size(), "Misma cantidad de acontecimientos");

        for (int i = 0; i < cronista1.historial().size(); i++) {
            Acontecimiento a1 = cronista1.historial().get(i);
            Acontecimiento a2 = cronista2.historial().get(i);
            assertEquals(a1.descripcion(), a2.descripcion(), "Acontecimiento " + i + " idéntico");
        }
    }

    @Test
    @DisplayName("Regla: los cruces de la primera ronda siguen el orden de inscripción")
    void crucesDePrimeraRondaSiguenElOrdenDeInscripcion() {
        Torneo torneo = new Torneo();
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        torneo.agregarObservador(cronista);

        Pareja p1 = new Pareja(1, creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "A1", escuela), creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "A2", escuela));
        Pareja p2 = new Pareja(2, creador.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "B1", escuela), creador.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "B2", escuela));
        Pareja p3 = new Pareja(3, creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "C1", escuela), creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "C2", escuela));
        Pareja p4 = new Pareja(4, creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "D1", escuela), creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "D2", escuela));

        torneo.inscribir(p1);
        torneo.inscribir(p2);
        torneo.inscribir(p3);
        torneo.inscribir(p4);
        torneo.iniciar();

        List<InicioCombateAcontecimiento> primerosCruces = cronista.historial().stream()
                .filter(InicioCombateAcontecimiento.class::isInstance)
                .map(InicioCombateAcontecimiento.class::cast)
                .filter(a -> a.ronda() == 1)
                .toList();

        assertEquals(2, primerosCruces.size());
        assertTrue(primerosCruces.get(0).pareja1().equals(p1) && primerosCruces.get(0).pareja2().equals(p2),
                "Primera contra segunda");
        assertTrue(primerosCruces.get(1).pareja1().equals(p3) && primerosCruces.get(1).pareja2().equals(p4),
                "Tercera contra cuarta");
    }

    @Test
    @DisplayName("Regla: los cruces de la ronda siguiente siguen el orden de los cruces de la ronda anterior")
    void crucesDeLaRondaSiguienteSiguenElOrdenDeLosCrucesAnteriores() {
        Torneo torneo = new Torneo();
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        torneo.agregarObservador(cronista);

        Pareja p1 = new Pareja(1, creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "A1", escuela), creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "A2", escuela));
        Pareja p2 = new Pareja(2, creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "B1", escuela), creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "B2", escuela));
        Pareja p3 = new Pareja(3, creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "C1", escuela), creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "C2", escuela));
        Pareja p4 = new Pareja(4, creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "D1", escuela), creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "D2", escuela));

        torneo.inscribir(p1);
        torneo.inscribir(p2);
        torneo.inscribir(p3);
        torneo.inscribir(p4);
        torneo.iniciar();

        List<ResultadoCombateAcontecimiento> resultadosRonda1 = cronista.historial().stream()
                .filter(ResultadoCombateAcontecimiento.class::isInstance)
                .map(ResultadoCombateAcontecimiento.class::cast)
                .filter(r -> r.ronda() == 1)
                .toList();
        assertEquals(2, resultadosRonda1.size());

        InicioCombateAcontecimiento cruceRonda2 = cronista.historial().stream()
                .filter(InicioCombateAcontecimiento.class::isInstance)
                .map(InicioCombateAcontecimiento.class::cast)
                .filter(a -> a.ronda() == 2)
                .findFirst()
                .orElseThrow();

        assertEquals(resultadosRonda1.get(0).ganadora(), cruceRonda2.pareja1(),
                "La ganadora del primer cruce es la primera pareja de la ronda siguiente");
        assertEquals(resultadosRonda1.get(1).ganadora(), cruceRonda2.pareja2(),
                "La ganadora del segundo cruce es la segunda pareja de la ronda siguiente");
    }

    @Test
    @DisplayName("Regla: entre rondas se avisa con las parejas que pasan a la ronda siguiente")
    void alPasarRondaSeRecibenLasParejasQueClasifican() {
        Torneo torneo = new Torneo();
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        torneo.agregarObservador(cronista);

        Pareja p1 = new Pareja(1, creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "A1", escuela), creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "A2", escuela));
        Pareja p2 = new Pareja(2, creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "B1", escuela), creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "B2", escuela));
        Pareja p3 = new Pareja(3, creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "C1", escuela), creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "C2", escuela));
        Pareja p4 = new Pareja(4, creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "D1", escuela), creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "D2", escuela));

        torneo.inscribir(p1);
        torneo.inscribir(p2);
        torneo.inscribir(p3);
        torneo.inscribir(p4);

        List<List<Pareja>> recibidas = new ArrayList<>();
        torneo.iniciar(recibidas::add);

        assertEquals(1, recibidas.size(), "Se avisa una vez por ronda y solo si hay ronda siguiente");
        assertEquals(2, recibidas.get(0).size(), "Pasan las dos parejas ganadoras de la ronda");

        InicioCombateAcontecimiento finalRonda = cronista.historial().stream()
                .filter(InicioCombateAcontecimiento.class::isInstance)
                .map(InicioCombateAcontecimiento.class::cast)
                .filter(a -> a.ronda() == 2)
                .findFirst()
                .orElseThrow();

        assertEquals(Set.of(finalRonda.pareja1(), finalRonda.pareja2()), Set.copyOf(recibidas.get(0)),
                "Las parejas que se avisan son las que juegan la ronda siguiente");
    }

    @Test
    @DisplayName("Regla: una pareja pierde cuando sus dos integrantes quedan fuera de combate")
    void unaParejaPierdeCuandoSusDosIntegrantesQuedanFueraDeCombate() {
        Pareja fuerte = new Pareja(1,
                creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Fuerte 1", escuela),
                creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Fuerte 2", escuela));

        Escuela escuelaDebil = new Escuela("Escuela Débil", List.of(), List.of());
        Pareja debil = new Pareja(2,
                creador.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "Débil 1", escuelaDebil),
                creador.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "Débil 2", escuelaDebil));

        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        EmisorAcontecimientos emisor = new EmisorAcontecimientos();
        emisor.registrarObservador(cronista);

        Pareja ganadora = new Combate(fuerte, debil, 1, emisor).resolver();

        assertFalse(debil.sigueEnPie(), "La pareja débil debe quedarse sin integrantes en pie");
        assertEquals(fuerte, ganadora, "Gana la pareja que sigue en pie");

        List<Acontecimiento> historial = cronista.historial();
        int ultimaEliminacion = -1;
        for (int i = 0; i < historial.size(); i++) {
            if (historial.get(i) instanceof EliminacionAcontecimiento eliminacion
                    && debil.combatientes().contains(eliminacion.combatiente())) {
                ultimaEliminacion = i;
            }
        }
        assertTrue(ultimaEliminacion >= 0, "Debe quedar registrada la eliminación del último integrante caído");
        for (int i = ultimaEliminacion + 1; i < historial.size(); i++) {
            assertFalse(historial.get(i) instanceof AccionAcontecimiento,
                    "El combate termina cuando la pareja se queda sin integrantes en pie");
        }
    }

    @Test
    @DisplayName("Regla: el ataque básico no cuesta energía, no tiene límite de usos y no cuenta para la regla del compañero")
    void ataqueBasicoNoCuestaEnergiaNiTieneLimiteDeUsos() {
        Escuela escuelaSinEquipo = new Escuela("Escuela sin Equipo", List.of(new CorteDeLosMilAmaneceres()), List.of());
        Escuela escuelaSinTecnicas = new Escuela("Escuela sin Técnicas", List.of(), List.of());

        Combatiente sinUsos = creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Sin Usos", escuelaSinEquipo);
        Combatiente companiero = creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Compañero", escuelaSinEquipo);
        Pareja p1 = new Pareja(1, sinUsos, companiero);

        Pareja p2 = new Pareja(2,
                creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "Blanco 1", escuelaSinTecnicas),
                creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "Blanco 2", escuelaSinTecnicas));

        Tecnica corte = sinUsos.escuela().tecnicas().get(0);
        while (sinUsos.usosRestantes(corte) > 0) {
            sinUsos.descontarUso(corte);
        }
        while (companiero.usosRestantes(corte) > 0) {
            companiero.descontarUso(corte);
        }
        int energiaInicial = sinUsos.energiaActual();

        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        EmisorAcontecimientos emisor = new EmisorAcontecimientos();
        emisor.registrarObservador(cronista);
        new Combate(p1, p2, 1, 1, emisor).resolver();

        assertTrue(cronista.historial().stream()
                        .filter(AccionAcontecimiento.class::isInstance)
                        .map(AccionAcontecimiento.class::cast)
                        .anyMatch(a -> a.ejecutor().equals(sinUsos) && a.accion().equals("Ataque básico")),
                "Sin usos restantes debe atacar básicamente");
        assertEquals(energiaInicial, sinUsos.energiaActual(), "El ataque básico no cuesta energía");
        assertEquals(0, sinUsos.usosRestantes(corte), "El ataque básico no consume usos de técnicas");
        assertTrue(p1.puedeUsarPorReglaCompaniero(companiero, corte),
                "El ataque básico no cuenta para la regla del compañero");

        sinUsos.equipar(new ArmaduraDorada());
        companiero.equipar(new ArmaduraDorada());

        CronistaDeLaSecta cronistaLargo = new CronistaDeLaSecta();
        EmisorAcontecimientos emisorLargo = new EmisorAcontecimientos();
        emisorLargo.registrarObservador(cronistaLargo);
        new Combate(p1, p2, 2, 10, emisorLargo).resolver();

        long ataques = cronistaLargo.historial().stream()
                .filter(AccionAcontecimiento.class::isInstance)
                .map(AccionAcontecimiento.class::cast)
                .filter(a -> a.ejecutor().equals(sinUsos) && a.accion().equals("Ataque básico"))
                .count();

        assertEquals(10, ataques, "El ataque básico se puede repetir todas las veces, sin límite de usos");
        assertEquals(sinUsos.energiaMaxima(), sinUsos.energiaActual(), "Los ataques básicos no gastan energía");
    }

    @Test
    @DisplayName("Regla: las técnicas defensivas y de recuperación se aplican al ejecutor")
    void tecnicasDefensivasYDeRecuperacionSeAplicanAlEjecutor() {
        Escuela monasterio = new Escuela("Monasterio",
                List.of(new MuroDelCuerpoDorado(), new MeditacionDelInmortal()), List.of());
        Combatiente monje = creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "Monje", monasterio);
        Combatiente companiero = creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "Compañero", monasterio);
        Pareja p1 = new Pareja(1, monje, companiero);

        Escuela escuelaAtacante = new Escuela("Escuela del Agresivo",
                List.of(new CorteDeLosMilAmaneceres()), List.of());
        Pareja p2 = new Pareja(2,
                creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "Atacante 1", escuelaAtacante),
                creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "Atacante 2", escuelaAtacante));

        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        EmisorAcontecimientos emisor = new EmisorAcontecimientos();
        emisor.registrarObservador(cronista);
        new Combate(p1, p2, 1, 2, emisor).resolver();

        List<AccionAcontecimiento> acciones = cronista.historial().stream()
                .filter(AccionAcontecimiento.class::isInstance)
                .map(AccionAcontecimiento.class::cast)
                .toList();

        assertFalse(acciones.isEmpty());
        for (AccionAcontecimiento accion : acciones) {
            if (accion.accion().equals("Muro del Cuerpo Dorado") || accion.accion().equals("Meditación del Inmortal")) {
                assertEquals(accion.ejecutor(), accion.objetivo(),
                        "La defensiva y la recuperación se aplican al ejecutor, no al rival");
            }
        }
        assertTrue(acciones.stream().anyMatch(a -> a.accion().equals("Muro del Cuerpo Dorado")),
                "El monje debe ejecutar su defensiva");
        assertTrue(acciones.stream().anyMatch(a -> a.accion().equals("Meditación del Inmortal")),
                "El monje debe ejecutar su recuperación al estar herido");
    }

    @Test
    @DisplayName("Regla: empieza la pareja de menor identificador")
    void empiezaLaParejaDeMenorIdentificador() {
        Pareja alta = new Pareja(9,
                creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Alto 1", escuela),
                creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Alto 2", escuela));
        Pareja baja = new Pareja(2,
                creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "Bajo 1", escuela),
                creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "Bajo 2", escuela));

        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        EmisorAcontecimientos emisor = new EmisorAcontecimientos();
        emisor.registrarObservador(cronista);

        new Combate(alta, baja, 1, 1, emisor).resolver();

        AccionAcontecimiento primeraAccion = (AccionAcontecimiento) cronista.historial().stream()
                .filter(AccionAcontecimiento.class::isInstance)
                .findFirst()
                .orElseThrow();

        assertTrue(baja.combatientes().contains(primeraAccion.ejecutor()),
                "Debe empezar la pareja de menor identificador, sin importar el orden en que se pasó al combate");
    }

    @Test
    @DisplayName("Regla: dentro de la pareja actúa primero el de menor identificador")
    void dentroDeLaParejaEmpiezaElDeMenorIdentificador() {
        Combatiente mayor = new HechiceroDelVacio(9, "Mayor", escuela);
        Combatiente menor = new HechiceroDelVacio(7, "Menor", escuela);
        Pareja pareja = new Pareja(1, mayor, menor); // orden invertido a propósito

        Pareja rival = new Pareja(2,
                new HechiceroDelVacio(20, "Rival 1", escuela),
                new HechiceroDelVacio(21, "Rival 2", escuela));

        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        EmisorAcontecimientos emisor = new EmisorAcontecimientos();
        emisor.registrarObservador(cronista);

        new Combate(pareja, rival, 1, 1, emisor).resolver();

        AccionAcontecimiento primeraAccion = (AccionAcontecimiento) cronista.historial().stream()
                .filter(AccionAcontecimiento.class::isInstance)
                .filter(a -> pareja.combatientes().contains(((AccionAcontecimiento) a).ejecutor()))
                .findFirst()
                .orElseThrow();

        assertEquals(menor, primeraAccion.ejecutor(),
                "Dentro de la pareja actúa primero el de menor identificador");
    }

    @Test
    @DisplayName("Regla: un caído no actúa ni es elegido como objetivo")
    void caidoNoActuaNiEsElegidoComoObjetivo() {
        Pareja fuerte = new Pareja(1,
                creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Fuerte 1", escuela),
                creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Fuerte 2", escuela));

        Escuela escuelaDebil = new Escuela("Escuela Débil", List.of(), List.of());
        Combatiente debil1 = creador.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "Débil 1", escuelaDebil);
        Combatiente debil2 = creador.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "Débil 2", escuelaDebil);
        Pareja debil = new Pareja(2, debil1, debil2);

        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        EmisorAcontecimientos emisor = new EmisorAcontecimientos();
        emisor.registrarObservador(cronista);

        new Combate(fuerte, debil, 1, emisor).resolver();

        assertFalse(debil1.estaEnPie() && debil2.estaEnPie(), "Debe haber al menos un caído");

        List<Acontecimiento> historial = cronista.historial();
        for (int i = 0; i < historial.size(); i++) {
            if (historial.get(i) instanceof EliminacionAcontecimiento eliminacion) {
                for (int j = i + 1; j < historial.size(); j++) {
                    if (historial.get(j) instanceof AccionAcontecimiento accion) {
                        assertNotEquals(eliminacion.combatiente(), accion.ejecutor(), "Un caído no actúa");
                        assertNotEquals(eliminacion.combatiente(), accion.objetivo(), "Un caído no es objetivo");
                    }
                }
            }
        }
    }

    @Test
    @DisplayName("Regla: al límite de asaltos gana la pareja con más vida total")
    void alLimiteDeAsaltosGanaLaParejaConMasVidaTotal() {
        Pareja cultivadores = new Pareja(1,
                creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "A1", escuela),
                creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "A2", escuela));
        Pareja hechiceros = new Pareja(2,
                creador.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "B1", escuela),
                creador.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "B2", escuela));

        Pareja ganadora = new Combate(cultivadores, hechiceros, 1, 1, null).resolver();

        assertNotEquals(cultivadores.vidaTotal(), hechiceros.vidaTotal(), "El caso debe ser desparejo");
        assertEquals(cultivadores.vidaTotal() > hechiceros.vidaTotal() ? cultivadores : hechiceros, ganadora,
                "Al agotar los asaltos gana la de más vida total");
    }

    @Test
    @DisplayName("Regla: al límite de asaltos, en empate de vida total, gana la pareja de menor identificador")
    void empateDeVidaTotalAlLimiteDeAsaltosGanaLaDeMenorIdentificador() {
        Pareja primera = new Pareja(1,
                creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "A1", escuela),
                creador.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "A2", escuela));
        Pareja segunda = new Pareja(2,
                creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "B1", escuela),
                creador.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "B2", escuela));

        Pareja ganadora = new Combate(primera, segunda, 1, 1, null).resolver();

        assertEquals(primera.vidaTotal(), segunda.vidaTotal(),
                "Las parejas deben terminar con la misma vida total");
        assertEquals(primera, ganadora, "En empate gana la pareja de menor identificador");
    }

    @Test
    @DisplayName("Regla: cada combatiente en pie recupera energía al comenzar cada asalto")
    void energiaSeRecuperaAlComienzoDeCadaAsalto() {
        Escuela escuelaSinEquipo = new Escuela("Escuela sin Equipo", List.of(new CorteDeLosMilAmaneceres()), List.of());
        Escuela escuelaSinTecnicas = new Escuela("Escuela sin Técnicas", List.of(), List.of());

        Combatiente lanzador = creador.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "Lanzador", escuelaSinEquipo);
        Combatiente pasivo = creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "Pasivo", escuelaSinTecnicas);
        Pareja p1 = new Pareja(1, lanzador, pasivo);

        Pareja p2 = new Pareja(2,
                creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "Blanco 1", escuelaSinTecnicas),
                creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "Blanco 2", escuelaSinTecnicas));

        Tecnica corte = lanzador.escuela().tecnicas().get(0);
        int costo = lanzador.calcularCostoEnergia(corte);
        int energiaInicial = lanzador.energiaActual();

        new Combate(p1, p2, 1, 3, null).resolver();

        assertEquals(2, lanzador.usosRestantes(corte), "Debe haber usado la técnica en los 3 asaltos");
        int esperada = energiaInicial - 3 * costo + 2 * Combate.ENERGIA_POR_ASALTO;
        assertEquals(esperada, lanzador.energiaActual(),
                "Recupera " + Combate.ENERGIA_POR_ASALTO + " de energía al inicio de cada asalto");

        lanzador.recuperarEnergia(1000);
        assertEquals(lanzador.energiaMaxima(), lanzador.energiaActual(),
                "La recuperación de energía nunca supera el máximo");
    }

    @Test
    @DisplayName("Regla: el equipamiento puede cambiarse entre rondas y surte efecto en el combate siguiente")
    void elEquipamientoPuedeCambiarseEntreRondasYSurteEfectoEnElCombateSiguiente() {
        Torneo torneo = armarTorneoCon(4);
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        torneo.agregarObservador(cronista);

        AtomicInteger corteDelHistorial = new AtomicInteger();
        AtomicReference<Combatiente> combatienteSinEspada = new AtomicReference<>();

        torneo.iniciar(rondas -> {
            corteDelHistorial.set(cronista.historial().size());
            Combatiente combatiente = rondas.get(0).combatientes().get(0);
            combatienteSinEspada.set(combatiente);
            combatiente.desequipar(CategoriaEquipamiento.ARMA);
        });

        Combatiente combatiente = combatienteSinEspada.get();
        assertNotNull(combatiente);
        assertEquals(33, combatiente.calcularDanioAtaque(25), "Sin la espada vuelve al daño base del Cultivador");

        List<Acontecimiento> historial = cronista.historial();
        List<Integer> danosRonda1 = new ArrayList<>();
        List<Integer> danosRonda2 = new ArrayList<>();
        for (int i = 0; i < historial.size(); i++) {
            if (!(historial.get(i) instanceof AccionAcontecimiento accion)
                    || !accion.ejecutor().equals(combatiente)) {
                continue;
            }
            if (i < corteDelHistorial.get()) {
                danosRonda1.add(danioDe(accion));
            } else {
                danosRonda2.add(danioDe(accion));
            }
        }

        assertFalse(danosRonda1.isEmpty(), "El combatiente debe haber actuado en la ronda 1");
        assertFalse(danosRonda2.isEmpty(), "El combatiente debe haber actuado en la ronda 2");
        assertEquals(43, danosRonda1.stream().mapToInt(Integer::intValue).max().orElseThrow(),
                "En la ronda 1 pegaba 43: 33 del Cultivador + 10 de la espada");
        assertTrue(danosRonda2.stream().mapToInt(Integer::intValue).max().orElseThrow() <= 33,
                "En la ronda 2 ya no puede pegar 43: se quitó la espada entre rondas");
    }

    @Test
    @DisplayName("Regla: el torneo rechaza una cantidad de parejas que no es 4 ni 8")
    void elTorneoRechazaCantidadesDeParejasInvalidas() {
        for (int cantidad : List.of(5, 6, 7)) {
            Torneo torneo = armarTorneoCon(cantidad);
            assertThrows(IllegalStateException.class, torneo::iniciar,
                    "Con " + cantidad + " parejas el torneo no debe poder empezar");
        }
    }

    @Test
    @DisplayName("Regla: un torneo de 8 parejas se juega completo y proclama un campeón")
    void unTorneoDeOchoParejasSeJuegaCompleto() {
        Torneo torneo = armarTorneoCon(8);
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        torneo.agregarObservador(cronista);

        Pareja campeon = torneo.iniciar();

        List<InicioCombateAcontecimiento> cruces = cronista.historial().stream()
                .filter(InicioCombateAcontecimiento.class::isInstance)
                .map(InicioCombateAcontecimiento.class::cast)
                .toList();
        assertEquals(7, cruces.size(), "8 parejas: 4 cruces en la ronda 1, 2 en la ronda 2 y 1 final");
        assertEquals(4, cruces.stream().filter(c -> c.ronda() == 1).count());
        assertEquals(2, cruces.stream().filter(c -> c.ronda() == 2).count());
        assertEquals(1, cruces.stream().filter(c -> c.ronda() == 3).count());
        assertEquals(1, cronista.historial().stream().filter(ProclamacionAcontecimiento.class::isInstance).count(),
                "Solo se proclama un campeón");
        assertNotNull(campeon);
    }

    @Test
    @DisplayName("Regla: el torneo termina cuando queda una sola pareja")
    void elTorneoTerminaCuandoQuedaUnaSolaPareja() {
        Torneo torneo = armarTorneoCon(4);
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        torneo.agregarObservador(cronista);

        Pareja campeon = torneo.iniciar();

        List<InicioCombateAcontecimiento> cruces = cronista.historial().stream()
                .filter(InicioCombateAcontecimiento.class::isInstance)
                .map(InicioCombateAcontecimiento.class::cast)
                .toList();
        assertEquals(3, cruces.size(), "4 parejas: 2 cruces en la ronda 1 y 1 final");
        assertEquals(1, cruces.stream().filter(c -> c.ronda() == 2).count(), "Solo hay un cruce en la ronda 2");

        ResultadoCombateAcontecimiento finalDelTorneo = cronista.historial().stream()
                .filter(ResultadoCombateAcontecimiento.class::isInstance)
                .map(ResultadoCombateAcontecimiento.class::cast)
                .filter(r -> r.ronda() == 2)
                .findFirst()
                .orElseThrow();
        assertEquals(campeon, finalDelTorneo.ganadora(), "El campeón es la pareja que ganó el último cruce");
        assertEquals(1, cronista.historial().stream().filter(ProclamacionAcontecimiento.class::isInstance).count(),
                "El torneo se proclama una sola vez, cuando queda una sola pareja");
    }

    @Test
    @DisplayName("Regla: en cada asalto cada pareja juega su turno completo")
    void enCadaAsaltoCadaParejaJuegaSuTurnoCompleto() {
        Escuela escuelaSinTecnicas = new Escuela("Escuela sin Técnicas", List.of(), List.of());
        CreadorCombatientes c = new CreadorCombatientes(1);
        Combatiente a1 = c.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "A1", escuelaSinTecnicas);
        Combatiente a2 = c.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "A2", escuelaSinTecnicas);
        Combatiente b1 = c.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "B1", escuelaSinTecnicas);
        Combatiente b2 = c.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "B2", escuelaSinTecnicas);
        Pareja pareja1 = new Pareja(1, a1, a2);
        Pareja pareja2 = new Pareja(2, b1, b2);

        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        EmisorAcontecimientos emisor = new EmisorAcontecimientos();
        emisor.registrarObservador(cronista);
        new Combate(pareja1, pareja2, 1, 1, emisor).resolver();

        List<AccionAcontecimiento> acciones = cronista.historial().stream()
                .filter(AccionAcontecimiento.class::isInstance)
                .map(AccionAcontecimiento.class::cast)
                .toList();

        assertEquals(4, acciones.size(), "Cada combatiente en pie realiza una acción en el asalto");
        assertEquals(4, acciones.stream().map(AccionAcontecimiento::ejecutor).distinct().count(),
                "Los cuatro combatientes en pie han hecho una acción cada uno");
        assertEquals(a1, acciones.get(0).ejecutor(), "Empieza la pareja de menor identificador");
        assertEquals(a2, acciones.get(1).ejecutor(), "Dentro de la pareja actúa primero el de menor identificador");
        assertEquals(b1, acciones.get(2).ejecutor(), "Después juega el turno completo de la otra pareja");
        assertEquals(b2, acciones.get(3).ejecutor());
    }

    @Test
    @DisplayName("Regla: el caído en el combate anterior vuelve a pelear en el siguiente porque recupera vida y energía al máximo")
    void elCaidoEnElCombateAnteriorVuelveAPelearEnElSiguiente() {
        Escuela escuelaConCorte = new Escuela("Escuela de la Espada",
                List.of(new CorteDeLosMilAmaneceres()), List.of());
        Escuela escuelaConEspada = new Escuela("Escuela de la Espada Celestial",
                List.of(new CorteDeLosMilAmaneceres()), List.of(new EspadaCelestial()));

        CreadorCombatientes c = new CreadorCombatientes(1);
        Combatiente caido = c.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "Caído", escuelaSinTecnicas);
        Combatiente superviviente = c.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Superviviente", escuelaConEspada);
        Pareja parejaQueAvanza = new Pareja(1, caido, superviviente);

        Pareja parejaRival = new Pareja(2,
                c.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Rival 1", escuelaConCorte),
                c.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Rival 2", escuelaConCorte));

        CronistaDeLaSecta cronista1 = new CronistaDeLaSecta();
        EmisorAcontecimientos emisor1 = new EmisorAcontecimientos();
        emisor1.registrarObservador(cronista1);
        new Combate(parejaQueAvanza, parejaRival, 1, emisor1).resolver();

        assertFalse(caido.estaEnPie(), "El primer integrante debe quedar fuera de combate");
        assertTrue(cronista1.historial().stream()
                        .anyMatch(h -> h instanceof EliminacionAcontecimiento eliminacion
                                && eliminacion.combatiente().equals(caido)),
                "La eliminación debe quedar registrada en el historial");

        Pareja otraParejaRival = new Pareja(3,
                c.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "Rival 3", escuelaSinTecnicas),
                c.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "Rival 4", escuelaSinTecnicas));

        CronistaDeLaSecta cronista2 = new CronistaDeLaSecta();
        EmisorAcontecimientos emisor2 = new EmisorAcontecimientos();
        emisor2.registrarObservador(cronista2);
        new Combate(parejaQueAvanza, otraParejaRival, 2, 1, emisor2).resolver();

        assertTrue(caido.estaEnPie(), "Al comenzar el combate recupera la vida y vuelve a estar en pie");
        assertEquals(80, caido.vidaActual(),
                "Entró con su vida máxima de 100 y solo recibió los 20 de daño de dos ataques básicos");
        assertTrue(cronista2.historial().stream()
                        .anyMatch(h -> h instanceof AccionAcontecimiento accion && accion.ejecutor().equals(caido)),
                "Vuelve a actuar en el combate siguiente");
    }

    @Test
    @DisplayName("Regla: en el combate, el compañero no puede usar la técnica mientras el primero conserve usos y siga en pie")
    void elCompaneroNoPuedeUsarLaTecnicaDelPrimeroDuranteElCombate() {
        EscenarioCompanero escenario = crearEscenarioCompanero();
        CronistaDeLaSecta cronista = jugarCombate(escenario.pareja(), escenario.rival(), 1);

        List<Acontecimiento> historial = cronista.historial();
        int ultimoUsoDelPrimero = -1;
        int primerUsoDelCompanero = -1;
        for (int i = 0; i < historial.size(); i++) {
            if (!(historial.get(i) instanceof AccionAcontecimiento accion)
                    || !accion.accion().equals("Corte de los Mil Amaneceres")) {
                continue;
            }
            if (accion.ejecutor().equals(escenario.primero())) {
                ultimoUsoDelPrimero = i;
            } else if (accion.ejecutor().equals(escenario.companion()) && primerUsoDelCompanero < 0) {
                primerUsoDelCompanero = i;
            }
        }

        assertTrue(ultimoUsoDelPrimero >= 0, "El primero debe poder usar la técnica");
        assertTrue(primerUsoDelCompanero < 0 || primerUsoDelCompanero > ultimoUsoDelPrimero,
                "El compañero solo puede usar la técnica cuando el primero ya agotó sus usos");

        List<AccionAcontecimiento> accionesDelCompanero = accionesDe(cronista, escenario.companion());
        assertFalse(accionesDelCompanero.isEmpty(), "El compañero debe haber actuado");
        assertTrue(accionesDelCompanero.stream()
                        .filter(a -> !a.accion().equals("Corte de los Mil Amaneceres"))
                        .allMatch(a -> a.accion().equals("Ataque básico")),
                "Mientras está bloqueado, el compañero ataca básicamente");
        assertTrue(escenario.primero().estaEnPie(), "El primero sigue en pie al terminar el combate");
    }

    @Test
    @DisplayName("Regla: en el combate siguiente el compañero puede volver a usar la técnica")
    void elCompaneroPuedeUsarLaTecnicaEnElCombateSiguiente() {
        EscenarioCompanero escenario = crearEscenarioCompanero();
        CronistaDeLaSecta cronista1 = jugarCombate(escenario.pareja(), escenario.rival(), 1);

        List<AccionAcontecimiento> accionesEnElPrimerCombate = accionesDe(cronista1, escenario.companion());
        assertFalse(accionesEnElPrimerCombate.isEmpty(), "El compañero debe actuar en el primer combate");
        assertEquals("Ataque básico", accionesEnElPrimerCombate.get(0).accion(),
                "En el primer combate el compañero está bloqueado y ataca básicamente");

        CreadorCombatientes c = new CreadorCombatientes(100);
        Pareja rivalNuevo = new Pareja(3,
                c.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Rival 3", escuelaSinTecnicas),
                c.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Rival 4", escuelaSinTecnicas));
        CronistaDeLaSecta cronista2 = jugarCombate(escenario.pareja(), rivalNuevo, 2);

        List<AccionAcontecimiento> accionesDelCompanero = accionesDe(cronista2, escenario.companion());
        assertFalse(accionesDelCompanero.isEmpty(), "El compañero debe actuar en el segundo combate");
        assertEquals("Corte de los Mil Amaneceres", accionesDelCompanero.get(0).accion(),
                "La restricción termina con el combate: en el siguiente el compañero puede usar la técnica");
    }

    @Test
    @DisplayName("Regla: agotados los cinco usos de la técnica en el torneo, no puede ejecutarla aunque tenga energía")
    void agotadosLosCincoUsosLaTecnicaNoSeEjecutaAunqueTengaEnergia() {
        CorteDeLosMilAmaneceres corte = new CorteDeLosMilAmaneceres();
        Escuela escuelaDelCultivador = new Escuela("Escuela del Cultivador",
                List.of(corte), List.of(new SelloDelFenix()));

        CreadorCombatientes c = new CreadorCombatientes(1);
        Combatiente ejecutor = c.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Ejecutor", escuelaDelCultivador);
        Combatiente companion = c.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "Compañero", escuelaSinTecnicas);
        Pareja pareja = new Pareja(1, ejecutor, companion);
        Pareja rival = new Pareja(2,
                c.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Rival 1", escuelaSinTecnicas),
                c.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Rival 2", escuelaSinTecnicas));

        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        EmisorAcontecimientos emisor = new EmisorAcontecimientos();
        emisor.registrarObservador(cronista);
        new Combate(pareja, rival, 1, emisor).resolver();

        List<AccionAcontecimiento> acciones = accionesDe(cronista, ejecutor);
        long usosDeLaTecnica = acciones.stream().filter(a -> a.accion().equals("Corte de los Mil Amaneceres")).count();
        assertEquals(5, usosDeLaTecnica, "La técnica se puede usar cinco veces en todo el torneo");
        assertEquals(0, ejecutor.usosRestantes(corte), "No quedan usos de la técnica");
        assertEquals("Ataque básico", acciones.get(acciones.size() - 1).accion(),
                "Sin usos restantes se recurre al ataque básico");
        assertTrue(ejecutor.energiaActual() >= ejecutor.calcularCostoEnergia(corte),
                "Tenía energía suficiente: lo que se agotó fueron los usos, no la energía");
    }

    private record EscenarioCompanero(Combatiente primero, Combatiente companion, Pareja pareja, Pareja rival) {
    }

    private EscenarioCompanero crearEscenarioCompanero() {
        CorteDeLosMilAmaneceres corte = new CorteDeLosMilAmaneceres();
        Escuela escuelaDeLaEspada = new Escuela("Escuela de la Espada", List.of(corte), List.of());
        Escuela escuelaSinTecnicas = new Escuela("Escuela sin Técnicas", List.of(), List.of());

        CreadorCombatientes c = new CreadorCombatientes(1);
        Combatiente primero = c.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Primero", escuelaDeLaEspada);
        Combatiente companion = c.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "Compañero", escuelaDeLaEspada);
        Pareja pareja = new Pareja(1, primero, companion);
        Pareja rival = new Pareja(2,
                c.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Rival 1", escuelaSinTecnicas),
                c.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Rival 2", escuelaSinTecnicas));

        return new EscenarioCompanero(primero, companion, pareja, rival);
    }

    private CronistaDeLaSecta jugarCombate(Pareja pareja, Pareja rival, int ronda) {
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        EmisorAcontecimientos emisor = new EmisorAcontecimientos();
        emisor.registrarObservador(cronista);
        new Combate(pareja, rival, ronda, emisor).resolver();
        return cronista;
    }

    private List<AccionAcontecimiento> accionesDe(CronistaDeLaSecta cronista, Combatiente ejecutor) {
        return cronista.historial().stream()
                .filter(AccionAcontecimiento.class::isInstance)
                .map(AccionAcontecimiento.class::cast)
                .filter(a -> a.ejecutor().equals(ejecutor))
                .toList();
    }

    private int danioDe(AccionAcontecimiento accion) {
        Matcher matcher = Pattern.compile("Daño: (\\d+)").matcher(accion.detalle());
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }

    private Torneo armarTorneoCon(int cantidadParejas) {
        Torneo torneo = new Torneo();
        CreadorCombatientes c = new CreadorCombatientes(1);
        for (int i = 0; i < cantidadParejas; i++) {
            torneo.inscribir(new Pareja(i + 1,
                    c.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "A" + i, escuela),
                    c.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "B" + i, escuela)));
        }
        return torneo;
    }

    private Torneo armarTorneo() {
        CreadorCombatientes c = new CreadorCombatientes(1);
        Torneo t = new Torneo();

        Pareja p1 = new Pareja(1, c.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Aurelio", escuela), c.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "Nébula", escuela));
        Pareja p2 = new Pareja(2, c.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "Kael", escuela), c.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Darian", escuela));
        Pareja p3 = new Pareja(3, c.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "Lin", escuela), c.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "Bren", escuela));
        Pareja p4 = new Pareja(4, c.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Thorin", escuela), c.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "Zhen", escuela));

        t.inscribir(p1);
        t.inscribir(p2);
        t.inscribir(p3);
        t.inscribir(p4);
        return t;
    }
}
