# Torneo de Cultivadores por Parejas - Paradigmas de Programación

Simulador de torneo de artes marciales y cultivación basado en combates por parejas por eliminación directa, desarrollado bajo los principios de la Programación Orientada a Objetos (POO) en Java.

---

## 📋 Descripción General

El sistema modela un torneo donde diversas **Escuelas** inscriben a sus **Combatientes** organizados en **Parejas** de dos integrantes. Los enfrentamientos se desarrollan por rondas de eliminación directa en combates por asaltos por turnos. 

El proyecto implementa reglas de combate, límites de recursos (energía y usos de técnicas), restricciones de equipamiento, efectos temporales (veneno, regeneración) y un sistema de eventos desacoplado mediante el patrón **Observer**.

---

## Reglas

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

## Generales

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
