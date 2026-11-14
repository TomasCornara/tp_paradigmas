package torneo;

import acontecimientos.EmisorAcontecimientos;
import acontecimientos.ObservadorAcontecimientos;
import acontecimientos.ProclamacionAcontecimiento;
import combatientes.Combatiente;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class Torneo {
    private final List<Pareja> parejas = new ArrayList<>();
    private final EmisorAcontecimientos emisor = new EmisorAcontecimientos();
    private int siguienteIdPareja = 1;

    public void agregarObservador(ObservadorAcontecimientos observador) {
        emisor.registrarObservador(observador);
    }

    public Pareja inscribir(Combatiente primero, Combatiente segundo) {
        Pareja pareja = new Pareja(siguienteIdPareja, primero, segundo);
        inscribir(pareja);
        siguienteIdPareja++;
        return pareja;
    }

    public void inscribir(Pareja pareja) {
        if (pareja == null) {
            throw new IllegalArgumentException("La pareja a inscribir no puede ser nula");
        }
        if (parejas.size() >= 8) {
            throw new IllegalStateException("El torneo admite como máximo 8 parejas");
        }

        if (parejas.contains(pareja)) {
            throw new IllegalArgumentException("La pareja #" + pareja.id() + " ya se encuentra registrada en el torneo");
        }

        for (Pareja inscripta : parejas) {
            for (Combatiente c : pareja.combatientes()) {
                if (inscripta.combatientes().contains(c)) {
                    throw new IllegalArgumentException("El combatiente " + c.nombre() + " ya está inscripto en otra pareja");
                }
            }
        }

        parejas.add(pareja);
    }

    public Pareja iniciar() {
        return iniciar(new Consumer<List<Pareja>>() {
            @Override
            public void accept(List<Pareja> ronda) {
            }
        });
    }

    public Pareja iniciar(Consumer<List<Pareja>> alPasarRonda) {
        if (alPasarRonda == null) {
            throw new IllegalArgumentException("El callback de paso de ronda no puede ser nulo");
        }
        if (parejas.size() != 4 && parejas.size() != 8) {
            throw new IllegalStateException("El torneo se debe jugar únicamente con 4 u 8 parejas. Registradas: " + parejas.size());
        }

        List<Pareja> rondaActual = new ArrayList<>(parejas);
        int numeroRonda = 1;

        while (rondaActual.size() > 1) {
            List<Pareja> ganadorasRonda = new ArrayList<>();

            for (int i = 0; i < rondaActual.size(); i += 2) {
                Pareja p1 = rondaActual.get(i);
                Pareja p2 = rondaActual.get(i + 1);

                Combate combate = new Combate(p1, p2, numeroRonda, emisor);
                Pareja ganadora = combate.resolver();
                ganadorasRonda.add(ganadora);
            }

            rondaActual = ganadorasRonda;
            if (rondaActual.size() > 1) {
                alPasarRonda.accept(Collections.unmodifiableList(new ArrayList<>(rondaActual)));
            }
            numeroRonda++;
        }

        Pareja campeona = rondaActual.get(0);
        emisor.notificar(new ProclamacionAcontecimiento(campeona));
        return campeona;
    }
}
