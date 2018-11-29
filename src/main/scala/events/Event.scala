package events

import model.Direction.Direction
import model._

sealed abstract class Event(val time: Double)

case class VehicleSpawn(lane: Lane,
                        vehicle: Vehicle,
                        override val time: Double) extends Event(time)

case class LightChange(direction: Direction,
                       newStateIsGreen: Boolean,
                       override val time: Double) extends Event(time)

case class VehiclePass(lane: Lane,
                       vehicle: Vehicle,
                       override val time: Double) extends Event(time)
