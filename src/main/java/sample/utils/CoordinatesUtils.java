package sample.utils;

import model.Lane;

import static sample.consts.LaneCoordinates.*;
import static model.Direction.*;

public class CoordinatesUtils {

    public static double getVehicleCoordinateX(Lane lane, int indexInQueue) {
        int directionK;
        if (Left() == lane.direction())
            directionK = 1;
        else if (Right() == lane.direction())
            directionK = -1;
        else directionK = 0;

        return getX(lane.id()) + directionK * (indexInQueue * getInterval() + getBeforeLightInterval());
    }

    public static double getVehicleCoordinateY(Lane lane, int indexInQueue) {
        int directionK;
        if (Up() == lane.direction())
            directionK = 1;
        else if (Down() == lane.direction())
            directionK = -1;
        else directionK = 0;

        return getY(lane.id()) + directionK * (indexInQueue * getInterval() + getBeforeLightInterval());
    }

    public static double getLightCoordinateX(Long laneId) {
        return getX(laneId);
    }

    public static double getLightCoordinateY(Long laneId) {
        return getY(laneId);
    }
}
