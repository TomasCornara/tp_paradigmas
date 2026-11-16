package combatientes;

public enum TipoCombatiente {
    CULTIVADOR_DE_LA_ESPADA("Cultivador de la Espada"),
    MONJE_DEL_CUERPO_CELESTIAL("Monje del Cuerpo Celestial"),
    HECHICERO_DEL_VACIO("Hechicero del Vacío");

    private final String etiqueta;

    TipoCombatiente(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String etiqueta() {
        return etiqueta;
    }
}
