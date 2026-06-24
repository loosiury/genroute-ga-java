package br.com.genroute.ga;

import br.com.genroute.model.Route;
import java.util.List;

public record GeneticResult(
        Route initialRoute,
        Route bestRoute,
        List<Double> bestDistanceHistory,
        List<Double> averageDistanceHistory,
        List<Route> bestRouteHistory,
        GeneticParameters parameters) {
    public GeneticResult {
        bestDistanceHistory = List.copyOf(bestDistanceHistory);
        averageDistanceHistory = List.copyOf(averageDistanceHistory);
        bestRouteHistory = List.copyOf(bestRouteHistory);
    }

    public double improvementPercent() {
        return ((initialRoute.distance() - bestRoute.distance()) / initialRoute.distance()) * 100.0;
    }

    public int generationsEvaluated() {
        return bestDistanceHistory.size() - 1;
    }
}
