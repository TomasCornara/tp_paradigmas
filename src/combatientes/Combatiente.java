package combatientes;

import acontecimientos.AccionAcontecimiento;
import acontecimientos.EliminacionAcontecimiento;
import acontecimientos.EmisorAcontecimientos;
import combatientes.tipos.TipoCombatiente;
import equipamiento.CategoriaEquipamiento;
import equipamiento.Equipamiento;
import equipamiento.ReglaEquipamientoException;
import torneo.Pareja;
import tecnicas.Tecnica;
import tecnicas.TipoTecnica;
import tecnicas.temporales.EfectoTemporal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Combatiente {
    public static final int VIDA_BASE_MAXIMA = 100;
    public static final int ENERGIA_BASE_MAXIMA = 100;

    private final int id;
    private final String nombre;
    private final Escuela escuela;

    private int vidaActual;
    private int energiaActual;

    private final List<Equipamiento> equipamientos = new ArrayList<>();

    private static class UsoTecnicaRestante {
        private final Tecnica tecnica;
        private int usos;

        public UsoTecnicaRestante(Tecnica tecnica, int usos) {
            this.tecnica = tecnica;
            this.usos = usos;
        }
    }

    private static class TecnicaActiva {
        private final Tecnica tecnica;
        private final int reduccionDanio;

        public TecnicaActiva(Tecnica tecnica, int reduccionDanio) {
            this.tecnica = tecnica;
            this.reduccionDanio = reduccionDanio;
        }
    }

    private final List<UsoTecnicaRestante> usosRestantes = new ArrayList<>();
    private final List<TecnicaActiva> tecnicasActivas = new ArrayList<>();
    private final List<EfectoTemporal> efectosTemporales = new ArrayList<>();

    public Combatiente(int id, String nombre, Escuela escuela) {
        if (id <= 0) {
            throw new IllegalArgumentException("El identificador del combatiente debe ser positivo");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El combatiente necesita nombre");
        }
        if (escuela == null) {
            throw new IllegalArgumentException("La escuela no puede ser nula");
        }
        this.id = id;
        this.nombre = nombre;
        this.escuela = escuela;

        for (Equipamiento eq : escuela.equipamientos()) {
            equipar(eq);
        }

        for (Tecnica t : escuela.tecnicas()) {
            this.usosRestantes.add(new UsoTecnicaRestante(t, 5));
        }

        this.vidaActual = vidaMaxima();
        this.energiaActual = energiaMaxima();
    }

    public int id() {
        return id;
    }

    public String nombre() {
        return nombre;
    }

    public Escuela escuela() {
        return escuela;
    }

    public int vidaActual() {
        return vidaActual;
    }

    public int energiaActual() {
        return energiaActual;
    }

    public int vidaMaxima() {
        int bono = 0;
        for (Equipamiento eq : equipamientos) {
            bono += eq.modificadorVidaMaxima();
        }
        return VIDA_BASE_MAXIMA + bono;
    }

    public int energiaMaxima() {
        int bono = 0;
        for (Equipamiento eq : equipamientos) {
            bono += eq.modificadorEnergiaMaxima();
        }
        return ENERGIA_BASE_MAXIMA + bono;
    }

    public boolean estaEnPie() {
        return vidaActual > 0;
    }

    public List<Equipamiento> equipamiento() {
        return Collections.unmodifiableList(equipamientos);
    }

    public abstract TipoCombatiente tipo();

    public int calcularDanioAtaque(int danioBase) {
        return danioBase + bonoDanioEquipamiento();
    }

    protected int bonoDanioEquipamiento() {
        int bono = 0;
        for (Equipamiento eq : equipamientos) {
            bono += eq.modificadorDanio();
        }
        return bono;
    }

    public int calcularCostoEnergia(Tecnica tecnica) {
        return Math.max(0, tecnica.costoEnergia());
    }

    public void recibirDanio(int danioEntrante) {
        if (!estaEnPie()) return;
        int danio = danioEntrante - bonoDefensaEquipamiento() - reduccionDefensaActiva();
        this.vidaActual = Math.max(0, this.vidaActual - Math.max(0, danio));
    }

    private int bonoDefensaEquipamiento() {
        int bono = 0;
        for (Equipamiento eq : equipamientos) {
            bono += eq.modificadorDefensa();
        }
        return bono;
    }

    public void recibirDanioDirecto(int danio) {
        if (!estaEnPie()) return;
        this.vidaActual = Math.max(0, this.vidaActual - Math.max(0, danio));
    }

    public void curar(int cantidad) {
        if (!estaEnPie() || cantidad <= 0) return;
        this.vidaActual = Math.min(vidaMaxima(), this.vidaActual + cantidad);
    }

    public void consumirEnergia(int cantidad) {
        this.energiaActual = Math.max(0, this.energiaActual - Math.max(0, cantidad));
    }

    public void recuperarEnergia(int cantidad) {
        if (!estaEnPie() || cantidad <= 0) return;
        this.energiaActual = Math.min(energiaMaxima(), this.energiaActual + cantidad);
    }

    public void reducirEnergia(int cantidad) {
        this.energiaActual = Math.max(0, this.energiaActual - Math.max(0, cantidad));
    }

    public void equipar(Equipamiento objeto) {
        if (objeto == null) {
            throw new IllegalArgumentException("El equipamiento no puede ser nulo");
        }
        if (equipamientos.size() >= Equipamiento.MAX_EQUIPAMIENTOS) {
            throw new ReglaEquipamientoException(
                    "Un combatiente puede llevar hasta " + Equipamiento.MAX_EQUIPAMIENTOS + " equipamientos");
        }
        for (Equipamiento eq : equipamientos) {
            if (eq.categoria() == objeto.categoria()) {
                throw new ReglaEquipamientoException("Ya posee un equipamiento de la categoría " + objeto.categoria());
            }
        }
        equipamientos.add(objeto);
    }

    public void desequipar(CategoriaEquipamiento categoria) {
        Equipamiento aQuitar = null;
        for (Equipamiento eq : equipamientos) {
            if (eq.categoria() == categoria) {
                aQuitar = eq;
                break;
            }
        }
        if (aQuitar == null) {
            throw new ReglaEquipamientoException("No posee equipamiento de la categoría " + categoria);
        }
        equipamientos.remove(aQuitar);
        if (this.vidaActual > vidaMaxima()) {
            this.vidaActual = vidaMaxima();
        }
        if (this.energiaActual > energiaMaxima()) {
            this.energiaActual = energiaMaxima();
        }
    }

    public int usosRestantes(Tecnica tecnica) {
        if (tecnica == null) return 0;
        for (UsoTecnicaRestante reg : usosRestantes) {
            if (reg.tecnica.equals(tecnica)) {
                return reg.usos;
            }
        }
        return 0;
    }

    public void descontarUso(Tecnica tecnica) {
        if (tecnica == null) return;
        for (UsoTecnicaRestante reg : usosRestantes) {
            if (reg.tecnica.equals(tecnica)) {
                if (reg.usos > 0) {
                    reg.usos--;
                }
                break;
            }
        }
    }

    public boolean puedeEjecutarTecnica(Tecnica tecnica, Pareja pareja) {
        if (tecnica == null) return false;
        if (usosRestantes(tecnica) <= 0) return false;
        if (this.energiaActual < calcularCostoEnergia(tecnica)) return false;
        return pareja == null || pareja.puedeUsarPorReglaCompaniero(this, tecnica);
    }

    public void activarTecnica(Tecnica tecnica, int reduccionDanioRecibido) {
        if (tecnica != null) {
            tecnicasActivas.add(new TecnicaActiva(tecnica, reduccionDanioRecibido));
        }
    }

    public boolean tieneDefensaActiva() {
        for (TecnicaActiva act : tecnicasActivas) {
            if (act.tecnica.tipo() == TipoTecnica.DEFENSIVA) {
                return true;
            }
        }
        return false;
    }

    private int reduccionDefensaActiva() {
        int suma = 0;
        for (TecnicaActiva act : tecnicasActivas) {
            if (act.tecnica.tipo() == TipoTecnica.DEFENSIVA) {
                suma += act.reduccionDanio;
            }
        }
        return suma;
    }

    public void restablecerCombate() {
        this.vidaActual = vidaMaxima();
        this.energiaActual = energiaMaxima();
        this.tecnicasActivas.clear();
        this.efectosTemporales.clear();
    }

    public void agregarEfectoTemporal(EfectoTemporal efecto) {
        if (efecto != null) {
            efectosTemporales.add(efecto);
        }
    }

    public void procesarEfectosAlInicioTurno(EmisorAcontecimientos emisor) {
        if (!estaEnPie()) return;
        List<EfectoTemporal> copia = new ArrayList<>(efectosTemporales);
        for (EfectoTemporal efecto : copia) {
            efecto.aplicarAlInicioTurno(this);
            if (emisor != null) {
                emisor.notificar(new AccionAcontecimiento(this, "Efecto " + efecto.nombre(), this,
                        "Turnos restantes: " + efecto.turnosRestantes()));
            }
            if (!estaEnPie()) {
                if (emisor != null) {
                    emisor.notificar(new EliminacionAcontecimiento(this));
                }
                break;
            }
        }
        List<EfectoTemporal> activos = new ArrayList<>();
        for (EfectoTemporal efecto : efectosTemporales) {
            if (!efecto.haTerminado()) {
                activos.add(efecto);
            }
        }
        efectosTemporales.clear();
        efectosTemporales.addAll(activos);
    }

    public Tecnica seleccionarTecnica(Pareja pareja) {
        List<Tecnica> tecnicas = escuela.tecnicas();

        if (vidaActual < vidaMaxima()) {
            for (Tecnica t : tecnicas) {
                if (t.tipo() == TipoTecnica.RECUPERACION && puedeEjecutarTecnica(t, pareja)) {
                    return t;
                }
            }
        }

        if (!tieneDefensaActiva()) {
            for (Tecnica t : tecnicas) {
                if (t.tipo() == TipoTecnica.DEFENSIVA && puedeEjecutarTecnica(t, pareja)) {
                    return t;
                }
            }
        }

        Tecnica mejorOfensiva = null;
        int maxUsos = -1;
        for (Tecnica t : tecnicas) {
            if (t.tipo() == TipoTecnica.OFENSIVA && puedeEjecutarTecnica(t, pareja)) {
                int usos = usosRestantes(t);
                if (usos > maxUsos) {
                    maxUsos = usos;
                    mejorOfensiva = t;
                }
            }
        }

        return mejorOfensiva;
    }

    public String estado() {
        return nombre + " #" + id + " [" + tipo().etiqueta() + "] (" + vidaActual + "/" + vidaMaxima() +
                " vida, " + energiaActual + "/" + energiaMaxima() + " energía)";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Combatiente)) return false;
        Combatiente other = (Combatiente) o;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return estado();
    }
}
