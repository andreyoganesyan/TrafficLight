package sample;

import events.EventQueue;
import events.SimulationConfig;
import events.SimulationController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.Crossroads;
import sample.service.DisplayService;
import scala.Tuple2;


import java.io.IOException;
import java.util.Properties;

import static sample.consts.Images.getBGHeight;
import static sample.consts.Images.getBGWifth;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Pane pane = new StackPane();
        DisplayService service = new DisplayService(pane);

        Properties simProps = new Properties();
        try {
            simProps.load(getClass().getClassLoader().getResourceAsStream("simulation.properties"));
            SimulationConfig config = new SimulationConfig(
                    Double.parseDouble(simProps.getProperty("avgGenerationTime")),
                    Double.parseDouble(simProps.getProperty("avgCarPassTime")),
                    Double.parseDouble(simProps.getProperty("trafficChangeTime")),
                    Double.parseDouble(simProps.getProperty("timeLimit")));
            SimulationController simulationController = new SimulationController(config, x -> {
                Platform.runLater(() -> service.display(x));
                return null;
            });
            simulationController.run();
            Scene scene = new Scene(pane, getBGWifth(), getBGHeight());
            primaryStage.setTitle("MY LOVELY CROSSROAD");
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
