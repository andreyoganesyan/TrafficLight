package events

import misc.StatisticsCollector
import model.{Crossroads, Vehicle}
import org.apache.commons.math3.distribution.ExponentialDistribution

class SimulationController(timeLimit: Double, config: SimulationConfig) {
  private val generationDistribution = new ExponentialDistribution(config.avgGenerationTime)
  private val passDistribution = new ExponentialDistribution(config.avgCarPassTime)

  def simulate(crossroads: Crossroads, eventQueue: EventQueue, time: Double): Unit = {

    if (eventQueue.nonEmpty && time < timeLimit) {

      val (event, queueTail) = eventQueue.dequeueEvent

      event match {
        case VehicleSpawn(laneId, vehicle, eventTime) =>
          val newCrossroadsState = crossroads.spawnVehicle(vehicle, laneId)
          val nextGenerationTime = eventTime + generationDistribution.sample()
          val newQueue = queueTail.addEvent(VehicleSpawn(laneId, Vehicle(nextGenerationTime), nextGenerationTime))
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

        case VehiclePass(laneId, vehicle, eventTime) =>
          StatisticsCollector.addTimeRecord(laneId, eventTime - vehicle.creationTime)
          val newCrossRoadsState = crossroads.removeVehicle(vehicle, laneId)
          val lane = newCrossRoadsState.lanes(laneId)
          val newQueue =
            if (lane.isGreen && lane.vehicles.nonEmpty) {
              val nextPassTime = eventTime + passDistribution.sample()
              queueTail.addEvent(VehiclePass(laneId, lane.vehicles.last, nextPassTime))
            }

      }

    }
  }


}

case class SimulationConfig(avgGenerationTime: Double,
                            avgCarPassTime: Double,
                            trafficChangeTime: Double)