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

public final class RouteCanvas extends Canvas {
    private static final double PADDING = 34.0;
    private static final double ARROW_SIZE = 9.0;

    private Route referenceRoute;
    private Route currentRoute;
    private boolean showReferenceRoute;
    private Color routeColor = Color.web("#123f91");
    private int generation;
    private int maxGeneration;
    private boolean showGenerationStatus;

    public RouteCanvas() {
        setWidth(620);
        setHeight(290);
        widthProperty().addListener((observable, oldValue, newValue) -> draw());
        heightProperty().addListener((observable, oldValue, newValue) -> draw());
        draw();
    }

    public void setRouteView(
            Route referenceRoute,
            Route currentRoute,
            boolean showReferenceRoute,
            Color routeColor,
            int generation,
            int maxGeneration,
            boolean showGenerationStatus) {
        this.referenceRoute = referenceRoute;
        this.currentRoute = currentRoute;
        this.showReferenceRoute = showReferenceRoute;
        this.routeColor = routeColor;
        this.generation = generation;
        this.maxGeneration = maxGeneration;
        this.showGenerationStatus = showGenerationStatus;
        draw();
    }

    public void setRoutes(Route initialRoute, Route currentRoute, boolean showInitialRoute, int generation, int maxGeneration) {
        setRouteView(initialRoute, currentRoute, showInitialRoute, Color.web("#123f91"), generation, maxGeneration, true);
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return 620;
    }

    @Override
    public double prefHeight(double width) {
        return 290;
    }

    private void draw() {
        GraphicsContext graphics = getGraphicsContext2D();
        double width = getWidth();
        double height = getHeight();

        graphics.setFill(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        drawGrid(graphics, width, height);

        if (currentRoute == null) {
            drawEmptyState(graphics, width, height);
            return;
        }

        Scale scale = calculateScale(currentRoute.cities(), width, height);

        if (showReferenceRoute && referenceRoute != null) {
            drawRoute(graphics, referenceRoute, scale, Color.web("#cbd5e1"), 1.5, true, false);
        }

        drawRoute(graphics, currentRoute, scale, routeColor, 2.6, false, true);
        drawCities(graphics, currentRoute.cities(), scale);
        drawDistance(graphics, width, height);

        if (showGenerationStatus) {
            drawGeneration(graphics);
        }
    }

    private void drawGrid(GraphicsContext graphics, double width, double height) {
        graphics.setStroke(Color.web("#eef2f7"));
        graphics.setLineWidth(1.0);
        for (double x = PADDING; x < width - PADDING / 2; x += 48) {
            graphics.strokeLine(x, PADDING / 2, x, height - PADDING / 2);
        }
        for (double y = PADDING; y < height - PADDING / 2; y += 48) {
            graphics.strokeLine(PADDING / 2, y, width - PADDING / 2, y);
        }
    }

    private void drawEmptyState(GraphicsContext graphics, double width, double height) {
        graphics.setFill(Color.web("#64748b"));
        graphics.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        graphics.fillText("Execute o algoritmo para visualizar a rota", width / 2 - 150, height / 2);
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
            boolean muted,
            boolean drawArrows) {
        List<City> ordered = new ArrayList<>(route.orderedCities());
        ordered.add(ordered.get(0));

        graphics.save();
        graphics.setStroke(color);
        graphics.setFill(color);
        graphics.setLineWidth(lineWidth);
        graphics.setGlobalAlpha(muted ? 0.62 : 1.0);

        for (int index = 0; index < ordered.size() - 1; index++) {
            Point start = scale.toPoint(ordered.get(index));
            Point end = scale.toPoint(ordered.get(index + 1));
            graphics.strokeLine(start.x, start.y, end.x, end.y);
            if (drawArrows) {
                drawArrow(graphics, start, end);
            }
        }
        graphics.restore();
    }

    private void drawArrow(GraphicsContext graphics, Point start, Point end) {
        double angle = Math.atan2(end.y - start.y, end.x - start.x);
        double arrowX = end.x - Math.cos(angle) * 11.0;
        double arrowY = end.y - Math.sin(angle) * 11.0;
        double left = angle - Math.PI / 7.0;
        double right = angle + Math.PI / 7.0;

        double[] xPoints = {
                arrowX,
                arrowX - Math.cos(left) * ARROW_SIZE,
                arrowX - Math.cos(right) * ARROW_SIZE
        };
        double[] yPoints = {
                arrowY,
                arrowY - Math.sin(left) * ARROW_SIZE,
                arrowY - Math.sin(right) * ARROW_SIZE
        };
        graphics.fillPolygon(xPoints, yPoints, 3);
    }

    private void drawCities(GraphicsContext graphics, List<City> cities, Scale scale) {
        graphics.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        for (int index = 0; index < cities.size(); index++) {
            City city = cities.get(index);
            Point point = scale.toPoint(city);
            boolean start = index == 0;

            if (start) {
                graphics.setFill(Color.web("#2563eb"));
                graphics.fillRect(point.x - 6, point.y - 6, 12, 12);
                graphics.setStroke(Color.WHITE);
                graphics.setLineWidth(2);
                graphics.strokeRect(point.x - 6, point.y - 6, 12, 12);
            } else {
                graphics.setFill(Color.web("#c7c9cc"));
                graphics.fillOval(point.x - 5, point.y - 5, 10, 10);
                graphics.setStroke(Color.WHITE);
                graphics.setLineWidth(1.5);
                graphics.strokeOval(point.x - 5, point.y - 5, 10, 10);
            }

            if (cities.size() <= 30 || start) {
                graphics.setFill(Color.web("#111827"));
                graphics.fillText(start ? "0" : String.valueOf(index), point.x + 8, point.y - 8);
            }
        }
    }

    private void drawDistance(GraphicsContext graphics, double width, double height) {
        graphics.setFill(routeColor);
        graphics.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        String text = "Distância: %.2f km".formatted(currentRoute.distance());
        double textWidth = graphics.getFont().getSize() * text.length() * 0.48;
        graphics.fillText(text, Math.max(PADDING, width - textWidth - 24), height - 18);
    }

    private void drawGeneration(GraphicsContext graphics) {
        graphics.setFill(Color.web("#475569"));
        graphics.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        graphics.fillText("Geração " + generation + " / " + maxGeneration, 16, 24);
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
