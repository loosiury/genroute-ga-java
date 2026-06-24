package br.com.genroute.model;

import java.nio.file.Path;
import java.util.List;

public record ProblemInstance(String name, Path source, List<City> cities) {
    public ProblemInstance {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("O problema precisa ter um nome.");
        }
        cities = List.copyOf(cities);
    }

    @Override
    public String toString() {
        return "%s (%d pontos)".formatted(name, cities.size());
    }
}
