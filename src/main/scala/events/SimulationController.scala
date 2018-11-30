package events

import java.util.concurrent.TimeUnit

import misc.StatisticsCollector
import model.{Crossroads, Direction, Lane, Vehicle}
import org.apache.commons.math3.distribution.ExponentialDistribution

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class SimulationController(config: SimulationConfig, onModelChange: java.util.List[Lane] => _) {
  private val generationDistribution = new ExponentialDistribution(config.avgGenerationTime)
  private val passDistribution = new ExponentialDistribution(config.avgCarPassTime)

  def run(): Unit = {
    implicit val ec: ExecutionContext = ExecutionContext.global
    val init = initializeModel
    Future {
      simulate(init._1, init._2, 0)
    } onComplete {
      case Success(value) =>
        println("Successfully finished the simulation")
        val laneStatistics: Map[Lane, Double] = StatisticsCollector.waitingTimeMap.toMap.map {
          case (laneId, waitTimes) =>
            (init._1.lanes(laneId), waitTimes.sum / waitTimes.size)
        }
        println("\nAverage waiting times:")
        laneStatistics.groupBy(_._1.direction).toSeq.sortBy(_._1.id) foreach {
          case (direction, waitTimeMap) =>
            println(s"\t$direction: ${waitTimeMap.values.sum / waitTimeMap.values.size}")
            waitTimeMap.toSeq.sortBy(_._1.id) foreach {
              case (lane, avgWaitTime) =>
                println(s"\t\tLane#${lane.id}: $avgWaitTime")
            }
        }
      case Failure(exception) => println(s"Exception: $exception")
    }
  }

  private def simulate(crossroads: Crossroads, eventQueue: EventQueue, time: Double): Unit = {
    import scala.collection.JavaConverters._
    onModelChange(crossroads.lanes.values.toList.asJava)

    if (eventQueue.nonEmpty && time < config.timeLimit) {

      val (event, queueTail) = eventQueue.dequeueEvent
      val sleepTime = (1000 * (event.time - time)).toLong
      println(s"Handling $event, sleeping for ${sleepTime}ms")

      TimeUnit.MILLISECONDS.sleep(sleepTime)

      event match {
        case VehicleSpawn(laneId, vehicle, eventTime) =>
          val newCrossroadsState = crossroads.spawnVehicle(vehicle, laneId)
          val nextGenerationTime = eventTime + generationDistribution.sample()
          val vehicleGenerationEvent = VehicleSpawn(laneId, Vehicle(nextGenerationTime), nextGenerationTime)
          var newQueue = queueTail.addEvent(vehicleGenerationEvent)
          if (newCrossroadsState.lanes(laneId).isGreen) {
            newQueue = newQueue.addEvent(VehiclePass(laneId, vehicle, eventTime + passDistribution.sample()))
          }
          simulate(newCrossroadsState, newQueue, eventTime)

        case LightChange(direction, newStateIsGreen, eventTime) =>
          val newCrossroadsState =
            if (newStateIsGreen)
              crossroads.turnGreen(direction)
            else
              crossroads.turnRed(direction)
          val nextChangeTime = eventTime + config.trafficChangeTime
          var newQueue = queueTail.addEvent(LightChange(direction, !newStateIsGreen, nextChangeTime))
          if (newStateIsGreen) {
            val carPassEvents = newCrossroadsState.lanes.values
              .filter(lane => lane.direction == direction && lane.vehicles.nonEmpty)
              .map(lane => VehiclePass(lane.id, lane.vehicles.head, eventTime + passDistribution.sample()))
            carPassEvents foreach { newEvent =>
              newQueue = newQueue.addEvent(newEvent)
            }
          }
          simulate(newCrossroadsState, newQueue, eventTime)

        case VehiclePass(laneId, vehicle, eventTime) =>
          StatisticsCollector.addTimeRecord(laneId, eventTime - vehicle.creationTime)
          val newCrossRoadsState = crossroads.removeVehicle(vehicle, laneId)
          val lane = newCrossRoadsState.lanes(laneId)
          val newQueue =
            if (lane.isGreen && lane.vehicles.nonEmpty) {
              val nextPassTime = eventTime + passDistribution.sample()
              queueTail.addEvent(VehiclePass(laneId, lane.vehicles.last, nextPassTime))
            } else
              queueTail
          simulate(newCrossRoadsState, newQueue, eventTime)

      }

    }
  }

  private def initializeModel: (Crossroads, EventQueue) = {

    val lanes = List(Lane(0, Direction.Up, isGreen = false),
      Lane(1, Direction.Up, isGreen = false),
      Lane(2, Direction.Up, isGreen = false),
      Lane(3, Direction.Left, isGreen = true),
      Lane(4, Direction.Left, isGreen = true),
      Lane(5, Direction.Left, isGreen = true),
      Lane(6, Direction.Down, isGreen = false),
      Lane(7, Direction.Down, isGreen = false),
      Lane(8, Direction.Down, isGreen = false),
      Lane(9, Direction.Right, isGreen = true),
      Lane(10, Direction.Right, isGreen = true),
      Lane(11, Direction.Right, isGreen = true))
    val crossroads = Crossroads(lanes.map(lane => (lane.id, lane)).toMap)


    val colorChangeStartingEvents = List(LightChange(Direction.Up, newStateIsGreen = true, config.trafficChangeTime),
      LightChange(Direction.Left, newStateIsGreen = false, config.trafficChangeTime),
      LightChange(Direction.Down, newStateIsGreen = true, config.trafficChangeTime),
      LightChange(Direction.Right, newStateIsGreen = false, config.trafficChangeTime))
    val firstCarSpawnEvents = lanes.map(lane => {
      val genTime = generationDistribution.sample()
      VehicleSpawn(lane.id, Vehicle(genTime), genTime)
    })
    val events = (colorChangeStartingEvents ::: firstCarSpawnEvents).sortBy(_.time)
    val eventQueue = new EventQueue(events)

    (crossroads, eventQueue)
  }

}


case class SimulationConfig(avgGenerationTime: Double,
                            avgCarPassTime: Double,
                            trafficChangeTime: Double,
                            timeLimit: Double)