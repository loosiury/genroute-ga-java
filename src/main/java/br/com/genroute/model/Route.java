package br.com.genroute.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public final class Route {
    private static final int START_CITY_INDEX = 0;

    private final List<City> cities;
    private final List<Integer> order;
    private final double distance;

    public Route(List<City> cities, List<Integer> order) {
        validate(cities, order);
        this.cities = List.copyOf(cities);
        this.order = List.copyOf(order);
        this.distance = calculateDistance();
    }

    public static Route random(List<City> cities, Random random) {
        if (cities.size() < 3) {
            throw new IllegalArgumentException("Use pelo menos 3 pontos para formar uma rota.");
        }

        List<Integer> order = new ArrayList<>();
        order.add(START_CITY_INDEX);
        for (int index = 1; index < cities.size(); index++) {
            order.add(index);
        }
        Collections.shuffle(order.subList(1, order.size()), random);
        return new Route(cities, order);
    }

    public List<City> cities() {
        return cities;
    }

    public List<Integer> order() {
        return order;
    }

    public double distance() {
        return distance;
    }

    public List<City> orderedCities() {
        return order.stream()
                .map(cities::get)
                .collect(Collectors.toUnmodifiableList());
    }

    public String orderAsText() {
        return order.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" -> "))
                + " -> "
                + START_CITY_INDEX;
    }

    private double calculateDistance() {
        double total = 0.0;
        for (int index = 0; index < order.size(); index++) {
            City current = cities.get(order.get(index));
            City next = cities.get(order.get((index + 1) % order.size()));
            total += current.distanceTo(next);
        }
        return total;
    }

    private static void validate(List<City> cities, List<Integer> order) {
        if (cities == null || order == null) {
            throw new IllegalArgumentException("Cidades e ordem da rota sao obrigatorias.");
        }
        if (cities.size() < 3) {
            throw new IllegalArgumentException("Use pelo menos 3 pontos para formar uma rota.");
        }
        if (cities.size() != order.size()) {
            throw new IllegalArgumentException("A rota deve conter todos os pontos exatamente uma vez.");
        }
        if (order.get(0) != START_CITY_INDEX) {
            throw new IllegalArgumentException("A rota deve iniciar no ponto 0.");
        }

        Set<Integer> used = new HashSet<>(order);
        if (used.size() != cities.size()) {
            throw new IllegalArgumentException("A rota nao pode repetir pontos.");
        }

        for (Integer cityIndex : order) {
            if (cityIndex == null || cityIndex < 0 || cityIndex >= cities.size()) {
                throw new IllegalArgumentException("Indice de ponto invalido na rota.");
            }
        }
    }
}
