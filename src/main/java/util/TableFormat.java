package util;

import model.MovieSummary;
import model.MovieDetails;
import model.CreditsResponse;
import model.Cast;
import model.Genre;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.Table;
import service.TmdbService;

import java.util.List;

public class TableFormat {

    public static void printMovieTable(List<MovieSummary> list, TmdbService service) {
        Table t = new Table(6, BorderStyle.CLASSIC);

        t.addCell("#");
        t.addCell("ID");
        t.addCell("Title");
        t.addCell("Release");
        t.addCell("Rating");
        t.addCell("Trailer");

        for (int i = 0; i < list.size(); i++) {
            MovieSummary m = list.get(i);

            String trailer;
            try {
                trailer = service.getTrailer(m.getId());
                if (trailer == null || trailer.isBlank()) trailer = "-";
            } catch (Exception e) {
                trailer = "-";
            }

            t.addCell(String.valueOf(i + 1));
            t.addCell(String.valueOf(m.getId()));
            t.addCell(m.getTitle() != null ? m.getTitle() : "-");
            t.addCell(m.getReleaseDate() != null ? m.getReleaseDate() : "-");
            t.addCell(String.format("%.1f", m.getVoteAverage()));
            t.addCell(trailer);
        }

        System.out.println(t.render());
    }

    public static void printMovieDetails(MovieDetails m, CreditsResponse c, String trailer) {
        Table table = new Table(2, BorderStyle.CLASSIC);

        table.setColumnWidth(0, 18, 25);
        table.setColumnWidth(1, 40, 80);

        CellStyle headerStyle = new CellStyle(CellStyle.HorizontalAlign.CENTER);
        table.addCell("MOVIE DETAILS", headerStyle, 2);

        table.addCell("Title");
        table.addCell(nvl(m.getTitle()));

        table.addCell("Tagline");
        table.addCell(nvl(m.getTagline()));

        table.addCell("Release Date");
        table.addCell(nvl(m.getReleaseDate()));

        table.addCell("Status");
        table.addCell(nvl(m.getStatus()));

        table.addCell("Rating");
        table.addCell(String.format("%.1f / 10 (%d votes)", m.getVoteAverage(), m.getVoteCount()));

        table.addCell("Runtime");
        table.addCell(m.getRuntime() + " min");

        table.addCell("Budget");
        table.addCell("$" + String.format("%,d", m.getBudget()));

        table.addCell("Revenue");
        table.addCell("$" + String.format("%,d", m.getRevenue()));

        String genres = (m.getGenres() == null || m.getGenres().isEmpty())
                ? "-"
                : String.join(", ", m.getGenres().stream().map(Genre::getName).toList());

        table.addCell("Genres");
        table.addCell(genres);

        String origin = m.getOriginalLanguage();
        table.addCell("Origin");
        table.addCell(origin);

        String cast = (c == null || c.getCast() == null || c.getCast().isEmpty())
                ? "-"
                : String.join(", ", c.getCast().stream().limit(6).map(Cast::getName).toList());

        table.addCell("Cast");
        table.addCell(cast);

        table.addCell("Trailer");
        table.addCell(nvl(trailer));

        table.addCell("Overview");
        table.addCell(nvl(m.getOverview()));

        System.out.println(table.render());
    }

    private static String nvl(String s) {
        return s != null && !s.isBlank() ? s : "-";
    }
}
