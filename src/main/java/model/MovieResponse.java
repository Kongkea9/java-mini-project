package model;

import lombok.Data;
import java.util.List;

@Data
public class MovieResponse {
    private int page;
    private int totalPages;
    private int totalResults;
    private List<MovieSummary> results;
}