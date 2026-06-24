package br.com.genroute.ga;

public record GeneticParameters(
        int populationSize,
        int generations,
        double mutationRate,
        int eliteCount,
        int tournamentSize) {
    public GeneticParameters {
        if (populationSize < 20) {
            throw new IllegalArgumentException("A populacao deve ter pelo menos 20 individuos.");
        }
        if (generations < 10) {
            throw new IllegalArgumentException("Use pelo menos 10 geracoes.");
        }
        if (mutationRate < 0.0 || mutationRate > 1.0) {
            throw new IllegalArgumentException("A taxa de mutacao deve ficar entre 0 e 1.");
        }
        if (eliteCount < 1 || eliteCount >= populationSize) {
            throw new IllegalArgumentException("O elitismo deve preservar entre 1 e populacao - 1 individuos.");
        }
        if (tournamentSize < 2 || tournamentSize > populationSize) {
            throw new IllegalArgumentException("O torneio deve ter entre 2 e o tamanho da populacao.");
        }
    }
}
