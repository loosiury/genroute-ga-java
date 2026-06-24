package br.com.genroute.ga;

import br.com.genroute.model.City;
import br.com.genroute.model.Route;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public final class GeneticAlgorithm {
    private final GeneticParameters parameters;
    private final Random random;

    public GeneticAlgorithm(GeneticParameters parameters, Random random) {
        this.parameters = parameters;
        this.random = random;
    }

    public GeneticResult solve(List<City> cities) {
        List<Route> population = createInitialPopulation(cities);
        Route initialRoute = population.get(0);
        Route bestOverall = initialRoute;

        List<Double> bestDistanceHistory = new ArrayList<>();
        List<Double> averageDistanceHistory = new ArrayList<>();
        List<Route> bestRouteHistory = new ArrayList<>();

        for (int generation = 0; generation <= parameters.generations(); generation++) {
            population.sort(Comparator.comparingDouble(Route::distance));
            Route generationBest = population.get(0);

            if (generationBest.distance() < bestOverall.distance()) {
                bestOverall = generationBest;
            }

            bestDistanceHistory.add(bestOverall.distance());
            averageDistanceHistory.add(averageDistance(population));
            bestRouteHistory.add(bestOverall);

            if (generation == parameters.generations()) {
                break;
            }

            population = nextGeneration(population, cities);
        }

        return new GeneticResult(
                initialRoute,
                bestOverall,
                bestDistanceHistory,
                averageDistanceHistory,
                bestRouteHistory,
                parameters);
    }

    private List<Route> createInitialPopulation(List<City> cities) {
        List<Route> population = new ArrayList<>(parameters.populationSize());
        for (int index = 0; index < parameters.populationSize(); index++) {
            population.add(Route.random(cities, random));
        }
        return population;
    }

    private List<Route> nextGeneration(List<Route> population, List<City> cities) {
        List<Route> next = new ArrayList<>(parameters.populationSize());

        for (int index = 0; index < parameters.eliteCount(); index++) {
            next.add(population.get(index));
        }

        while (next.size() < parameters.populationSize()) {
            Route parentA = tournamentSelection(population);
            Route parentB = tournamentSelection(population);
            List<Integer> childOrder = orderedCrossover(parentA.order(), parentB.order());
            mutate(childOrder);
            next.add(new Route(cities, childOrder));
        }

        return next;
    }

    private Route tournamentSelection(List<Route> population) {
        Route best = null;
        for (int index = 0; index < parameters.tournamentSize(); index++) {
            Route candidate = population.get(random.nextInt(population.size()));
            if (best == null || candidate.distance() < best.distance()) {
                best = candidate;
            }
        }
        return best;
    }

    private List<Integer> orderedCrossover(List<Integer> parentA, List<Integer> parentB) {
        int size = parentA.size();
        int[] child = new int[size];
        Arrays.fill(child, -1);
        child[0] = 0;

        int start = 1 + random.nextInt(size - 1);
        int end = 1 + random.nextInt(size - 1);
        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }

        boolean[] used = new boolean[size];
        used[0] = true;
        for (int index = start; index <= end; index++) {
            int city = parentA.get(index);
            child[index] = city;
            used[city] = true;
        }

        int writeIndex = nextGeneIndex(end, size);
        int readIndex = nextGeneIndex(end, size);
        for (int checked = 1; checked < size; checked++) {
            int city = parentB.get(readIndex);
            if (!used[city]) {
                child[writeIndex] = city;
                used[city] = true;
                writeIndex = nextGeneIndex(writeIndex, size);
            }
            readIndex = nextGeneIndex(readIndex, size);
        }

        List<Integer> order = new ArrayList<>(size);
        for (int city : child) {
            order.add(city);
        }
        return order;
    }

    private int nextGeneIndex(int current, int size) {
        int next = current + 1;
        return next >= size ? 1 : next;
    }

    private void mutate(List<Integer> order) {
        if (random.nextDouble() > parameters.mutationRate()) {
            return;
        }

        int first = 1 + random.nextInt(order.size() - 1);
        int second = 1 + random.nextInt(order.size() - 1);
        while (first == second) {
            second = 1 + random.nextInt(order.size() - 1);
        }

        int temp = order.get(first);
        order.set(first, order.get(second));
        order.set(second, temp);
    }

    private double averageDistance(List<Route> population) {
        return population.stream()
                .mapToDouble(Route::distance)
                .average()
                .orElse(0.0);
    }
}
