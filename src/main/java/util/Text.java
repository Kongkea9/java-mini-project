package util;

public class Text {


    public static java.util.List<String> wrap(String text, int width) {
        java.util.List<String> lines = new java.util.ArrayList<>();

        if (text == null || text.isEmpty()) {
            lines.add("-");
            return lines;
        }

        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            if (line.length() + word.length() > width) {
                lines.add(line.toString());
                line = new StringBuilder();
            }
            line.append(word).append(" ");
        }

        if (!line.isEmpty()) {
            lines.add(line.toString());
        }

        return lines;
    }

}
