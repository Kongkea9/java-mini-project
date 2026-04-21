package model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDetails {
    private String title;
    private String releaseDate;
    private double voteAverage;
    private int runtime;
    private int budget;
    private String originalLanguage;
    private List<Genre> genres;
}