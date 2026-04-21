package model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieResponse {
    private int page;
    private int totalPages;
    private List<MovieSummary> results;
}