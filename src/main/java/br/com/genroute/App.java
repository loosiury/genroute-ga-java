package br.com.genroute;

import br.com.genroute.ga.GeneticAlgorithm;
import br.com.genroute.ga.GeneticParameters;
import br.com.genroute.ga.GeneticResult;
import br.com.genroute.io.ProblemRepository;
import br.com.genroute.model.ProblemInstance;
import br.com.genroute.visual.RouteCanvas;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public final class App extends Application {
    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,##0.00");
    private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("#,##0.00'%'");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final Color INITIAL_ROUTE_COLOR = Color.web("#123f91");
    private static final Color BEST_ROUTE_COLOR = Color.web("#078b5d");

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
    private Label footerProblemLabel;
    private Label footerPopulationLabel;
    private Label footerGenerationLabel;
    private Label footerMutationLabel;
    private Label footerTimeLabel;
    private RouteCanvas initialRouteCanvas;
    private RouteCanvas bestRouteCanvas;
    private LineChart<Number, Number> chart;
    private XYChart.Series<Number, Number> bestSeries;
    private Timeline animation;
    private GeneticResult currentResult;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setLeft(createSidebar());
        root.setCenter(createDashboard());
        root.setBottom(createFooter());

        Scene scene = new Scene(root, 1440, 820);
        String stylesheet = getClass().getResource("/styles/app.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);

        stage.setTitle("GenRoute GA - JavaFX");
        stage.setScene(scene);
        stage.setMinWidth(1120);
        stage.setMinHeight(720);
        stage.show();

        loadProblems();
        if (!problemCombo.getItems().isEmpty()) {
            problemCombo.getSelectionModel().selectLast();
            runAlgorithm();
        }
    }

    private VBox createSidebar() {
        Label logoIcon = new Label("⌖");
        logoIcon.getStyleClass().add("logo-icon");

        Label logoText = new Label("GenRoute\nGA");
        logoText.getStyleClass().add("logo-text");

        HBox logo = new HBox(10, logoIcon, logoText);
        logo.setAlignment(Pos.CENTER_LEFT);
        logo.getStyleClass().add("logo");

        problemCombo = new ComboBox<>();
        problemCombo.setMaxWidth(Double.MAX_VALUE);

        populationSpinner = new Spinner<>();
        populationSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(40, 800, 200, 20));
        populationSpinner.setEditable(true);

        generationSpinner = new Spinner<>();
        generationSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(30, 1200, 100, 10));
        generationSpinner.setEditable(true);

        mutationSlider = new Slider(0.01, 0.30, 0.02);
        mutationSlider.setShowTickLabels(true);
        mutationSlider.setShowTickMarks(true);
        mutationSlider.setMajorTickUnit(0.05);
        mutationValueLabel = new Label(formatPercent(mutationSlider.getValue()));
        mutationValueLabel.getStyleClass().add("sidebar-small-value");
        mutationSlider.valueProperty().addListener((observable, oldValue, newValue) ->
                mutationValueLabel.setText(formatPercent(newValue.doubleValue())));

        speedSlider = new Slider(1, 5, 3);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(1);
        speedSlider.setMinorTickCount(0);
        speedSlider.setSnapToTicks(true);

        showInitialRouteCheck = new CheckBox("Referência inicial");
        showInitialRouteCheck.setSelected(true);
        showInitialRouteCheck.selectedProperty().addListener((observable, oldValue, newValue) -> replayCurrentFrame());

        runButton = new Button("Executar AG");
        runButton.getStyleClass().add("primary-button");
        runButton.setMaxWidth(Double.MAX_VALUE);
        runButton.setOnAction(event -> runAlgorithm());

        replayButton = new Button("Reanimar");
        replayButton.setMaxWidth(Double.MAX_VALUE);
        replayButton.setDisable(true);
        replayButton.setOnAction(event -> animateResult(currentResult));

        statusLabel = new Label("Carregando problemas...");
        statusLabel.getStyleClass().add("sidebar-status");
        statusLabel.setWrapText(true);

        VBox controls = new VBox(
                12,
                navItem("◉", "Visualização", true),
                navItem("☰", "Parâmetros", false),
                field("Problema", problemCombo),
                field("População", populationSpinner),
                field("Gerações", generationSpinner),
                fieldWithSuffix("Mutação", mutationSlider, mutationValueLabel),
                field("Velocidade", speedSlider),
                showInitialRouteCheck,
                runButton,
                replayButton);
        controls.getStyleClass().add("sidebar-controls");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox statusBox = new VBox(4, new Label("✓ Execução"), statusLabel);
        statusBox.getStyleClass().add("sidebar-status-box");

        VBox sidebar = new VBox(22, logo, controls, spacer, statusBox);
        sidebar.getStyleClass().add("sidebar");
        return sidebar;
    }

    private ScrollPane createDashboard() {
        Label title = new Label("Visualização do processo");
        title.getStyleClass().add("dashboard-title");

        Label subtitle = new Label("Acompanhe a evolução do Algoritmo Genético e compare as rotas");
        subtitle.getStyleClass().add("dashboard-subtitle");

        VBox heading = new VBox(3, title, subtitle);

        Button reportButton = new Button("Exportar relatório");
        reportButton.getStyleClass().add("report-button");
        reportButton.setOnAction(event -> statusLabel.setText("Relatório atualizado em docs/Documentacao_Seminario_Algoritmo_Genetico-iury-atualizada.pdf"));

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        HBox header = new HBox(16, heading, headerSpacer, reportButton);
        header.setAlignment(Pos.CENTER_LEFT);

        initialDistanceLabel = metricValue("-");
        finalDistanceLabel = metricValue("-");
        improvementLabel = metricValue("-");

        HBox metricRow = new HBox(
                18,
                metricCard("Distância inicial", initialDistanceLabel, "Rota construída inicialmente", "△", "metric-blue"),
                metricCard("Distância final", finalDistanceLabel, "Melhor rota encontrada", "⚑", "metric-green"),
                metricCard("Melhoria", improvementLabel, "Redução em relação à inicial", "↗", "metric-teal"));
        metricRow.getStyleClass().add("metric-row");

        initialRouteCanvas = new RouteCanvas();
        bestRouteCanvas = new RouteCanvas();

        HBox routeRow = new HBox(
                18,
                routePanel("Rota inicial", "route-title-blue", initialRouteCanvas),
                routePanel("Melhor rota encontrada", "route-title-green", bestRouteCanvas));
        routeRow.getStyleClass().add("route-row");

        VBox chartPanel = createChartPanel();

        VBox dashboard = new VBox(18, header, metricRow, routeRow, chartPanel);
        dashboard.getStyleClass().add("dashboard");

        ScrollPane scrollPane = new ScrollPane(dashboard);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("dashboard-scroll");
        return scrollPane;
    }

    private HBox createFooter() {
        footerProblemLabel = footerItem("Problema", "-");
        footerPopulationLabel = footerItem("População", "-");
        footerGenerationLabel = footerItem("Gerações", "-");
        footerMutationLabel = footerItem("Mutação", "-");
        footerTimeLabel = footerItem("Atualizado", "-");
        generationLabel = footerItem("Geração atual", "-");

        HBox footer = new HBox(
                28,
                footerItem("Algoritmo", "Algoritmo Genético"),
                footerProblemLabel,
                footerPopulationLabel,
                footerGenerationLabel,
                footerMutationLabel,
                generationLabel,
                footerTimeLabel);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.getStyleClass().add("footer");
        return footer;
    }

    private VBox createChartPanel() {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Geração");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Distância");

        chart = new LineChart<>(xAxis, yAxis);
        chart.setAnimated(false);
        chart.setCreateSymbols(true);
        chart.setLegendVisible(true);
        chart.getStyleClass().add("evolution-chart");

        bestSeries = new XYChart.Series<>();
        bestSeries.setName("Melhor distância");
        chart.getData().add(bestSeries);

        Label chartTitle = new Label("Evolução da melhor solução");
        chartTitle.getStyleClass().add("panel-title");

        VBox chartPanel = new VBox(8, chartTitle, chart);
        chartPanel.getStyleClass().add("panel");
        chartPanel.setMinHeight(230);
        chartPanel.setPrefHeight(260);
        VBox.setVgrow(chart, Priority.ALWAYS);
        return chartPanel;
    }

    private HBox navItem(String icon, String text, boolean active) {
        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("nav-icon");
        Label textLabel = new Label(text);
        textLabel.getStyleClass().add("nav-text");
        HBox row = new HBox(10, iconLabel, textLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add(active ? "nav-item-active" : "nav-item");
        return row;
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
        row.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(control, Priority.ALWAYS);
        return new VBox(5, fieldLabel, row);
    }

    private VBox metricCard(String title, Label value, String detail, String icon, String iconStyle) {
        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().addAll("metric-icon", iconStyle);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("metric-title");

        Label detailLabel = new Label(detail);
        detailLabel.getStyleClass().add("metric-detail");

        VBox text = new VBox(5, titleLabel, value, detailLabel);
        HBox.setHgrow(text, Priority.ALWAYS);

        HBox card = new HBox(16, iconLabel, text);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("metric-card");
        card.setMaxWidth(Double.MAX_VALUE);

        VBox wrapper = new VBox(card);
        wrapper.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(wrapper, Priority.ALWAYS);
        return wrapper;
    }

    private Label metricValue(String value) {
        Label label = new Label(value);
        label.getStyleClass().add("metric-value");
        return label;
    }

    private VBox routePanel(String title, String titleStyleClass, RouteCanvas canvas) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().addAll("panel-title", titleStyleClass);

        Label legend = new Label("● Depósito   ● Cliente");
        legend.getStyleClass().add("route-legend");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox header = new HBox(12, titleLabel, spacer, legend);
        header.setAlignment(Pos.CENTER_LEFT);

        StackPane canvasHost = new StackPane(canvas);
        canvasHost.getStyleClass().add("canvas-host");
        canvasHost.setMinHeight(260);
        canvasHost.setPrefHeight(290);
        canvas.widthProperty().bind(canvasHost.widthProperty());
        canvas.heightProperty().bind(canvasHost.heightProperty());

        VBox panel = new VBox(8, header, canvasHost);
        panel.getStyleClass().add("panel");
        panel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(panel, Priority.ALWAYS);
        return panel;
    }

    private Label footerItem(String label, String value) {
        Label footerLabel = new Label(label + ": " + value);
        footerLabel.getStyleClass().add("footer-item");
        return footerLabel;
    }

    private void updateFooterItem(Label footerLabel, String label, String value) {
        footerLabel.setText(label + ": " + value);
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
            updateFooter(problem, currentResult);
            animateResult(currentResult);
            runButton.setDisable(false);
            replayButton.setDisable(false);
            statusLabel.setText("Execução concluída.");
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
        double initial = result.initialRoute().distance();
        double best = result.bestRoute().distance();
        double gain = initial - best;

        initialDistanceLabel.setText(formatDistance(initial));
        finalDistanceLabel.setText(formatDistance(best));
        improvementLabel.setText(formatDistance(gain) + " (" + PERCENT_FORMAT.format(result.improvementPercent()) + ")");
    }

    private void updateFooter(ProblemInstance problem, GeneticResult result) {
        updateFooterItem(footerProblemLabel, "Problema", problem.name());
        updateFooterItem(footerPopulationLabel, "População", String.valueOf(result.parameters().populationSize()));
        updateFooterItem(footerGenerationLabel, "Gerações", String.valueOf(result.parameters().generations()));
        updateFooterItem(footerMutationLabel, "Mutação", formatPercent(result.parameters().mutationRate()));
        updateFooterItem(footerTimeLabel, "Atualizado", DATE_TIME_FORMAT.format(LocalDateTime.now()));
    }

    private void animateResult(GeneticResult result) {
        if (result == null) {
            return;
        }

        stopAnimation();
        bestSeries.getData().clear();

        int totalFrames = result.bestDistanceHistory().size();
        double delay = 60.0 / speedSlider.getValue();
        final int[] frame = {0};

        animation = new Timeline(new KeyFrame(Duration.millis(delay), event -> {
            int index = frame[0];
            bestSeries.getData().add(new XYChart.Data<>(index, result.bestDistanceHistory().get(index)));
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
        initialRouteCanvas.setRouteView(
                null,
                result.initialRoute(),
                false,
                INITIAL_ROUTE_COLOR,
                index,
                result.generationsEvaluated(),
                false);
        bestRouteCanvas.setRouteView(
                result.initialRoute(),
                result.bestRouteHistory().get(index),
                showInitialRouteCheck.isSelected(),
                BEST_ROUTE_COLOR,
                index,
                result.generationsEvaluated(),
                true);
        updateFooterItem(generationLabel, "Geração atual", index + " / " + result.generationsEvaluated());
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

    private String formatDistance(double value) {
        return NUMBER_FORMAT.format(value) + " km";
    }

    private String formatPercent(double value) {
        return PERCENT_FORMAT.format(value * 100.0);
    }
}
