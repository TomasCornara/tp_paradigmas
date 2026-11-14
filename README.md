# Torneo de Cultivadores por Parejas - Paradigmas de Programación

Simulador de torneo de artes marciales y cultivación basado en combates por parejas por eliminación directa, desarrollado bajo los principios de la Programación Orientada a Objetos (POO) en Java.

---

## 📋 Descripción General

El sistema modela un torneo donde diversas **Escuelas** inscriben a sus **Combatientes** organizados en **Parejas** de dos integrantes. Los enfrentamientos se desarrollan por rondas de eliminación directa en combates por asaltos por turnos. 

El proyecto implementa reglas de combate, límites de recursos (energía y usos de técnicas), restricciones de equipamiento, efectos temporales (veneno, regeneración) y un sistema de eventos desacoplado mediante el patrón **Observer**.

---

## 📁 Estructura del Proyecto

```text
tp_paradigmas/
├── src/
│   ├── Main.java                              # Punto de entrada y simulación del torneo
│   ├── acontecimientos/                       # Patrón Observer: eventos y observadores
│   │   ├── Acontecimiento.java                # Interfaz base de eventos
│   │   ├── AccionAcontecimiento.java          # Evento de acción (técnica o ataque básico)
│   │   ├── EliminacionAcontecimiento.java     # Evento cuando un combatiente cae
│   │   ├── EmisorAcontecimientos.java         # Sujeto observable (Subject)
│   │   ├── EntradaTablaPosiciones.java        # Fila de estadísticas de la tabla final
│   │   ├── Heraldo.java                       # Observador que relata combates en consola
│   │   ├── InicioCombateAcontecimiento.java   # Evento de apertura de combate
│   │   ├── ObservadorAcontecimientos.java     # Interfaz Observer
│   │   ├── ProclamacionAcontecimiento.java    # Evento de consagración del campeón
│   │   ├── ResultadoCombateAcontecimiento.java# Evento de resultado y vidas finales
│   │   └── CronistaDeLaSecta.java             # Observador que arma el historial y tabla
│   ├── combatientes/                          # Lógica del combatiente y escuelas
│   │   ├── Combatiente.java                   # Clase base abstracta de combatiente
│   │   ├── CreadorCombatientes.java           # Fábrica de combatientes con ID secuencial
│   │   ├── Escuela.java                       # Escuela con técnicas y kit inicial
│   │   └── tipos/                             # Especializaciones por herencia
│   │       ├── CultivadorDeLaEspada.java      # Especialista en daño (+30% de ataque)
│   │       ├── HechiceroDelVacio.java         # Especialista en energía (-8 costo)
│   │       ├── MonjeDelCuerpoCelestial.java   # Especialista defensivo (mitiga 30%)
│   │       └── TipoCombatiente.java           # Enum descriptor y fábrica
│   ├── equipamiento/                          # Sistema de ítems e inventario
│   │   ├── CategoriaEquipamiento.java         # Enum (ARMA, ARMADURA, AMULETO, RELIQUIA)
│   │   ├── Equipamiento.java                  # Interfaz base de equipamiento
│   │   ├── ReglaEquipamientoException.java    # Excepción por violar reglas de ítems
│   │   └── items/
│   │       ├── AnilloEspiritual.java          # +30 Energía máxima
│   │       ├── ArmaduraDorada.java            # +8 Defensa
│   │       ├── EspadaCelestial.java           # +10 Daño
│   │       └── SelloDelFenix.java             # +40 Vida máxima
│   ├── tecnicas/                              # Técnicas y habilidades
│   │   ├── ContextoEjecucion.java             # Contexto (usuario y objetivo)
│   │   ├── Tecnica.java                       # Interfaz de técnica ejecutable
│   │   ├── TipoTecnica.java                   # Enum (OFENSIVA, DEFENSIVA, RECUPERACION)
│   │   ├── inmediatas/                        # Habilidades de efecto directo
│   │   │   ├── CorteDeLosMilAmaneceres.java   # Ofensiva con daño potenciado
│   │   │   ├── DominioDeLaNada.java           # Ofensiva del vacío
│   │   │   ├── GolpeVenenoso.java             # Ofensiva que aplica veneno
│   │   │   ├── MeditacionDelInmortal.java     # Recuperación directa de vida
│   │   │   ├── MuroDelCuerpoDorado.java       # Defensiva (escudo para asaltos)
│   │   │   ├── RegeneracionVital.java         # Aplica regeneración progresiva
│   │   │   └── RugidoDeLaBestiaPrimordial.java# Ofensiva de daño masivo
│   │   └── temporales/                        # Estados persistentes por turnos
│   │       ├── EfectoTemporal.java            # Clase base de efecto por turnos
│   │       ├── Regeneracion.java              # Cura vida al inicio del turno
│   │       └── Veneno.java                    # Daña al inicio del turno
│   └── torneo/                                # Dominio del torneo y combates
│       ├── Combate.java                       # Lógica de un combate entre 2 parejas
│       ├── Pareja.java                        # Dúo de combatientes y regla del compañero
│       └── Torneo.java                        # Llaves, rondas y eliminación directa
└── test/
    └── torneo/                                # Batería de pruebas unitarias (JUnit 5)
        ├── CombateYTorneoTest.java
        ├── CombatienteTest.java
        ├── EquipamientoTest.java
        ├── EscuelaTest.java
        ├── ParejaYReglaCompanieroTest.java
        └── TecnicaYAccionTest.java
```

