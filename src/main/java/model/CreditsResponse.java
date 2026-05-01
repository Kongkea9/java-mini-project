package model;

import lombok.Data;
import java.util.List;

@Data
public class CreditsResponse {
    private List<Cast> cast;
}