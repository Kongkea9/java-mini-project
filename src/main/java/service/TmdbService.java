package service;



import client.TmdbClient;
import client.TmdbConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.MovieDetails;
import model.MovieResponse;
import model.ReviewResponse;


import java.util.List;

public class TmdbService {

    private final TmdbClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public TmdbService(TmdbClient client) {
        this.client = client;
    }

    // 🔎 SEARCH
    public MovieResponse searchMovies(String query, int page) throws Exception {
        String path = "/search/movie?api_key=" + TmdbConfig.API_KEY +
                "&query=" + query +
                "&page=" + page +
                "&language=en-US";

        return mapper.readValue(client.get(path), MovieResponse.class);
    }

    // 📄 DETAILS
    public MovieDetails getMovieDetails(int id) throws Exception {
        return mapper.readValue(
                client.get("/movie/" + id + "?language=en-US"),
                MovieDetails.class
        );
    }

    // ▶️ TRAILER
    public String getTrailer(int id) throws Exception {

        var root = mapper.readTree(client.get("/movie/" + id + "/videos"));
        var results = root.get("results");

        for (var v : results) {
            if ("YouTube".equals(v.get("site").asText())
                    && "Trailer".equals(v.get("type").asText())) {

                return "https://www.youtube.com/watch?v=" + v.get("key").asText();
            }
        }
        return "No trailer";
    }

    // 🆕 LATEST
    public MovieDetails getLatest() throws Exception {
        return mapper.readValue(client.get("/movie/latest"), MovieDetails.class);
    }

    // ⭐ REVIEWS
    public ReviewResponse getReviews(int id) throws Exception {
        return mapper.readValue(
                client.get("/movie/" + id + "/reviews"),
                ReviewResponse.class
        );
    }
}