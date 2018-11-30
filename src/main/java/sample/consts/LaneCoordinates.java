package sample.consts;

import java.util.Arrays;
import java.util.List;

import static sample.consts.Images.getVehicle;

public class LaneCoordinates {
    private static final List<Double> X_COORDINATES = Arrays.asList(17.0, 52., 87.,
            113., 113., 113.,
            -20., -55., -90.,
            -116., -116., -116.);
    private static final List<Double> Y_COORDINATES = Arrays.asList(113.0, 113., 113.,
            -20., -55., -90.,
            -116., -116., -116.,
            17., 52., 87.);
    private static final double INTERVAL = getVehicle().getHeight() + 5;
    private static final double BEFORE_LIGHT_INTERVAL = getVehicle().getHeight() / 2 + 5;

    public static double getInterval() {
        return INTERVAL;
    }

    public static double getX(Long laneId) {
        return X_COORDINATES.get(laneId.intValue());
    }

    public static double getY(Long laneId) {
        return Y_COORDINATES.get(laneId.intValue());
    }

    public static double getBeforeLightInterval() {
        return BEFORE_LIGHT_INTERVAL;
    }
}
