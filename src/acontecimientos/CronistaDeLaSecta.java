package acontecimientos;

import torneo.Pareja;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private final List<InfoPareja> tablaParejas = new ArrayList<>();

    private InfoPareja obtenerOCrear(Pareja pareja) {
        for (InfoPareja info : tablaParejas) {
            if (info.pareja.equals(pareja)) {
                return info;
            }
        }
        InfoPareja nuevo = new InfoPareja(pareja);
        tablaParejas.add(nuevo);
        return nuevo;
    }

    @Override
    public void notificar(Acontecimiento acontecimiento) {
        historial.add(acontecimiento);

        if (acontecimiento instanceof InicioCombateAcontecimiento) {
            InicioCombateAcontecimiento inicio = (InicioCombateAcontecimiento) acontecimiento;
            obtenerOCrear(inicio.pareja1()).ultimaRonda = inicio.ronda();
            obtenerOCrear(inicio.pareja2()).ultimaRonda = inicio.ronda();
        } else if (acontecimiento instanceof ResultadoCombateAcontecimiento) {
            ResultadoCombateAcontecimiento res = (ResultadoCombateAcontecimiento) acontecimiento;
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
        for (InfoPareja info : tablaParejas) {
            tabla.add(new EntradaTablaPosiciones(info.pareja, info.ultimaRonda, info.vidaFinal));
        }
        Collections.sort(tabla);
        return Collections.unmodifiableList(tabla);
    }
}
