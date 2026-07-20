package lugus.service.core;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TranslationService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public String translate(String text) {

        try {
            String body = mapper.writeValueAsString(Map.of(
                "q", text,
                "source", "en",
                "target", "es",
                "format", "text"
            ));

            HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://lingva.ml/api/v1/en/es/"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

            LibreTranslateResponse dto =
                mapper.readValue(res.body(), LibreTranslateResponse.class);

            return dto.translatedText;

        } catch (Exception e) {
            throw new RuntimeException("Error traduciendo texto", e);
        }
    }
    
    public class LibreTranslateResponse {
        public String translatedText;
    }
}
