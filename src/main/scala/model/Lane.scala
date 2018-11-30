package model

object Direction extends Enumeration {
  type Direction = Value
  val Up: Direction = Value(0)
  val Left: Direction = Value(1)
  val Down: Direction = Value(2)
  val Right: Direction = Value(3)
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

