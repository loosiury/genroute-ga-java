package br.com.genroute.visual;

import br.com.genroute.model.City;
import br.com.genroute.model.Route;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Affine;

public final class RouteCanvas extends Canvas {
    private static final double PADDING = 46.0;

    private Route initialRoute;
    private Route currentRoute;
    private boolean showInitialRoute = true;
    private int generation;
    private int maxGeneration;

    public RouteCanvas() {
        setWidth(720);
        setHeight(540);
        widthProperty().addListener((observable, oldValue, newValue) -> draw());
        heightProperty().addListener((observable, oldValue, newValue) -> draw());
        draw();
    }

    public void setRoutes(Route initialRoute, Route currentRoute, boolean showInitialRoute, int generation, int maxGeneration) {
        this.initialRoute = initialRoute;
        this.currentRoute = currentRoute;
        this.showInitialRoute = showInitialRoute;
        this.generation = generation;
        this.maxGeneration = maxGeneration;
        draw();
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return 720;
    }

    @Override
    public double prefHeight(double width) {
        return 540;
    }

    private void draw() {
        GraphicsContext graphics = getGraphicsContext2D();
        double width = getWidth();
        double height = getHeight();

        graphics.setFill(Color.web("#f8fafc"));
        graphics.fillRect(0, 0, width, height);
        drawGrid(graphics, width, height);

        if (currentRoute == null) {
            drawEmptyState(graphics, width, height);
            return;
        }

        Scale scale = calculateScale(currentRoute.cities(), width, height);

        if (showInitialRoute && initialRoute != null) {
            drawRoute(graphics, initialRoute, scale, Color.web("#94a3b8"), 1.4, true);
        }

        drawRoute(graphics, currentRoute, scale, Color.web("#2563eb"), 3.0, false);
        drawCities(graphics, currentRoute.cities(), scale);
        drawStatus(graphics);
    }

    private void drawGrid(GraphicsContext graphics, double width, double height) {
        graphics.setStroke(Color.web("#e2e8f0"));
        graphics.setLineWidth(1.0);
        for (double x = PADDING; x < width - PADDING / 2; x += 50) {
            graphics.strokeLine(x, PADDING / 2, x, height - PADDING / 2);
        }
        for (double y = PADDING; y < height - PADDING / 2; y += 50) {
            graphics.strokeLine(PADDING / 2, y, width - PADDING / 2, y);
        }
    }

    private void drawEmptyState(GraphicsContext graphics, double width, double height) {
        graphics.setFill(Color.web("#475569"));
        graphics.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        graphics.fillText("Selecione um problema e execute o algoritmo", width / 2 - 185, height / 2);
    }

    private Scale calculateScale(List<City> cities, double canvasWidth, double canvasHeight) {
        double minX = cities.stream().mapToDouble(City::x).min().orElse(0);
        double maxX = cities.stream().mapToDouble(City::x).max().orElse(1);
        double minY = cities.stream().mapToDouble(City::y).min().orElse(0);
        double maxY = cities.stream().mapToDouble(City::y).max().orElse(1);

        double usableWidth = Math.max(1, canvasWidth - PADDING * 2);
        double usableHeight = Math.max(1, canvasHeight - PADDING * 2);
        double dataWidth = Math.max(1, maxX - minX);
        double dataHeight = Math.max(1, maxY - minY);
        double ratio = Math.min(usableWidth / dataWidth, usableHeight / dataHeight);

        double drawingWidth = dataWidth * ratio;
        double drawingHeight = dataHeight * ratio;
        double offsetX = (canvasWidth - drawingWidth) / 2.0;
        double offsetY = (canvasHeight - drawingHeight) / 2.0;

        return new Scale(minX, maxY, ratio, offsetX, offsetY);
    }

    private void drawRoute(
            GraphicsContext graphics,
            Route route,
            Scale scale,
            Color color,
            double lineWidth,
            boolean dashed) {
        List<City> ordered = new ArrayList<>(route.orderedCities());
        ordered.add(ordered.get(0));

        graphics.save();
        graphics.setStroke(color);
        graphics.setLineWidth(lineWidth);
        if (dashed) {
            graphics.setLineDashes(8);
        } else {
            graphics.setLineDashes();
        }

        for (int index = 0; index < ordered.size() - 1; index++) {
            Point start = scale.toPoint(ordered.get(index));
            Point end = scale.toPoint(ordered.get(index + 1));
            graphics.strokeLine(start.x, start.y, end.x, end.y);
        }
        graphics.restore();
    }

    private void drawCities(GraphicsContext graphics, List<City> cities, Scale scale) {
        graphics.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        for (int index = 0; index < cities.size(); index++) {
            City city = cities.get(index);
            Point point = scale.toPoint(city);
            boolean start = index == 0;

            graphics.setFill(start ? Color.web("#f97316") : Color.web("#10b981"));
            graphics.fillOval(point.x - 6, point.y - 6, 12, 12);
            graphics.setStroke(Color.WHITE);
            graphics.setLineWidth(2);
            graphics.strokeOval(point.x - 6, point.y - 6, 12, 12);

            if (cities.size() <= 20 || start) {
                graphics.setFill(Color.web("#0f172a"));
                graphics.fillText(start ? "0" : String.valueOf(index), point.x + 8, point.y - 8);
            }
        }
    }

    private void drawStatus(GraphicsContext graphics) {
        graphics.setTransform(new Affine());
        graphics.setFill(Color.web("#0f172a"));
        graphics.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        graphics.fillText("Geracao " + generation + " / " + maxGeneration, 18, 28);
        graphics.setFont(Font.font("Segoe UI", 13));
        graphics.fillText("Distancia: %.2f".formatted(currentRoute.distance()), 18, 48);
    }

    private record Scale(double minX, double maxY, double ratio, double offsetX, double offsetY) {
        Point toPoint(City city) {
            double x = offsetX + (city.x() - minX) * ratio;
            double y = offsetY + (maxY - city.y()) * ratio;
            return new Point(x, y);
        }
    }

    private record Point(double x, double y) {
    }
}
