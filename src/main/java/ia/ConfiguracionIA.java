package ia;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfiguracionIA {

    private static final Path ARCHIVO_LLAVE = Path.of("apikey.txt");

    public static String obtenerApiKey() {
        String variable = System.getenv("ANTHROPIC_API_KEY");
        if (variable != null && !variable.isBlank()) {
            return variable.trim();
        }

        if (Files.exists(ARCHIVO_LLAVE)) {
            try {
                String contenido = Files.readString(ARCHIVO_LLAVE).trim();
                if (!contenido.isBlank()) {
                    return contenido;
                }
            } catch (IOException ignored) {
            }
        }

        return null;
    }
}
