package acontecimientos;

import java.util.ArrayList;
import java.util.List;

public class EmisorAcontecimientos {
    private final List<ObservadorAcontecimientos> observadores = new ArrayList<>();

    public void registrarObservador(ObservadorAcontecimientos observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void notificar(Acontecimiento acontecimiento) {
        List<ObservadorAcontecimientos> copia = new ArrayList<>(observadores);
        for (ObservadorAcontecimientos observador : copia) {
            observador.notificar(acontecimiento);
        }
    }
}
