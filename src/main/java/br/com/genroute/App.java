package br.com.genroute;

import br.com.genroute.ga.GeneticAlgorithm;
import br.com.genroute.ga.GeneticParameters;
import br.com.genroute.ga.GeneticResult;
import br.com.genroute.io.ProblemRepository;
import br.com.genroute.model.ProblemInstance;
import br.com.genroute.visual.RouteCanvas;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public final class App extends Application {
    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,##0.00");
    private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("#,##0.00'%'");

    private final ProblemRepository problemRepository = new ProblemRepository();

    private ComboBox<ProblemInstance> problemCombo;
    private Spinner<Integer> populationSpinner;
    private Spinner<Integer> generationSpinner;
    private Slider mutationSlider;
    private Slider speedSlider;
    private CheckBox showInitialRouteCheck;
    private Button runButton;
    private Button replayButton;
    private Label mutationValueLabel;
    private Label statusLabel;
    private Label initialDistanceLabel;
    private Label finalDistanceLabel;
    private Label improvementLabel;
    private Label generationLabel;
    private Label routeLabel;
    private RouteCanvas routeCanvas;
    private LineChart<Number, Number> chart;
    private XYChart.Series<Number, Number> bestSeries;
    private XYChart.Series<Number, Number> averageSeries;
    private Timeline animation;
    private GeneticResult currentResult;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setTop(createHeader());
        root.setLeft(createControls());
        root.setCenter(createWorkspace());
        root.setBottom(createMetrics());

        Scene scene = new Scene(root, 1180, 760);
        String stylesheet = getClass().getResource("/styles/app.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);

        stage.setTitle("GenRoute GA");
        stage.setScene(scene);
        stage.setMinWidth(1024);
        stage.setMinHeight(680);
        stage.show();

        loadProblems();
        if (!problemCombo.getItems().isEmpty()) {
            problemCombo.getSelectionModel().selectLast();
            runAlgorithm();
        }
    }

    private VBox createHeader() {
        Label title = new Label("GenRoute GA");
        title.getStyleClass().add("title");

        Label subtitle = new Label("Otimizacao de rotas com Algoritmo Genetico");
        subtitle.getStyleClass().add("subtitle");

        VBox header = new VBox(4, title, subtitle);
        header.getStyleClass().add("header");
        return header;
    }

    private ScrollPane createControls() {
        problemCombo = new ComboBox<>();
        problemCombo.setMaxWidth(Double.MAX_VALUE);

        populationSpinner = new Spinner<>();
        populationSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(40, 800, 180, 20));
        populationSpinner.setEditable(true);

        generationSpinner = new Spinner<>();
        generationSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(30, 1200, 320, 20));
        generationSpinner.setEditable(true);

        mutationSlider = new Slider(0.01, 0.30, 0.08);
        mutationSlider.setShowTickLabels(true);
        mutationSlider.setShowTickMarks(true);
        mutationSlider.setMajorTickUnit(0.05);
        mutationValueLabel = new Label(formatPercent(mutationSlider.getValue()));
        mutationSlider.valueProperty().addListener((observable, oldValue, newValue) ->
                mutationValueLabel.setText(formatPercent(newValue.doubleValue())));

        speedSlider = new Slider(1, 5, 3);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(1);
        speedSlider.setMinorTickCount(0);
        speedSlider.setSnapToTicks(true);

        showInitialRouteCheck = new CheckBox("Comparar rota inicial");
        showInitialRouteCheck.setSelected(true);
        showInitialRouteCheck.selectedProperty().addListener((observable, oldValue, newValue) -> replayCurrentFrame());

        runButton = new Button("Executar GA");
        runButton.getStyleClass().add("primary-button");
        runButton.setMaxWidth(Double.MAX_VALUE);
        runButton.setOnAction(event -> runAlgorithm());

        replayButton = new Button("Reanimar resultado");
        replayButton.setMaxWidth(Double.MAX_VALUE);
        replayButton.setDisable(true);
        replayButton.setOnAction(event -> animateResult(currentResult));

        statusLabel = new Label("Carregando problemas...");
        statusLabel.getStyleClass().add("status-label");
        statusLabel.setWrapText(true);

        VBox controls = new VBox(
                12,
                field("Problema", problemCombo),
                field("Populacao", populationSpinner),
                field("Geracoes", generationSpinner),
                fieldWithSuffix("Mutacao", mutationSlider, mutationValueLabel),
                field("Velocidade", speedSlider),
                showInitialRouteCheck,
                runButton,
                replayButton,
                statusLabel);
        controls.getStyleClass().add("controls");

        ScrollPane scrollPane = new ScrollPane(controls);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("controls-scroll");
        return scrollPane;
    }

    private BorderPane createWorkspace() {
        routeCanvas = new RouteCanvas();
        StackPane canvasPane = new StackPane(routeCanvas);
        canvasPane.getStyleClass().add("panel");
        routeCanvas.widthProperty().bind(canvasPane.widthProperty().subtract(20));
        routeCanvas.heightProperty().bind(canvasPane.heightProperty().subtract(20));

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Geracao");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Distancia");
        chart = new LineChart<>(xAxis, yAxis);
        chart.setAnimated(false);
        chart.setCreateSymbols(false);
        chart.setLegendVisible(true);
        chart.getStyleClass().add("evolution-chart");

        bestSeries = new XYChart.Series<>();
        bestSeries.setName("Melhor");
        averageSeries = new XYChart.Series<>();
        averageSeries.setName("Media da populacao");
        chart.getData().addAll(bestSeries, averageSeries);

        VBox chartPanel = new VBox(8, new Label("Evolucao da distancia"), chart);
        chartPanel.getStyleClass().add("panel");
        VBox.setVgrow(chart, Priority.ALWAYS);

        javafx.scene.control.SplitPane splitPane = new javafx.scene.control.SplitPane(canvasPane, chartPanel);
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPositions(0.58);

        BorderPane workspace = new BorderPane(splitPane);
        workspace.setPadding(new Insets(16));
        return workspace;
    }

    private HBox createMetrics() {
        initialDistanceLabel = metric("Distancia inicial", "-");
        finalDistanceLabel = metric("Distancia final", "-");
        improvementLabel = metric("Melhoria", "-");
        generationLabel = metric("Geracao atual", "-");
        routeLabel = metric("Rota", "-");
        routeLabel.setWrapText(true);

        HBox metrics = new HBox(
                14,
                metricBox(initialDistanceLabel),
                metricBox(finalDistanceLabel),
                metricBox(improvementLabel),
                metricBox(generationLabel),
                metricBox(routeLabel));
        metrics.getStyleClass().add("metrics");
        return metrics;
    }

    private VBox field(String label, javafx.scene.Node control) {
        Label fieldLabel = new Label(label);
        fieldLabel.getStyleClass().add("field-label");
        VBox box = new VBox(5, fieldLabel, control);
        box.setFillWidth(true);
        return box;
    }

    private VBox fieldWithSuffix(String label, javafx.scene.Node control, Label suffix) {
        Label fieldLabel = new Label(label);
        fieldLabel.getStyleClass().add("field-label");
        HBox row = new HBox(8, control, suffix);
        HBox.setHgrow(control, Priority.ALWAYS);
        return new VBox(5, fieldLabel, row);
    }

    private Label metric(String title, String value) {
        Label label = new Label(title + "\n" + value);
        label.getStyleClass().add("metric-label");
        return label;
    }

    private VBox metricBox(Label label) {
        VBox box = new VBox(label);
        box.getStyleClass().add("metric-box");
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    private void loadProblems() {
        try {
            List<ProblemInstance> problems = problemRepository.loadProblems();
            problemCombo.getItems().setAll(problems);
            statusLabel.setText(problems.isEmpty()
                    ? "Nenhum CSV encontrado em data/problemas."
                    : problems.size() + " problema(s) carregado(s).");
        } catch (IOException exception) {
            statusLabel.setText("Falha ao carregar problemas: " + exception.getMessage());
        }
    }

    private void runAlgorithm() {
        ProblemInstance problem = problemCombo.getValue();
        if (problem == null) {
            statusLabel.setText("Selecione um problema para executar.");
            return;
        }

        stopAnimation();
        runButton.setDisable(true);
        replayButton.setDisable(true);
        statusLabel.setText("Executando algoritmo...");

        int population = populationSpinner.getValue();
        int generations = generationSpinner.getValue();
        int eliteCount = Math.max(2, population / 45);
        GeneticParameters parameters = new GeneticParameters(
                population,
                generations,
                mutationSlider.getValue(),
                eliteCount,
                4);

        Task<GeneticResult> task = new Task<>() {
            @Override
            protected GeneticResult call() {
                GeneticAlgorithm algorithm = new GeneticAlgorithm(parameters, new Random());
                return algorithm.solve(problem.cities());
            }
        };

        task.setOnSucceeded(event -> {
            currentResult = task.getValue();
            updateFinalMetrics(currentResult);
            animateResult(currentResult);
            runButton.setDisable(false);
            replayButton.setDisable(false);
            statusLabel.setText("Resultado pronto para apresentacao.");
        });

        task.setOnFailed(event -> {
            runButton.setDisable(false);
            statusLabel.setText("Erro: " + task.getException().getMessage());
        });

        Thread worker = new Thread(task, "genroute-ga-worker");
        worker.setDaemon(true);
        worker.start();
    }

    private void updateFinalMetrics(GeneticResult result) {
        initialDistanceLabel.setText("Distancia inicial\n" + NUMBER_FORMAT.format(result.initialRoute().distance()));
        finalDistanceLabel.setText("Distancia final\n" + NUMBER_FORMAT.format(result.bestRoute().distance()));
        improvementLabel.setText("Melhoria\n" + PERCENT_FORMAT.format(result.improvementPercent()));
        routeLabel.setText("Rota\n" + result.bestRoute().orderAsText());
    }

    private void animateResult(GeneticResult result) {
        if (result == null) {
            return;
        }

        stopAnimation();
        bestSeries.getData().clear();
        averageSeries.getData().clear();

        int totalFrames = result.bestDistanceHistory().size();
        double delay = 55.0 / speedSlider.getValue();
        final int[] frame = {0};

        animation = new Timeline(new KeyFrame(Duration.millis(delay), event -> {
            int index = frame[0];
            bestSeries.getData().add(new XYChart.Data<>(index, result.bestDistanceHistory().get(index)));
            averageSeries.getData().add(new XYChart.Data<>(index, result.averageDistanceHistory().get(index)));
            renderFrame(result, index);

            frame[0]++;
            if (frame[0] >= totalFrames) {
                stopAnimation();
                renderFrame(result, totalFrames - 1);
            }
        }));
        animation.setCycleCount(totalFrames);
        animation.play();
    }

    private void renderFrame(GeneticResult result, int index) {
        routeCanvas.setRoutes(
                result.initialRoute(),
                result.bestRouteHistory().get(index),
                showInitialRouteCheck.isSelected(),
                index,
                result.generationsEvaluated());
        generationLabel.setText("Geracao atual\n" + index + " / " + result.generationsEvaluated());
    }

    private void replayCurrentFrame() {
        if (currentResult == null) {
            return;
        }

        int index = Math.max(0, currentResult.bestRouteHistory().size() - 1);
        renderFrame(currentResult, index);
    }

    private void stopAnimation() {
        if (animation != null) {
            animation.stop();
            animation = null;
        }
    }

    private String formatPercent(double value) {
        return PERCENT_FORMAT.format(value * 100.0);
    }
}
