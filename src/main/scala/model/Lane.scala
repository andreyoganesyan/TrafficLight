package model

object Direction extends Enumeration {
  type Direction = Value
  val Up, Down, Right, Left = Value
}

import model.Direction._

case class Lane(id: Long,
                direction: Direction,
                isGreen: Boolean,
                vehicles: List[Vehicle] = Nil) {
  def turnGreen: Lane = this.copy(isGreen = true)

  def turnRed: Lane = this.copy(isGreen = false)

  def removeVehicle(vehicle: Vehicle): Lane = this.copy(vehicles = vehicles.filter(_ != vehicle))

  def spawnVehicle(vehicle: Vehicle): Lane = this.copy(vehicles = vehicle :: vehicles)
}

