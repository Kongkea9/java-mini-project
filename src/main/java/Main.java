import client.TmdbClient;
import model.*;
import service.TmdbService;
import util.Spinner;
import util.TableFormat;
import util.Text;

import java.util.Scanner;
import java.util.concurrent.Callable;

public class Main {

    static final Scanner sc = new Scanner(System.in);
    static TmdbService service;
    static String menu = """
               [n] Next Page          [b] Back
               [p] Previous Page      [e] Exit
               [g] Go To
               [md] Movie Detail
                """;

    public static void main(String[] args) throws Exception {


        service = new TmdbService(new TmdbClient());

        while (true) {
            menu();

            switch (sc.nextLine().trim()) {
                case "1" -> search();
                case "2" -> browse("popular");
                case "3" -> browse("top_rated");
                case "4" -> browse("now_playing");
                case "5" -> browse("upcoming");
                case "0" -> System.exit(0);
                default -> System.out.println("Invalid option");
            }
        }
    }

    static void menu() {
        System.out.println("\n====================");
        System.out.println(" TMDB CLI");
        System.out.println("====================");
        System.out.println("1. Search");
        System.out.println("2. Popular");
        System.out.println("3. Top Rated");
        System.out.println("4. Now Playing");
        System.out.println("5. Upcoming");
        System.out.println("6. Latest");
        System.out.println("0. Exit");
        System.out.println("====================");
        System.out.print("Choice: ");
    }

    static <T> T load(Callable<T> task) throws Exception {
        Spinner sp = new Spinner();
        Thread t = new Thread(sp);
        t.start();
        try {
            return task.call();
        } finally {
            sp.stop();
            t.join();
        }
    }

    static void search() throws Exception {

        System.out.print("Movie name: ");
        String query = sc.nextLine().trim();
        if (query.isEmpty()) return;

        int[] page = {1};

        while (true) {

            MovieResponse res = load(() ->
                    service.searchMovies(query, null, page[0])
            );

            if (empty(res)) {
                System.out.println("\nNo results found.");
                return;
            }

            render(res, page);

            System.out.println(menu);
            System.out.print("-> ");

            if (!handle(res, page)) return;
        }
    }

    static void browse(String type) throws Exception {

        PageFetcher fetcher = switch (type) {
            case "popular" -> service::getPopular;
            case "top_rated" -> service::getTopRated;
            case "now_playing" -> service::getNowPlaying;
            case "upcoming" -> service::getUpcoming;
            default -> throw new IllegalStateException();
        };

        int[] page = {1};

        while (true) {

            MovieResponse res = load(() -> fetcher.fetch(page[0]));

            if (empty(res)) {
                System.out.println("\nNo results found.");
                return;
            }

            render(res, page);

            System.out.println(menu);
            System.out.print("-> ");

            if (!handle(res, page)) return;
        }
    }

    static void render(MovieResponse res, int[] page) {
        System.out.println("\nPage " + page[0] + " / " + res.getTotalPages());
        TableFormat.printMovieTable(res.getResults(), service);
    }

    static boolean handle(MovieResponse res, int[] page) throws Exception {

        String cmd = sc.nextLine().trim();

        switch (cmd) {

            case "n" -> {
                if (page[0] < res.getTotalPages()) {
                    page[0]++;
                } else {
                    System.out.println("Already on last page");
                }
            }

            case "p" -> {
                if (page[0] > 1) {
                    page[0]--;
                } else {
                    System.out.println("Already on first page");
                }
            }

            case "g" -> {
                System.out.print("Enter page (1 - " + res.getTotalPages() + "): ");
                try {
                    int p = Integer.parseInt(sc.nextLine().trim());
                    if (p >= 1 && p <= res.getTotalPages()) {
                        page[0] = p;
                    } else {
                        System.out.println("Invalid range");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid number");
                }
            }

            case "b" -> {
                return false;
            }

            case "e" -> {
                System.exit(0);
            }

            case "md" -> {
                openDetailFlow(res);
            }

            default -> {
                try {
                    int id = Integer.parseInt(cmd);
                    movieMenu(id);
                } catch (Exception e) {
                    System.out.println("Unknown command");
                }
            }
        }

        return true;
    }

    static void openDetailFlow(MovieResponse res) throws Exception {

        System.out.print("Enter movie ID: ");
        String input = sc.nextLine().trim();

        try {
            movieMenu(Integer.parseInt(input));
        } catch (Exception e) {
            System.out.println("Invalid ID");
        }
    }

    static void movieMenu(int id) throws Exception {

        while (true) {

            System.out.println("\n================");
            System.out.println(" Movie Menu");
            System.out.println("================");
            System.out.println("1. Details");
            System.out.println("2. Reviews");
            System.out.println("3. Similar");
            System.out.println("4. Trailer");
            System.out.println("0. Back");
            System.out.println("================");
            System.out.print("-> ");

            switch (sc.nextLine().trim()) {

                case "1" -> showDetails(id);

                case "2" -> reviews(id);

                case "3" -> similar(id);

                case "4" -> trailer(id);

                case "0" -> {
                    return;
                }

                default -> System.out.println("Invalid");
            }
        }
    }

    static void showDetails(int id) throws Exception {
        MovieDetails m = load(() -> service.getMovieDetails(id));
        CreditsResponse c = load(() -> service.getCredits(id));
        String t = load(() -> service.getTrailer(id));

        TableFormat.printMovieDetails(m, c, t);

        System.out.println("Press Enter...");
        sc.nextLine();
    }

    static void reviews(int id) throws Exception {

        int[] page = {1};

        while (true) {

            ReviewResponse rr = load(() -> service.getReviews(id, page[0]));

            if (rr == null || rr.getResults() == null || rr.getResults().isEmpty()) {
                System.out.println("\nNo reviews found");
                return;
            }

            System.out.println("\n==============================");
            System.out.println(" Reviews Page " + page[0]);
            System.out.println("==============================");

            for (Review r : rr.getResults()) {

                System.out.println("\n------------------------------");
                System.out.println("Author: " + r.getAuthor());
                System.out.println("------------------------------");

                String content = r.getContent();

                for (String line : Text.wrap(content, 80)) {
                    System.out.println(line);
                }

                System.out.println("------------------------------");
            }

            System.out.println("\n[n] Next  [p] Prev  [b] Back");
            System.out.print("-> ");

            String c = sc.nextLine().trim();

            if (c.equals("n")) {
                if (page[0] < rr.getTotalPages()) page[0]++;
                else System.out.println("Already LAST page");
            }
            else if (c.equals("p")) {
                if (page[0] > 1) page[0]--;
                else System.out.println("Already FIRST page");
            }
            else if (c.equals("b")) {
                return;
            }
        }
    }

    static void similar(int id) throws Exception {

        int[] page = {1};

        while (true) {

            MovieResponse res = load(() -> service.getSimilar(id, page[0]));

            if (empty(res)) {
                System.out.println("No similar movies");
                return;
            }

            render(res, page);

            System.out.println(menu);
            System.out.print("-> ");

            if (!handle(res, page)) return;
        }
    }


    static void trailer(int id) throws Exception {
        System.out.println(load(() -> service.getTrailer(id)));
        System.out.println("Press Enter...");
        sc.nextLine();
    }



    static boolean empty(MovieResponse r) {
        return r == null || r.getResults() == null || r.getResults().isEmpty();
    }

    interface PageFetcher {
        MovieResponse fetch(int page) throws Exception;
    }
}