---

## ⚔️ Reglas de Negocio y Dominio

### 1. Combatientes
- **Valores Base**: Cada combatiente inicia con **100 de Vida Máxima** y **100 de Energía Máxima**.
- **Tipos de Combatiente**:
  - **Cultivador de la Espada**: Aplica un **30% extra** al daño base de sus ataques.
  - **Hechicero del Vacío**: Reduce en **8 puntos** el costo de energía de sus técnicas (costo mínimo: 1).
  - **Monje del Cuerpo Celestial**: Mitiga un **30% del daño entrante**.

### 2. Equipamiento
- Un combatiente puede portar hasta un **máximo de 3 objetos**.
- No puede portar dos equipamientos de la **misma categoría** (`ARMA`, `ARMADURA`, `AMULETO`, `RELIQUIA`).
- Equipar un objeto que incrementa la vida o energía máxima no incrementa el valor actual de forma inmediata.
- Si se desequipa un objeto y los puntos actuales superan el nuevo límite máximo, se recortan al nuevo tope.
- El equipamiento puede ajustarse entre rondas del torneo.

### 3. Técnicas y Limitaciones
- **Tipos de técnica**: `OFENSIVA` (se lanza sobre un rival), `DEFENSIVA` y `RECUPERACION` (se aplican sobre el propio ejecutor).
- **Límite de Usos**: Cada técnica asignada al combatiente dispone de un máximo de **5 usos para todo el torneo**. Agotados los 5 usos, el combatiente no podrá volver a usarla en ningún combate posterior.
- **Regla del Compañero**: En un mismo combate, si un integrante de la pareja utiliza una técnica, su compañero **no puede utilizar la misma técnica** mientras el primero siga en pie y conserve usos de ella. Si el primero cae o agota sus usos, el compañero queda habilitado. Esta restricción se reinicia al comenzar un nuevo combate.
- **Ataque Básico**: Si un combatiente no puede ejecutar técnicas (falta de energía, usos agotados o bloqueo por regla del compañero), ejecuta un ataque básico (10 de daño base, costo 0 de energía y usos ilimitados).

### 4. Dinámica del Combate
- Cada asalto, los combatientes en pie recuperan **10 puntos de energía**.
- Se juegan turnos completos por pareja:
  - Inicia la pareja con **menor ID**.
  - Dentro de la pareja, actúa primero el combatiente con **menor ID**.
