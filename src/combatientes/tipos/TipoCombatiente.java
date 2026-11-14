package combatientes.tipos;

import combatientes.Combatiente;
import combatientes.Escuela;

public enum TipoCombatiente {
    CULTIVADOR_DE_LA_ESPADA("Cultivador de la Espada") {
        @Override
        public Combatiente crear(int id, String nombre, Escuela escuela) {
            return new CultivadorDeLaEspada(id, nombre, escuela);
        }
    },
    MONJE_DEL_CUERPO_CELESTIAL("Monje del Cuerpo Celestial") {
        @Override
        public Combatiente crear(int id, String nombre, Escuela escuela) {
            return new MonjeDelCuerpoCelestial(id, nombre, escuela);
        }
    },
    HECHICERO_DEL_VACIO("Hechicero del Vacío") {
        @Override
        public Combatiente crear(int id, String nombre, Escuela escuela) {
            return new HechiceroDelVacio(id, nombre, escuela);
        }
    };

    private final String etiqueta;

    TipoCombatiente(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String etiqueta() {
        return etiqueta;
    }

    public abstract Combatiente crear(int id, String nombre, Escuela escuela);
}
