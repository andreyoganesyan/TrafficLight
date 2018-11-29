package misc

import scala.collection.mutable

object StatisticsCollector {
  val waitingTimeMap: mutable.Map[Long, Seq[Double]] = new mutable.TreeMap[Long, Seq[Double]]() withDefaultValue Nil

  def addTimeRecord(laneId: Long, time: Double): Unit = waitingTimeMap.update(laneId, time :: waitingTimeMap(laneId))
}
