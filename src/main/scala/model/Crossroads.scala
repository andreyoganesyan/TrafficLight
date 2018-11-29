package model

import Direction._

case class Crossroads(lanes: List[Lane]) {

  def spawnVehicle(vehicle: Vehicle, lane: Lane): Crossroads = {
    this.copy(lanes = lane.copy(vehicles = vehicle :: lane.vehicles) :: lanes.filter(_ != lane))
  }

  private def editLanes(predicate: Lane => Boolean)(f: Lane => Lane): Crossroads =
    this.copy(lanes = lanes.map { lane =>
      if (predicate(lane))
        f(lane)
      else
        lane
    })

  def turnGreen(direction: Direction): Crossroads = editLanes(_.direction == direction)(_.turnGreen)

  def turnRed(direction: Direction): Crossroads = editLanes(_.direction == direction)(_.turnRed)


}
