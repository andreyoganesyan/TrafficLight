package model

import model.Direction._

case class Crossroads(lanes: Map[Long, Lane]) {

  def spawnVehicle(vehicle: Vehicle, laneId: Long): Crossroads = {
    this.copy(lanes = lanes updated(laneId, lanes(laneId).spawnVehicle(vehicle)))
  }

  def removeVehicle(vehicle: Vehicle, laneId: Long): Crossroads = {
    this.copy(lanes = lanes updated(laneId, lanes(laneId).removeVehicle(vehicle)))
  }

  private def editLanes(predicate: Lane => Boolean)(f: Lane => Lane): Crossroads =
    this.copy(lanes = lanes.mapValues { lane =>
      if (predicate(lane))
        f(lane)
      else
        lane
    })

  def turnGreen(direction: Direction): Crossroads = editLanes(_.direction == direction)(_.turnGreen)

  def turnRed(direction: Direction): Crossroads = editLanes(_.direction == direction)(_.turnRed)


}
