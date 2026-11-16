package combatientes;

import java.util.HashMap;
import java.util.Map;

public class CreadorCombatientes {
    private int siguienteId = 1;
    private final Map<String, FabricaCombatiente> fabricas = new HashMap<>();

    @FunctionalInterface
    public interface FabricaCombatiente {
        Combatiente crear(int id, String nombre, Escuela escuela);
    }

    public CreadorCombatientes() {
        this(1);
    }

    public CreadorCombatientes(int idInicial) {
        if (idInicial <= 0) {
            throw new IllegalArgumentException("El identificador inicial debe ser positivo");
        }
        this.siguienteId = idInicial;
        registrarTiposPorDefecto();
    }

    private void registrarTiposPorDefecto() {
        registrarTipo("Cultivador de la Espada", CultivadorDeLaEspada::new);
        registrarTipo("Monje del Cuerpo Celestial", MonjeDelCuerpoCelestial::new);
        registrarTipo("Hechicero del Vacío", HechiceroDelVacio::new);
        registrarTipo("Hechicero del Vacio", HechiceroDelVacio::new);
    }

    public void registrarTipo(String nombreTipo, FabricaCombatiente fabrica) {
        if (nombreTipo == null || nombreTipo.isBlank() || fabrica == null) {
            throw new IllegalArgumentException("El nombre del tipo y la fábrica no pueden ser nulos ni vacíos");
        }
        fabricas.put(nombreTipo.trim().toLowerCase(), fabrica);
    }

    public Combatiente crear(String tipo, String nombre, Escuela escuela) {
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("El tipo de combatiente no puede ser nulo");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo");
        }
        if (escuela == null) {
            throw new IllegalArgumentException("La escuela no puede ser nula");
        }

        FabricaCombatiente fabrica = fabricas.get(tipo.trim().toLowerCase());
        if (fabrica == null) {
            throw new IllegalArgumentException("Tipo de combatiente desconocido: " + tipo);
        }

        Combatiente nuevo = fabrica.crear(siguienteId, nombre, escuela);
        siguienteId++;
        return nuevo;
    }

    public Combatiente crear(TipoCombatiente tipo, String nombre, Escuela escuela) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de combatiente no puede ser nulo");
        }
        return crear(tipo.etiqueta(), nombre, escuela);
    }
}