- Al inicio de su turno, se procesan los **efectos temporales** (Veneno / Regeneración).
- **Elección de acción**:
  1. Si ha perdido vida, prioriza una técnica de `RECUPERACION`.
  2. Si no tiene defensa activa, busca una técnica `DEFENSIVA`.
  3. En otro caso, elige la técnica `OFENSIVA` con mayor cantidad de usos restantes.
  4. De no poder ejecutar técnicas, ejecuta un ataque básico.
- **Selección de objetivo**: Ataca al rival en pie con **menor vida actual**. En caso de empate, elige al de menor ID.
- **Criterio de Victoria**:
  - Gana la pareja que mantenga al menos un combatiente en pie cuando la otra quede completamente derrotada.
  - Si se alcanza el límite de asaltos (por defecto 30), gana la pareja con **mayor vida total combinada**. En caso de empate exacto, prevalece la pareja con menor ID.
- Al terminar un combate, la vida y la energía de los combatientes (incluso los caídos) se restablecen al 100% para la siguiente ronda, pero los usos de técnicas consumidos **se mantienen gastados**.

### 5. Estructura del Torneo
- Se disputa por llaves de eliminación directa con exactamente **4 u 8 parejas**.
- Los cruces iniciales siguen el orden de inscripción.
- Entre rondas, el torneo ofrece un callback (`alPasarRonda`) para que las parejas clasificadas puedan modificar o redistribuir su equipamiento.
- Al finalizar el torneo se proclama a la pareja campeona y se calcula la **Tabla de Posiciones Final**, ordenada por:
  1. Mayor última ronda alcanzada.
  2. Mayor vida total final en su último combate.
  3. Menor identificador de pareja.

---

## 🏛️ Conceptos de Diseño Aplicados

- **Herencia y Polimorfismo**:
  - Jerarquía de `Combatiente` y sus especializaciones (`CultivadorDeLaEspada`, `HechiceroDelVacio`, `MonjeDelCuerpoCelestial`).
  - Polimorfismo en las implementaciones de `Tecnica`, `Equipamiento` y `EfectoTemporal`.
- **Encapsulamiento y Modelado Puro**:
  - Estado protegido y privado con validaciones en constructores y métodos de dominio.
  - Modelado de relaciones mediante clases de dominio simples y colecciones estándar (`List`, `ArrayList`), sin dependencias de estructuras complejas innecesarias (`HashMap`).
- **Patrón Observer**:
  - `EmisorAcontecimientos` notifica eventos desacoplados (`Acontecimiento`) a observadores suscritos.
  - `Heraldo` reporta la narrativa del combate en tiempo real.
  - `CronistaDeLaSecta` almacena el histórico y computa las posiciones.
- **Separación de Responsabilidades**:
  - Desacoplamiento total entre las reglas del torneo, la mecánica del combate por asaltos y la presentación en consola.

---

## 🚀 Compilación y Ejecución

### Prerrequisitos
- **Java JDK 17** o superior instalado.
- Opcional: **Eclipse IDE** o **VS Code** con soporte para Java.

### 1. Compilación desde la Terminal
Para compilar todo el código fuente hacia la carpeta `bin/`:

```powershell
# En Windows (PowerShell):
$srcFiles = Get-ChildItem -Path "src" -Filter *.java -Recurse | Select-Object -ExpandProperty FullName
javac -d bin -cp src $srcFiles
```

### 2. Ejecutar la Simulación Principal
Para correr el torneo de ejemplo preconfigurado en `Main`:

```powershell
java -cp bin Main
```

### 3. Ejecutar las Pruebas Unitarias
El proyecto cuenta con **51 pruebas unitarias** que validan la totalidad de las reglas de negocio. En Eclipse o VS Code pueden ejecutarse haciendo clic derecho sobre la carpeta `test/` y seleccionando **Run As -> JUnit Test**.

---

## 👥 Materia
- **Materia**: Paradigmas de Programación
- **Universidad**: Universidad Nacional de La Matanza (UNLaM)
