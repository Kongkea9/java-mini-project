
import client.TmdbClient;
import model.MovieDetails;
import model.MovieResponse;
import model.ReviewResponse;
import service.TmdbService;
import util.Spinner;
import util.TableFormat;

import java.util.*;

import static util.TableFormat.printTable;


public class Main {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws Exception {

        TmdbService service = new TmdbService(new TmdbClient());

        while (true) {

            System.out.println("\n🎬 TMDB CLI");
            System.out.println("1️⃣ Search Movie");
            System.out.println("2️⃣ Latest Movie");
            System.out.println("0️⃣ Exit");

            String opt = sc.nextLine();

            switch (opt) {

                case "1" -> search(service);
                case "2" -> showLatest(service);
                case "0" -> System.exit(0);

                default -> System.out.println("❌ Invalid");
            }
        }
    }

    static void search(TmdbService service) throws Exception {

        System.out.print("🔎 Enter movie: ");
        String q = sc.nextLine();

        int page = 1;

        while (true) {

            Spinner sp = new Spinner();
            Thread t = new Thread(sp);
            t.start();

            MovieResponse res = service.searchMovies(q, page);

            sp.stop();
            t.join();

            TableFormat.printTable(res.getResults(), service);

            System.out.println("\n[n] next [p] prev [g] goto [md] detail [b] back");

            String cmd = sc.nextLine();

            switch (cmd) {
                case "n" -> page++;
                case "p" -> page = Math.max(1, page - 1);

                case "g" -> {
                    System.out.print("Page: ");
                    page = Integer.parseInt(sc.nextLine());
                }

                case "md" -> {
                    System.out.print("ID: ");
                    int id = Integer.parseInt(sc.nextLine());
                    showDetail(service, id);
                }

                case "b" -> { return; }
            }
        }
    }

    static void showDetail(TmdbService service, int id) throws Exception {

        MovieDetails m = service.getMovieDetails(id);
        ReviewResponse r = service.getReviews(id);

        System.out.println("\n🎬 " + m.getTitle());
        System.out.println("📅 " + m.getReleaseDate());
        System.out.println("⭐ " + m.getVoteAverage());
        System.out.println("⏱ " + m.getRuntime() + " min");
        System.out.println("💰 $" + m.getBudget());

        System.out.print("🎭 ");
        m.getGenres().forEach(g -> System.out.print(g.getName() + " "));

        System.out.println("\n\n⭐ Reviews:");
        r.getResults().stream().limit(2).forEach(rev ->
                System.out.println("👤 " + rev.getAuthor() + ": " +
                        rev.getContent().substring(0, Math.min(200, rev.getContent().length())) + "...\n")
        );
    }

    static void showLatest(TmdbService service) throws Exception {

        MovieDetails m = service.getLatest();

        System.out.println("\n🆕 Latest Movie");
        System.out.println("🎬 " + m.getTitle());
        System.out.println("📅 " + m.getReleaseDate());
        System.out.println("⭐ " + m.getVoteAverage());
    }
}