package br.com.genroute.io;

import br.com.genroute.model.City;
import br.com.genroute.model.ProblemInstance;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public final class ProblemRepository {
    public static final Path DEFAULT_PROBLEMS_DIR = Path.of("data", "problemas");

    public List<ProblemInstance> loadProblems() throws IOException {
        if (!Files.isDirectory(DEFAULT_PROBLEMS_DIR)) {
            return List.of();
        }

        try (Stream<Path> files = Files.list(DEFAULT_PROBLEMS_DIR)) {
            List<Path> csvFiles = files
                    .filter(path -> path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".csv"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .toList();

            List<ProblemInstance> problems = new ArrayList<>();
            for (Path csvFile : csvFiles) {
                problems.add(load(csvFile));
            }
            return problems;
        }
    }

    private ProblemInstance load(Path path) throws IOException {
        List<City> cities = new ArrayList<>();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
            String line = lines.get(lineIndex).trim();
            if (line.isBlank() || line.startsWith("#")) {
                continue;
            }

            String[] columns = line.contains(";") ? line.split(";") : line.split(",");
            if (isHeader(columns)) {
                continue;
            }
            if (columns.length < 3) {
                throw new IOException("Linha invalida em " + path + ": " + (lineIndex + 1));
            }

            String name = columns[0].trim();
            double x = parseCoordinate(columns[1], path, lineIndex + 1);
            double y = parseCoordinate(columns[2], path, lineIndex + 1);
            cities.add(new City(name, x, y));
        }

        if (cities.size() < 3) {
            throw new IOException("O problema " + path + " precisa ter pelo menos 3 pontos.");
        }

        return new ProblemInstance(problemName(path), path, cities);
    }

    private boolean isHeader(String[] columns) {
        return columns.length >= 3 && columns[0].trim().equalsIgnoreCase("nome");
    }

    private double parseCoordinate(String value, Path path, int line) throws IOException {
        try {
            return Double.parseDouble(value.trim().replace(',', '.'));
        } catch (NumberFormatException exception) {
            throw new IOException("Coordenada invalida em " + path + ", linha " + line, exception);
        }
    }

    private String problemName(Path path) {
        String fileName = path.getFileName().toString();
        int extensionStart = fileName.lastIndexOf('.');
        if (extensionStart > 0) {
            fileName = fileName.substring(0, extensionStart);
        }
        return fileName.replace('_', ' ');
    }
}
