package equipamiento;

import java.io.Serial;

public class ReglaEquipamientoException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ReglaEquipamientoException(String mensaje) {
        super(mensaje);
    }
}
