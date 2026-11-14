import acontecimientos.CronistaDeLaSecta;
import acontecimientos.Heraldo;
import combatientes.Combatiente;
import combatientes.CreadorCombatientes;
import combatientes.Escuela;
import combatientes.tipos.TipoCombatiente;
import equipamiento.CategoriaEquipamiento;
import equipamiento.ReglaEquipamientoException;
import equipamiento.items.AnilloEspiritual;
import equipamiento.items.ArmaduraDorada;
import equipamiento.items.EspadaCelestial;
import equipamiento.items.SelloDelFenix;
import torneo.*;
import tecnicas.inmediatas.CorteDeLosMilAmaneceres;
import tecnicas.inmediatas.DominioDeLaNada;
import tecnicas.inmediatas.GolpeVenenoso;
import tecnicas.inmediatas.MeditacionDelInmortal;
import tecnicas.inmediatas.MuroDelCuerpoDorado;
import tecnicas.inmediatas.RegeneracionVital;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<tecnicas.Tecnica> tecnicasEspada = new ArrayList<>();
        tecnicasEspada.add(new CorteDeLosMilAmaneceres());
        List<equipamiento.Equipamiento> itemsEspada = new ArrayList<>();
        itemsEspada.add(new EspadaCelestial());
        Escuela escuelaDeLaEspada = new Escuela("Escuela de la Espada Celestial", tecnicasEspada, itemsEspada);

        List<tecnicas.Tecnica> tecnicasVacio = new ArrayList<>();
        tecnicasVacio.add(new GolpeVenenoso());
        tecnicasVacio.add(new DominioDeLaNada());
        tecnicasVacio.add(new MeditacionDelInmortal());
        List<equipamiento.Equipamiento> itemsVacio = new ArrayList<>();
        itemsVacio.add(new AnilloEspiritual());
        Escuela palacioDelVacio = new Escuela("Palacio del Vacío", tecnicasVacio, itemsVacio);

        List<tecnicas.Tecnica> tecnicasMonasterio = new ArrayList<>();
        tecnicasMonasterio.add(new MuroDelCuerpoDorado());
        tecnicasMonasterio.add(new RegeneracionVital());
        List<equipamiento.Equipamiento> itemsMonasterio = new ArrayList<>();
        itemsMonasterio.add(new ArmaduraDorada());
        Escuela monasterio = new Escuela("Monasterio del Cuerpo Dorado", tecnicasMonasterio, itemsMonasterio);

        CreadorCombatientes creador = new CreadorCombatientes();
        Combatiente aurelio = creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Aurelio", escuelaDeLaEspada);
        Combatiente nebula = creador.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "Nébula", palacioDelVacio);
        Combatiente kael = creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "Kael", palacioDelVacio);
        Combatiente darian = creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Darian", escuelaDeLaEspada);

        Combatiente lin = creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "Lin", monasterio);
        Combatiente bren = creador.crear(TipoCombatiente.HECHICERO_DEL_VACIO, "Bren", palacioDelVacio);
        Combatiente thorin = creador.crear(TipoCombatiente.CULTIVADOR_DE_LA_ESPADA, "Thorin", escuelaDeLaEspada);
        Combatiente zhen = creador.crear(TipoCombatiente.MONJE_DEL_CUERPO_CELESTIAL, "Zhen", monasterio);

        Torneo torneo = new Torneo();
        Heraldo heraldo = new Heraldo();
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        torneo.agregarObservador(heraldo);
        torneo.agregarObservador(cronista);

        torneo.inscribir(aurelio, nebula);
        torneo.inscribir(kael, darian);
        torneo.inscribir(lin, bren);
        torneo.inscribir(thorin, zhen);

        torneo.iniciar(clasifican -> ajustarEquipamiento(heraldo, clasifican, aurelio, darian, nebula));

        heraldo.presentarTablaPosiciones(cronista.obtenerTablaPosiciones());
    }

    private static void ajustarEquipamiento(Heraldo heraldo, List<Pareja> clasifican,
                                            Combatiente aurelio, Combatiente darian, Combatiente nebula) {
        if (sigueEnCompetencia(aurelio, clasifican)) {
            try {
                aurelio.desequipar(CategoriaEquipamiento.ARMA);
                heraldo.presentarAjusteEquipamiento(aurelio, "Quita la Espada Celestial.");
                aurelio.equipar(new SelloDelFenix());
                heraldo.presentarAjusteEquipamiento(aurelio, "Se equipa el Sello del Fénix (+40 vida máxima).");
            } catch (ReglaEquipamientoException e) {
                heraldo.presentarAjusteEquipamiento(aurelio, "Cambio rechazado: " + e.getMessage());
            }
        }

        if (sigueEnCompetencia(darian, clasifican)) {
            try {
                darian.equipar(new ArmaduraDorada());
                heraldo.presentarAjusteEquipamiento(darian, "Se equipa la Armadura Dorada (+8 defensa).");
            } catch (ReglaEquipamientoException e) {
                heraldo.presentarAjusteEquipamiento(darian, "Cambio rechazado: " + e.getMessage());
            }

            try {
                darian.equipar(new EspadaCelestial());
                heraldo.presentarAjusteEquipamiento(darian, "Se equipa la Espada Celestial (+10 daño).");
            } catch (ReglaEquipamientoException e) {
                heraldo.presentarAjusteEquipamiento(darian, "Cambio rechazado: " + e.getMessage());
            }
        }

        if (sigueEnCompetencia(nebula, clasifican)) {
            try {
                nebula.equipar(new AnilloEspiritual());
                heraldo.presentarAjusteEquipamiento(nebula, "Se equipa el Anillo Espiritual (+30 energía máxima).");
            } catch (ReglaEquipamientoException e) {
                heraldo.presentarAjusteEquipamiento(nebula, "Cambio rechazado: " + e.getMessage());
            }
        }
    }

    private static boolean sigueEnCompetencia(Combatiente combatiente, List<Pareja> clasifican) {
        for (Pareja p : clasifican) {
            if (p.combatientes().contains(combatiente)) {
                return true;
            }
        }
        return false;
    }
}
