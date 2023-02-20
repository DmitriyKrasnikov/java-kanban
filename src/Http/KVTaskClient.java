package Http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class KVTaskClient {
    private final String apiToken;
    private final String storageServerURL;


    public KVTaskClient(String serverURL){
        this.storageServerURL = serverURL;
        apiToken = register();
    }

    public String register(){
        URI uri = URI.create(this.storageServerURL + "/register");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString()
            );
            return response.body();
        }catch (IOException | InterruptedException e){
            System.out.println("Произошла ошибка при регистрации");
            return "0";
        }
    }

    public void put(String key, String json) {
        URI uri = URI.create(storageServerURL + "/save/" + key + "?API_TOKEN=" + apiToken);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );
            if (response.statusCode() != 200) {
                System.out.println("Не удалось сохранить данные");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String load(String key) {
        URI uri = URI.create(storageServerURL + "/load/" + key + "?API_TOKEN=" + apiToken);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );
            return response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Не удалось загрузить данные";
        }
    }
}