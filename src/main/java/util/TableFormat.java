package util;

import model.MovieSummary;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.Table;
import service.TmdbService;

import java.util.List;

public class TableFormat {



    public static void printTable(List<MovieSummary> list, TmdbService service) {

        Table t = new Table(5, BorderStyle.CLASSIC);

        t.addCell("ID");
        t.addCell("Title");
        t.addCell("Release");
        t.addCell("Rating");
        t.addCell("Trailer");

        for (var m : list) {
            try {
                String trailer = service.getTrailer(m.getId());

                t.addCell(String.valueOf(m.getId()));
                t.addCell(m.getTitle());
                t.addCell(m.getReleaseDate());
                t.addCell(String.valueOf(m.getVoteAverage()));
                t.addCell(trailer);

            } catch (Exception e) {
                t.addCell(String.valueOf(m.getId()));
                t.addCell(m.getTitle());
                t.addCell(m.getReleaseDate());
                t.addCell(String.valueOf(m.getVoteAverage()));
                t.addCell("N/A");
            }
        }

        System.out.println(t.render());
    }
}
