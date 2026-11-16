package acontecimientos;

import combatientes.Combatiente;

import java.util.List;

public class HeraldoImperial implements ObservadorAcontecimientos {

    @Override
    public void notificar(Acontecimiento acontecimiento) {
        if (acontecimiento instanceof InicioCombateAcontecimiento) {
            System.out.println("\n=======================================================");
            System.out.println("  " + acontecimiento.descripcion());
            System.out.println("=======================================================");
        } else if (acontecimiento instanceof ProclamacionAcontecimiento) {
            System.out.println("\n*******************************************************");
            System.out.println("  " + acontecimiento.descripcion());
            System.out.println("*******************************************************\n");
        } else if (acontecimiento instanceof ResultadoCombateAcontecimiento) {
            System.out.println(">> " + acontecimiento.descripcion());
        } else if (acontecimiento instanceof EliminacionAcontecimiento) {
            System.out.println("   [!] " + acontecimiento.descripcion());
        } else {
            System.out.println("   * " + acontecimiento.descripcion());
        }
    }

    public void presentarAjusteEquipamiento(Combatiente combatiente, String descripcion) {
        StringBuilder kit = new StringBuilder();
        for (int i = 0; i < combatiente.equipamiento().size(); i++) {
            if (i > 0) {
                kit.append(", ");
            }
            kit.append(combatiente.equipamiento().get(i).nombre());
        }
        String kitTexto = kit.length() == 0 ? "ninguno" : kit.toString();

        System.out.println("\n-------------------------------------------------------");
        System.out.println("  AJUSTE DE EQUIPAMIENTO - " + combatiente.nombre());
        System.out.println("  " + descripcion);
        System.out.println("  Kit: " + kitTexto
                + " | Vida máx: " + combatiente.vidaMaxima()
                + " | Energía máx: " + combatiente.energiaMaxima());
        System.out.println("-------------------------------------------------------");
    }

    public void presentarTablaPosiciones(List<EntradaTablaPosiciones> tabla) {
        System.out.println("=======================================================");
        System.out.println("               TABLA DE POSICIONES FINAL               ");
        System.out.println("=======================================================");
        System.out.printf("%-6s | %-6s | %-12s | %-30s%n", "Pos", "Pareja", "Últ. Ronda", "Integrantes");
        System.out.println("-------------------------------------------------------");
        int pos = 1;
        for (EntradaTablaPosiciones entrada : tabla) {
            System.out.printf("%-6d | #%-5d | %-12d | %-30s (Vida final: %d)%n",
                    pos++,
                    entrada.pareja().id(),
                    entrada.ultimaRonda(),
                    entrada.pareja().nombreCompuesto(),
                    entrada.vidaTotalFinal());
        }
        System.out.println("=======================================================\n");
    }
}
