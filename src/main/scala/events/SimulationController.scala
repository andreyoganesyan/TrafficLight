package events

import model.{Crossroads, Vehicle}
import org.apache.commons.math3.distribution.ExponentialDistribution

class SimulationController(timeLimit: Double, config: SimulationConfig) {
  val expDistribution = new ExponentialDistribution(config.avgGenerationTime);

  def simulate(crossroads: Crossroads, eventQueue: EventQueue, time: Double): Unit = {

    if (eventQueue.nonEmpty && time < timeLimit) {

      val (event, queueTail) = eventQueue.dequeueEvent

      event match {
        case VehicleSpawn(lane, vehicle, eventTime) =>
          val newCrossroadsState = crossroads.spawnVehicle(vehicle, lane)
          val nextGenerationTime = eventTime + expDistribution.sample()
          val newQueue = queueTail.addEvent(VehicleSpawn(lane, Vehicle(nextGenerationTime), nextGenerationTime))
          simulate(newCrossroadsState, newQueue, eventTime)

        case LightChange(direction, newStateIsGreen, eventTime) =>
          val newCrossroadsState =
            if (newStateIsGreen)
              crossroads.turnGreen(direction)
            else
              crossroads.turnRed(direction)
          val nextChangeTime = eventTime + config.trafficChangeTime
          val newQueue = queueTail.addEvent(LightChange(direction, !newStateIsGreen, nextChangeTime))
          simulate(newCrossroadsState, newQueue, eventTime)

        case VehiclePass(lane, vehicle, eventTime) =>
          val newCrossRoadsState = crossroads.
      }

    }
  }


}

case class SimulationConfig(avgGenerationTime: Double,
                            trafficChangeTime: Double)