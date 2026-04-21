package client;

import java.net.URI;
import java.net.http.*;

public class TmdbClient {

    private final HttpClient client = HttpClient.newHttpClient();

    public String get(String path) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TmdbConfig.BASE_URL + path))
                .header("Authorization", "Bearer " + TmdbConfig.TOKEN)
                .header("accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200)
            throw new RuntimeException("API ERROR: " + response.body());

        return response.body();
    }
}