package model;


import lombok.Data;
import java.util.List;

@Data
public class MovieDetails {
    private int id;
    private String title;
    private String overview;
    private String tagline;
    private String releaseDate;
    private double voteAverage;
    private int voteCount;
    private int runtime;
    private long budget;
    private long revenue;
    private String status;
    private String originalLanguage;
    private List<Genre> genres;
}