package sample.service;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import model.Lane;
import scala.Enumeration;

import java.util.List;

import static sample.consts.Images.*;
import static sample.utils.CoordinatesUtils.*;

public class DisplayService {
    private Pane crossroad;

    public DisplayService(Pane crossroad) {
        this.crossroad = crossroad;
        initCossroad();
    }

    public void display(List<Lane> lanes) {
        crossroad.getChildren().clear();
        initCossroad();
        lanes.forEach(lane -> {
            displayVehicles(lane);
            displayLight(lane);
        });
    }

    private void displayVehicles(Lane lane) {
        for (int i = 0; i < lane.vehicles().size(); i++) {
            ImageView vehicle = new ImageView(getVehicle());
            vehicle.setRotate(getRotationAngle(lane.direction()));
            vehicle.setTranslateX(getVehicleCoordinateX(lane, i));
            vehicle.setTranslateY(getVehicleCoordinateY(lane, i));
            crossroad.getChildren().add(vehicle);
        }
    }

    private void displayLight(Lane lane) {
        ImageView img = new ImageView(lane.isGreen() ? getGreenLight() : getRedLight());
        img.setRotate(getRotationAngle(lane.direction()));
        img.setTranslateX(getLightCoordinateX(lane.id()));
        img.setTranslateY(getLightCoordinateY(lane.id()));
        crossroad.getChildren().add(img);
    }

    private double getRotationAngle(Enumeration.Value direction) {
        return -90 * direction.id();
    }

    private void initCossroad() {
        crossroad.getChildren().add(getCrossroad());
    }
}
