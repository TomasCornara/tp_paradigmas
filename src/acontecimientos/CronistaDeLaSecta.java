package acontecimientos;

import torneo.Pareja;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CronistaDeLaSecta implements ObservadorAcontecimientos {
    private final List<Acontecimiento> historial = new ArrayList<>();

    private static class InfoPareja {
        private final Pareja pareja;
        private int ultimaRonda;
        private int vidaFinal;

        public InfoPareja(Pareja pareja) {
            this.pareja = pareja;
        }
    }

    private final Map<Pareja, InfoPareja> tablaParejas = new LinkedHashMap<>();

    private InfoPareja obtenerOCrear(Pareja pareja) {
        return tablaParejas.computeIfAbsent(pareja, InfoPareja::new);
    }

    @Override
    public void notificar(Acontecimiento acontecimiento) {
        historial.add(acontecimiento);

        if (acontecimiento instanceof InicioCombateAcontecimiento inicio) {
            obtenerOCrear(inicio.pareja1()).ultimaRonda = inicio.ronda();
            obtenerOCrear(inicio.pareja2()).ultimaRonda = inicio.ronda();
        } else if (acontecimiento instanceof ResultadoCombateAcontecimiento res) {
            InfoPareja g = obtenerOCrear(res.ganadora());
            g.ultimaRonda = res.ronda();
            g.vidaFinal = res.vidaTotalGanadora();

            InfoPareja p = obtenerOCrear(res.perdedora());
            p.ultimaRonda = res.ronda();
            p.vidaFinal = res.vidaTotalPerdedora();
        }
    }

    public List<Acontecimiento> historial() {
        return Collections.unmodifiableList(historial);
    }

    public List<EntradaTablaPosiciones> obtenerTablaPosiciones() {
        List<EntradaTablaPosiciones> tabla = new ArrayList<>();
        for (InfoPareja info : tablaParejas.values()) {
            tabla.add(new EntradaTablaPosiciones(info.pareja, info.ultimaRonda, info.vidaFinal));
        }
        Collections.sort(tabla);
        return Collections.unmodifiableList(tabla);
    }
}
