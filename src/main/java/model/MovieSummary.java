package model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieSummary {
    private int id;
    private String title;
    private String releaseDate;
    private double voteAverage;
}