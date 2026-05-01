package service;

import client.TmdbClient;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import model.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TmdbService {

    private final TmdbClient client;
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    public TmdbService(TmdbClient client) {
        this.client = client;
    }

    public MovieResponse searchMovies(String query, Integer year, int page) throws Exception {
        StringBuilder path = new StringBuilder("/search/movie?query=")
                .append(URLEncoder.encode(query, StandardCharsets.UTF_8))
                .append("&page=").append(page)
                .append("&language=en-US");
        if (year != null) path.append("&year=").append(year);
        return mapper.readValue(client.get(path.toString()), MovieResponse.class);
    }

    public MovieResponse getPopular(int page) throws Exception {
        return mapper.readValue(client.get("/movie/popular?page=" + page + "&language=en-US"), MovieResponse.class);
    }

    public MovieResponse getTopRated(int page) throws Exception {
        return mapper.readValue(client.get("/movie/top_rated?page=" + page + "&language=en-US"), MovieResponse.class);
    }

    public MovieResponse getNowPlaying(int page) throws Exception {
        return mapper.readValue(client.get("/movie/now_playing?page=" + page + "&language=en-US"), MovieResponse.class);
    }

    public MovieResponse getUpcoming(int page) throws Exception {
        return mapper.readValue(client.get("/movie/upcoming?page=" + page + "&language=en-US"), MovieResponse.class);
    }

    public MovieDetails getMovieDetails(int id) throws Exception {
        return mapper.readValue(client.get("/movie/" + id + "?language=en-US"), MovieDetails.class);
    }

    public CreditsResponse getCredits(int id) throws Exception {
        return mapper.readValue(client.get("/movie/" + id + "/credits"), CreditsResponse.class);
    }

    public MovieResponse getSimilar(int id, int page) throws Exception {
        return mapper.readValue(
                client.get("/movie/" + id + "/similar?page=" + page + "&language=en-US"),
                MovieResponse.class
        );
    }

    public ReviewResponse getReviews(int id, int page) throws Exception {
        return mapper.readValue(
                client.get("/movie/" + id + "/reviews?page=" + page + "&language=en-US"),
                ReviewResponse.class
        );
    }

    public String getTrailer(int id) throws Exception {
        var root    = mapper.readTree(client.get("/movie/" + id + "/videos"));
        var results = root.get("results");
        if (results != null) {
            for (var v : results) {
                if ("YouTube".equals(v.get("site").asText())
                        && "Trailer".equals(v.get("type").asText())) {
                    return "https://youtu.be/" + v.get("key").asText();
                }
            }
        }
        return "No trailer found";
    }

}