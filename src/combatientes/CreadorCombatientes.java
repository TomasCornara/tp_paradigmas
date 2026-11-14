package combatientes;

import combatientes.tipos.TipoCombatiente;

public class CreadorCombatientes {
    private int siguienteId = 1;

    public CreadorCombatientes() {
    }

    public CreadorCombatientes(int idInicial) {
        if (idInicial <= 0) {
            throw new IllegalArgumentException("El identificador inicial debe ser positivo");
        }
        this.siguienteId = idInicial;
    }

    public Combatiente crear(TipoCombatiente tipo, String nombre, Escuela escuela) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de combatiente no puede ser nulo");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo");
        }
        if (escuela == null) {
            throw new IllegalArgumentException("La escuela no puede ser nula");
        }

        Combatiente nuevo = tipo.crear(siguienteId, nombre, escuela);
        siguienteId++;
        return nuevo;
    }
}
