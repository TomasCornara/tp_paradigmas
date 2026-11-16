import acontecimientos.CronistaDeLaSecta;
import acontecimientos.HeraldoImperial;
import combatientes.Combatiente;
import combatientes.CreadorCombatientes;
import combatientes.Escuela;
import equipamiento.AnilloEspiritual;
import equipamiento.ArmaduraDorada;
import equipamiento.EspadaCelestial;
import tecnicas.CorteDeLosMilAmaneceres;
import tecnicas.DominioDeLaNada;
import tecnicas.MeditacionDelInmortal;
import tecnicas.MuroDelCuerpoDorado;
import tecnicas.RegeneracionVital;
import torneo.Pareja;
import torneo.Torneo;

import java.util.List;

public class TorneoDeLosInmortales {
    public static void main(String[] args) {
        // Escuela = configuración: técnicas y equipamiento iniciales.
        Escuela escuelaDeLaEspada = new Escuela(
                "Escuela de la Espada Celestial",
                List.of(new CorteDeLosMilAmaneceres()),
                List.of(new EspadaCelestial()));

        Escuela palacioDelVacio = new Escuela(
                "Palacio del Vacío",
                List.of(new DominioDeLaNada(), new MeditacionDelInmortal()),
                List.of(new AnilloEspiritual()));

        Escuela monasterio = new Escuela(
                "Monasterio del Cuerpo Dorado",
                List.of(new MuroDelCuerpoDorado(), new RegeneracionVital()),
                List.of(new ArmaduraDorada()));

        // La creación de combatientes está centralizada.
        CreadorCombatientes creador = new CreadorCombatientes();
        Combatiente aurelio = creador.crear(
                "Cultivador de la Espada", "Aurelio", escuelaDeLaEspada);
        Combatiente nebula = creador.crear(
                "Hechicero del Vacío", "Nébula", palacioDelVacio);
        Combatiente kael = creador.crear(
                "Monje del Cuerpo Celestial", "Kael", palacioDelVacio);
        Combatiente darian = creador.crear(
                "Cultivador de la Espada", "Darian", escuelaDeLaEspada);

        Combatiente lin = creador.crear(
                "Monje del Cuerpo Celestial", "Lin", monasterio);
        Combatiente bren = creador.crear(
                "Hechicero del Vacío", "Bren", palacioDelVacio);
        Combatiente thorin = creador.crear(
                "Cultivador de la Espada", "Thorin", escuelaDeLaEspada);
        Combatiente zhen = creador.crear(
                "Monje del Cuerpo Celestial", "Zhen", monasterio);

        Torneo torneo = new Torneo();
        // Registrar al Heraldo y al Cronista como interesados.
        HeraldoImperial heraldo = new HeraldoImperial();
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        torneo.agregarObservador(heraldo);
        torneo.agregarObservador(cronista);

        Pareja primera = new Pareja(1, aurelio, nebula);
        torneo.inscribir(primera);
        torneo.inscribir(new Pareja(2, kael, darian));
        torneo.inscribir(new Pareja(3, lin, bren));
        torneo.inscribir(new Pareja(4, thorin, zhen));

        // Iniciar torneo
        torneo.iniciar();

        // El Heraldo anuncia a los Dos Santos Supremos; después, el main
        // le pide la tabla al Cronista y el Heraldo la presenta.
        heraldo.presentarTablaPosiciones(cronista.obtenerTablaPosiciones());
    }
}
