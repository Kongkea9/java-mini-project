package model;

import lombok.Data;
import java.util.List;

@Data
public class ReviewResponse {
    private int page;
    private int totalPages;
    private int totalResults;
    private List<Review> results;
}