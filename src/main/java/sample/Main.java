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


import static sample.consts.Images.getBGHeight;
import static sample.consts.Images.getBGWifth;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Pane pane = new StackPane();
        DisplayService service = new DisplayService(pane);

        SimulationConfig config = new SimulationConfig(3, 1, 10, 100);
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
    }

    public static void main(String[] args) {
        launch(args);
    }
}
