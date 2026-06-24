package br.com.genroute.model;

public record City(String name, double x, double y) {
    public City {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("O nome da cidade nao pode ser vazio.");
        }
    }

    public double distanceTo(City other) {
        double deltaX = x - other.x;
        double deltaY = y - other.y;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }
}
