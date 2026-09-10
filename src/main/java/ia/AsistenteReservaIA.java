package ia;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class AsistenteReservaIA {

    public record SugerenciaReserva(
            String actividad,
            String fecha,
            String horaInicio,
            String horaFin,
            String categoria
    ) {
    }

    private static final String MODELO = "claude-haiku-4-5-20251001";
    private static final String URL_API = "https://api.anthropic.com/v1/messages";

    public SugerenciaReserva interpretar(String solicitudUsuario, String apiKey) throws Exception {
        String promptSistema = construirPromptSistema();

        String cuerpo = "{"
                + "\"model\":\"" + MODELO + "\","
                + "\"max_tokens\":200,"
                + "\"system\":\"" + escapar(promptSistema) + "\","
                + "\"messages\":[{\"role\":\"user\",\"content\":\"" + escapar(solicitudUsuario) + "\"}]"
                + "}";

        HttpRequest peticion = HttpRequest.newBuilder()
                .uri(URI.create(URL_API))
                .header("Content-Type", "application/json")
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .POST(HttpRequest.BodyPublishers.ofString(cuerpo))
                .build();

        HttpClient cliente = HttpClient.newHttpClient();
        HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());

        if (respuesta.statusCode() != 200) {
            throw new RuntimeException("La API respondio con error " + respuesta.statusCode()
                    + ": " + respuesta.body());
        }

        String textoRespuesta = extraerTexto(respuesta.body());
        return parsear(textoRespuesta);
    }

    private String construirPromptSistema() {
        LocalDate hoy = LocalDate.now();
        String diaSemana = hoy.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es"));

        return "Convertis solicitudes de reserva de recursos en texto libre a una sola linea con "
                + "este formato exacto, sin texto adicional ni explicaciones: "
                + "ACTIVIDAD|FECHA|HORA_INICIO|HORA_FIN|CATEGORIA. "
                + "Hoy es " + diaSemana + " " + hoy + ". "
                + "FECHA en formato AAAA-MM-DD. "
                + "HORA_INICIO y HORA_FIN en formato HH:mm, siempre en punto (por ejemplo 14:00), "
                + "redondeando a la hora en punto mas cercana si el usuario da otra cosa. "
                + "CATEGORIA es el tipo de recurso pedido, en pocas palabras (ejemplo: sala de reuniones, "
                + "proyector, laboratorio de computo). "
                + "Si no podes determinar un dato, escribi NULL en ese campo.";
    }

    private String extraerTexto(String cuerpoJson) {
        String marcador = "\"text\":\"";
        int inicio = cuerpoJson.indexOf(marcador);
        if (inicio < 0) {
            throw new RuntimeException("No se pudo leer la respuesta de la IA: " + cuerpoJson);
        }
        inicio += marcador.length();

        StringBuilder texto = new StringBuilder();
        for (int i = inicio; i < cuerpoJson.length(); i++) {
            char actual = cuerpoJson.charAt(i);
            if (actual == '\\' && i + 1 < cuerpoJson.length()) {
                char siguiente = cuerpoJson.charAt(i + 1);
                if (siguiente == 'n') {
                    texto.append('\n');
                } else {
                    texto.append(siguiente);
                }
                i++;
            } else if (actual == '"') {
                break;
            } else {
                texto.append(actual);
            }
        }
        return texto.toString().trim();
    }

    private SugerenciaReserva parsear(String linea) {
        String[] partes = linea.split("\\|");
        if (partes.length != 5) {
            throw new RuntimeException("La IA respondio en un formato inesperado: " + linea);
        }
        return new SugerenciaReserva(
                partes[0].trim(), partes[1].trim(), partes[2].trim(), partes[3].trim(), partes[4].trim()
        );
    }

    private String escapar(String texto) {
        return texto
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }
}
