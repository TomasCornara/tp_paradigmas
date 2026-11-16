package combatientes;

import equipamiento.CategoriaEquipamiento;
import equipamiento.Equipamiento;
import equipamiento.ReglaEquipamientoException;
import tecnicas.Tecnica;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Escuela {
    private final String nombre;
    private final List<Tecnica> tecnicas;
    private final List<Equipamiento> equipamientos;

    public Escuela(String nombre, List<Tecnica> tecnicas, List<Equipamiento> equipamientos) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("La escuela debe tener nombre");
        }
        if (tecnicas == null) {
            throw new IllegalArgumentException("Las técnicas no pueden ser nulas");
        }
        if (equipamientos == null) {
            throw new IllegalArgumentException("Los equipamientos no pueden ser nulos");
        }
        validarKitInicial(equipamientos);
        this.nombre = nombre;
        this.tecnicas = Collections.unmodifiableList(new ArrayList<>(tecnicas));
        this.equipamientos = Collections.unmodifiableList(new ArrayList<>(equipamientos));
    }

    private static void validarKitInicial(List<Equipamiento> kit) {
        if (kit.size() > Equipamiento.MAX_EQUIPAMIENTOS) {
            throw new ReglaEquipamientoException("El equipamiento inicial admite hasta "
                    + Equipamiento.MAX_EQUIPAMIENTOS + " objetos");
        }
        List<CategoriaEquipamiento> categorias = new ArrayList<>();
        for (Equipamiento eq : kit) {
            if (categorias.contains(eq.categoria())) {
                throw new ReglaEquipamientoException(
                        "El equipamiento inicial no puede repetir la categoría " + eq.categoria());
            }
            categorias.add(eq.categoria());
        }
    }

    public String nombre() {
        return nombre;
    }

    public List<Tecnica> tecnicas() {
        return tecnicas;
    }

    public List<Equipamiento> equipamientos() {
        return equipamientos;
    }
}
